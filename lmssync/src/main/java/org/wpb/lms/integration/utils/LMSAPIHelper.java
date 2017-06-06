package org.wpb.lms.integration.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.Credentials;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.Groups;
import org.wpb.lms.entities.HttpError;
import org.wpb.lms.entities.ProfileCategories;
import org.wpb.lms.entities.ProfileCategory;
import org.wpb.lms.entities.Users;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LMSAPIHelper {
	private static final Logger log = LogManager.getLogger(LMSAPIHelper.class);

	/**
	 * Given a LMS userID, this method returns Employee including Links to
	 * related entities
	 * 
	 * @param userID
	 * @return {@link Employee}
	 * @throws IOException
	 */
	public Employee getEmployeeByUserID(String userID) throws IOException {
		Employee employee = new Employee();

		Response response = getUserSite(userID).request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken()).get();

		ObjectMapper mapper = new ObjectMapper();

		try {
			employee = mapper.readValue(response.readEntity(String.class), Employee.class);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}

		// log.debug(employee.toString());
		// System.out.println(employee);
		response.close();
		return employee;
	}

	/**
	 * Given a WPB Employee ID, this method returns Employee including Links to
	 * related entities
	 * 
	 * @param empNo
	 * @return {@link Employee}
	 * @throws IOException
	 */
	public Employee getEmployeeByEmpNo(String empNo) throws IOException {
		Users employee = new Users();

		Response response = getUsersSite().queryParam("employeeid", empNo)
				.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken()).get();

		ObjectMapper mapper = new ObjectMapper();

		try {
			employee = mapper.readValue(response.readEntity(String.class), Users.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// log.debug(employee.toString());
		// System.out.println(employee.getUsers().get(0));
		response.close();
		return employee != null ? (employee.getUsers() != null ? employee.getUsers().get(0) : null) : null;
	}

	/**
	 * This method returns Credentials of an employee identified by WPB Employee
	 * Number
	 * 
	 * @param empNo
	 * @return {@link Credentials} credentials
	 * @throws IOException
	 */
	public Credentials getEmployeeCredentials(String empNo) throws IOException {
		String userid = getEmployeeByEmpNo(empNo).getUserid();
		Credentials credentials = new Credentials();
		WebTarget userCredentialsSite = getUserCredentialsSite(userid);
		Response response = userCredentialsSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken()).get();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		try {
			credentials = mapper.readValue(response.readEntity(String.class), Credentials.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// log.debug(credentials.toString());
		// System.out.println(credentials.getCredentials().get(0));
		response.close();
		return credentials;

	}

	public Groups getEmployeeGroups(String empNo) throws IOException {
		String userid = getEmployeeByEmpNo(empNo).getUserid();
		Groups groups = new Groups();
		WebTarget userGroupsSite = getUserGroupsSite(userid);
		Response response = userGroupsSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken()).get();

		ObjectMapper mapper = new ObjectMapper();

		try {
			groups = mapper.readValue(response.readEntity(String.class), Groups.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// log.debug(groups.toString());
		// System.out.println(groups.getGroups().size() > 0 ?
		// groups.getGroups().get(0) : null);

		response.close();
		return groups;
	}

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
		String empString = "{\"firstname\": \"Joe\", \"password\":\"Welcome1\", \"employeeid\": 779988, \"username\": \"JFLUM\", \"lastname\": \"Flum\"}";
		try {
			WebTarget usersSite = getCreateUserSite();

			ObjectMapper mapper = new ObjectMapper();

			response = usersSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken())
					.post(Entity.entity(empString, MediaType.APPLICATION_JSON));
			responseCode = String.valueOf(response.getStatus());
			// responseEmp = mapper.readValue(response.readEntity(String.class),
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

	public void updateEmployee(Employee emp) {
		// TODO
	}

	public void updateEmployeeCredentials(String empNo, List<Credentials> credentials) {
		// TODO
	}

	public void updateEmployeeGroups(String empNo, List<Groups> groups) {
		// TODO
	}

	public String deleteEmployee(String empNo) {
		Response response = null;
		String responseMessage = null;

		try {
			// Get LMS EmployeeID for this employee number.
			Employee emp = getEmployeeByEmpNo(empNo);

			//If employee status was already set to Inactive, return success message
			if(!emp.getStatus().equals("Inactive")) {
				log.debug("Employee with ID: " + empNo + " was already deleted in the system");
				return "Employee with ID: " + empNo + " was already deleted in the system";
			}
			
			if (emp != null && emp.getHttpcode() == null && !emp.getUserid().isEmpty()) {
				WebTarget usersSite = getUserSite(emp.getUserid());

				// Create Employee object with status as "Inactive"
				emp = new Employee();
				emp.setStatus("Inactive");

				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_EMPTY);
				response = usersSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
						.header("AccessToken", PropertiesUtils.getAccessToken())
						.put(Entity.entity(mapper.writeValueAsString(emp), MediaType.APPLICATION_JSON));
				if (response.getStatus() == 202) {
					responseMessage = "Employee with ID: " + empNo + " deleted successfully";
				} else {
					HttpError error = mapper.readValue(response.readEntity(String.class), HttpError.class);
					responseMessage = "Employee with ID: " + empNo + " was not deleted. LMS error message is: "
							+ error.getStatus() + ", " + error.getDevelopermessage();
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			responseMessage = "Employee with ID: " + empNo
					+ " was not deleted due to technical errors. Please verify the record in DB..";
		}
		return responseMessage;
	}

	/**
	 * This method returns all additional attributes (columns) to employee like
	 * Department, Division, Title etc... TargetSolutions calls these additional
	 * attributes as categories GET
	 * http://api.targetsolutions.com/v1/sites/28658/categories/profile
	 * 
	 * @return
	 */
	public Map<String, String> getProfileCategories() {
		Response response = null;
		ProfileCategories profileCategories = null;
		HashMap<String, String> categories = new HashMap<String, String>();
		try {
			WebTarget profileCategoriesSite = getMainSite().path("sites").path(PropertiesUtils.getSiteID())
					.path("categories").path("profile");
			response = profileCategoriesSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken()).get();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			profileCategories = mapper.readValue(response.readEntity(String.class), ProfileCategories.class);

			for (ProfileCategory category : profileCategories.getProfilecategories()) {
				categories.put(category.getCategoryname(), String.valueOf(category.getCategoryid()));
			}
			response.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return categories;
	}

	/**
	 * This method returns Users site to perform further operations using Users
	 * site Example: http://devsandbox.targetsolutions.com/v1
	 * 
	 * @param userid
	 * @return {@link WebTarget} mainSiteURL
	 * @throws IOException
	 */

	public WebTarget getMainSite() throws IOException {
		return ClientBuilder.newClient().target(PropertiesUtils.getSiteURI());
	}

	/**
	 * This method returns Users site to perform further operations using Users
	 * site Example: http://devsandbox.targetsolutions.com/v1/users
	 * 
	 * @param userid
	 * @return {@link WebTarget} usersSite
	 * @throws IOException
	 */
	public WebTarget getUsersSite() throws IOException {
		return getMainSite().path("users");
	}

	/**
	 * This method returns User site for given userid to perform further
	 * operations using Users site Example:
	 * http://devsandbox.targetsolutions.com/v1/users/1339145/
	 * 
	 * @param userid
	 * @return {@link WebTarget} userSite
	 * @throws IOException
	 */
	public WebTarget getUserSite(String userid) throws IOException {
		return getUsersSite().path(userid);
	}

	/**
	 * This method returns direct credentials URL without having to go through
	 * Users site Example: http://devsandbox.targetsolutions.com/v1/credentials
	 * 
	 * @return {@link WebTarget} credentialsSite
	 * @throws IOException
	 */
	public WebTarget getCredentialsSite() throws IOException {
		return getMainSite().path("credentials");
	}

	/**
	 * This method returns User Credentials URL for a given LMS userID
	 * 
	 * Example:
	 * http://devsandbox.targetsolutions.com/v1/users/1265568/credentials
	 * 
	 * @param userid
	 * @return
	 * @throws IOException
	 */
	public WebTarget getUserCredentialsSite(String userid) throws IOException {
		return getUserSite(userid).path("credentials");
	}

	/**
	 * This method returns direct credentials URL without having to go through
	 * Users site using credentialid Example:
	 * http://devsandbox.targetsolutions.com/v1/credentials/162095
	 * 
	 * @param userid
	 * @param credentialid
	 * @return {@link WebTarget} credentialSite
	 * @throws IOException
	 */
	public WebTarget getCredentialSite(String credentialid) throws IOException {
		return getCredentialsSite().path(credentialid);
	}

	/**
	 * This method returns User Groups URL for a given LMS userID
	 * 
	 * @param userid
	 * @return {@link WebTarget} groupsSite
	 * @throws IOException
	 */
	public WebTarget getUserGroupsSite(String userid) throws IOException {
		return getUserSite(userid).path("groups");
	}

	public WebTarget getCreateUserSite() throws IOException {
		return getMainSite().path("sites").path(PropertiesUtils.getSiteID()).path("users");
	}
}
