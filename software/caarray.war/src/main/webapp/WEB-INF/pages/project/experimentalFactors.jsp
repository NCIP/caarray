<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3>Experimental Factors</h3>
        <div class="addlink">
            <c:url value="Project_addFactor.action" var="addFactorUrl" />
            <a href="${addFactorUrl}" class="add">Add a new factor</a>
        </div>
    </div>

    <c:url var="sortUrl" value="/ajax/experiment/search/doSearch.action" />
    <c:url var="loadUrlBase" value="/experiment/management/load.action" />
    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" list="${proposal.project.experiment.factors}" requestURI="${sortUrl}" id="row" pagesize="20">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.experimentalFactors.name" sortable="true"/>
            <display:column property="type.category.name" titleKey="experiment.experimentalFactors.category" sortable="true" />
            <display:column titleKey="experiment.samples.tissueSite">
                ${!empty row.tissueSite ? row.tissueSite.name : 'No Tissue Site'}
            </display:column>
            <display:column titleKey="button.edit">
                <a href="#"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
            </display:column>
            <display:column titleKey="button.copy">
                <a href="#"><img src="<c:url value="/images/ico_copy.gif"/>" alt="<fmt:message key="button.copy"/>" /></a>
            </display:column>
            <display:column titleKey="button.delete">
                <a href="#"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
            </display:column>
        </display:table>
    </ajax:displayTag>
    </div>
</caarray:tabPane>
