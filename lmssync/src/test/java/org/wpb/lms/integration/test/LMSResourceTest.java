package org.wpb.lms.integration.test;

import java.io.IOException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wpb.lms.entities.Employee;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LMSResourceTest {
	String accessToken = "y32O919rSTVgDxlj3Ne/lvQM0/wtfSm06u4G1r9d1oK85rvElX1NkMIPCFTlRmw9";

	public void test() {

		WebTarget target = getSite().path("users").path("1339145");

		// path("users").path("1339145")
		// Form form = new Form();
		// form.param("x", "foo");
		// form.param("y", "bar");

		// target.
		Response response = target.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", accessToken).get();

		System.out.println(response.getStatus());
		System.out.println(response.readEntity(String.class));
	}

	public static void main(String[] args) {
		new LMSResourceTest().getEmployee("1339145");
	}

	public Employee getEmployee(String userID) {
		Employee employee = new Employee();

		WebTarget target = getSite().path("users").path(userID);
		Response response = target.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", accessToken).get();

		ObjectMapper mapper = new ObjectMapper();

		try {
			employee = mapper.readValue(response.readEntity(String.class), Employee.class);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		}

		System.out.println(employee);
		return employee;
	}

	public WebTarget getSite() {
		return ClientBuilder.newClient().target("http://devsandbox.targetsolutions.com/v1/");
	}

}
