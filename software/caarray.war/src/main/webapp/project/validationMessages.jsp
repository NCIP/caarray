<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Validation Messages</title>
</head>
<body>
<h1>Validation Messages</h1>
<f:view>
<%@include file="/navigation/menu.jsp" %>

<h:form id="filesForm">
  <h:messages/>
  <p>Validation Messages for <h:outputText value="#{projectManagementBean.caArrayFile.name}"/></p>
  <h:dataTable id="files" value="#{projectManagementBean.caArrayFile.validationResult.messages}" var="message" >
    <f:facet name="header">
      <h:outputText value="Messages"/>
    </f:facet>
    <h:column>
      <f:facet name="header">
        <h:outputText value="type"/>
      </f:facet>
      <h:outputText id="type" value="#{message.type}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="line"/>
      </f:facet>
      <h:outputText id="line" value="#{message.line}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="column"/>
      </f:facet>
      <h:outputText id="column" value="#{message.column}"/>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:outputText value="message"/>
      </f:facet>
      <h:outputText id="message" value="#{message.message}"/>
    </h:column>
  </h:dataTable>
</h:form>
</f:view>
</body>
</html>