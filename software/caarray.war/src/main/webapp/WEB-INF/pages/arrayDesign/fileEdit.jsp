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

<jsp:useBean id="currentTime" class="java.util.Date"/>
<c:set var="uploadId" value="${currentTime.time}"/>
<c:url value="/ajax/uploadProgress.action" var="uploadProgressUrl">
        <c:param name="__multipart_upload_id" value="${uploadId}"/>
</c:url>
<c:url value="/protected/ajax/arrayDesign/save.action/${uploadId}" var="uploadActionUrl"/>
<page:applyDecorator name="popup">
<script type="text/javascript">
    var progressBar;
    var pbPoller;

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


    function updateProgress(itemNumber, percentComplete) {
        var progressTable = $('uploadProgressFileList');
        var fileRow = progressTable.tBodies[0].rows[progressTable.tBodies[0].rows.length-2];
        progressBar.setProgress(percentComplete);
        fileRow.cells[1].innerHTML = "In Progress";

        // if the upload is completed, set to Done
        if (percentComplete == 100) {
          fileRow.cells[1].innerHTML = "Done";
          $('uploadingMessage').innerHTML = "Your file(s) have finished uploading and are now being processed by the server. Please continue to leave this window open until this is complete.";
              new Effect.Highlight('uploadingMessage', { duration: 5.0 });
          pbPoller.stop();
        }
    }

    function beginUpload() {

      if (isFilenameSupported()) {
          $('arrayDesignForm').action='${uploadActionUrl}';
          $('uploadFileDiv').hide();
          initializeProgressDisplay();
          $('arrayDesignForm').submit();
          window.onbeforeunload = confirmCloseWindow;
        } else {
            alert('<fmt:message key="arrayDesign.error.unsupportedFile"/>');
        }

    }

    function confirmCloseWindow() {
       return "Your files are currently uploading. Closing this window now could result in the upload being aborted.";
    }

    function initializeProgressDisplay() {
        var myForm = $('arrayDesignForm');
        var progressTable = $('uploadProgressFileList');
        var formFiles = myForm.getElementsBySelector('input[type=file]');
        var fnameRegexp = new RegExp("[^/\\\\]+$");
        var fileRow = progressTable.tBodies[0].insertRow(progressTable.tBodies[0].rows.length-1);

        for (var i = 0; i < formFiles.length; i++) {
            if (formFiles[i].value.length > 0) {
                var fileRow = progressTable.tBodies[0].insertRow(progressTable.tBodies[0].rows.length-1);
                fileRow.insertCell(0).innerHTML = formFiles[i].value.match(fnameRegexp)[0];
                fileRow.insertCell(1);
            } else {
                formFiles[i].disabled=true;
            }
        }

        $('uploadProgress').show();

        progressBar = new Control.ProgressBar('uploadProgressBar', { afterChange: function(progress, active) { $('uploadPercent').innerHTML = progress; } })
        pbPoller = new PeriodicalExecuter(function() {
            new Ajax.Request('${uploadProgressUrl}', {
                onSuccess: function(request) {
                    var json = eval('(' + request.responseText + ')');
                     updateProgress(json.itemNumber, json.percentComplete);
                }
            });
         }, 2);
    };


</script>
<html>
<head>
    <title>Manage Array Designs</title>
</head>
<body>
    <h1>Manage Array Designs</h1>
    <caarray:helpPrint/>

    <div class="padme" id="uploadFileDiv">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <span class="dark">
                        <s:if test="${empty arrayDesign.id}">
                            New Array Design (Step 2)
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

                <div>
                    <s:form action="/protected/ajax/arrayDesign/save" cssClass="form" enctype="multipart/form-data" method="post" id="arrayDesignForm">
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
                            <s:if test="${!empty arrayDesign.designFile}">
                            	<s:hidden name="arrayDesign.id"/>
                            </s:if>
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
                         <s:if test="${arrayDesign.designFile.status != importingStatus && !locked}">
                            <caarray:action onclick="beginUpload();" actionClass="save" text="Save" tabindex="10"/>
                        </s:if>
                    </caarray:actions>
                </div>
            </div>
        </div>
    </div>

    <div class="padme" id="uploadProgress" style="display: none">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <span class="dark">File Upload</span>
                </h3>
            </div>

            <div class="boxpad">
               <div id="uploadingMessage">
                  <fmt:message key="data.file.upload.inProgress"/>
               </div>

               <table id="uploadProgressFileList" class="searchresults">
                  <tbody>
                      <tr>
                          <td><span class="dark">Overall progress</span></td>
                          <td><div style="float: right"><span id="uploadPercent">0</span>%</div><div id="uploadProgressBar"></div></td>
                      </tr>
                  </tbody>
               </table>
           </div>
       </div>
   </div>
</body>
</html>
</page:applyDecorator>