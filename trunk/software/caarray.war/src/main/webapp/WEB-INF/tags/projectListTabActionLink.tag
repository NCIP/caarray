<%@ tag display-name="projectListTabActionLink"
        description="Renders an action (edit, copy, etc) link for a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="action" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="isSubtab" required="false"%>
<%@ attribute name="linkContent" required="false" %>
<%@ attribute name="linkRenderer" required="false" fragment="true"%>
<%@ attribute name="confirmText" required="false"%>
<%@ attribute name="maxWidth" required="false" type="java.lang.Integer"%>

<%@ variable name-given="actionUrl"%>
<%@ variable name-given="loadTabFunction"%>
<%@ variable name-given="tabCaption"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:url value="/ajax/project/listTab/${plural}/${action}.action" var="actionUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="current${entityName}.id" value="${itemId}" />
</c:url>

<c:set var="loadTabFunction" value="${isSubtab ? 'loadLinkInSubTab' : 'loadLinkInTab' }"/>
<c:set var="showPopup" value="${action ne 'delete' || empty confirmText ? 'false' : 'true'}"/>
<fmt:message key="project.tabs.${pluralLower}" var="tabCaption" />

<c:choose>
    <c:when test="${empty linkRenderer}">
        <c:if test="${empty linkContent}">
            <c:set var="linkContent">
            	<img src="<c:url value="/images/ico_${action}.gif"/>" 
            		 alt="<fmt:message key="button.${action}"/>"
            		 title="<fmt:message key="button.${action}"/>">
          	</c:set>
        </c:if>
        <a href="#" onclick="if (!${showPopup} || confirm('${confirmText}')) { TabUtils.${loadTabFunction}('${tabCaption}', '${actionUrl}');} return false;">
            <caarray:abbreviate value="${linkContent}" maxWidth="${maxWidth}" escapeXml="false"/>
        </a>
    </c:when>
    <c:otherwise>
        <jsp:invoke fragment="linkRenderer"/>
    </c:otherwise>
</c:choose>
