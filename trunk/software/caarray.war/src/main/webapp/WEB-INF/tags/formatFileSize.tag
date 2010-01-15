<%@ tag display-name="formatFileSize" description="format byte value into TB,GB,KB" body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ attribute name="value" required="true" %>

<s:if test="${value >= 1099511627776}">
    <fmt:formatNumber value="${value / 1099511627776}" maxFractionDigits="1"/>&nbsp;<fmt:message key="file.size.tb"/>
</s:if>
<s:elseif test="${value >= 1073741824}">
    <fmt:formatNumber value="${value / 1073741824}" maxFractionDigits="1"/>&nbsp;<fmt:message key="file.size.gb"/>
</s:elseif>
<s:elseif test="${value >= 1048576}">
    <fmt:formatNumber value="${value / 1048576}" maxFractionDigits="1"/>&nbsp;<fmt:message key="file.size.mb"/>
</s:elseif>
<s:elseif test="${value >= 1024}">
    <fmt:formatNumber value="${value / 1024}" maxFractionDigits="0"/>&nbsp;<fmt:message key="file.size.kb"/>
</s:elseif>
<s:else>
    <fmt:formatNumber value="${value}" maxFractionDigits="0"/>&nbsp;<fmt:message key="file.size.by"/>
</s:else>