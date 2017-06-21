package org.wpb.lms.integration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.wpb.lms.entities.DBEmployee;
import org.wpb.lms.entities.Employee;
import org.wpb.lms.integration.api.helpers.CreateEmployee;
import org.wpb.lms.integration.api.helpers.GetEmployee;
import org.wpb.lms.integration.api.helpers.UpdateEmployee;

/**
 * Servlet implementation class JobTrigger
 */
public class JobTrigger extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final float FAILURE_THRESHOLD = 10;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JobTrigger() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Context initContext;
		DataSource ds;
		Connection conn = null;
		Statement jobStmt = null;
		ResultSet jobRS = null;
		
		Statement empStmt = null;
		ResultSet empRS = null;
		
		Statement totalRowCountStmt = null;
		ResultSet totalRowCountRS = null;
		
		int totalRows = 0;
		int totalRowsProcessed = 0;
		int failureCount = 0;
		
		try {
			DBEmployee hrEmp = new DBEmployee();
			String hrEmpSyncResult = "";
			int syncJobID = 0;
			String syncStatus = "";
			
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/lmssyncdatasource");
			conn = ds.getConnection();
			jobStmt = conn.createStatement();
			empStmt = conn.createStatement();
			totalRowCountStmt = conn.createStatement();
			
			//Create a job record when job starts
			jobRS = jobStmt.executeQuery("select wpb_lms_DataSync_Job_ID_SEQ.nextval from dual");
			if(jobRS.next()) {
				syncJobID = jobRS.getInt(1);
				int rowCount = jobStmt.executeUpdate("INSERT INTO wpb_lms_DataSync_Job VALUES (" + syncJobID + ", sysdate, 0, 0)");
				if(rowCount == 0) {
					response.getWriter().append("Unable to create Job ID in wpb_lms_DataSync_Job with JobID: '" + syncJobID + "'. Please verify data integrity of wpb_lms_DataSync_Job table");
					return;
				}
			}

			//Get total row count for threshold checking purposes
			totalRowCountRS = totalRowCountStmt.executeQuery("select count(*) from wpb_lms_Employee where SYNC_STATUS = 'NEW'");
			if(totalRowCountRS.next())
				totalRows = totalRowCountRS.getInt(1);

			empRS = empStmt.executeQuery("select * from wpb_lms_Employee where SYNC_STATUS = 'NEW'");
			while (empRS.next()) {
				
				//Verify if failures are within the threshold. Stop the job if threshold exceeds
				if(failureCount/totalRows > FAILURE_THRESHOLD) {
					response.getWriter().append("Too many failures occuring!! Please review the data or server health before rerunning the job");
					//Now update the total and failed counts in wpb_lms_DataSync_Job table
					jobStmt.executeUpdate("update wpb_lms_DataSync_Job "
							+ "set  rundate = sysdate"
							+ ", DataSync_Job_ID = '" + syncJobID + "'"
							+ ", total = '" + totalRowsProcessed + "' "
							+", failed = '" + failureCount + "' "
							+ "where DataSync_Job_ID = '" + syncJobID + "'");
					return;
				}
				
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
				
				Employee emp = new GetEmployee().getEmployeeByEmpNo(hrEmp.getEMPLOYEE_ID());

				if(emp == null) {//Employee doesn't exist in LMS. Create it
					hrEmpSyncResult = new CreateEmployee().createEmployee(hrEmp);
					
					if(hrEmpSyncResult.contains("created")) { 
						syncStatus = "SYNC_SUCCESS";
					} else {
						syncStatus = "SYNC_FAILURE";
					}
				} else if(emp != null && emp.getUserid() != null && !emp.getUserid().isEmpty()) {
					hrEmpSyncResult = new UpdateEmployee().updateEmployee(hrEmp);
					if(hrEmpSyncResult.contains("updated")) { 
						syncStatus = "SYNC_SUCCESS";
						totalRowsProcessed++;
					} else {
						syncStatus = "SYNC_FAILURE";
						failureCount++;
					}
				}
				//TODO: Once EBS HR extract has employment status field, we will add deleteEmployee part
				
				//Update the employee record with sync status
				empStmt.executeUpdate("update wpb_lms_Employee "
						+ "set sync_status = '" + syncStatus + "'"
						+ ", SYNC_TIMESTAMP = sysdate"
						+ ", DataSync_Job_ID = '" + syncJobID + "'"
						+ ", SYNC_REASON = '" + hrEmpSyncResult + "' "
						+ "where employee_id = '" + hrEmp.getEMPLOYEE_ID() + "'");
				
			}
			
			//Now update the total and failed counts in wpb_lms_DataSync_Job table
			jobStmt.executeUpdate("update wpb_lms_DataSync_Job "
					+ "set  rundate = sysdate"
					+ ", DataSync_Job_ID = '" + syncJobID + "'"
					+ ", total = '" + totalRowsProcessed + "' "
					+", failed = '" + failureCount + "' "
					+ "where DataSync_Job_ID = '" + syncJobID + "'");
		
		} catch (NamingException e) {
			failureCount++;
			e.printStackTrace();
		} catch (SQLException e) {
			failureCount++;
			e.printStackTrace();
		} finally {
			try {
				if(empRS != null)
					empRS.close();
				if(jobRS != null)
					jobRS.close();
				if(jobStmt != null)
					jobStmt.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				//ignore the exception.. 
				e.printStackTrace();
			}
		}

		response.getWriter().append("Job completed successfully...");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
