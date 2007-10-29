<%@ tag display-name="projectListTabEditLink"
        description="Renders the edit link for a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="action" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<ajax:anchors target="${tabAnchor}">
    <c:url value="/protected/ajax/project/listTab/${plural}/${action}.action" var="actionUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="current${entityName}.id" value="${itemId}" />
    </c:url>
    <a href="${actionUrl}"><img src="<c:url value="/images/ico_${action}.gif"/>" alt="<fmt:message key="button.${action}"/>" /></a>
</ajax:anchors>
