<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/ajax/project/files/listUnimportedForm.action" var="listUnimportedFormUrl" />
<c:url value="/protected/ajax/project/files/uploadInBackground.action" var="uploadInBackgroundUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>

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

    doFilter = function() {
      Caarray.submitAjaxForm('selectFilesForm', 'unimportedForm', {url: '${listUnimportedFormUrl}'});
    }
    
    openUploadWindow = function() {
       window.open('${uploadInBackgroundUrl}', '_blank', "width=685,height=350,left=0,top=0,toolbar,scrollbars,resizable,status=yes");     
       $('uploadFileDiv').hide();
       $('uploadInProgressDiv').show();
    }
</script>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.unimportedFiles" /></h3>
        <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
            <div class="addlink">
                <fmt:message key="experiment.data.upload" var="uploadLabel" />
                <caarray:linkButton actionClass="add" text="${uploadLabel}" onclick="Element.show('uploadFileDiv');"/>
            </div>
        </c:if>
    </div>
    
    <div id="uploadFileDiv" style="display: none;">
        <div class="boxpad2extend">
            <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
                <s:form action="project/files/upload" id="uploadForm" namespace="" enctype="multipart/form-data" method="post">
                    <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
                    <s:file id="upload" name="upload" label="File" />
                </s:form>

                <caarray:actions>
                    <caarray:linkButton actionClass="cancel" text="Cancel" onclick="Effect.Fade('uploadFileDiv', { duration: 0.1 } );"/>
                    <caarray:linkButton actionClass="add" text="Add More Files" onclick="moreUploads();"/>
                    <caarray:linkButton actionClass="save" text="Upload" onclick="openUploadWindow()"/>
                </caarray:actions>
            </c:if>
        </div>
    </div>

    <div id="uploadInProgressDiv" style="display: none;">
        <fmt:message key="data.file.upload.inProgress"/>
    </div>    
    
  <div class="tableboxpad" id="unimportedForm">
      <%@ include file="/WEB-INF/pages/project/files/listUnimportedForm.jsp" %>
    </div>

    <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
        <caarray:actions divclass="actionsthin">
            <c:url value="/protected/ajax/project/files/deleteFiles.action" var="deleteUrl" />
            <caarray:linkButton actionClass="delete" text="Delete" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${deleteUrl}', 'tabboxlevel2wrapper');" />
            <c:url value="/protected/ajax/project/files/editFiles.action" var="editUrl" />
            <caarray:linkButton actionClass="edit" text="Change File Type" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${editUrl}', 'tabboxlevel2wrapper');" />
            <c:url value="/protected/ajax/project/files/validateFiles.action" var="validateUrl" />
            <caarray:linkButton actionClass="validate" text="Validate" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${validateUrl}', 'tabboxlevel2wrapper');" />
            <c:url value="/protected/ajax/project/files/importFiles.action" var="importUrl"/>
            <caarray:linkButton actionClass="import" text="Import" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${importUrl}', 'tabboxlevel2wrapper');" />
            <c:url value="/protected/ajax/project/files/addSupplementalFiles.action" var="supplementalUrl"/>
            <caarray:linkButton actionClass="import" text="Add Supplemental Files" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${supplementalUrl}', 'tabboxlevel2wrapper');" />
            <c:url value="/protected/ajax/project/files/listUnimported.action" var="unimportedUrl"/>
            <caarray:linkButton actionClass="import" text="Refresh Status" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${unimportedUrl}', 'tabboxlevel2wrapper');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>