package org.wpb.lms.integration.utils;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.Credentials;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.Groups;

public class UpdateEmployee extends APIBase {
	private static final Logger log = LogManager.getLogger(UpdateEmployee.class);

	public void updateEmployee(Employee emp) {
		// TODO
	}

	public void updateEmployeeCredentials(String empNo, List<Credentials> credentials) {
		// TODO
	}

	public void updateEmployeeGroups(String empNo, List<Groups> groups) {
		// TODO
	}
}
