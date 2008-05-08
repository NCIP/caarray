<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.file.FileStatus"%>
<s:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</s:if>
<s:if test="${locked || !editMode}">
    <c:set var="lockedTheme" value="readonly"/>
</s:if>
<s:else>
    <c:set var="lockedTheme" value="xhtml"/>
</s:else>
<page:applyDecorator name="popup">
<script type="text/javascript">
    finalize = function() {
        if (isFilenameSupported()) {
            TabUtils.showSubmittingText();
            document.getElementById('arrayDesignForm').submit();
        } else {
            alert('<fmt:message key="arrayDesign.error.unsupportedFile"/>');
        }
    }

    unsupportedFilenames = new Array();
    <s:iterator id="file" value="@gov.nih.nci.caarray.domain.file.UnsupportedAffymetrixCdfFiles@values()">
        unsupportedFilenames.push('${file.filename}');
    </s:iterator>

    isFilenameSupported = function() {
        var filename = $('arrayDesignForm_upload').value;
        var indx = filename.lastIndexOf('/');
        if (indx < 0) {
            indx = filename.lastIndexOf('\\');
        }
        if (indx >= 0 && indx < filename.length - 1) {
            filename = filename.substring(indx + 1);
        }

        return unsupportedFilenames.indexOf(filename) == -1;
    }

</script>
<html>
<head>
    <title>Manage Array Designs</title>
</head>
<body>
    <h1>Manage Array Designs</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <span class="dark">
                        <s:if test="${empty arrayDesign.id}">
                            New Array Design
                        </s:if>
                        <s:else>
                            ${arrayDesign.name}
                        </s:else>
                    </span>
                </h3>
                <caarray:successMessages />
            </div>
            <div class="boxpad">
                <div id="submittingText" style="display: none;">
                    <div><img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" />
                    <fmt:message key="arraydesign.file.upload.inProgress" /></div>
                </div>
                <s:if test="${locked}">
                    <div class="instructions">
                       The file of this array design may not be modified because it is already associated with an existing experiment.
                       If uploading a zipped file, only the first entry in the zip will be processed as an array design.
                    </div>
                </s:if>
                <div id="theForm">
                    <s:form action="/protected/ajax/arrayDesign/save.action" onsubmit="TabUtils.showSubmittingText(); return true;" cssClass="form" enctype="multipart/form-data" method="post" id="arrayDesignForm">
                        <tbody>
                            <tr><th colspan="2">Upload Array Design File</th></tr>
                            <s:if test="${!empty arrayDesign.designFile}">
                                <s:textfield theme="readonly" key="arrayDesign.designFile.name" label="Current File"/>
                            </s:if>
                            <s:if test="${editMode && !locked}">
                                <s:file required="${empty arrayDesign.id}" name="upload" label="Browse to File" tabindex="9"/>
                                <s:select name="uploadFormatType" key="arrayDesign.designFile.fileType" tabindex="10"
                                          list="@gov.nih.nci.caarray.domain.file.FileType@getArrayDesignFileTypes()"
                                          listValue="%{getText('experiment.files.filetype.' + name)}"
                                          listKey="name" headerKey="" headerValue="Automatic"/>
                            </s:if>
                            <s:hidden name="arrayDesign.id"/>
                            <s:hidden name="arrayDesign.description"/>
                            <s:hidden name="arrayDesign.assayType"/>
                            <s:hidden name="arrayDesign.provider"/>
                            <s:hidden name="arrayDesign.version"/>
                            <s:hidden name="arrayDesign.technologyType"/>
                            <s:hidden name="arrayDesign.organism"/>
                            <s:hidden name="createMode"/>
                            <s:hidden name="editMode"/>
                        </tbody>
                        <input type="submit" class="enableEnterSubmit"/>
                    </s:form>
                    <caarray:actions>
                        <caarray:linkButton actionClass="cancel" text="Cancel" onclick="window.close()"/>
                        <c:set var="importingStatus" value="<%= FileStatus.IMPORTING.name() %>"/>
                         <s:if test="${arrayDesign.designFile.status != importingStatus}">
                            <caarray:action onclick="finalize();" actionClass="save" text="Save" tabindex="10"/>
                        </s:if>
                    </caarray:actions>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
</page:applyDecorator>
