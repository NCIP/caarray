<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Antibody" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Antibodies/load.action" var="sortUrl">
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
            <display:column titleKey="experiment.antibody.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Antibody" action="view" itemId="${row.id}" isSubtab="false" maxWidth="30"/>
            </display:column>
            <display:column property="provider" sortProperty="TYPE" titleKey="experiment.antibody.provider" sortable="true" maxLength="30"/>
            <display:column property="catalogId" sortProperty="TYPE" titleKey="experiment.antibody.catalogid" sortable="true" maxLength="30"/>
           <display:column property="lotId" sortProperty="TYPE" titleKey="experiment.antibody.lotid" sortable="true" maxLength="30"/>
           
          
           
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
