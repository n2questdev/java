package org.wpb.lms.integration.api.helpers;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.DBEmployee;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.Group;
import org.wpb.lms.entities.Groups;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateEmployee extends APIBase {
	private static final Logger log = LogManager.getLogger(UpdateEmployee.class);

	/**
	 * Following method creates a new employee in the collection Site URL: 
	 * 1. PUT Employee info to http://devsandbox.targetsolutions.com/v1/sites/28658/users 
	 * 2. Get userid from the response of above 
	 * 3. PUT Employee email to http://devsandbox.targetsolutions.com/v1/users/{userid}/emails
	 * 4. Get Profile categories (columns) map 
	 * 5. If Group updated, then delete old group from http://devsandbox.targetsolutions.com/v1/sites/28658/categories/profile/{categoryid}/groups/{groupID}
	 * 6. POST new group to http://devsandbox.targetsolutions.com/v1/sites/28658/categories/profile/{categoryid}/groups/{groupID}/users
	 * 
	 * Note: Credentials are ignored for the moment. WPB data export doesn't have credentials
	 * @param DBEmployee
	 * @return "updated" or "full failure or partial failure messages"
	 */
	public String updateEmployee(DBEmployee dbEmp) {
		StringBuilder errorMessages = new StringBuilder();
		Response response = null;
		Employee responseEmp = new Employee();
		Employee emp = new Employee();
		emp.setFirstname(dbEmp.getFIRST_NAME_MI());
		emp.setLastname(dbEmp.getLAST_NAME());
		emp.setUsername(dbEmp.getUSERNAME());
		emp.setEmployeeid(dbEmp.getEMPLOYEE_ID());

		Employee lmsEmp;
		try {
			lmsEmp = new GetEmployee().getEmployeeByEmpNo(dbEmp.getEMPLOYEE_ID());
			if(lmsEmp == null) {
				log.error("Employee not found in LMS! shouldn't you create first?");
				return "Employee not found in LMS! shouldn't you create first?";
			}
			WebTarget usersSite = getUserSite(lmsEmp.getUserid());

			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_EMPTY);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

			response = usersSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.put(Entity.entity(mapper.writeValueAsString(emp), MediaType.APPLICATION_JSON));

			responseEmp = mapper.readValue(response.readEntity(String.class), Employee.class);

			if (responseEmp != null && ((responseEmp.getStatus() != null) && !responseEmp.getStatus().equals("accepted"))) {
				log.error("Unable to update employee! API Response:: Status: " + responseEmp.getStatus()
						+ ", Developer Message: " + responseEmp.getDevelopermessage());
				errorMessages.append("Unable to update employee! API Response:: Status: " + responseEmp.getStatus()
				+ ", Developer Message: " + responseEmp.getDevelopermessage());
			} else {
				log.debug("Employee: " + emp.getUsername() + " successfully updated. Now updating groups...");
			}
			
			//PUT won't populate userID or employeeID in responseEmp. So populating those explicitly before calling setting Groups...
			responseEmp.setUserid(lmsEmp.getUserid());
			responseEmp.setEmployeeid(lmsEmp.getEmployeeid());
			
			setEmployeeGroups(dbEmp, errorMessages, responseEmp, mapper);

			log.debug("Employee and group updates are completed. Now updating emails...");

			response = setEmployeeEmail(dbEmp, errorMessages, responseEmp, mapper);
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Close all connections
			if(response != null)
				response.close();
		}

		if (errorMessages.length() > 0) { // there are few group assignment errors
			return "Employee update partially successful. Errors - " + errorMessages;
		} else {
			return "updated";
		}
	}

	private Response setEmployeeEmail(DBEmployee dbEmp, StringBuilder errorMessages, Employee responseEmp,
			ObjectMapper mapper) throws IOException, JsonParseException, JsonMappingException {
		Response response;
		WebTarget emailSite = getUserSite(responseEmp.getUserid()).path("emails");
		response = emailSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken())
				.post(Entity.entity("{\"email\":\"" + dbEmp.getEMAIL() + "\"}", MediaType.APPLICATION_JSON));

		responseEmp = mapper.readValue(response.readEntity(String.class), Employee.class);

		if ((responseEmp != null && responseEmp.getDevelopermessage() != null) && responseEmp.getDevelopermessage().contains("Email address is already in use")) {
			log.debug("Email update attempted for Employee with ID: " + dbEmp.getEMPLOYEE_ID() + ". But " + dbEmp.getEMAIL() + " is already in use");
		} else if((responseEmp != null && responseEmp.getStatus() != null) &&  responseEmp.getStatus().contains("created")) {
			log.debug("Email update attempted for Employee with ID: " + dbEmp.getEMPLOYEE_ID() + ". " + dbEmp.getEMAIL() + " successfully updated");
		} else {
			errorMessages.append("Unable to set email address! API Response:: Status: " + responseEmp.getStatus()
			+ ", Developer message: " + responseEmp.getDevelopermessage() + ".");
		}
		return response;
	}

	private void setEmployeeGroups(DBEmployee dbEmp, StringBuilder errorMessages, Employee responseEmp,
			ObjectMapper mapper) throws IOException, JsonParseException, JsonMappingException {
		
		final Map<String, String> categories = getProfileCategories();
		String categoryID;
		String assignGroupResponse;
		
		// Set Department
		if (!dbEmp.getDEPT().isEmpty()) {
			categoryID = categories.get("Department");
			assignGroupResponse = setGroup(dbEmp.getDEPT(), responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Department " + assignGroupResponse + ". ");
		}
		// Set DIVISION
		if (!dbEmp.getDIVISION().isEmpty()) {
			categoryID = categories.get("Division");
			assignGroupResponse = setGroup(dbEmp.getDIVISION(), responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Division " + assignGroupResponse + ". ");
		}
		// Set JOB_TITLE
		if (!dbEmp.getJOB_TITLE().isEmpty()) {
			categoryID = categories.get("Job Title");
			assignGroupResponse = setGroup(dbEmp.getJOB_TITLE(), responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Job Title " + assignGroupResponse + ". ");
		}

		// Set MANAGEMENT
		if (!dbEmp.getMANAGEMENT().isEmpty()) {
			categoryID = categories.get("Management");
			assignGroupResponse = setGroup(dbEmp.getMANAGEMENT(), responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Management " + assignGroupResponse + ". ");
		}
		// Set EMPLOYEE_GROUP
		if (!dbEmp.getEMPLOYEE_GROUP().isEmpty()) {
			categoryID = categories.get("Employee Group");
			assignGroupResponse = setGroup(dbEmp.getEMPLOYEE_GROUP(), responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Employee Group " + assignGroupResponse + ". ");
		}
		// Set EMPLOYEE_CATEGORY
		if (!dbEmp.getEMPLOYEE_CATEGORY().isEmpty()) {
			categoryID = categories.get("Employment Category");
			assignGroupResponse = setGroup(dbEmp.getEMPLOYEE_CATEGORY(), responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Employment Category " + assignGroupResponse + ". ");
		}
		// Set EFFECTIVE_HIRE
		if (!dbEmp.getEFFECTIVE_HIRE().isEmpty()) {
			categoryID = categories.get("Effective Hire Date");
			assignGroupResponse = setGroup(dbEmp.getEFFECTIVE_HIRE(), responseEmp, mapper, categoryID, true);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Effective Hire Date " + assignGroupResponse + ". ");
		} 
		// Set SUPERVISOR. Note: City Commissioners dont have supervisor. LMS need to have a placeholder value like 'None' or 'No Supervisor'. 
		if (!dbEmp.getSUPERVISOR().isEmpty()) {
			categoryID = categories.get("Supervisor");
			assignGroupResponse = setGroup(dbEmp.getSUPERVISOR(), responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Supervisor " + assignGroupResponse + ". ");
		}
		// Set SUPERVISOR_RESP. YES = true, NO = false
		if (!dbEmp.getSUPERVISOR_RESP().isEmpty()) {
			categoryID = categories.get("Supervisor Responsibility");
			assignGroupResponse = setGroup(dbEmp.getSUPERVISOR_RESP().equals("NO") ? "false" : "true",
					responseEmp, mapper, categoryID, false);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Supervisor Responsibility " + assignGroupResponse + ". ");
		}
	}
	
	private String getGroupIDByCategoryID(Groups employeeGroups, String categoryID) {
		for (Group group : employeeGroups.getGroups()) {
			if(group.getCategoryid().equals(categoryID)) {
				return group.getGroupid();
			}
		}
		return null;
	}

	private String setGroup(String newGroupValue, Employee responseEmp, ObjectMapper mapper, String categoryID, boolean createIfMissing)
			throws IOException, JsonParseException, JsonMappingException {
		Response response;
		Groups responseGroups;
		
		// Get old groupID from LMS by categoryID. Assumption is only one group exist under a given category right?
		String oldGroupID = getGroupIDByCategoryID(getEmployeeGroups(responseEmp.getEmployeeid()), categoryID);
				
		// Get new groupID from newGroupValue
		String newGroupID = getGroupsByProfileCategory(categoryID).get(newGroupValue);
		
		if(oldGroupID.equals(newGroupID))
			return "updated";

		WebTarget site = getProfileCategoriesSite().path(categoryID).path("groups");

		// create new group if it doesn't exist
		if ((newGroupID == null || newGroupID.isEmpty()) && createIfMissing) {
			response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity("{\"groupname\":\"" + newGroupValue + "\"}", MediaType.APPLICATION_JSON));

			responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);
			// return as failure. If group is not updated here, following steps will fail anyway coz group doesn't exist
			if (responseGroups.getStatus() != null && !responseGroups.getStatus().equals("updated")) {
				log.error("failure - failed creating missing group " + newGroupValue + ", developermessage: " 
						+ responseGroups.getDevelopermessage());
				return "failure - failed creating missing group, developermessage: "
						+ responseGroups.getDevelopermessage();
			}
			log.debug("successfully created new group. " + newGroupValue + " under categoryID: " + categoryID);
		} else if((newGroupID == null || newGroupID.isEmpty()) && !createIfMissing) {
			log.error("failure - group " + newGroupValue + " doesn't exist, and I did not created it because createIfMissing is false");
			return "failure - group  " + newGroupValue + " doesn't exist, and I did not created it because createIfMissing is false"; 
		}
		
		//delete old group assignment

		String deleteResponse = deleteEmployeeGroup(responseEmp.getUserid(), mapper, oldGroupID);
		if (!deleteResponse.equals("accepted")) {
			log.error("failure - failed deleting old group. " + deleteResponse);
			return "failure - failed deleting old group. " + deleteResponse;
		}
		log.debug("successfully deleted old group...");
		
		//create new group assignment
		site = getProfileCategoriesSite().path(categoryID).path("groups").path(newGroupID).path("users");

		response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken())
				.post(Entity.entity("{\"userid\":\"" + responseEmp.getUserid() + "\"}", MediaType.APPLICATION_JSON));

		responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);
		if (!responseGroups.getStatus().equals("created")) {
			log.error("failed setting new group to user. API Response:: Status: " + responseGroups.getStatus() + ", Developer message: "
					+ responseGroups.getDevelopermessage());
			return "API Response:: Status: " + responseGroups.getStatus() + ", Developer message: "
					+ responseGroups.getDevelopermessage();
		} else {
			return "updated";
		}
	}

	private String deleteEmployeeGroup(String userID, ObjectMapper mapper, String groupID)
			throws IOException, JsonParseException, JsonMappingException {
		Response response;
		Groups responseGroups;
		
		WebTarget site = getUserGroupsSite(userID).path(groupID);
		
		response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken())
				.delete();
		
		responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);
		
		if(responseGroups != null && responseGroups.getStatus() != null && !responseGroups.getStatus().equals("accepted")) {
			return "API Response:: Status: " + responseGroups.getStatus() + ", Developer message: "
					+ responseGroups.getDevelopermessage() + ";";
		} else {
			return "accepted";
		}
	}
}
