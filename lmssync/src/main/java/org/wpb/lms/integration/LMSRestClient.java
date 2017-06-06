package org.wpb.lms.integration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.Credential;
import org.wpb.lms.entities.Credentials;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.entities.Group;
import org.wpb.lms.entities.Groups;
import org.wpb.lms.integration.utils.LMSAPIHelper;

/**
 * This is the REST Client with several utilities to interact with Target
 * Solutions REST API. Wherever you see userid in the method parameters, it is
 * always Target Solution's assigned userID but not WPB employeeID
 * 
 * @author Abhilash Juluri
 *
 */
public class LMSRestClient {
	private static final Logger log = LogManager.getLogger(LMSRestClient.class);

	// Logger.getLogger(TargetSolutionsRestClient.class.getName());

	public static void main(String[] args) {
		try {
			//Try delete (marking user as inactive)
//			Employee emp = new Employee();
//			
//			emp.setEmployeeid("6950");
//			
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.setSerializationInclusion(Include.NON_EMPTY);
//			System.out.println(mapper.writeValueAsString(emp));
			
			System.out.println("Getting Credentials of employee with ID: 6950");
			LMSAPIHelper lmsAPIHelper = new LMSAPIHelper();
			
			String deleteResult = lmsAPIHelper.deleteEmployee("7926");
			System.out.println(deleteResult);
			
			Credentials credentials = lmsAPIHelper.getEmployeeCredentials("6950");
			List<Credential> creds = credentials.getCredentials();
			for (Credential credential : creds) {
				log.debug(credential);
				System.out.println(credential);
			}
			
			System.out.println("Getting groups of employee with ID: 6950");
			Groups groups = lmsAPIHelper.getEmployeeGroups("6950");
			List<Group> grps = groups.getGroups();
			for (Group group : grps) {
//				log.debug(group);
				System.out.println(group);
			}
			
			System.out.println("Getting Profile Categories");
			Map<String, String> categories = lmsAPIHelper.getProfileCategories();
			for (String category : categories.keySet()) {
				System.out.println("Category Name: " + category + ", ID: " + categories.get(category));
			}
			
			System.out.println("Creating a new Employee without Credentials and Groups");
			Employee emp = new Employee();
			emp.setFirstname("Joe");
			emp.setLastname("Flom");
			emp.setUsername("JFLOM2");
			emp.setEmployeeid("998866");
			emp.setPassword("Welcome1");
			String status = lmsAPIHelper.createEmployee(emp, new Credentials(), new Groups());
		
			System.out.println(status);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
