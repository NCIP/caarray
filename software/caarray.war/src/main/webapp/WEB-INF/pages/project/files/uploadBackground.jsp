<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<page:applyDecorator name="popup">
    <html>
        <head>
            <title>Experiment Data Upload</title>
        </head>
        <body>

    <h1>Experiment Data Upload</h1>

    <jsp:useBean id="currentTime" class="java.util.Date"/>
    <c:set var="uploadId" value="${currentTime.time}"/>

    <c:url value="/project/files/upload.action/${uploadId}" var="uploadActionUrl"/>
    <c:url value="/ajax/uploadProgress.action" var="uploadProgressUrl">
        <c:param name="__multipart_upload_id" value="${uploadId}"/>
    </c:url>
     <c:url value="/project/details.action" var="projectDetailsUrl">
        <c:param name="project.id" value="${project.id}"/>
        <c:param name="initialTab" value="data" />
    </c:url>

    <iframe id='target_upload' name='target_upload' src='' style='display: none'> </iframe>
    <div id="uploadFileDiv">
        <div class="boxpad2extend">
            <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
                <s:form action="project/files/upload" id="uploadForm" namespace="" enctype="multipart/form-data" method="post"  target="target_upload">
                    <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
                    <input type=hidden name="selectedFilesToUnpack" value="-1" />
                    <s:file id="upload0" name="upload" label="File" onchange="setCheckboxVal(this.value)">
                         <s:param name="after">
                             <s:checkbox id="checkbox0" name="selectedFilesToUnpack" fieldValue="0" value="false" theme="simple"/>
                             <s:label for="uploadForm_selectedFilesToUnpack" value="Unpack Compressed Archive" theme="simple"/>
                         </s:param>
                    </s:file>
                </s:form>

                <caarray:actions>
                    <caarray:linkButton actionClass="cancel" text="Cancel" onclick="window.close()"/>
                    <caarray:linkButton actionClass="add" text="Add More Files" onclick="moreUploads();"/>
                    <caarray:linkButton actionClass="save" text="Upload" onclick="beginUpload()"/>
                </caarray:actions>
            </c:if>
        </div>
    </div>

    <div class="padme" id="uploadProgress" style="display: none">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <span class="dark">Experiment:</span> ${project.experiment.title}
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

               <div id="closeWindow" style="display: none">
                    <caarray:actions>
                        <caarray:action actionClass="cancel" text="Close Window" onclick="window.close()" />
                        <caarray:action actionClass="import" text="Close Window and go to Experiment Data" onclick="closeAndGoToProjectData()" />
                    </caarray:actions>
               </div>
           </div>
       </div>
   </div>

<script type="text/javascript">
    var progressBar;
    var pbPoller;
    var currentItemNumber = 1;
    var processingFinished = false;
    var fileCount =1;

   function moreUploads() {
        formTable = $('uploadFileDiv').getElementsByTagName('table')[0];
        newRow = $(formTable.rows[0].cloneNode(true));
        newFile = newRow.down('input');
        newFile.value = '';
        newFile.id = 'upload'+fileCount;
        newCheckbox = newFile.next('input');
        newCheckbox.value = fileCount;
        newCheckbox.checked=false;
        newCheckbox.id = 'checkbox'+fileCount;
        formTable.tBodies[0].appendChild(newRow);
        fileCount++;
    }

    function setCheckboxVal(name) {
        if (name.match('\.zip$')) {
            $('checkbox' + (fileCount - 1)).disabled = false;
            $('checkbox' + (fileCount - 1)).checked = true;
        } else {
            $('checkbox' + (fileCount - 1)).disabled = true;
        }
    }

    function closeAndGoToProjectData() {
        window.opener.location = '${projectDetailsUrl}';
        window.close();
    }

    function confirmCloseWindow() {
        if (!processingFinished) {
            return "Your files are currently uploading. Closing this window now could result in the upload being aborted.";
        }
    }

    function updateProgress(itemNumber, percentComplete) {
        if (itemNumber < 2) { return; }
        progressBar.setProgress(percentComplete);
        while (itemNumber - 1 != currentItemNumber) {
            $('uploadProgressFileList').tBodies[0].rows[currentItemNumber - 1].cells[1].innerHTML = "Done";
            currentItemNumber++;
        }
        $('uploadProgressFileList').tBodies[0].rows[currentItemNumber - 1].cells[1].innerHTML = "In Progress";
        if (percentComplete == 100) {
          $('uploadProgressFileList').tBodies[0].rows[currentItemNumber - 1].cells[1].innerHTML = "Done";
          if (!processingFinished) {
              $('uploadingMessage').innerHTML = "Your file(s) have finished uploading and are now being processed by the server. Please continue to leave this window open until this is complete.";
              new Effect.Highlight('uploadingMessage', { duration: 5.0 });
          }
          pbPoller.stop();
        }
    }

    function uploadFinished(messages) {
        processingFinished = true;
        $("uploadingMessage").innerHTML = messages;
        $("uploadProgressFileList").style.display = "none";
        $("closeWindow").style.display = "";
        alert('Your file upload is complete');
    }

    function beginUpload() {
        $('uploadForm').action='${uploadActionUrl}';
        $('uploadFileDiv').hide();
        initializeProgressDisplay();
        $('uploadForm').submit();
        window.onbeforeunload = confirmCloseWindow;
    }


    function initializeProgressDisplay() {
        var myForm = $('uploadForm');
        var formFiles = myForm.getElementsBySelector('input[type=file]');
        var progressTable = $('uploadProgressFileList');
        var fnameRegexp = new RegExp("[^/\\\\]+$");

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
    </body>
</html>
</page:applyDecorator>
