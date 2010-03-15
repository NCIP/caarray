<%@ tag display-name="formatFileSize" description="format byte value into TB,GB,KB" body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ attribute name="value" required="true" %>

<c:choose>
    <c:when test="${value >= 1099511627776}">
        <fmt:formatNumber value="${value / 1099511627776}" maxFractionDigits="1"/>&nbsp;<fmt:message key="file.size.tb"/>
    </c:when>
    <c:when test="${value >= 1073741824}">
        <fmt:formatNumber value="${value / 1073741824}" maxFractionDigits="1"/>&nbsp;<fmt:message key="file.size.gb"/>
    </c:when>
    <c:when test="${value >= 1048576}">
        <fmt:formatNumber value="${value / 1048576}" maxFractionDigits="1"/>&nbsp;<fmt:message key="file.size.mb"/>
    </c:when>
    <c:when test="${value >= 1024}">
        <fmt:formatNumber value="${value / 1024}" maxFractionDigits="0"/>&nbsp;<fmt:message key="file.size.kb"/>
    </c:when>
    <c:otherwise>
        <fmt:formatNumber value="${value}" maxFractionDigits="0"/>&nbsp;<fmt:message key="file.size.by"/>
    </c:otherwise>
</c:choose>
