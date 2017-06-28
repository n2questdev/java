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
			// Get LMS Employee for this employee number.
			Employee emp = new GetEmployee().getEmployeeByEmpNo(empNo);
			
			if(emp == null) {
				log.error("Employee not found in LMS! shouldn't you create first?");
				return "Employee not found in LMS! shouldn't you create first?";
			}


			//If employee status was already set to Inactive or Offline, return success message
			if(emp.getStatus().equals("Inactive") || emp.getStatus().equals("Offline")) {
				log.debug("Employee with ID: " + empNo + " was already marked as '" + emp.getStatus() + "' in LMS");
				return "deleted";
			}
			
			if (emp != null && emp.getHttpcode() == null && !emp.getUserid().isEmpty()) {
				WebTarget usersSite = getUserSite(emp.getUserid());

				// Create query object with status as "Inactive"
				emp = new Employee();
				emp.setStatus("Inactive");

				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_EMPTY);
				response = usersSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
						.header("AccessToken", PropertiesUtils.getAccessToken())
						.put(Entity.entity(mapper.writeValueAsString(emp), MediaType.APPLICATION_JSON));
				if (response.getStatus() == 202) {
					log.debug("Employee with ID: " + empNo + " has been marked as Inactive");
					responseMessage = "deleted";
				} else {
					HttpError error = mapper.readValue(response.readEntity(String.class), HttpError.class);
					log.debug("Employee with ID: " + empNo + " was not Inactivated. LMS error message is: "
							+ error.getStatus() + ", " + error.getDevelopermessage());
					responseMessage = "Employee with ID: " + empNo + " was not Inactivated. LMS error message is: "
							+ error.getStatus() + ", " + error.getDevelopermessage();
				}
			}
		} catch (IOException e) {
			log.error("Employee with ID: " + empNo
					+ " was not Inactivated due to technical errors. Please verify the record in DB or retry running the job. Exception message: " + e.getMessage(), e);
			responseMessage = "Employee with ID: " + empNo
					+ " was not Inactivated due to technical errors. Please verify the record in DB or retry running the job...";
		}
		return responseMessage;
	}
}
