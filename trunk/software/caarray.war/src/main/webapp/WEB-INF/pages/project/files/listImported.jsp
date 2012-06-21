<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/project/files/listImportedForm.action" var="listImportedFormUrl" />
<script type="text/javascript">
    importedFilterCallBack = function() {
        TabUtils.hideLoadingText();
    }
    doImportedFilter = function() {
        var checkboxIds = $('selectFilesForm').__checkbox_selectedFileIds || {};
        TabUtils.disableFormCheckboxes(checkboxIds);
        TabUtils.showLoadingTextKeepMainContent();
        Caarray.submitAjaxForm('selectFilesForm', 'importedForm', {url: '${listImportedFormUrl}', onComplete: importedFilterCallBack});
    }
</script>
<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <caarray:successMessages />

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.importedFiles" /></h3>
    </div>
    <div class="tableboxpad" id="importedForm">
            <%@ include file="/WEB-INF/pages/project/files/listImportedForm.jsp" %>
    </div>

     <c:if test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
        <caarray:actions divclass="actionsthin">
            <c:url value="/protected/ajax/project/files/deleteImportedFiles.action" var="deleteUrl" />
            <caarray:linkButton actionClass="delete" text="Delete" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${deleteUrl}', 'tabboxlevel2wrapper');" />
            <c:url value="/protected/ajax/project/files/reparseFiles.action" var="reparseUrl" />
            <caarray:linkButton actionClass="import" text="Reparse" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${reparseUrl}', 'tabboxlevel2wrapper');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>