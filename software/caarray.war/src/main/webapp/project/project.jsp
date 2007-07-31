<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manage Project</title>
</head>
<body>
<f:view>
<%@include file="/navigation/menu.jsp" %>

<h:form id="projectManagementForm">
  <h:messages/>
  <p>You have selected <h:outputText value="#{projectManagementBean.project.investigation.title}"/>.  What would you
     like to do?</p>
    <ul>
      <li>
        <h:commandLink id="manageProjectFilesCommandLink" action="manageProjectFiles">
          <h:outputText value="Manage files"/>
        </h:commandLink>
      </li>
      <li>Other choices here...</li>
    </ul>
    <p>Project details:
    <p>
    <pre><h:outputText value="#{projectManagementBean.project}"/></pre>
</h:form>
</f:view>
</body>
</html>