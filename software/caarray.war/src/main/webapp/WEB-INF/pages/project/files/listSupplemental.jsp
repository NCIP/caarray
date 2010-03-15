<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <caarray:successMessages />

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.supplementalFiles" /></h3>
    </div>

    <div class="tableboxpad">
        <s:form action="protected/ajax/project/files/process" id="selectFilesForm" method="post" theme="simple">
            <s:hidden name="project.id" value="%{project.id}" />
            <%@ include file="/WEB-INF/pages/project/files/listTable.jsp" %>
        </s:form>
    </div>
    <c:if test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
        <caarray:actions divclass="actionsthin">
            <c:url value="/protected/ajax/project/files/deleteSupplementalFiles.action" var="deleteUrl" />
            <caarray:linkButton actionClass="delete" text="Delete" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${deleteUrl}', 'tabboxlevel2wrapper');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>