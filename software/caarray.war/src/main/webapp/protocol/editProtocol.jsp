<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Edit Protocol</title>
</head>
<body>
<f:view>
  <h1>Edit Protocol</h1>
  <%@include file="/mainMenu.jsp" %>
  <h:form id="protocolForm">

    <table>
      <tr>
        <th><h:outputLabel for="protocolName" value="Name" /></th>
        <td><h:inputText id="protocolName" value="#{protocolBean.protocol.name}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolTitle" value="Title" /></th>
        <td><h:inputText id="protocolTitle" value="#{protocolBean.protocol.title}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolText" value="Text" /></th>
        <td><h:inputTextarea id="protocolText" value="#{protocolBean.protocol.text}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolUrl" value="URL" /></th>
        <td><h:inputText id="protocolUrl" value="#{protocolBean.protocol.url}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolType" value="Type" /></th>
        <td>
          <h:selectOneMenu id="protocolType" value="#{protocolBean.protocolTypeId}">
            <f:selectItems value="#{protocolBean.protocolTypeItems}" />
          </h:selectOneMenu><h:message for="protocolType" />
        </td>

      <tr>
        <td colspan="2">
          <h:commandButton id="saveButton" value="save" action="#{protocolBean.save}" />
        </td>
      </tr>

    </table>

  </h:form>
</f:view>
</body>

</html>
