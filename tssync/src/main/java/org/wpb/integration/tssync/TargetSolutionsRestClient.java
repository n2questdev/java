package org.wpb.integration.tssync;

import java.io.IOException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.integration.tssync.utils.PropertiesUtils;
import org.wpb.targetsolutions.entities.Employee;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TargetSolutionsRestClient {
	private static final Logger log = LogManager.getLogger(TargetSolutionsRestClient.class);
			
//			Logger.getLogger(TargetSolutionsRestClient.class.getName());

	public static void main(String[] args) {
		try {
			new TargetSolutionsRestClient().getEmployee("1339145");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Employee getEmployee(String userID) throws IOException {
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

	public WebTarget getMainSite() throws IOException {
		return ClientBuilder.newClient().target(PropertiesUtils.getSiteURI());
	}
	
	public WebTarget getUsersSite() throws IOException {
		return getMainSite().path("users");
	}
	
	public WebTarget getUser(String userid) throws IOException {
		return getUsersSite().path(userid);
	}

}
