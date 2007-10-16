<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3>Samples</h3>
        <div class="addlink">
            <c:url value="Project_addSample.action" var="addSampleUrl" />
            <a href="${addSampleUrl}" class="add">Add a new sample</a>
        </div>
    </div>

    <c:url var="sortUrl" value="/ajax/experiment/search/doSearch.action" />
    <c:url var="loadUrlBase" value="/experiment/management/load.action" />
    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" list="${samples}" requestURI="${sortUrl}" id="row" pagesize="20">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.samples.name" sortable="true"/>
            <display:column property="description" titleKey="experiment.samples.description" sortable="true" />
            <display:column property="organism.commonName" titleKey="experiment.samples.organism" sortable="true" />
            <display:column titleKey="experiment.samples.tissueSite">
                ${!empty row.tissueSite ? row.tissueSite.name : 'No Tissue Site'}
            </display:column>
            <display:column titleKey="experiment.samples.sources">
                <a href="#">mouse-11111</a>
            </display:column>
            <display:column titleKey="experiment.samples.extracts">
                <a href="#">view</a>
            </display:column>
            <display:column titleKey="button.edit">
                <a href="#"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
            </display:column>
            <display:column titleKey="button.download">
                <a href="#"><img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>" /></a>
            </display:column>
            <display:column titleKey="button.copy">
                <a href="#"><img src="<c:url value="/images/ico_copy.gif"/>" alt="<fmt:message key="button.copy"/>" /></a>
            </display:column>
            <display:column titleKey="button.delete">
                <a href="#"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
            </display:column>
        </display:table>
    </ajax:displayTag>
    <div class="tableboxpad">
</caarray:tabPane>
