<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Publication"/>

    <c:url value="/ajax/project/listTab/Publications/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.publications}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.publications.title" sortable="true" sortProperty="title">
                <caarray:projectListTabActionLink linkContent="${row.title}" entityName="Publication" action="view" itemId="${row.id}"/>
            </display:column>
            <display:column property="authors" titleKey="experiment.publications.authors" sortable="true" />
            <display:column property="uri" autolink="true" titleKey="experiment.publications.uri" sortable="true"/>
            <caarray:projectListTabActionColumns entityName="Publication" item="${row}" actions="!edit,!delete"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>