<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Hybridization" isSubtab="true"/>

    <c:url value="/protected/ajax/project/listTab/Hybridization/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.hybridizations}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.hybridizations.name" sortable="true">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Hybridization" action="view" itemId="${row.id}" isSubtab="true"/>
\            </display:column>
            <display:column titleKey="experiment.hybridizations.relatedLabeledExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.hybridizations.fileSize" sortable="true">
                TODO
            </display:column>
            <display:column titleKey="button.edit">
                <caarray:projectListTabActionLink entityName="Hybridization" action="edit" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="button.download">
                <a href="<c:url value="/notYetImplemented.jsp" />"><img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>" /></a>
            </display:column>
            <display:column titleKey="button.delete">
                <caarray:projectListTabActionLink entityName="Hybridization" action="delete" itemId="${row.id}" isSubtab="true"/>            
            </display:column>
        </display:table>
    </ajax:displayTag>

    <caarray:projectListTabHiddenForm entityName="Hybridization" isSubtab="true"/>
    </div>
</caarray:tabPane>
