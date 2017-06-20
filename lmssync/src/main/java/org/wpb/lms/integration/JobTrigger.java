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
		Statement stmt = null;
		ResultSet jobIDRS = null;
		ResultSet rs = null;
		
		try {
			DBEmployee hrEmp = new DBEmployee();
			String hrEmpSyncResult = "";
			int syncJobID = 0;
			String syncStatus = "";
			String syncReason = "";
			String syncTimestamp = "";
			
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/lmssyncdatasource");
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			jobIDRS = stmt.executeQuery("select DataSync_Job_ID_SEQ.nextval from dual");
			
			if(jobIDRS.next()) {
				syncJobID = jobIDRS.getInt(1);
				int rowCount = stmt.executeUpdate("INSERT INTO DataSync_Job VALUES (" + syncJobID + ", sysdate, 0, 0");
				if(rowCount == 0) {
					response.getWriter().append("Unable to create Job ID in DataSync_Job with JobID: '" + syncJobID + "'. Please verify data integrity of DataSync_Job table");
					return;
				}
			}
			
			rs = stmt.executeQuery("select * from employee where SYNC_STATUS = 'NEW'");
			while (rs.next()) {
				hrEmp.setFIRST_NAME_MI(rs.getString("FIRST_NAME_MI"));
				hrEmp.setLAST_NAME(rs.getString("LAST_NAME"));
				hrEmp.setEMPLOYEE_ID(rs.getString("EMPLOYEE_ID"));
				hrEmp.setUSERNAME(rs.getString("USERNAME"));
				hrEmp.setEMAIL(rs.getString("EMAIL"));
				hrEmp.setTEMP_PASSWORD(rs.getString("TEMP_PASSWORD"));
				hrEmp.setDEPT(rs.getString("DEPT"));
				hrEmp.setDIVISION(rs.getString("DIVISION"));
				hrEmp.setJOB_TITLE(rs.getString("JOB_TITLE"));
				hrEmp.setMANAGEMENT(rs.getString("MANAGEMENT"));
				hrEmp.setEMPLOYEE_GROUP(rs.getString("EMPLOYEE_GROUP"));
				hrEmp.setEMPLOYEE_CATEGORY(rs.getString("EMPLOYEE_CATEGORY"));
				hrEmp.setEFFECTIVE_HIRE(rs.getString("EFFECTIVE_HIRE"));
				hrEmp.setSUPERVISOR(rs.getString("SUPERVISOR"));
				hrEmp.setSUPERVISOR_RESP(rs.getString("SUPERVISOR_RESP"));
				
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
					} else {
						syncStatus = "SYNC_FAILURE";
					}
				} 
				
				//Update the record with sync status
				stmt.executeUpdate("update employee "
						+ "set sync_status = '" + syncStatus + "'"
						+ ", SYNC_TIMESTAMP = sysdate"
						+ ", DataSync_Job_ID = '" + syncJobID + "'"
						+ ", SYNC_REASON = '" + hrEmpSyncResult + "' "
						+ "where employee_id = '" + hrEmp.getEMPLOYEE_ID() + "'");
				
				//TODO: Once EBS HR extract has employment status field, we will add deleteEmployee part
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(jobIDRS != null)
					jobIDRS.close();
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				//ignore the exception.. 
				e.printStackTrace();
			}
		}

		response.getWriter().append("Served at: ").append(request.getContextPath());
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
