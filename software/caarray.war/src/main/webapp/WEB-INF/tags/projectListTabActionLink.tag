<%@ tag display-name="projectListTabActionLink"
        description="Renders an action (edit, copy, etc) link for a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="action" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="isSubtab" required="false"%>
<%@ attribute name="linkRenderer" required="false" fragment="true"%>

<%@ variable name-given="actionUrl"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${empty linkContent}">
    <c:set var="linkContent"></c:set>
</c:if>

<ajax:anchors target="${tabAnchor}">
    <c:url value="/protected/ajax/project/listTab/${plural}/${action}.action" var="actionUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="current${entityName}.id" value="${itemId}" />
    </c:url>
    <c:choose>
        <c:when test="${empty linkRenderer}">
            <a href="${actionUrl}"><img src="<c:url value="/images/ico_${action}.gif"/>" alt="<fmt:message key="button.${action}"/>" /></a>
        </c:when>
        <c:otherwise>
            <jsp:invoke fragment="linkRenderer"/>
        </c:otherwise>    
    </c:choose>
</ajax:anchors>
