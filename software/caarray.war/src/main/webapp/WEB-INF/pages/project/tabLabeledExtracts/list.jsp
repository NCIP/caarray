<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="LabeledExtract" isSubtab="true"/>

    <c:url value="/protected/ajax/project/listTab/LabeledExtract/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.labeledExtracts}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.labeledExtracts.name" sortable="true">
                <caarray:projectListTabActionLink entityName="LabeledExtract" action="view" itemId="${row.id}" isSubtab="true">
                    <jsp:attribute name="linkRenderer">
                        <a href="${actionUrl}">${row.name}</a>
                    </jsp:attribute>
                </caarray:projectListTabActionLink>
            </display:column>
            <display:column property="description" titleKey="experiment.labeledExtracts.description" sortable="true" />
            <display:column property="label.value" titleKey="experiment.labeledExtracts.label" sortable="true" />
            <display:column titleKey="experiment.labeledExtracts.amount" sortable="true">
                TODO
            </display:column>
            <display:column titleKey="experiment.labeledExtracts.protocol" sortable="true">
                TODO
            </display:column>
            <display:column titleKey="experiment.labeledExtracts.relatedExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.labeledExtracts.relatedHybridizations">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.hybridizations}" relatedEntityName="Hybridization" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.edit">
                <caarray:projectListTabActionLink entityName="LabeledExtract" action="edit" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.copy">
                <caarray:projectListTabActionLink entityName="LabeledExtract" action="copy" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.delete">
                <caarray:projectListTabActionLink entityName="LabeledExtract" action="delete" itemId="${row.id}" isSubtab="true"/>            
            </display:column>
        </display:table>
    </ajax:displayTag>

    <caarray:projectListTabHiddenForm entityName="LabeledExtract" isSubtab="true"/>
    </div>
</caarray:tabPane>
