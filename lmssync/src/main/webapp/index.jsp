<%@page import="org.wpb.integration.tssync.utils.LMSAPIHelper"%>
<%@page import="org.apache.cxf.common.util.StringUtils"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<html>
<head>
<style>
table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
}
</style>
<title>LMS Employee Lookup</title>
</head>
<body bgcolor="white">
<H1>Welcome to LMS Lookup Page! </H1>
	<form name="searchEmployee" action="index.jsp" method="post">
			<b>Enter WPB Employee ID:</b> <input name="empNo" type="text">
			<input type="submit" name="Submit" value="Get Employee">
			<BR><BR>
		<%
			if (!StringUtils.isEmpty(request.getParameter("empNo"))) {
				String responseVal = new LMSAPIHelper().getEmployeeByEmpNo(request.getParameter("empNo")) != null ? new LMSAPIHelper().getEmployeeByEmpNo(request.getParameter("empNo")).htmlPrint() : "<H2>Sorry! Requested employee is not found in LMS system. Please verify employee number..</H2>";
				out.println(responseVal);
			}
		%>
	</form>
</body>
</html>