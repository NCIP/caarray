<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">
    <div class="tableboxpad">
    <c:url value="ajax_Project_loadGenericTab_downloadData.action" var="sortUrl">
        <c:param name="proposalKey" value="${proposalKey}" />
        <c:param name="ajax" value="true" />
    </c:url>

    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${proposal.project.files}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="proposalKey">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.files.name" sortable="true">
                <c:url var="downloadUrl" value="/protected/File_download.action">
                	<c:param name="downloadIds" value="${row.id}"/>
                </c:url>
            	<a href="${downloadUrl}">${row.name}</a>
            </display:column>
            <display:column property="type.name" titleKey="experiment.files.type" sortable="true" />
            <display:column property="status" titleKey="experiment.files.status" sortable="true" />
        </display:table>
    </ajax:displayTag>
    </div>
</caarray:tabPane>
