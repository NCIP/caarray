<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Source" isSubtab="true"/>

    <c:url value="/protected/ajax/project/listTab/Source/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.sources}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.sources.name" sortable="true">
                <caarray:projectListTabActionLink entityName="Source" action="view" itemId="${row.id}" isSubtab="true">
                    <jsp:attribute name="linkRenderer">
                        <a href="${actionUrl}">${row.name}</a>
                    </jsp:attribute>
                </caarray:projectListTabActionLink>
            </display:column>
            <display:column property="description" titleKey="experiment.sources.description" sortable="true" />
            <display:column property="organism.commonName" titleKey="experiment.sources.organism" sortable="true" />
            <display:column titleKey="experiment.sources.relatedSamples">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.samples}" relatedEntityName="Sample" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.edit">
                <caarray:projectListTabActionLink entityName="Source" action="edit" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.copy">
                <caarray:projectListTabActionLink entityName="Source" action="copy" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.delete">
                <caarray:projectListTabActionLink entityName="Source" action="delete" itemId="${row.id}" isSubtab="true"/>            
            </display:column>
        </display:table>
    </ajax:displayTag>

    <caarray:projectListTabHiddenForm entityName="Source" isSubtab="true"/>
    </div>
</caarray:tabPane>
