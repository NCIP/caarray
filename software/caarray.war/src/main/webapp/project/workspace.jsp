<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Workspace</title>
</head>
<body>
<h1>User Workspace</h1>
<f:view>
  <%@include file="/navigation/menu.jsp"%>

  <h:form id="workspaceForm">
    <h:messages />
    <h:dataTable id="projects" value="#{projectManagementBean.projects}" var="project" binding="#{projectManagementBean.projectTable}">
      <f:facet name="header">
        <h:outputText value="Select a project" />
      </f:facet>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Title" />
        </f:facet>
        <h:commandLink id="openProject" action="#{projectManagementBean.openProject}">
          <h:outputText id="title" value="#{project.experiment.title}" />
        </h:commandLink>
      </h:column>
    </h:dataTable>
  </h:form>
</f:view>
</body>
</html>
