<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="LabeledExtract" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/LabeledExtracts/load.action" var="sortUrl">
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
            <display:column titleKey="experiment.labeledExtracts.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="LabeledExtract" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="description" sortProperty="DESCRIPTION" titleKey="experiment.labeledExtracts.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentLabeledExtract.materialType"/>
            <display:column titleKey="experiment.labeledExtracts.relatedExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.labeledExtracts.relatedHybridizations">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.hybridizations}" relatedEntityName="Hybridization" nameProperty="name" isSubtab="true"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="LabeledExtract" item="${row}" actions="!edit,!copy,!delete" 
                isSubtab="true" canWriteProject="${canWriteProject}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
