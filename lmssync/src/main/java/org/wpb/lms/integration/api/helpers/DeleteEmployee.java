package org.wpb.lms.integration.api.helpers;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.HttpError;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeleteEmployee extends APIBase {
	private static final Logger log = LogManager.getLogger(DeleteEmployee.class);

	public String deleteEmployee(String empNo) {
		Response response = null;
		String responseMessage = null;

		try {
			// Get LMS EmployeeID for this employee number.
			Employee emp = new GetEmployee().getEmployeeByEmpNo(empNo);

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
}
