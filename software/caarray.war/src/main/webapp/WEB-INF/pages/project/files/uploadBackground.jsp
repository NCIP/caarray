<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<page:applyDecorator name="popup">
    <h1>Experiment Data Upload</h1>

    <jsp:useBean id="currentTime" class="java.util.Date"/>
    <c:set var="uploadId" value="${currentTime.time}"/>
    
    <c:url value="/ajax/uploadProgress.action" var="uploadProgressUrl">
        <c:param name="__multipart_upload_id" value="${uploadId}"/>
    </c:url>

     <c:url value="/project/details.action" var="projectDetailsUrl">
        <c:param name="project.id" value="${project.id}"/>
        <c:param name="initialTab" value="data" />
    </c:url>
 
    <iframe id='target_upload' name='target_upload' src='' style='display: none'> </iframe>
    <div id="uploadFileDiv" style="display: none">
        <form action="<c:url value="/project/files/upload.action/${uploadId}"/>" id="uploadForm" enctype="multipart/form-data" method="post" target="target_upload">
            <input type="hidden" name="project.id" value="${project.id}"/>
        </form>
    </div>

    <div class="padme">
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
    var progressBar = new Control.ProgressBar('uploadProgressBar', { afterChange: function(progress, active) { $('uploadPercent').innerHTML = progress; } });
    var pbPoller;
    var currentItemNumber = 1;
    var processingFinished = false;

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
            
    (function () {
        var myForm = $('uploadForm');
        var origFormFiles = $(window.opener.document.getElementById('uploadForm')).getElementsBySelector('input[type=file]');
        var progressTable = $('uploadProgressFileList');
        var fnameRegexp = new RegExp("[^/]+$");

        for (var i = 0; i < origFormFiles.length; i++) {
            if (origFormFiles[i].value.length > 0) {
                var fileCopy = origFormFiles[i].cloneNode(true);
                myForm.appendChild(fileCopy);
                var fileRow = progressTable.tBodies[0].insertRow(progressTable.tBodies[0].rows.length-1);          
                fileRow.insertCell(0).innerHTML = fileCopy.value.match(fnameRegexp)[0];          
                fileRow.insertCell(1);                    
            }
        }

        pbPoller = new PeriodicalExecuter(function() {
            new Ajax.Request('${uploadProgressUrl}', {
                onSuccess: function(request) {
                    var json = eval('(' + request.responseText + ')');
                    updateProgress(json.itemNumber, json.percentComplete);
                }
            });
         }, 2);

        window.onbeforeunload = confirmCloseWindow;
        
        $('uploadForm').submit();
    })();
</script>
</page:applyDecorator>
    