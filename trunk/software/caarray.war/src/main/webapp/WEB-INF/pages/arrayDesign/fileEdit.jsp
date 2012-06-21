<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.file.FileStatus"%>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>


<c:choose>
    <c:when test="${locked || !editMode}">
        <c:set var="lockedTheme" value="readonly"/>
    </c:when>
    <c:otherwise>
        <c:set var="lockedTheme" value="xhtml"/>
    </c:otherwise>
</c:choose>

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
    var fileCount =1;
    var currentItemNumber = 2;
    var rowNumber = 1;

    unsupportedFilenames = new Array();
    <s:iterator id="file" value="@gov.nih.nci.caarray.domain.file.UnsupportedAffymetrixCdfFiles@values()">
        unsupportedFilenames.push('${file.filename}');
    </s:iterator>

    isFilenameSupported = function() {
    	 var myForm = $('arrayDesignForm');
         var myFiles = myForm.getElementsBySelector('input[type=file]');
         for (var i = 0; i < myFiles.length; i++) {
            var filename = myFiles[i].value;
	        var indx = filename.lastIndexOf('/');
    	    if (indx < 0) {
        	    indx = filename.lastIndexOf('\\');
        	}
        	if (indx >= 0 && indx < filename.length - 1) {
            	filename = filename.substring(indx + 1);
        	}

        	if (unsupportedFilenames.indexOf(filename) != -1) {
        		return false;
        	}
		}

		return true;
    }



    function updateProgress(itemNumber, percentComplete) {
        if (itemNumber < 10) { return; }
        progressBar.setProgress(percentComplete);

       while (itemNumber - 8 != currentItemNumber) {
            $('uploadProgressFileList').tBodies[0].rows[rowNumber].cells[1].innerHTML = "Done";
            currentItemNumber = currentItemNumber+2;
            rowNumber++;
        }

        $('uploadProgressFileList').tBodies[0].rows[rowNumber].cells[1].innerHTML = "In Progress";
        if (percentComplete == 100) {
          $('uploadProgressFileList').tBodies[0].rows[rowNumber].cells[1].innerHTML = "Done";
          if (!processingFinished) {
              $('uploadingMessage').innerHTML = "Your file(s) have finished uploading and are now being processed by the server. Please continue to leave this window open until this is complete.";
              new Effect.Highlight('uploadingMessage', { duration: 5.0 });
          }
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

    function moreUploads(offset) {
        formTable = $('arrayFileDiv').getElementsByTagName('table')[0];

        newRow = $(formTable.rows[offset].cloneNode(true));
        newFile = newRow.down('input');
        if (newFile == null ) {
        	moreUploads(offset+1);
        	return;
        }
        newFile.value = '';
        newFile.id = 'upload'+fileCount;

        newSelectRow = $(formTable.rows[offset+1].cloneNode(true));
        newSelectbox = newSelectRow.down('select');
        newSelectbox.value = '';
        newSelectbox.id = 'fileFormatType'+fileCount;

        formTable.tBodies[0].appendChild(newRow);
        formTable.tBodies[0].appendChild(newSelectRow);

        fileCount++;
    }


</script>
<html>
<head>
<s:set var="fileTypes" value=""/>
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
                        <c:choose>
                            <c:when test="${empty arrayDesign.id}">
                                New Array Design (Step 2)
                            </c:when>
                            <c:otherwise>
                                ${arrayDesign.name}
                            </c:otherwise>
                        </c:choose>
                    </span>
                </h3>
                <caarray:successMessages />
            </div>
            <div class="boxpad">
                <div id="submittingText" style="display: none;">
                    <div><img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" />
                    <fmt:message key="arraydesign.file.upload.inProgress" /></div>
                </div>
                <c:if test="${locked}">
                    <div class="instructions">
                       The file of this array design may not be modified because it is already associated with an existing experiment.
                       If uploading a zipped file, only the first entry in the zip will be processed as an array design.
                    </div>
                </c:if>

                <div id="arrayFileDiv">
                    <s:form action="/protected/ajax/arrayDesign/save" cssClass="form" enctype="multipart/form-data" method="post" id="arrayDesignForm">
                        <s:token/>
                        <tbody>
                            <tr><th colspan="2">Upload Array Design File</th></tr>
                            <c:if test="${!empty arrayDesign.designFiles}">
                                <s:select theme="readonly" list="arrayDesign.designFiles" value="arrayDesign.designFiles" listValue="name" label="Current File" multiple="true"/>
                            </c:if>
							<s:hidden name="arrayDesign.id"/>
	                        <s:hidden name="arrayDesign.description"/>
	                        <s:hidden name="arrayDesign.assayType"/>
	                        <s:hidden name="arrayDesign.provider"/>
	                        <s:hidden name="arrayDesign.version"/>
                            <s:hidden name="arrayDesign.geoAccession"/>
	                        <s:hidden name="arrayDesign.technologyType"/>
	                        <s:hidden name="arrayDesign.organism"/>
	                        <s:hidden name="createMode"/>
	                        <s:hidden name="editMode"/>
                            <s:hidden name="arrayDesign.assayTypes"/>
                            <c:forEach items="${arrayDesign.assayTypes}" var="currAssayType">
                                <input name="arrayDesign.assayTypes" type="hidden" value="${currAssayType.id}"/>
                            </c:forEach>
                            <c:if test="${editMode && !locked}">
                                <s:file id="upload0" required="%{arrayDesign.id != null}" name="upload" label="Browse to File" tabindex="9"/>
                                <s:select id="fileFormatType0" name="fileFormatType" key="arrayDesign.designFile.fileType" tabindex="10"
                                          list="%{arrayDesignTypes}"
                                          listValue="%{getText('experiment.files.filetype.' + name)}"
                                          listKey="name" headerKey="" headerValue="Automatic"/>                                
                            </c:if>
                        </tbody>
                        <input type="submit" class="enableEnterSubmit"/>
                    </s:form>
                    <caarray:actions>
                        <caarray:linkButton actionClass="cancel" text="Cancel" onclick="window.close()"/>
                        <c:set var="importingStatus" value="<%= FileStatus.IMPORTING.name() %>"/>
                         <c:if test="${arrayDesign.designFileSet.status != importingStatus && !locked}">
                            <caarray:action onclick="beginUpload();" actionClass="save" text="Save" tabindex="10"/>
                            <c:if test="${editMode && !locked}">
                            	<caarray:linkButton actionClass="add" text="Add More Files" onclick="moreUploads(1);"/>
                            </c:if>
                        </c:if>
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
