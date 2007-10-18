<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3><fmt:message key="experiment.samples" /></h3>
        <div class="addlink">
            <c:url value="/protected/ajax_Project_loadGenericTab_sampleEdit.action" var="addSampleUrl">
                <c:param name="proposalKey" value="${proposalKey}" />
                <c:param name="ajax" value="true" />
            </c:url>
            <ajax:anchors target="tabboxlevel2wrapper">
                <a href="${addSampleUrl}" class="add"><fmt:message key="experiment.samples.add" /></a>
            </ajax:anchors>
        </div>
    </div>

    <c:url value="ajax_Project_loadGenericTab_samples.action" var="sortUrl">
        <c:param name="proposalKey" value="${proposalKey}" />
        <c:param name="ajax" value="true" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${proposal.project.experiment.samples}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="proposalKey">
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
                <ajax:anchors target="tabboxlevel2wrapper">
                    <s:set name="index" value="proposal.project.experiment.samples.indexOf(#attr.row)" />
                    <c:url value="/protected/ajax_Project_loadGenericTab_sampleEdit.action" var="editSampleUrl">
                        <c:param name="proposalKey" value="${proposalKey}" />
                        <c:param name="currentSampleIndex" value="${index}" />
                        <c:param name="ajax" value="true" />
                    </c:url>
                    <a href="${editSampleUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.download">
                <a href="#"><img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>" /></a>
            </display:column>
            <display:column titleKey="button.copy">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <s:set name="index" value="proposal.project.experiment.samples.indexOf(#attr.row)" />
                    <c:url value="/protected/ajax_Project_copy_sample.action" var="copySampleUrl">
                        <c:param name="proposalKey" value="${proposalKey}" />
                        <c:param name="currentSampleIndex" value="${index}" />
                        <c:param name="ajax" value="true" />
                    </c:url>
                    <a href="${copySampleUrl}"><img src="<c:url value="/images/ico_copy.gif"/>" alt="<fmt:message key="button.copy"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.delete">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <s:set name="index" value="proposal.project.experiment.samples.indexOf(#attr.row)" />
                    <c:url value="/protected/ajax_Project_remove_sample.action" var="removeSampleUrl">
                        <c:param name="proposalKey" value="${proposalKey}" />
                        <c:param name="currentSampleIndex" value="${index}" />
                        <c:param name="ajax" value="true" />
                    </c:url>
                    <a href="${removeSampleUrl}"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
                </ajax:anchors>
            </display:column>
        </display:table>
    </ajax:displayTag>

    <s:form action="ajax_Project_saveGenericTab_sources" cssClass="form" id="projectForm" method="get">
        <s:hidden name="proposalKey" />
        <s:hidden name="ajax" value="%{'true'}"/>
    </s:form>

    </div>
</caarray:tabPane>