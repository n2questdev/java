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
			response.close();
		}

		if (errorMessages.length() > 0) { // there are few group assignment errors
			return "Employee creation partially successful. Errors - " + errorMessages;
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
			assignGroupResponse = setGroup(dbEmp.getDEPT(), responseEmp, mapper, categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Department " + assignGroupResponse + ". ");
		}
		// Set DIVISION
		if (!dbEmp.getDIVISION().isEmpty()) {
			categoryID = categories.get("Division");
			assignGroupResponse = setGroup(dbEmp.getDIVISION(), responseEmp, mapper, categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Division " + assignGroupResponse + ". ");
		}
		// Set JOB_TITLE
		if (!dbEmp.getJOB_TITLE().isEmpty()) {
			categoryID = categories.get("Job Title");
			assignGroupResponse = setGroup(dbEmp.getJOB_TITLE(), responseEmp, mapper, categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Job Title " + assignGroupResponse + ". ");
		}

		// Set MANAGEMENT
		if (!dbEmp.getMANAGEMENT().isEmpty()) {
			categoryID = categories.get("Management");
			assignGroupResponse = setGroup(dbEmp.getMANAGEMENT(), responseEmp, mapper, categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Management " + assignGroupResponse + ". ");
		}
		// Set EMPLOYEE_GROUP
		if (!dbEmp.getEMPLOYEE_GROUP().isEmpty()) {
			categoryID = categories.get("Employee Group");
			assignGroupResponse = setGroup(dbEmp.getEMPLOYEE_GROUP(), responseEmp, mapper,
					categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Employee Group " + assignGroupResponse + ". ");
		}
		// Set EMPLOYEE_CATEGORY
		if (!dbEmp.getEMPLOYEE_CATEGORY().isEmpty()) {
			categoryID = categories.get("Employment Category");
			assignGroupResponse = setGroup(dbEmp.getEMPLOYEE_CATEGORY(), responseEmp, mapper,
					categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Employment Category " + assignGroupResponse + ". ");
		}
		// Set EFFECTIVE_HIRE
		if (!dbEmp.getEFFECTIVE_HIRE().isEmpty()) {
			categoryID = categories.get("Effective Hire Date");
			assignGroupResponse = setGroup(dbEmp.getEFFECTIVE_HIRE(), responseEmp, mapper,
					categoryID, true);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Effective Hire Date " + assignGroupResponse + ". ");
		} 
		// Set SUPERVISOR
		if (!dbEmp.getSUPERVISOR().isEmpty()) {
			categoryID = categories.get("Supervisor");
			assignGroupResponse = setGroup(dbEmp.getSUPERVISOR(), responseEmp, mapper, categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Supervisor " + assignGroupResponse + ". ");
		}
		// Set SUPERVISOR_RESP. YES = true, NO = false
		if (!dbEmp.getSUPERVISOR_RESP().isEmpty()) {
			categoryID = categories.get("Supervisor Responsibility");
			assignGroupResponse = setGroup(dbEmp.getSUPERVISOR_RESP().equals("NO") ? "false" : "true",
					responseEmp, mapper, categoryID);
			if (!assignGroupResponse.equals("updated"))
				errorMessages.append("Unable to set Supervisor Responsibility " + assignGroupResponse + ". ");
		}
	}
	
	private boolean isGroupChanged(Groups employeeGroups, String groupValue, String categoryID) {
		for (Group group : employeeGroups.getGroups()) {
			if(group.getCategoryid().equals(categoryID)) {
				return group.getGroupname().equals(groupValue);
			}
		}
		
		return false;
	}
	
	private String getGroupIDByGroupName(Groups employeeGroups, String groupValue) {
		for (Group group : employeeGroups.getGroups()) {
			if(group.getGroupname().equals(groupValue)) {
				return group.getGroupid();
			}
		}
		return null;
	}

	private String setGroup(String newGroupValue, Employee responseEmp, ObjectMapper mapper, String categoryID)
			throws IOException, JsonParseException, JsonMappingException {
		Response response;
		Groups responseGroups;

		// Get employee groups
		Groups employeeGroups = getEmployeeGroups(responseEmp.getEmployeeid());

		//verify if its changed from previous group
		// if(isGroupChanged(employeeGroups, groupValue, categoryID)) { 
		String newGroupID = getGroupIDByGroupName(employeeGroups, newGroupValue);
		String oldGroupID = getGroupIDByGroupName(employeeGroups, newGroupValue);
				
		if(newGroupID != null) {
			// delete old group
			String deleteResponse = deleteEmployeeGroup(responseEmp.getUserid(), mapper, newGroupID);
			if (!deleteResponse.equals("accepted")) {
				return "failure - failed deleting old group. " + deleteResponse;
			}
		}

		// set new group
		if (getGroupsByProfileCategory(categoryID).get(newGroupValue) != null
				&& !getGroupsByProfileCategory(categoryID).get(newGroupValue).isEmpty()) {
			WebTarget site = getProfileCategoriesSite().path(categoryID).path("groups")
					// group from given categoryID
					.path(getGroupsByProfileCategory(categoryID).get(newGroupValue)).path("users");

			response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity("{\"userid\":\"" + responseEmp.getUserid() + "\"}", MediaType.APPLICATION_JSON));

			responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);
			if (!responseGroups.getStatus().equals("updated")) {
				return "API Response:: Status: " + responseGroups.getStatus() + ", Developer message: "
						+ responseGroups.getDevelopermessage() + ";";
			} else {
				return "updated";
			}
		} else {
			return "failure - null group";
		}
		// } else { //group didn't changed, just return as updated
		// return "updated";
		// }
	}

	private String setGroup(String newGroupValue, Employee responseEmp, ObjectMapper mapper, String categoryID,
			boolean createIfMissing) throws IOException, JsonParseException, JsonMappingException {
		Response response;
		Groups responseGroups;

		Groups employeeGroups = getEmployeeGroups(responseEmp.getEmployeeid());
		// if(isGroupChanged(employeeGroups, groupValue, categoryID)) { //verify
		// if its changed from previous group

		String lmsGroupID = getGroupIDByGroupName(employeeGroups, newGroupValue);

		if (lmsGroupID != null) {
			// delete old group
			String deleteResponse = deleteEmployeeGroup(responseEmp.getUserid(), mapper, lmsGroupID);
			if (!deleteResponse.equals("accepted")) {
				return "failure - failed deleting old group. " + deleteResponse;
			}
		}

		// set new group

		// Get groupID if it exists
		String groupID = getGroupsByProfileCategory(categoryID).get(newGroupValue);

		WebTarget site = getProfileCategoriesSite().path(categoryID).path("groups");

		// create group if it doesn't exist
		if ((groupID == null || groupID.isEmpty()) && createIfMissing) {
			response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity("{\"groupname\":\"" + newGroupValue + "\"}", MediaType.APPLICATION_JSON));

			responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);
			// return as failure. If group is not updated here, following steps
			// will fail anyway coz group doesn't exist
			if (responseGroups.getStatus() != null && !responseGroups.getStatus().equals("updated")) {
				return "failure - failed creating missing group, developermessage: "
						+ responseGroups.getDevelopermessage();
			}
		} else if ((groupID == null || groupID.isEmpty()) && !createIfMissing) {
			return "failure - group doesn't exist, and I did not updated it because createIfMissing is false";
		}

		// In the previous step, API doesn't return new groupID in the response.
		// So get groups from given categoryID again.
		site = site.path(getGroupsByProfileCategory(categoryID).get(newGroupValue)).path("users");

		response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken())
				.post(Entity.entity("{\"userid\":\"" + responseEmp.getUserid() + "\"}", MediaType.APPLICATION_JSON));

		responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);
		if (!responseGroups.getStatus().equals("updated")) {
			return "API Response:: Status: " + responseGroups.getStatus() + ", Developer message: "
					+ responseGroups.getDevelopermessage() + ";";
		} else {
			return "updated";
		}

	//} else { //group didn't changed, just return as updated
	// return "updated";
	// }
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
