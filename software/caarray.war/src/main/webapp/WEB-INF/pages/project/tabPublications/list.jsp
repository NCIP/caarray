<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Publication"/>

    <c:url value="/ajax/project/listTab/Publications/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <c:set var="canWriteProject" value="${caarrayfn:canWrite(project, caarrayfn:currentUser())}"/>
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.publications.title" sortable="true" sortProperty="TITLE">
                <caarray:projectListTabActionLink linkContent="${!empty row.title ? row.title : 'View'}" entityName="Publication" action="view" itemId="${row.id}"/>
            </display:column>
            <display:column property="authors" sortProperty="AUTHORS" titleKey="experiment.publications.authors" sortable="true" />
            <display:column titleKey="experiment.publications.uri" sortProperty="URI" sortable="true">
                <a href="${row.uri}" target="_blank">${row.uri}</a>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Publication" item="${row}" actions="!edit,!delete" 
                canWriteProject="${canWriteProject}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>