<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to caArray 2</title>
</head>
<body>
<h1>Welcome to caArray 2</h1>
<f:view>
<%@include file="/navigation/menu.jsp" %>

<h:form id="projectProposalForm">
<h:messages/>
<table>
  <tr>
    <th>
      <h:outputLabel for="projectTitle">
        <h:outputText value="Title for new project:"/>
      </h:outputLabel>
    </th>
    <td>
      <h:inputText id="projectTitle" value="#{projectProposalBean.proposal.project.investigation.title}"/>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <h:commandButton id="submitCommandButton" value="Submit" action="#{projectProposalBean.submitNew}"/>
    </td>
  </tr>
</table>
</h:form>
</f:view>
</body>
</html>