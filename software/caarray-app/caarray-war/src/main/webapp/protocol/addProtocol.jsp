<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Protocol</title>
</head>

<body>
    <f:view>
    <h1>AddProtocol</h1>
        <h:form id="protocolForm">
            <h:outputLabel for="protocolType" value="Type"/>
            <h:selectOneListbox>
                <f:selectItems value="#{EditProtocolBean.protocolTypes}" />
            </h:selectOneListbox>
        </h:form>
    </f:view>
</body>

</html>