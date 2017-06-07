package org.wpb.lms.integration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.wpb.lms.entities.Email;
import org.wpb.lms.entities.Employee;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JobTriggerMain {

	public static void main(String[] args) {
		
		//create new Employee
		Employee obj = new Employee();
		List<Email> emails = new ArrayList<Email>();
		
		emails.add(new Email("jdoe@gmail.com", "Active"));
		emails.add(new Email("jdoe@yahoo.com", "Active"));
		obj.setFirstname("John");
		obj.setLastname("Doe");
		obj.setEmployeeid("1234");
		obj.setUsertype("Admin");
		obj.setUserid("jdoe");
		obj.setStatus("Active");
		obj.setPassword("Welcome1");
		
		obj.setEmail(emails);
		
		
		ObjectMapper mapper = new ObjectMapper();

		//Object to JSON in file
		String resourceName = "testemployee.json"; // could also be a constant
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream resourceStream = loader.getResourceAsStream(resourceName);

		File jsonOutput = new File("C:\\temp\\input\\my.json");
		System.out.println(resourceStream);
		try {
			mapper.writeValue(jsonOutput, obj);
			System.out.println(obj);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
