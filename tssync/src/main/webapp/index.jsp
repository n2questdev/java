<%@page import="org.apache.cxf.common.util.StringUtils"%>
<%@page import="org.wpb.integration.tssync.TargetSolutionsRestClient"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<html>
<head>
<title>Target Solutions Employee Management page..</title>
</head>
<body bgcolor="white">

	<form name="searchEmployee" action="index.jsp" method="post">
		<%
			if (StringUtils.isEmpty(request.getParameter("empID"))) {
		%>
			<b>Enter WPB Employee ID:</b> <input name="empID" type="text">
			<input type="submit" name="Submit" value="Get Employee">
		<%
			} else {
				out.println(new TargetSolutionsRestClient().getEmployee(request.getParameter("empID")));
			}
		%>
	</form>
</body>
</html>