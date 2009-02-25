<%@ tag display-name="outputUrl"
        description="Renders a full URL (including scheme, host and port if necessary) for a given relative url, to the variable with given name"
        body-content="empty"%>
<%@ attribute name="url" required="false"%>
<%@ attribute name="var" required="true" rtexprvalue="false" type="java.lang.String" %>
<%@ variable name-from-attribute="var" alias="urlVar" scope="AT_END" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="scheme" value="${pageContext.request.scheme}"/>
<c:set var="serverName" value="${pageContext.request.serverName}"/>
<c:set var="port" value="${pageContext.request.serverPort}"/>
<c:if test="${scheme == 'http' && port != 80 || scheme == 'https' && port != 443}">
    <c:set var="outputPort" value=":${port}"/>
</c:if>
<c:set var="urlVar" value="${scheme}://${serverName}${outputPort}${url}"/>
