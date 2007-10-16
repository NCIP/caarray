<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3>Sources</h3>
        <div class="addlink">
            <c:url value="/protected/ajax_Project_loadGenericTab_sourceEdit.action" var="addSourceUrl">
                <c:param name="proposalKey" value="${proposalKey}" />
                <c:param name="ajax" value="true" />
            </c:url>
            <ajax:anchors target="tabboxlevel2wrapper">
                <a href="${addSourceUrl}" class="add"><fmt:message key="experiment.sources.add" /></a>
            </ajax:anchors>
        </div>
    </div>

    <c:url value="ajax_Project_loadGenericTab_sources.action" var="sortUrl">
        <c:param name="proposalKey" value="${proposalKey}" />
        <c:param name="ajax" value="true" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${proposal.project.experiment.sources}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="proposalKey">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.sources.name" sortable="true"/>
            <display:column property="description" titleKey="experiment.sources.description" sortable="true" />
            <display:column property="organism.commonName" titleKey="experiment.sources.organism" sortable="true" />
            <display:column titleKey="experiment.sources.relatedSamples">
                <a href="#">view</a>
            </display:column>
            <display:column titleKey="button.edit">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <s:set name="index" value="proposal.project.experiment.sources.indexOf(#attr.row)" />
                    <c:url value="/protected/ajax_Project_loadGenericTab_sourceEdit.action" var="editSourceUrl">
                        <c:param name="proposalKey" value="${proposalKey}" />
                        <c:param name="currentSourceIndex" value="${index}" />
                        <c:param name="ajax" value="true" />
                    </c:url>
                <a href="${editSourceUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                </ajax:anchors>
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
