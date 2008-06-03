<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <caarray:successMessages />

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.importedFiles" /></h3>
    </div>

    <div class="tableboxpad" id="importedForm">
        <s:form action="/ajax/project/files/process" id="selectFilesForm" method="post" theme="simple">
            <s:hidden name="project.id" value="${project.id}" />
            <%@ include file="/WEB-INF/pages/project/files/listTable.jsp" %>
        </s:form>
    </div>
     <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
        <caarray:actions divclass="actionsthin">
            <c:url value="/protected/ajax/project/files/deleteImportedFiles.action" var="deleteUrl" />
            <caarray:linkButton actionClass="delete" text="Delete" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${deleteUrl}', 'tabboxlevel2wrapper');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>