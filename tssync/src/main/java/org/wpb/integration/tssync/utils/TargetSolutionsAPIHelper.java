package org.wpb.integration.tssync.utils;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.targetsolutions.entities.Credentials;
import org.wpb.targetsolutions.entities.Employee;
import org.wpb.targetsolutions.entities.Groups;
import org.wpb.targetsolutions.entities.Users;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TargetSolutionsAPIHelper {
	private static final Logger log = LogManager.getLogger(TargetSolutionsAPIHelper.class);
	
	/**
	 * Given a Target Solutions userID, this method returns Employee including
	 * Links to related entities
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
			e.printStackTrace();
		}

		log.debug(employee.toString());
		System.out.println(employee);
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

		log.debug(employee.toString());
		System.out.println(employee.getUsers().get(0));
		return employee.getUsers().get(0);
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

		try {
			credentials = mapper.readValue(response.readEntity(String.class), Credentials.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.debug(credentials.toString());
		System.out.println(credentials.getCredentials().get(0));

		return credentials;

	}

	public Groups getEmployeeGroups(String empNo) throws IOException {
		return new Groups();
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

	public void createEmployee(Employee emp, List<Credentials> credentials, List<Groups> groups) {
		// TODO
	}

	public void deleteEmployee(String empNo) {
		// TODO
	}

	/**
	 * This method returns Users site to perform further operations using Users
	 * site Example: http://devsandbox.targetsolutions.com/v1/users/1339145/
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
	 * This method returns User Credentials URL for a given Target Solutions
	 * userID
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
	 * This method returns User Groups URL for a given Target Solutions userID
	 * 
	 * @param userid
	 * @return {@link WebTarget} groupsSite
	 * @throws IOException
	 */
	public WebTarget getUserGroupsSite(String userid) throws IOException {
		return getUserSite(userid).path("groups");
	}
}
