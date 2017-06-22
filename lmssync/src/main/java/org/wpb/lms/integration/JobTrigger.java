package org.wpb.lms.integration;

import java.io.IOException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static final Logger log = LogManager.getLogger(JobTrigger.class);

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

		ArrayList<DBEmployee> employeeList = new ArrayList<>();
		JobHelper jobHelper = new JobHelper();
		Context initContext = null;
		DataSource ds;

		int totalRows = 0;
		int failureCount = 0;

		try {
			String hrEmpSyncResultMessage = "";
			int syncJobID = 0;
			String syncStatus = "";

			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/lmssyncdatasource");

			syncJobID = jobHelper.initialize(ds);
			if (syncJobID == -1) {
				response.getWriter()
						.append("Job initialization failed. Please review the log files to determine the root cause!");
				return;
			}

			employeeList = jobHelper.getEmployees(ds);
			totalRows = employeeList.size();

			for (DBEmployee dbEmployee : employeeList) {

				// Verify if failures are within the threshold. Stop the job if
				// threshold exceeds
				if (failureCount * 100 / totalRows > JobHelper.FAILURE_THRESHOLD) {
					response.getWriter().append(
							"Too many failures occuring!! Please review the data or server health before rerunning the job. ");

					// Now update the total and failed counts in wpb_lms_DataSync_Job table
					int rowsUpdated = jobHelper.updateJobStatus(ds, syncJobID, totalRows, failureCount);
					if (rowsUpdated < 1) {
						log.error(
								"Unable to update the job status. Please review the logs for root cause of the problem...");
						response.getWriter().append(
								"Unable to update the job status. Please review the logs for root cause of the problem...");
					}
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
					hrEmpSyncResultMessage = new UpdateEmployee().updateEmployee(dbEmployee);
					if (hrEmpSyncResultMessage.contains("updated")) {
						syncStatus = "SYNC_SUCCESS";
					} else {
						syncStatus = "SYNC_FAILURE";
						failureCount++;
					}
					// TODO: Once EBS HR extract has employment status field, we will add deleteEmployee part

					// Update the employee record with sync status
					int rowsUpdated = jobHelper.updateEmployeeSyncStatus(ds, syncStatus, syncJobID,
							hrEmpSyncResultMessage, dbEmployee);
					if (rowsUpdated < 1) {
						log.error(
								"Unable to update the job status. Please review the logs for root cause of the problem...");
						response.getWriter().append(
								"Unable to update the job status. Please review the logs for root cause of the problem...");
					}
				}
			}
			// Now update the total and failed counts in wpb_lms_DataSync_Job table
			jobHelper.updateJobStatus(ds, syncJobID, totalRows, failureCount);
		} catch (NamingException e) {
			failureCount++;
			e.printStackTrace();
		} finally {
			try {
				initContext.close();
			} catch (NamingException e) {
				log.warn("Unable to close JDBC resources.. There is a potential for resource leak.. sorry!", e);
			}
		}

		response.getWriter().append("Job ran successfully! Total employees count in this batch = " + totalRows + ", failed processing = " + failureCount);
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
