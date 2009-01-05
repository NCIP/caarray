<%@ tag display-name="abbreviate" description="Abbreviates a string using an ellipsis, providing a tooltip with the full string.  If maxWitdh is set, it must be at least 4." body-content="empty" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="maxWidth" required="false" type="java.lang.Integer" %>
<%@ attribute name="defaultStr" required="false" %>
<%@ attribute name="escapeXml" required="false" %>
<%@ attribute name="title" required="false" %>

<%@ taglib uri="/WEB-INF/caarray-functions.tld" prefix="caarrayfn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty escapeXml}">
    <c:set var="escapeXml" value="true"/>
</c:if>

<c:choose>
    <c:when test="${empty title}">
        <span title="${value}">
    </c:when>
    <c:otherwise>
        <span title="${title}">
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${empty maxWidth || maxWidth < 4}">
        <c:out value="${value}" default="${defaultStr}" escapeXml="${escapeXml}"/>
    </c:when>
    <c:otherwise>
      <c:out value="${caarrayfn:abbreviate(value, maxWidth)}" default="${defaultStr}" escapeXml="${escapeXml}"/>
    </c:otherwise>
</c:choose>
</span>