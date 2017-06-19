package org.wpb.lms.integration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.DBEmployee;
import org.wpb.lms.integration.api.helpers.UpdateEmployee;

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
		
		DBEmployee dbEmp = new DBEmployee();
		dbEmp.setFIRST_NAME_MI("Joe");
		dbEmp.setLAST_NAME("Flom");
		dbEmp.setEMPLOYEE_ID("7202136350");
		dbEmp.setUSERNAME("WPB7202136350");
		dbEmp.setEMAIL("jflom@yahoo.com");
		dbEmp.setTEMP_PASSWORD("Welcome1");
		dbEmp.setDEPT("City Attorney Department");
		dbEmp.setDIVISION("City Attorney Admin");
		dbEmp.setJOB_TITLE("Assistant City Attorney I");
		dbEmp.setMANAGEMENT("II");
		dbEmp.setEMPLOYEE_GROUP("RGUNCL");
		dbEmp.setEMPLOYEE_CATEGORY("FR");
		dbEmp.setEFFECTIVE_HIRE("1947/11/05");
		dbEmp.setSUPERVISOR("Rothenburg Kimberly L");
		dbEmp.setSUPERVISOR_RESP("NO");
		
		String status = new UpdateEmployee().updateEmployee(dbEmp);

		log.debug(status);
	}
}
