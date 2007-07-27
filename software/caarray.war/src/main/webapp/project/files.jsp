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
<f:view>
<%@include file="/navigation/menu.jsp" %>

<h:form>
  <h:messages/>
  <p>You are managing files for <h:outputText value="#{projectManagementBean.project.investigation.title}"/>.</p>
  <h:dataTable value="#{projectManagementBean.project.filesList}"
               var="file">
    <f:facet name="header">
      <h:outputText value="Current files"/>
    </f:facet>
    <h:column>
      <f:facet name="header">
        <h:outputText value="Path"/>
      </f:facet>
      <h:outputText value="#{file.path}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="File Type"/>
      </f:facet>
      <h:outputText value="#{file.type}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="Status"/>
      </f:facet>
      <h:outputText value="#{file.status}"/>
    </h:column>
  </h:dataTable>
  <f:facet name="footer">
    <h:commandLink action="importProjectFiles">
      <h:outputText value="Import"/>
    </h:commandLink>
  </f:facet>
  <h:commandLink action="importProjectFiles">
    <h:outputText value="Import"/>
  </h:commandLink>
</h:form>
<h:form enctype="multipart/form-data">
  <t:inputFileUpload value="#{projectManagementBean.uploadFile}" storage="file" required="true"/>
  <h:commandButton action="#{projectManagementBean.upload}" value="Upload"/>
</h:form>
</f:view>
</body>
</html>