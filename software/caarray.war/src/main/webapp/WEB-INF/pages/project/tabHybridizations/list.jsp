<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Hybridization" isSubtab="true"/>

    <c:url value="/protected/ajax/project/listTab/Hybridizations/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.hybridizations}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.hybridizations.name" sortable="true" sortProperty="name">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Hybridization" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.hybridizations.relatedLabeledExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.hybridizations.fileSize" sortable="true">
                TODO
            </display:column>
            <caarray:projectListTabActionColumns entityName="Hybridization" itemId="${row.id}" actions="!edit,download,!delete" isSubtab="true"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
