<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Registration</title>
</head>
<body>
    <h1>Registration</h1>
    <fmt:message key="registration.success.msg">
        <fmt:param><a href="<c:url value="/protected/project/list.action" />" tabindex="1"><fmt:message key="registration.here" /></a></fmt:param>
    </fmt:message>
</body>
</html>