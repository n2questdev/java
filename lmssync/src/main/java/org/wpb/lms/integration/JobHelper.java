package org.wpb.lms.integration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpb.lms.entities.DBEmployee;
import org.wpb.lms.entities.Email;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.integration.api.helpers.CreateEmployee;
import org.wpb.lms.integration.api.helpers.DeleteEmployee;
import org.wpb.lms.integration.api.helpers.GetEmployee;
import org.wpb.lms.integration.api.helpers.PropertiesUtils;
import org.wpb.lms.integration.api.helpers.UpdateEmployee;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JobHelper {
	private static final Logger log = LogManager.getLogger(JobHelper.class);
	public static final float FAILURE_THRESHOLD; 
	static float threshold = 15;
	static {
		try {
			threshold = PropertiesUtils.getFailureThreshold();
			log.debug("Successfully initialized the job's FAILURE THRESHOLD to: " + threshold + "%");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		FAILURE_THRESHOLD = threshold;
	}
	InitialContext initContext = null;
	DataSource ds = null;
	
	public JobHelper() {
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/lmssyncdatasource");
		}catch(NamingException e) {
			log.fatal("Unable to create database connection! Can't proceed with the job..", e);
		}
	}

	public void runBatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ArrayList<DBEmployee> employeeList = new ArrayList<>();

		int totalRows = 0;
		int failureCount = 0;

		String hrEmpSyncResultMessage = "";
		int syncJobID = 0;
		String syncStatus = "";
		
		String processingMode = request.getParameter("n");
		
		syncJobID = initialize();
		if (syncJobID == -1) {
			response.getWriter()
					.append("Job initialization failed. Please review the log files to determine the root cause!");
			return;
		}

		//Get the Job mode. 
		
		employeeList = getEmployees(processingMode);
		totalRows = employeeList.size();

		for (DBEmployee dbEmployee : employeeList) {

			// Verify if failures are within the threshold. Stop the job if
			// threshold exceeds
			if ((failureCount * 100 / totalRows) > JobHelper.FAILURE_THRESHOLD) {
				response.getWriter().append(
						"Too many failures occuring!! Please review the data or server health before rerunning the job. ");

				// Now update the total and failed counts in
				// wpb_lms_DataSync_Job table
				int rowsUpdated = updateJobStatus(syncJobID, totalRows, failureCount);
				if (rowsUpdated < 1) {
					log.error(
							"Unable to update the job status. Please review the logs for root cause of the problem...");
					response.getWriter().append(
							"Unable to update the job status. Please review the logs for root cause of the problem...");
				}
				return;
			}

			Employee emp = new GetEmployee().getEmployeeByEmpNo(dbEmployee.getEMPLOYEE_ID());

			if (emp == null) {// Employee doesn't exist in LMS. Create it
				hrEmpSyncResultMessage = new CreateEmployee().createEmployee(dbEmployee);

				if (hrEmpSyncResultMessage.contains("created")) {
					syncStatus = "SYNC_SUCCESS";
				} else {
					syncStatus = "SYNC_FAILURE";
					failureCount++;
				}
			} else if (emp != null && emp.getUserid() != null && !emp.getUserid().isEmpty()) {
				
				if(dbEmployee.getSTATUS().equalsIgnoreCase("Inactive")) { //delete
					hrEmpSyncResultMessage = new DeleteEmployee().deleteEmployee(dbEmployee.getEMPLOYEE_ID());
					if (hrEmpSyncResultMessage.contains("deleted")) {
						syncStatus = "SYNC_SUCCESS";
					} else {
						syncStatus = "SYNC_FAILURE";
						failureCount++;
					}
				}
				else { //update
					hrEmpSyncResultMessage = new UpdateEmployee().updateEmployee(dbEmployee);
					if (hrEmpSyncResultMessage.contains("updated")) {
						syncStatus = "SYNC_SUCCESS";
					} else {
						syncStatus = "SYNC_FAILURE";
						failureCount++;
					}
				}

				// Update the employee record with sync status
				int rowsUpdated = updateEmployeeSyncStatus(syncStatus, syncJobID, hrEmpSyncResultMessage,
						dbEmployee);
				if (rowsUpdated < 1) {
					log.error(
							"Unable to update the employee record with job ID. Please review the logs for root cause of the problem...");
					response.getWriter().append(
							"Unable to update the employee record with job ID. Please review the logs for root cause of the problem...");
				}
			}
		}
		// Now update the total and failed counts in wpb_lms_DataSync_Job table
		int rowsUpdated = updateJobStatus(syncJobID, totalRows, failureCount);
		if (rowsUpdated < 1) {
			log.error(
					"Unable to update the job status. Please review the logs for root cause of the problem...");
			response.getWriter().append(
					"Unable to update the job status. Please review the logs for root cause of the problem...");
		} else {
			log.debug("Job ran successfully! Total employees count in this batch = " + totalRows
					+ ", and " + failureCount + " of them have warnings or errors." + ((failureCount > 0) ? "Please see database for respective failures by jobID: " + syncJobID : ""));
			response.getWriter().append("Job ran successfully! Total employees count in this batch = " + totalRows
					+ ", and " + failureCount + " of them have warnings or errors." + ((failureCount > 0) ? "Please see database for respective failures by jobID: " + syncJobID : ""));
		}
	}
	public static void main(String[] args) {

		// create new Employee
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

		// Object to JSON in file
		String resourceName = "testemployee.json"; // could also be a constant
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream resourceStream = loader.getResourceAsStream(resourceName);

		File jsonOutput = new File("C:\\temp\\input\\my.json");
		System.out.println(resourceStream);
		try {
			mapper.writeValue(jsonOutput, obj);
			System.out.println(obj);
		} catch (JsonParseException e) {
			log.fatal(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.fatal(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		}
	}

	public int initialize() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int syncJobID;

		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();

			// Create a job record when job starts
			rs = stmt.executeQuery("select wpb_lms_DataSync_Job_ID_SEQ.nextval from dual");
			if (rs.next()) {
				syncJobID = rs.getInt(1);
				int rowCount = stmt
						.executeUpdate("INSERT INTO wpb_lms_DataSync_Job VALUES (" + syncJobID + ", sysdate, 0, 0)");
				if (rowCount == 0) {
					log.error("Job initialization failed. Unable to insert new JobID in wpb_lms_DataSync_Job");
					syncJobID = -1;
				}
			} else {
				log.error(
						"Job initialization failed. Unable to generate get sequence number from wpb_lms_DataSync_Job_ID_SEQ");
				syncJobID = -1;
			}
		} catch (SQLException e) {
			log.error("Job initialization failed. Unable to insert jobID into wpb_lms_DataSync_Job. Exception is: "
					+ e.getMessage(), e);
			syncJobID = -1;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.warn("Unable to close JDBC resources.. There is a potential for resource leak.. sorry!", e);
			}
		}

		return syncJobID;
	}

	public int updateJobStatus(int jobID, int totalCount, int failureCount) {

		Connection conn = null;
		Statement stmt = null;
		int rowsUpdated;
		String sqlString = "update wpb_lms_DataSync_Job set rundate = sysdate"
				+ ", DataSync_Job_ID = '" + jobID + "', total = '" + totalCount + "', failed = '"
				+ failureCount + "' " + "where DataSync_Job_ID = '" + jobID + "'";
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();

			rowsUpdated = stmt.executeUpdate(sqlString);
		} catch (SQLException e) {
			log.error("Unable to update the job status. Exception: " + e.getMessage(), e);
			rowsUpdated = -1;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.warn("Unable to close JDBC resources.. There is a potential for resource leak.. sorry!", e);
			}
		}
		return rowsUpdated;
	}

	public ArrayList<DBEmployee> getEmployees(String processingMode) {
		ArrayList<DBEmployee> employeeList = new ArrayList<>();
		Connection conn = null;
		Statement empStmt = null;
		ResultSet empRS = null;

		if(processingMode == null) {
			processingMode = "NEW";
		} else {
			processingMode = "SYNC_FAILURE";
		}
		try {
			conn = ds.getConnection();
			empStmt = conn.createStatement();
			empRS = empStmt.executeQuery("select * from wpb_lms_Employee where SYNC_STATUS is null or SYNC_STATUS = '" + processingMode + "'");
			DBEmployee hrEmp;
			while (empRS.next()) {
				hrEmp = new DBEmployee();
				hrEmp.setFIRST_NAME_MI(empRS.getString("FIRST_NAME_MI"));
				hrEmp.setLAST_NAME(empRS.getString("LAST_NAME"));
				hrEmp.setEMPLOYEE_ID(empRS.getString("EMPLOYEE_ID"));
				hrEmp.setUSERNAME(empRS.getString("USERNAME"));
				hrEmp.setEMAIL(empRS.getString("EMAIL"));
				hrEmp.setTEMP_PASSWORD(empRS.getString("TEMP_PASSWORD"));
				hrEmp.setDEPT(empRS.getString("DEPT"));
				hrEmp.setDIVISION(empRS.getString("DIVISION"));
				hrEmp.setJOB_TITLE(empRS.getString("JOB_TITLE"));
				hrEmp.setMANAGEMENT(empRS.getString("MANAGEMENT"));
				hrEmp.setEMPLOYEE_GROUP(empRS.getString("EMPLOYEE_GROUP"));
				hrEmp.setEMPLOYEE_CATEGORY(empRS.getString("EMPLOYEE_CATEGORY"));
				hrEmp.setEFFECTIVE_HIRE(empRS.getString("EFFECTIVE_HIRE"));
				hrEmp.setSUPERVISOR(empRS.getString("SUPERVISOR"));
				hrEmp.setSUPERVISOR_RESP(empRS.getString("SUPERVISOR_RESP"));
				hrEmp.setSTATUS(empRS.getString("STATUS"));
				employeeList.add(hrEmp);
			}
		} catch (SQLException e) {
			log.error("Unable to retrieve employee records from wpb_lms_Employee. Exception: " + e.getMessage(), e);
		} finally {
			try {
				if (empRS != null)
					empRS.close();
				if (empStmt != null)
					empStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.warn("Unable to close JDBC resources.. There is a potential for resource leak.. sorry!", e);
			}
		}
		return employeeList;
	}

	public int updateEmployeeSyncStatus(String syncStatus, int syncJobID, String hrEmpSyncResult,
			DBEmployee dbEmployee) {
		Connection conn = null;
		Statement stmt = null;
		int rowsUpdated = -1;
		String sqlString = "update wpb_lms_Employee set sync_status = '" + syncStatus + "'"
				+ ", SYNC_TIMESTAMP = sysdate, DataSync_Job_ID = '" + syncJobID + "', SYNC_REASON = '"
				+ hrEmpSyncResult + "' where employee_id = '" + dbEmployee.getEMPLOYEE_ID() + "'";
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rowsUpdated = stmt.executeUpdate(sqlString);

		} catch (SQLException e) {
			log.error("Unable to update employee record in wpb_lms_Employee with JobID and sync status. Exception: "
					+ e.getMessage(), e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.warn("Unable to close JDBC resources.. There is a potential for resource leak.. sorry!", e);
			}
		}
		return rowsUpdated;

	}
}
