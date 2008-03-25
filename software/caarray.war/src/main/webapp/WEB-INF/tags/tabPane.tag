<%@ tag display-name="tabPane"
        description="Formats a single tab pane for experiment management"
        body-content="scriptless"%>

<%@ attribute name="paneTitleKey" required="false"%>
<%@ attribute name="submittingPaneMessageKey" required="false"%>
<%@ attribute name="loadingPaneMessageKey" required="false"%>
<%@ attribute name="ignoreSuccessMessage" required="false" %>
<%@ attribute name="subtab" required="false"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:if test="${empty loadingPaneMessageKey}">
    <c:set var="loadingPaneMessageKey" value="message.loading" />
</c:if>
<c:if test="${empty submittingPaneMessageKey}">
    <c:set var="submittingPaneMessageKey" value="message.saving" />
</c:if>

<c:set var="loadingDivId" value="loadingText"/>
<div id="${loadingDivId}" class="loadingText" style="display: none;">
    <div><img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> <fmt:message key="${loadingPaneMessageKey}" /></div>
</div>
<c:if test="${!empty paneTitleKey}">
    <div id="tabHeader" <c:if test="${subtab}">class="boxpad2"</c:if>>
        <h3><fmt:message key="${paneTitleKey}" /></h3>
    </div>
</c:if>
<c:set var="submittingDivId" value="submittingText"/>
<div id="${submittingDivId}" style="display: none;">
    <div><img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> <span id="submittingTextSpan"><fmt:message key="${submittingPaneMessageKey}" /></span></div>
</div>
<c:set var="formDivId" value="theForm"/>
<div id="${formDivId}">
    <caarray:successMessages />
    <s:actionerror/>
    <jsp:doBody />
</div>
<script type="text/javascript">
TabUtils.updateSavedFormData();
</script>