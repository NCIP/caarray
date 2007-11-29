<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="LabeledExtract" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/LabeledExtracts/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.labeledExtracts}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.labeledExtracts.name" sortable="true" sortProperty="name">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="LabeledExtract" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="description" titleKey="experiment.labeledExtracts.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentLabeledExtract.materialType" sortable="true" />
            <display:column titleKey="experiment.labeledExtracts.relatedExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.labeledExtracts.relatedHybridizations">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.hybridizations}" relatedEntityName="Hybridization" nameProperty="name" isSubtab="true"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="LabeledExtract" item="${row}" actions="!edit,!copy,!delete" isSubtab="true"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
