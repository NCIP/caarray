<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3><fmt:message key="experiment.sources" /></h3>
        <div class="addlink">
            <c:url value="/protected/ajax/project/loadGenericTab/sourceEdit.action" var="addSourceUrl">
                <c:param name="project.id" value="${project.id}" />
            </c:url>
            <ajax:anchors target="tabboxlevel2wrapper">
                <a href="${addSourceUrl}" class="add"><fmt:message key="experiment.sources.add" /></a>
            </ajax:anchors>
        </div>
    </div>

    <c:url value="/protected/ajax/project/loadGenericTab/sources.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.sources}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.sources.name" sortable="true"/>
            <display:column property="description" titleKey="experiment.sources.description" sortable="true" />
            <display:column property="organism.commonName" titleKey="experiment.sources.organism" sortable="true" />
            <display:column titleKey="experiment.sources.relatedSamples">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <a href="<c:url value="/ajax/notYetImplemented.jsp" />">view</a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.edit">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax/project/loadGenericTab/sourceEdit.action" var="editSourceUrl">
                        <c:param name="project.id" value="${project.id}" />
                        <c:param name="currentSource.id" value="${row.id}" />
                    </c:url>
                    <a href="${editSourceUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.copy">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax/project/copy/source.action" var="copySourceUrl">
                        <c:param name="project.id" value="${project.id}" />
                        <c:param name="currentSource.id" value="${row.id}" />
                    </c:url>
                    <a href="${copySourceUrl}"><img src="<c:url value="/images/ico_copy.gif"/>" alt="<fmt:message key="button.copy"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.delete">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax/project/remove/source.action" var="removeSourceUrl">
                        <c:param name="project.id" value="${project.id}" />
                        <c:param name="currentSource.id" value="${row.id}" />
                    </c:url>
                    <a href="${removeSourceUrl}"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
                </ajax:anchors>
            </display:column>
        </display:table>
    </ajax:displayTag>

    <s:form action="ajax/project/saveGenericTab/sources" cssClass="form" id="projectForm" theme="simple">
        <s:hidden name="project.id" />
    </s:form>
    </div>
</caarray:tabPane>
