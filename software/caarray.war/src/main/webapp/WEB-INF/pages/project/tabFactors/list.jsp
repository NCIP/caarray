<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Factor" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Factors/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.factors}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.factors.name" sortable="true" sortProperty="name">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Factor" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column property="type.value" titleKey="experiment.factors.category" sortable="true" />
            <caarray:projectListTabActionColumns entityName="Factor" item="${row}" actions="!edit,!copy,!delete" isSubtab="true"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
