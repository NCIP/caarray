<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3><fmt:message key="experiment.samples" /></h3>
        <div class="addlink">
            <c:url value="/protected/ajax/project/loadGenericTab/sampleEdit.action" var="addSampleUrl">
                <c:param name="project.id" value="${project.id}" />
            </c:url>
            <ajax:anchors target="tabboxlevel2wrapper">
                <a href="${addSampleUrl}" class="add"><fmt:message key="experiment.samples.add" /></a>
            </ajax:anchors>
        </div>
    </div>

    <c:url value="/protected/ajax/project/loadGenericTab/samples.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.samples}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.samples.name" sortable="true"/>
            <display:column property="description" titleKey="experiment.samples.description" sortable="true" />
            <display:column property="organism.commonName" titleKey="experiment.samples.organism" sortable="true" />
            <display:column titleKey="experiment.samples.tissueSite">
                ${!empty row.tissueSite ? row.tissueSite.name : 'No Tissue Site'}
            </display:column>
            <display:column titleKey="experiment.samples.sources">
                <ajax:anchors target="tabboxlevel2wrapper">
                        <a href="<c:url value="/ajax/notYetImplemented.jsp" />">mouse-11111</a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="experiment.samples.extracts">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <a href="<c:url value="/ajax/notYetImplemented.jsp" />">view</a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.edit">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax/project/loadGenericTab/sampleEdit.action" var="editSampleUrl">
                        <c:param name="project.id" value="${project.id}" />
                        <c:param name="currentSample.id" value="${row.id}" />
                    </c:url>
                    <a href="${editSampleUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.download">
                <a href="<c:url value="/notYetImplemented.jsp" />"><img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>" /></a>
            </display:column>
            <display:column titleKey="button.copy">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax/project/copy/sample.action" var="copySampleUrl">
                        <c:param name="project.id" value="${project.id}" />
                        <c:param name="currentSample.id" value="${row.id}" />
                    </c:url>
                    <a href="${copySampleUrl}"><img src="<c:url value="/images/ico_copy.gif"/>" alt="<fmt:message key="button.copy"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.delete">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax/project/remove/sample.action" var="removeSampleUrl">
                        <c:param name="project.id" value="${project.id}" />
                        <c:param name="currentSample.id" value="${row.id}" />
                    </c:url>
                    <a href="${removeSampleUrl}"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
                </ajax:anchors>
            </display:column>
        </display:table>
    </ajax:displayTag>

    <s:form action="ajax/project/saveGenericTab/sources" cssClass="form" id="projectForm">
        <s:hidden name="project.id" />
    </s:form>

    </div>
</caarray:tabPane>