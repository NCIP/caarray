<%@ tag display-name="tabPane"
        description="Formats a single tab pane for experiment management"
        body-content="scriptless"%>

<%@ attribute name="paneTitleKey" required="false"%>
<%@ attribute name="submittingPaneMessageKey" required="false"%>
<%@ attribute name="loadingPaneMessageKey" required="false"%>
<%@ attribute name="ignoreSuccessMessage" required="false" %>
<%@ attribute name="subtab" required="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${empty loadingPaneMessageKey}">
    <c:set var="loadingPaneMessageKey" value="message.loading" />
</c:if>
<c:if test="${empty submittingPaneMessageKey}">
    <c:set var="submittingPaneMessageKey" value="message.saving" />
</c:if>

<c:if test="${!empty paneTitleKey}">
    <h3><fmt:message key="${paneTitleKey}" /></h3>
</c:if>

<c:set var="loadingDivId" value="loadingText${subtab ? 'Subtab' : ''}"/>
<div id="${loadingDivId}" style="display: none;">
    <div><fmt:message key="${loadingPaneMessageKey}" /></div>
</div>
<c:set var="submittingDivId" value="submittingText${subtab ? 'Subtab' : ''}"/>
<div id="${submittingDivId}" style="display: none;">
    <div><fmt:message key="${submittingPaneMessageKey}" /></div>
</div>
<%-- Success Messages --%>
<c:if test="${not empty messages}">
    <div class="message" id="successMessages">
        <c:forEach var="msg" items="${messages}">
            <img src="<c:url value="/images/iconInformation.gif"/>"
                alt="<fmt:message key="icon.information"/>" class="icon" />
            <c:out value="${msg}" escapeXml="false"/><br />
        </c:forEach>
    </div>
    <c:remove var="messages" scope="session"/>
</c:if>
<c:set var="formDivId" value="theForm${subtab ? 'Subtab' : ''}"/>
<div id="${formDivId}">
    <c:if test="${not empty successMessage && ignoreSuccessMessage != 'true'}">
        <div class="confirm_msg">${successMessage}</div>
    </c:if>
    <jsp:doBody />
</div>
