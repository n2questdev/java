package org.wpb.lms.integration.utils;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.Credentials;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.Groups;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateEmployee extends APIBase {
	private static final Logger log = LogManager.getLogger(CreateEmployee.class);

	/**
	 * Following method creates a new employee in the collection Site URL: 1.
	 * POST to http://devsandbox.targetsolutions.com/v1/sites/28658/users 2. Get
	 * userid from the response of above 3. Create emails by POSTing to
	 * http://devsandbox.targetsolutions.com/v1/sites/28658/users/{userid}/emails/
	 * 4. Get Profile categories (columns) map 5. Link user to groups by POSTing
	 * to
	 * http://devsandbox.targetsolutions.com/v1/sites/28658/categories/profile/{categoryid}/groups/{groupID}/users
	 * 
	 * @param emp
	 * @param credentials
	 * @param groups
	 * @return "success" or "failure"
	 */
	public String createEmployee(Employee emp, Credentials credentials, Groups groups) {
		String responseCode = "400";
		Response response = null;
		Employee responseEmp = new Employee();
//		String empString = "{\"firstname\": \"Joe\", \"password\":\"Welcome1\", \"employeeid\": 779988, \"username\": \"JFLUM\", \"lastname\": \"Flum\"}";
		try {
			WebTarget usersSite = getCreateUserSite();

			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_EMPTY);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

			response = usersSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity(mapper.writeValueAsString(emp), MediaType.APPLICATION_JSON));
			responseCode = String.valueOf(response.getStatus());
			responseEmp = mapper.readValue(response.readEntity(String.class), Employee.class);
			// Employee.class);
			System.out.println(responseCode);
			System.out.println(response.readEntity(String.class));
			// System.out.println(responseEmp.getUserid());

			// POST Credentials

			// Link User to Group(s)
			// URL/sites/28658/categories/profile/{categoryid}/groups/{groupID}/users

			// Close all connections
			response.close();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response.getStatus() == 200 && !responseEmp.getUserid().isEmpty()) {
			return "Employee: " + responseEmp.getFirstname() + ", " + responseEmp.getLastname()
					+ " created successfully";
		}
		return responseCode;
	}
}
