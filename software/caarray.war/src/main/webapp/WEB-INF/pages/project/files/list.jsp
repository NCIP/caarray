<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    moreUploads = function() {
        formTable = $('uploadFileDiv').getElementsByTagName('table')[0];
        newRow = formTable.rows[0].cloneNode(true);
        newFile = newRow.getElementsByTagName('input')[0];
        newFile.value = '';
        newFile.id = '';

        formTableTbody = formTable.getElementsByTagName('tbody')[0];
        formTableTbody.appendChild(newRow);
    }
    selectAll = function(selectAllBox, theform) {
       var state = selectAllBox.checked;
        for (i = 0; i < theform.elements.length; i++) {
            var element = theform.elements[i];
            if ("checkbox" == element.type) {
                element.checked = state;
            }
        }
    }
    setExperimentTitleHeader('${project.experiment.title}');
</script>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <caarray:successMessages />

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.manageData" /></h3>
        <div class="addlink">
            <fmt:message key="experiment.data.upload" var="uploadLabel" />
            <caarray:linkButton actionClass="add" text="${uploadLabel}" onclick="Element.show('uploadFileDiv');"/>
        </div>
    </div>

    <div id="uploadFileDiv" style="display: none;">
        <div class="boxpad2extend">
            <s:form action="project/files/upload" id="uploadForm" enctype="multipart/form-data" method="post">
                <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
                <s:file id="upload" name="upload" label="File" />
            </s:form>
            <caarray:actions>
                <caarray:linkButton actionClass="cancel" text="Cancel" onclick="Effect.Fade('uploadFileDiv', { duration: 0.1 } );"/>
                <caarray:linkButton actionClass="add" text="Add More Files" onclick="moreUploads();"/>
                <caarray:linkButton actionClass="save" text="Upload" onclick="TabUtils.showSubtabSubmittingText(); document.getElementById('uploadForm').submit();"/>
            </caarray:actions>
        </div>
    </div>

    <div class="tableboxpad">
        <s:form action="protected/ajax/project/files/process" id="selectFilesForm" method="post" theme="simple">
            <s:hidden name="project.id" value="${project.id}" />
            <%@ include file="/WEB-INF/pages/project/files/listTable.jsp" %>
        </s:form>
        <s:form action="ajax/project/listTab/Sources/saveList" cssClass="form" id="projectForm" theme="simple">
            <s:hidden name="project.id" />
        </s:form>
    </div>
    <caarray:actions divclass="actionsthin">
        <c:url value="/protected/ajax/project/files/deleteFiles.action" var="deleteUrl" />
        <caarray:linkButton actionClass="delete" text="Delete" onclick="TabUtils.submitSubTabFormToUrl('selectFilesForm', '${deleteUrl}', 'tabboxlevel2wrapper');" />
        <c:url value="/protected/ajax/project/files/validateFiles.action" var="validateUrl" />
        <caarray:linkButton actionClass="validate" text="Validate" onclick="TabUtils.submitSubTabFormToUrl('selectFilesForm', '${validateUrl}', 'tabboxlevel2wrapper');" />
        <c:url value="/protected/ajax/project/files/importFiles.action" var="importUrl"/>
        <caarray:linkButton actionClass="import" text="Import" onclick="TabUtils.submitSubTabFormToUrl('selectFilesForm', '${importUrl}', 'tabboxlevel2wrapper');" />
        <c:url value="/ajax/notYetImplemented.jsp" var="associationsUrl" />
        <caarray:linkButton actionClass="manage_associations" text="Manage Associations" onclick="TabUtils.submitSubTabFormToUrl('selectFilesForm', '${associationsUrl}', 'tabboxlevel2wrapper');" />
    </caarray:actions>
</caarray:tabPane>