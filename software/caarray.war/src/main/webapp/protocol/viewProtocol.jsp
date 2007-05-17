<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Protocol</title>
</head>
<body>
<f:view>
  <h1>View Protocol</h1>
  <%@include file="/mainMenu.jsp" %>
  <h:form id="protocolForm">
    <table>
      <tr>
        <th>Name</th>
        <td><h:outputText value="#{protocolBean.protocol.name}" />
      </tr>
      <tr>
        <th>Title</th>
        <td><h:outputText value="#{protocolBean.protocol.title}" />
      </tr>
      <tr>
        <th>Text</th>
        <td><h:outputText value="#{protocolBean.protocol.text}" />
      </tr>
      <tr>
        <th>URL</th>
        <td><h:outputText value="#{protocolBean.protocol.url}" />
      </tr>
      <tr>
        <th>Type</th>
        <td><h:outputText value="#{protocolBean.protocol.type.value}" /></td>
      </tr>
      <tr>
        <td colspan="2"><h:commandButton id="modifyButton" value="Modify" action="editProtocol" /></td>
      </tr>
    </table>
  </h:form>
</f:view>
</body>
</html>