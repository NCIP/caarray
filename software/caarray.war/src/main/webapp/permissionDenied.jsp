<%@ page language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
    <title>Permission Denied</title>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/caarray.css'/>" />
</head>

<body id="error">
    <div id="page">
        <div id="content" class="clearfix">
            <div id="main">
                <h1>Permission Denied</h1>
                <p><s:property value="%{exception.message}"/></p>
                <c:if test="${pageContext.request.remoteUser == null}">
                    <p>Please click <a href="<c:url value='/login.action'/>">here</a> to login.</p>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>
