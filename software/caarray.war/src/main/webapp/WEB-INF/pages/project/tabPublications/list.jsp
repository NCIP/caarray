<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Publication"/>

    <c:url value="/protected/ajax/project/listTab/Publications/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.publications}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.publications.title" sortable="true">
                <caarray:projectListTabActionLink entityName="Publication" action="view" itemId="${row.id}" isSubtab="true">
                    <jsp:attribute name="linkRenderer">
                        <a href="${actionUrl}">${row.title}</a>
                    </jsp:attribute>
                </caarray:projectListTabActionLink>
            </display:column>
            <display:column property="authors" titleKey="experiment.publications.authors" sortable="true" />
            <display:column property="uri" titleKey="experiment.publications.uri" sortable="true" />
            <display:column titleKey="button.edit">
                <caarray:projectListTabActionLink entityName="Publication" itemId="${row.id}" action="edit"/>
            </display:column>
            <display:column titleKey="button.delete">
                <caarray:projectListTabActionLink entityName="Publication" itemId="${row.id}" action="delete"/>
            </display:column>
        </display:table>
    </ajax:displayTag>

    <s:form action="ajax/project/listTab/Publications/save" cssClass="form" id="projectForm">
        <s:hidden name="project.id" />
    </s:form>

    <caarray:projectListTabHiddenForm entityName="Publication"/>

    </div>
</caarray:tabPane>