<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Factor" isSubtab="true"/>

    <c:url value="/protected/ajax/project/listTab/Factors/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.factors}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.factors.name" sortable="true">
                <caarray:projectListTabActionLink entityName="Factor" action="view" itemId="${row.id}" isSubtab="true">
                    <jsp:attribute name="linkRenderer">
                        <a href="${actionUrl}">${row.name}</a>
                    </jsp:attribute>
                </caarray:projectListTabActionLink>            
            </display:column>
            <display:column property="type.category.name" titleKey="experiment.factors.category" sortable="true" />
            <display:column titleKey="button.edit">
                <caarray:projectListTabActionLink entityName="Factor" action="edit" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.copy">
                <caarray:projectListTabActionLink entityName="Factor" action="copy" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.delete">
                <caarray:projectListTabActionLink entityName="Factor" action="delete" itemId="${row.id}" isSubtab="true"/>            
            </display:column>
        </display:table>
    </ajax:displayTag>

    <caarray:projectListTabHiddenForm entityName="Factor" isSubtab="true"/>
    </div>
</caarray:tabPane>
