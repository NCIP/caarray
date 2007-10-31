<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Extract" isSubtab="true"/>

    <c:url value="/protected/ajax/project/listTab/Extract/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.extracts}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.extracts.name" sortable="true">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Extract" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="description" titleKey="experiment.extracts.description" sortable="true" />
            <display:column titleKey="experiment.extracts.nucleicAcidType" sortable="true">
                TODO
            </display:column>
            <display:column titleKey="experiment.extracts.relatedSamples">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.samples}" relatedEntityName="Sample" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.extracts.labeledExtracts">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.edit">
                <caarray:projectListTabActionLink entityName="Extract" action="edit" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.copy">
                <caarray:projectListTabActionLink entityName="Extract" action="copy" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.delete">
                <caarray:projectListTabActionLink entityName="Extract" action="delete" itemId="${row.id}" isSubtab="true"/>            
            </display:column>
        </display:table>
    </ajax:displayTag>

    <caarray:projectListTabHiddenForm entityName="Extract" isSubtab="true"/>
    </div>
</caarray:tabPane>
