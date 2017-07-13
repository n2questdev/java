package org.wpb.lms.integration.api.helpers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.DBEmployee;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.Groups;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateEmployee extends APIBase {
	private static final Logger log = LogManager.getLogger(CreateEmployee.class);

	/**
	 * Following method creates a new employee in the collection Site URL: 
	 * 1. POST employee info to http://devsandbox.targetsolutions.com/v1/sites/28658/users 
	 * 2. Get userid from the response of above 
	 * 3. POST employee email to http://devsandbox.targetsolutions.com/v1/users/{userid}/emails
	 * 4. Get Profile categories (columns) map 
	 * 5. POST groups to http://devsandbox.targetsolutions.com/v1/sites/28658/categories/profile/{categoryid}/groups/{groupID}/users
	 * 
	 * Note: Credentials are ignored for the moment. WPB data export doesn't have credentials
	 * @param DBEmployee
	 * @return "created" or "full failure or partial failure messages"
	 */
	public String createEmployee(DBEmployee dbEmp) {
		StringBuilder errorMessages = new StringBuilder();
		Response response = null;
		Employee responseEmp = new Employee();

		Employee emp = new Employee();
		emp.setFirstname(dbEmp.getFIRST_NAME_MI());
		emp.setLastname(dbEmp.getLAST_NAME());
		emp.setUsername(dbEmp.getUSERNAME());
		emp.setEmployeeid(dbEmp.getEMPLOYEE_ID());
		emp.setPassword(dbEmp.getTEMP_PASSWORD());
		
		try {
			WebTarget usersSite = getCreateUserSite();

			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_EMPTY);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

			response = usersSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity(mapper.writeValueAsString(emp), MediaType.APPLICATION_JSON));

			responseEmp = mapper.readValue(response.readEntity(String.class), Employee.class);

			if (responseEmp != null && (responseEmp.getUserid() == null || responseEmp.getUserid().isEmpty())) {
				return "Unable to create employee! API Response:: Status: " + responseEmp.getStatus()
						+ ", Developer Message: " + responseEmp.getDevelopermessage();
			}

			log.debug("employee: " + emp.getUsername() + " successfully created. Now assigning groups...");

			setEmployeeGroups(dbEmp, errorMessages, responseEmp.getUserid(), mapper);

			log.debug("employee created and group assignments completed. Now assigning emails...");

			response = setEmployeeEmail(dbEmp, errorMessages, responseEmp, mapper);
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(response != null)
				response.close();
		}

		if (errorMessages.length() > 0) { // there are few group assignment errors
			return "employee creation partially successful. Errors - " + errorMessages;
		} else {
			return "created";
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

		if ((responseEmp == null || responseEmp.getStatus() == null) && !responseEmp.getStatus().equals("created")) {
			errorMessages.append("Unable to set email address! API Response:: Status: " + responseEmp.getStatus()
					+ ", Developer message: " + responseEmp.getDevelopermessage() + ".");
		}
		return response;
	}

	private void setEmployeeGroups(DBEmployee dbEmp, StringBuilder errorMessages, String userID,
			ObjectMapper mapper) throws IOException, JsonParseException, JsonMappingException {
		final Map<String, String> categories = getProfileCategories();
		String categoryID;
		String assignGroupResponse;
		// Set Department
		if (!dbEmp.getDEPT().isEmpty()) {
			categoryID = categories.get("Department");
			assignGroupResponse = setGroup(dbEmp.getDEPT(), userID, mapper, categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Department " + assignGroupResponse + ". ");
		}
		// Set DIVISION
		if (!dbEmp.getDIVISION().isEmpty()) {
			categoryID = categories.get("Division");
			assignGroupResponse = setGroup(dbEmp.getDIVISION(), userID, mapper, categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Division " + assignGroupResponse + ". ");
		}
		// Set JOB_TITLE
		if (!dbEmp.getJOB_TITLE().isEmpty()) {
			categoryID = categories.get("Job Title");
			assignGroupResponse = setGroup(dbEmp.getJOB_TITLE(), userID, mapper, categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Job Title " + assignGroupResponse + ". ");
		}

		// Set MANAGEMENT
		if (!dbEmp.getMANAGEMENT().isEmpty()) {
			categoryID = categories.get("Management");
			assignGroupResponse = setGroup(dbEmp.getMANAGEMENT(), userID, mapper, categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Management " + assignGroupResponse + ". ");
		}
		// Set EMPLOYEE_GROUP
		if (!dbEmp.getEMPLOYEE_GROUP().isEmpty()) {
			categoryID = categories.get("Employee Group");
			assignGroupResponse = setGroup(dbEmp.getEMPLOYEE_GROUP(), userID, mapper,
					categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Employee Group " + assignGroupResponse + ". ");
		}
		// Set EMPLOYEE_CATEGORY
		if (!dbEmp.getEMPLOYEE_CATEGORY().isEmpty()) {
			categoryID = categories.get("Employment Category");
			assignGroupResponse = setGroup(dbEmp.getEMPLOYEE_CATEGORY(), userID, mapper,
					categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Employment Category " + assignGroupResponse + ". ");
		}
		// Set EFFECTIVE_HIRE
		if (!dbEmp.getEFFECTIVE_HIRE().isEmpty()) {
			categoryID = categories.get("Effective Hire Date");

//			assignGroupResponse = setGroup(new SimpleDateFormat(PropertiesUtils.getDateFormat()).format(dbEmp.getEFFECTIVE_HIRE()), userID, mapper,
//					categoryID, true);
			assignGroupResponse = setGroup(dbEmp.getEFFECTIVE_HIRE(), userID, mapper, categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Effective Hire Date " + assignGroupResponse + ". ");
		} 
		// Set SUPERVISOR
		if (!dbEmp.getSUPERVISOR().isEmpty()) {
			categoryID = categories.get("Supervisor");
			assignGroupResponse = setGroup(dbEmp.getSUPERVISOR(), userID, mapper, categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Supervisor " + assignGroupResponse + ". ");
		}
		// Set SUPERVISOR_RESP. YES = true, NO = false
		if (!dbEmp.getSUPERVISOR_RESP().isEmpty()) {
			categoryID = categories.get("Supervisor Responsibility");
			assignGroupResponse = setGroup(dbEmp.getSUPERVISOR_RESP(), userID, mapper, categoryID, true);
			if (!assignGroupResponse.equals("created"))
				errorMessages.append("Unable to set Supervisor Responsibility " + assignGroupResponse + ". ");
		}
	}

	private String setGroup(String groupValue, String userID, ObjectMapper mapper, String categoryID)
			throws IOException, JsonParseException, JsonMappingException {
		Response response;
		Groups responseGroups;
		String groupID = getGroupIDByName(categoryID, groupValue).get(groupValue);
		
		if (groupID != null && !groupID.isEmpty()) {
			WebTarget site = getProfileCategoriesSite().path(categoryID).path("groups").path(groupID).path("users");

			response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity("{\"userid\":\"" + userID + "\"}", MediaType.APPLICATION_JSON));

			responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);

			if (responseGroups.getStatus() != null && !responseGroups.getStatus().equals("updated")
					&& !responseGroups.getStatus().equals("created")
					&& !responseGroups.getStatus().contains("conflict")) {
				return "API Response:: Status: " + responseGroups.getStatus() + ", Developer message: "
						+ responseGroups.getDevelopermessage() + ";";
			} else {
				return "created";
			}
		} else {
			return "failure - null group";
		}
	}

	private String setGroup(String groupValue, String userID, ObjectMapper mapper, String categoryID,
			boolean createIfMissing) throws IOException, JsonParseException, JsonMappingException {
		Response response;
		Groups responseGroups;
		// Get groupID if it exists
		String groupID;
		
		//TODO: Bad Bad Bad... LMS is storing YES and NO as true and false, and they return true and false when queried. 
		if(categoryID.equals("25128")) {
			groupID = getGroupIDByName(categoryID, groupValue).get(groupValue.equalsIgnoreCase("YES") ? "true" : "false");
		} else {
			groupID = getGroupIDByName(categoryID, groupValue).get(groupValue);
		}

		WebTarget site = getProfileCategoriesSite().path(categoryID).path("groups");
		
		// verify whether you can create it if it doesn't exist
		if ((groupID == null || groupID.isEmpty()) && createIfMissing) {
			response = site.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity("{\"groupname\":\"" + groupValue + "\"}", MediaType.APPLICATION_JSON));

			responseGroups = mapper.readValue(response.readEntity(String.class), Groups.class);
			// return as failure. If group is not created here, following steps
			// will fail anyway coz group doesn't exist
			if (responseGroups.getStatus() != null && !responseGroups.getStatus().equals("updated") && !responseGroups.getStatus().equals("created")
					&& !responseGroups.getStatus().contains("conflict")) {
				log.error("failure - failed creating missing group " + groupValue + ", request status: " + responseGroups.getStatus() + ", developermessage: " 
						+ responseGroups.getDevelopermessage());
				return "failure - failed creating missing group " + groupValue + ", request status: " + responseGroups.getStatus() + ", developermessage: " 
						+ responseGroups.getDevelopermessage();
			}
			log.debug("successfully created new group. " + groupValue + " under categoryID: " + categoryID);
		} else if ((groupID == null || groupID.isEmpty()) && !createIfMissing) {
			return "failure - group doesn't exist, and I did not created it because createIfMissing is false";
		}

		// API doesn't include newGroupID in the response, so calling setGroup with groupValue instead of groupID again. 
		
		return setGroup(groupValue, userID, mapper, categoryID);
	}
}
