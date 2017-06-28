package org.wpb.lms.integration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JobTrigger
 */
public class JobTrigger extends HttpServlet {
	private static final long serialVersionUID = 1L;
//	private static final Logger log = LogManager.getLogger(JobTrigger.class);

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
		
		JobHelper jobHelper = new JobHelper();
		jobHelper.runBatch(request, response);
//		ArrayList<DBEmployee> employees = jobHelper.getEmployees();
//		
//		for (DBEmployee dbEmployee : employees) {
//			response.getWriter().append(dbEmployee.getEMPLOYEE_ID());
//		}
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
