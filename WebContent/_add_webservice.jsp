<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.AddAppsManager"%>
<%@ page import="java.util.Properties"%>
<%
	UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
	if(userInfo == null || !userInfo.isValid()){
		session.invalidate();
		out.print("Your session is invalid!<br>Please logout/login to create a new one.");
		return;
	}
	
	// Get current settings
	String active = String.valueOf(request.getParameter("active"));
	String name = String.valueOf(request.getParameter("name"));
	String url = String.valueOf(request.getParameter("url"));
	String soapaction = String.valueOf(request.getParameter("soapaction"));
	String postxml = String.valueOf(request.getParameter("postxml"));
	String expectedxmlresponse = String.valueOf(request.getParameter("expectedxmlresponse"));
	String executeoffline = String.valueOf(request.getParameter("executeoffline"));
	String groups = String.valueOf(request.getParameter("groups"));
	
	// Send to server for validation and update
	AddAppsManager addwebapp = new AddAppsManager(userInfo);
	String operationResult = addwebapp.addWebService(userInfo.getKey(), name, url, soapaction, 
			postxml, expectedxmlresponse, executeoffline, groups, active);
	out.print(operationResult);
%>