<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Sample" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Samples/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.samples}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.samples.name" sortable="true" sortProperty="name">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Sample" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="description" titleKey="experiment.samples.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentSample.materialType" sortable="true" />
            <display:column titleKey="experiment.samples.sources">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.sources}" relatedEntityName="Source" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.samples.extracts">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Sample" item="${row}" actions="!edit,!copy,!delete" isSubtab="true"/>
            <display:column titleKey="button.download">
        <c:url value="/ajax/project/listTab/Samples/download.action" var="actionUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="currentSample.id" value="${row.id}" />
            <c:param name="editMode" value="${editMode}" />
        </c:url>
            <a href="${actionUrl}">
                <img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>">
            </a>
            </display:column>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>