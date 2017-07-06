package org.wpb.lms.integration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.DBEmployee;
import org.wpb.lms.integration.api.helpers.PropertiesUtils;
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

	public static void main(String[] args) throws ParseException, IOException {
		
		DBEmployee dbEmp = new DBEmployee();

		dbEmp.setFIRST_NAME_MI("Suzanne");
		dbEmp.setLAST_NAME("Payson");
		dbEmp.setEMPLOYEE_ID("3241");
		dbEmp.setUSERNAME("WPB3241");
		dbEmp.setEMAIL("SPayson@wpb.org");
		dbEmp.setTEMP_PASSWORD("Welcome1");
		dbEmp.setDEPT("City Commission Department");
		dbEmp.setDIVISION("City Commission Admin");
		dbEmp.setJOB_TITLE("City Commissioner");
		dbEmp.setMANAGEMENT("None");
		dbEmp.setEMPLOYEE_GROUP("ELEC");
		dbEmp.setEMPLOYEE_CATEGORY("EL");
		dbEmp.setEFFECTIVE_HIRE("2011/03/31");	
		dbEmp.setSUPERVISOR("");
		dbEmp.setSUPERVISOR_RESP("YES");
		
		String status = new UpdateEmployee().updateEmployee(dbEmp);

		log.debug(status);
	}
}
