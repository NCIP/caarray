<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="RplArray" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/RplArray/load.action" var="sortUrl">
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
            <display:column titleKey="Name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="RplArray" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column titleKey="Barcode" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="RplArray" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
           
           
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
