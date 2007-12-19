<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Extract" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Extracts/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.extracts.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Extract" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="description" sortProperty="DESCRIPTION" titleKey="experiment.extracts.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentExtract.materialType"/>
            <display:column titleKey="experiment.extracts.relatedSamples">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.samples}" relatedEntityName="Sample" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.extracts.labeledExtracts">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Extract" item="${row}" actions="!edit,!copy,!delete" isSubtab="true"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
