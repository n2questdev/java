package org.wpb.integration.tssync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.integration.tssync.utils.PropertiesUtils;
import org.wpb.targetsolutions.entities.Credentials;
import org.wpb.targetsolutions.entities.Employee;
import org.wpb.targetsolutions.entities.Groups;
import org.wpb.targetsolutions.entities.Users;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TargetSolutionsRestClient {
	private static final Logger log = LogManager.getLogger(TargetSolutionsRestClient.class);

	// Logger.getLogger(TargetSolutionsRestClient.class.getName());

	public static void main(String[] args) {
		try {
			new TargetSolutionsRestClient().getEmployeeByEmpNo("6950");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given a Target Solutions userID, this method returns Employee including Links to related entities
	 * @param userID
	 * @return {@link Employee}
	 * @throws IOException
	 */
	public Employee getEmployeeByUserID(String userID) throws IOException {
		Employee employee = new Employee();

		Response response = getUser(userID).request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
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
	 * Given a WPB Employee ID, this method returns Employee including Links to related entities
	 * @param empNo
	 * @return {@link Employee}
	 * @throws IOException
	 */
	public Employee getEmployeeByEmpNo(String empNo) throws IOException {
		Users employee = new Users();

		Response response = getUsersSite().queryParam("employeeid", empNo).request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
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

	public Credentials getEmployeeCredentials(String empNo) throws IOException {
		return new Credentials();
		
	}
	
	public Groups getEmployeeGroups(String empNo) throws IOException {
		return new Groups();
	}
	
	public void updateEmployee(Employee emp) {
		//TODO
	}
	
	public void updateEmployeeCredentials(String empNo, List<Credentials> credentials) {
		//TODO
	}

	public void updateEmployeeGroups(String empNo, List<Groups> groups) {
		//TODO
	}
	
	public void createEmployee(Employee emp, List<Credentials> credentials, List<Groups> groups) {
		//TODO
	}
	
	public void deleteEmployee(String empNo) {
		//TODO
	}
	
	public WebTarget getMainSite() throws IOException {
		return ClientBuilder.newClient().target(PropertiesUtils.getSiteURI());
	}

	public WebTarget getUsersSite() throws IOException {
		return getMainSite().path("users");
	}

	public WebTarget getUser(String userid) throws IOException {
		return getUsersSite().path(userid);
	}

	public WebTarget getCredentialsSite() throws IOException {
		return getMainSite().path("credentials");
	}

	/**
	 * This method returns Credentials URL using credentialid directly
	 * @param userid
	 * @param credentialid
	 * @return {@link WebTarget}
	 * @throws IOException
	 */
	public WebTarget getCredentials(String credentialid) throws IOException {
		return getCredentialsSite().path(credentialid);
	}

	public WebTarget getUserGroups(String userid) throws IOException {
		return getUser(userid).path("groups");
	}
	
	public WebTarget getUserCredentials(String userid) throws IOException {
		return getUser(userid).path("credentials");
	}
}
