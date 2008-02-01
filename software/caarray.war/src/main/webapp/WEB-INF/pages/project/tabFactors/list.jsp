<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Factor" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Factors/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <c:set var="canWriteProject" value="${caarrayfn:canWrite(project, caarrayfn:currentUser())}"/>    
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id">
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.factors.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Factor" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="type.value" sortProperty="TYPE" titleKey="experiment.factors.type" sortable="true" />
            <caarray:projectListTabActionColumns entityName="Factor" item="${row}" actions="!edit,!copy,!delete" 
                isSubtab="true" canWriteProject="${canWriteProject}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
