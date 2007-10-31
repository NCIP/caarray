<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Sample" isSubtab="true"/>
    
    <c:url value="/protected/ajax/project/listTab/Sample/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.samples}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.samples.name" sortable="true">
                <caarray:projectListTabActionLink entityName="Sample" action="view" itemId="${row.id}" isSubtab="true">
                    <jsp:attribute name="linkRenderer">
                        <a href="${actionUrl}">${row.name}</a>
                    </jsp:attribute>
                </caarray:projectListTabActionLink>            
            </display:column>
            <display:column property="description" titleKey="experiment.samples.description" sortable="true" />
            <display:column property="organism.commonName" titleKey="experiment.samples.organism" sortable="true" />
            <display:column titleKey="experiment.samples.tissueSite">
                ${!empty row.tissueSite ? row.tissueSite.name : 'No Tissue Site'}
            </display:column>
            <display:column titleKey="experiment.samples.sources">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.sources}" relatedEntityName="Source" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.samples.extracts">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.edit">
                <caarray:projectListTabActionLink entityName="Sample" action="edit" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.download">
                <a href="<c:url value="/notYetImplemented.jsp" />"><img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>" /></a>
            </display:column>
            <display:column titleKey="button.copy">
                <caarray:projectListTabActionLink entityName="Sample" action="copy" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.delete">
                <caarray:projectListTabActionLink entityName="Sample" action="delete" itemId="${row.id}" isSubtab="true"/>            
            </display:column>
        </display:table>
    </ajax:displayTag>

    <caarray:projectListTabHiddenForm entityName="Sample" isSubtab="true"/>

    </div>
</caarray:tabPane>