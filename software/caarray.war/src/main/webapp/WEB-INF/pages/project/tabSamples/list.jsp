<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Sample" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Samples/load.action" var="sortUrl">
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
            <display:column titleKey="experiment.samples.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Sample" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="description" sortProperty="DESCRIPTION" titleKey="experiment.samples.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentSample.materialType"/>
            <display:column titleKey="experiment.samples.sources">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.sources}" relatedEntityName="Source" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.samples.extracts">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Sample" item="${row}" actions="!edit,!copy,!delete" 
                isSubtab="true" canWriteProject="${canWriteProject}"/>
            <display:column titleKey="button.download">
        <c:url value="/ajax/project/listTab/Samples/download.action" var="actionUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="currentSample.id" value="${row.id}" />
            <c:param name="editMode" value="${editMode}" />
        </c:url>
            <a href="${actionUrl}">
                <img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>">
            </a>
            </display:column>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>