<%@ tag display-name="projectListTabDownloadLink"
        description="Renders a download column for a project annotation subtab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="itemId" required="true"%>

<%@ include file="projectListTabCommon.tagf"%>

<display:column titleKey="button.download">
    <c:url value="/ajax/project/listTab/${plural}/download.action" var="actionUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="current${entityName}.id" value="${itemId}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    <a href="${actionUrl}">
        <img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>">
    </a>
</display:column>
