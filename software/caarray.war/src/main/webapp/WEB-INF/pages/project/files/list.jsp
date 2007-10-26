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
</script>

<caarray:tabPane subtab="true">

    <caarray:successMessages />

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.manageData" /></h3>
        <div class="addlink">
            <a href="#" onclick="Element.show('uploadFileDiv');" class="add"><fmt:message key="experiment.data.upload" /></a>
        </div>
    </div>

    <div id="uploadFileDiv" style="display: none;" class="boxpad2">
        <s:form action="project/files/upload" id="uploadForm" enctype="multipart/form-data" method="post">
            <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
            <s:file id="upload" name="upload" label="File" />
        </s:form>
        <div class="actions">
            <a href="#" onclick="Effect.Fade('uploadFileDiv', { duration: 0.1 } );">Cancel</a>
            <a href="#" onclick="moreUploads();">Add More Files</a>
            <a href="#" onclick="document.getElementById('uploadForm').submit();">Upload</a>
        </div>
    </div>

    <div class="tableboxpad">
        <s:form action="protected/ajax/project/files/process" id="projectForm" method="post" theme="simple">
            <s:hidden name="project.id" value="${project.id}" />
            <%@ include file="/WEB-INF/pages/project/files/listTable.jsp" %>
        </s:form>
    </div>
    <div class="actionsthin">
        <a href="#" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '<c:url value="/protected/ajax/project/files/deleteFiles.action" />', 'tabboxlevel2wrapper');">
            <img src="<c:url value="/images/btn_delete.gif"/>" alt="Delete">
        </a>
        <a href="#" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '<c:url value="/protected/ajax/project/files/validateFiles.action" />', 'tabboxlevel2wrapper');">
            <img src="<c:url value="/images/btn_validate.gif"/>" alt="Validate">
        </a>
        <a href="#" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '<c:url value="/protected/ajax/project/files/importFiles.action" />', 'tabboxlevel2wrapper');">
            <img src="<c:url value="/images/btn_import.gif"/>" alt="Import">
        </a>
        <a href="#" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '<c:url value="/ajax/notYetImplemented.jsp" />', 'tabboxlevel2wrapper');">
            <img src="<c:url value="/images/btn_manage_associations.gif"/>" alt="Manage Associations">
        </a>
    </div>
</caarray:tabPane>
