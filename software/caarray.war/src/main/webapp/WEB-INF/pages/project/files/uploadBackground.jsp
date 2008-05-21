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
                    <s:file id="upload0" name="upload" label="File" onchange="setCheckboxVal(this.value, this.id)">
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
    var lastItemNumber = 0;
    var lastCalcItemNumber = 0;

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

    function setCheckboxVal(name, id_name) {
        var index = id_name.substring(6);
        var match = name.match('\.zip$');
        $('checkbox' + index).disabled = !match;
        $('checkbox' + index).checked = match;

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
        // itemNumber - is the input item being processed at the time by
        // struts. only those uploads that take an extended period of
        // time to process will be sent to this method. the total
        // itemNumber reflects both file inputs and other inputs from the
        // page, including hidden fields and checkboxes.

        // if itemNumber is less than 2 it means that no files were selected to upload
        if (itemNumber < 2) {
            return;
        }

        // lastItemNumber - is the itemNumber param sent into this
        // method when executed last. set to 0 by page default

        // this will be the determined row number of the
        // upload progress table. while itemNumber reflects the
        // items position in the overall list of input fields
        // on the page, the calcItemNumber reflects the position
        // within the ordered list of files being uploaded.
        var calcItemNumber = 0;

        // lastCalcItemNumber - is the calcItemNumber determined the
        // last time this method ran. set to 0 by page default

        progressBar.setProgress(percentComplete);

        // steps to calculate the calcItemNumber:
        if (itemNumber == lastItemNumber) {
            // if the itemNumber passed in, is identical to that
            // which was passed in the last time this method ran,
            // it means that the same file is still being uploaded by struts.
            calcItemNumber = lastCalcItemNumber;
        } else {
            // we must determine the relative position in the list of uploads
            // and prune out the checkbox inputs associated with each file.
            // we determine the relative row of items by subtracting the previous
            // item number from the current item number. since the items are arranged
            // as checkbox array item, file input item, and checkbox input item we must
            // divide by half and round up to the nearest integer to determine the
            // position of the file input item.
            // if this is the first path through this method then this is
            // the absolute position in the list of upload file progress.
            calcItemNumber = Math.round((itemNumber - lastItemNumber) / 2);

            // if this is NOT the first time we enter this method
            // we need to find the absolute position in the list of uploads
            // by adding the relative position to the previous absolute position.
            // we must also subtract 1 so that we do not count the same position twice.
            if (lastItemNumber > 0) {
                 calcItemNumber += lastCalcItemNumber - 1;
            }
        }

        // record for future iterations
        lastItemNumber = itemNumber;
        lastCalcItemNumber = calcItemNumber;

        // since we are processing these files in order, we can assume that all files previous
        // to this iteration have finished uploading.
        while (calcItemNumber - 1 != currentItemNumber) {
            $('uploadProgressFileList').tBodies[0].rows[currentItemNumber - 1].cells[1].innerHTML = "Done";
            currentItemNumber++;
        }

        // automatically set status to In Progress
        $('uploadProgressFileList').tBodies[0].rows[currentItemNumber - 1].cells[1].innerHTML = "In Progress";

        // if the upload is completed, set to Done
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
