<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Source" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Sources/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.sources}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.sources.name" sortable="true" sortProperty="name">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Source" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="description" titleKey="experiment.sources.description" sortable="true" />
            <display:column property="tissueSite.value" titleKey="currentSource.tissueSite" sortable="true" />
            <display:column titleKey="experiment.sources.relatedSamples">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.samples}" relatedEntityName="Sample" nameProperty="name" isSubtab="true"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Source" item="${row}" actions="!edit,!copy,!delete" isSubtab="true"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
