package org.wpb.integration.tssync;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.integration.tssync.utils.TargetSolutionsAPIHelper;
import org.wpb.targetsolutions.entities.Credential;
import org.wpb.targetsolutions.entities.Credentials;

/**
 * This is the REST Client with several utilities to interact with Target
 * Solutions REST API. Wherever you see userid in the method parameters, it is
 * always Target Solution's assigned userID but not WPB employeeID
 * 
 * @author Abhilash Juluri
 *
 */
public class TargetSolutionsRestClient {
	private static final Logger log = LogManager.getLogger(TargetSolutionsRestClient.class);

	// Logger.getLogger(TargetSolutionsRestClient.class.getName());

	public static void main(String[] args) {
		try {
			Credentials credentials = new TargetSolutionsAPIHelper().getEmployeeCredentials("6950");
			List<Credential> creds = credentials.getCredentials();
			for (Credential credential : creds) {
				log.debug(credential);
				System.out.println(credential);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
