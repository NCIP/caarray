<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="RplaHybridization" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/RplaHybridizations/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <c:set var="canWriteProject" value="${caarrayfn:canWrite(project, caarrayfn:currentUser())}"/>
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.rplahybridizations.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="RplaHybridization" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
             <display:column property="date" sortProperty="NAME" titleKey="experiment.rplahybridizations.date" sortable="true" maxLength="30"/>
            <s:set name="showDownloadGroups" value="%{@gov.nih.nci.caarray.web.action.project.AbstractProjectProtocolAnnotationListTabAction@isWillPerformDownloadByGroups(#attr.row.getAllDataFiles())}"/>
            <caarray:projectListTabDownloadColumn entityName="RplaHybridization" itemId="${row.id}" showDownloadGroups="${showDownloadGroups}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
