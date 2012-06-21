<%@ tag display-name="projectListTabDownloadColumn"
        description="Renders a download column for a project annotation subtab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="showDownloadGroups" required="true" type="java.lang.Boolean"%>

<%@ include file="projectListTabCommon.tagf"%>

<display:column titleKey="button.download">
    <c:url value="/ajax/project/listTab/${plural}/download.action" var="actionUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="current${entityName}.id" value="${itemId}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    
    <c:choose>
        <c:when test="${showDownloadGroups}">
            <%-- Load the download groups page within the Data (tab) -> Download data (subtab) --%>
            <c:url value="/ajax/project/tab/Data/load.action" var="showDataTabDownloadDataSubTabWithDownloadGroupsUrl">
                <c:param name="project.id" value="${project.id}" />
                <c:param name="editMode" value="${editMode}" />
                <c:param name="initialTab" value="data"/>
                <c:param name="initialTab2" value="downloadData"/>    
                <c:param name="initialTab2Url" value="${actionUrl}"/>    
            </c:url>
            <a onclick="TabUtils.loadLinkInTab('Data', '${showDataTabDownloadDataSubTabWithDownloadGroupsUrl}'); return false;">
                <img src="<c:url value="/images/ico_download.gif"/>" 
                	 alt="<fmt:message key="button.download"/>"
                	 title="<fmt:message key="button.download"/>">
            </a>
        </c:when>
        <c:otherwise>
            <a href="${actionUrl}">
                <img src="<c:url value="/images/ico_download.gif"/>" 
                	 alt="<fmt:message key="button.download"/>"
                	 title="<fmt:message key="button.download"/>">
            </a>
        </c:otherwise>
    </c:choose>
</display:column>
