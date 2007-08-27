<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manage Project Files</title>
</head>
<body>
<h1>Manage Project Files</h1>
<f:view>
<%@include file="/navigation/menu.jsp" %>

<h:form id="filesForm">
  <h:messages/>
  <p>You are managing files for <h:outputText value="#{projectManagementBean.project.experiment.title}"/>.</p>
  <h:dataTable id="files" value="#{projectManagementBean.project.filesList}" var="file" binding="#{projectManagementBean.fileTable}" >
    <f:facet name="header">
      <h:outputText value="Current files"/>
    </f:facet>
    <h:column>
      <f:facet name="header">
        <h:outputText value="Select"/>
      </f:facet>
      <h:selectBooleanCheckbox id="selected" binding="#{projectManagementBean.fileSelected}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="Name"/>
      </f:facet>
      <h:outputText id="filename" value="#{file.name}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="File Type"/>
      </f:facet>
      <h:outputText id="type" value="#{file.type}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="Status"/>
      </f:facet>
      <h:outputText id="status" value="#{file.status}"/>
    </h:column>
  </h:dataTable>
  <h:commandButton id="import" action="#{projectManagementBean.importProjectFiles}" value="Import" />
  <h:commandButton id="validate" action="#{projectManagementBean.validateProjectFiles}" value="Validate" />
</h:form>
<h:form id="uploadForm" enctype="multipart/form-data">
  <t:inputFileUpload id="inputFileUpload" value="#{projectManagementBean.uploadFile}" storage="file" required="true"/>
  <h:commandButton id="upload" action="#{projectManagementBean.upload}" value="Upload"/>
</h:form>
</f:view>
</body>
</html>