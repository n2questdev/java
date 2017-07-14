package org.wpb.lms.integration.api.helpers;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.Users;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetEmployee extends APIBase {
	private static final Logger log = LogManager.getLogger(GetEmployee.class);
	
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
			log.fatal(e.getMessage(), e);
		} finally {
			if(response != null)
				response.close();
		}

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
			log.fatal(e.getMessage(), e);
		} finally {
			if(response != null)
				response.close();
		}

		log.debug("getEmployeeByEmpNo :: details for employee: " + empNo + " are: " + employee.toString());
		// System.out.println(employee.getUsers().get(0));
		return employee != null ? (employee.getUsers() != null ? employee.getUsers().get(0) : null) : null;
	}
}
