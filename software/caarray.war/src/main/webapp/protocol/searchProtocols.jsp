<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Protocols</title>
</head>
<body>
<h1>Search Protocols</h1>
<f:view>
<%@include file="/mainMenu.jsp" %>
  <h:form id="searchProtocolForm">
    <table>
      <tr>
        <th><h:outputLabel for="protocolName" value="Name" /></th>
        <td><h:inputText id="protocolName" value="#{searchProtocolsBean.protocol.name}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolTitle" value="Title" /></th>
        <td><h:inputText id="protocolTitle" value="#{searchProtocolsBean.protocol.title}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolText" value="Text" /></th>
        <td><h:inputTextarea id="protocolText" value="#{searchProtocolsBean.protocol.text}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolUrl" value="URL" /></th>
        <td><h:inputText id="protocolUrl" value="#{searchProtocolsBean.protocol.url}" /></td>
      </tr>

      <tr>
        <th><h:outputLabel for="protocolType" value="Type" /></th>
        <td>
          <h:selectOneMenu id="protocolType" value="#{searchProtocolsBean.protocolTypeId}">
            <f:selectItem itemLabel="(Any)" itemValue="0" />
            <f:selectItems value="#{searchProtocolsBean.protocolTypeItems}" />
          </h:selectOneMenu><h:message for="protocolType" />
        </td>

      <tr>
        <td colspan="2">
          <h:commandButton id="searchButton" value="Search" action="#{searchProtocolsBean.search}" />
        </td>
      </tr>

    </table>

    <h:dataTable value="#{searchProtocolsBean.matchingProtocols}" var="protocol">
      <h:column>
        <f:facet name="header">
          <h:outputText value="Name" />
        </f:facet>
        <h:commandLink action="#{searchProtocolsBean.viewProtocol}">
          <h:outputText value="#{protocol.name}" />
        </h:commandLink>
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Type" />
        </f:facet>
        <h:outputText value="#{protocol.type.value}" />
      </h:column>
    </h:dataTable>

  </h:form>
</f:view>
</body>
</html>