<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<page:applyDecorator name="upload">
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
            <p>Limitation: The size of each file you upload must be less than 2 GB in Internet Explorer.</p>
            <c:if test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
                <s:form action="project/files/upload" id="fileupload" namespace="" enctype="multipart/form-data" method="post"  target="target_upload">
                    <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
                    <input type=hidden name="selectedFilesToUnpack" value="-1" />
                    <div class="fileupload-loading"></div><br>
                    <table class="table table-striped"><tbody class="files" data-toggle="modal-gallery" data-target="#modal-gallery"></tbody></table>
                    <caarray:actions>
                        <div class="row fileupload-buttonbar">
                            <div class="span7">
                                <span class="btn btn-success fileinput-button"><span><i class="icon-plus icon-white"></i>Add Files</span><input name="files[]" multiple="" type="file"></span>
                                <button type="submit" class="btn btn-primary start"><i class="icon-upload icon-white"></i>Upload</button>
                                <button type="reset" class="btn btn-warning cancel"><i class="icon-ban-circle icon-white"></i>Cancel</button>
                                <input class="toggle" type="checkbox">
                            </div>
                            <div class="span5"><div class="progress progress-success progress-striped active fade"><div class="bar" style="width:0%;"></div></div></div>
                        </div>
                    </caarray:actions>
                </s:form>
            </c:if>
        </div>
    </div>
  <!--
    <form id="fileupload" action="server/php/" method="POST" enctype="multipart/form-data">
        <div class="row fileupload-buttonbar">
            <div class="span7">
                <span class="btn btn-success fileinput-button"><span><i class="icon-plus icon-white"></i>Add Files</span><input name="files[]" multiple="" type="file"></span>
                <button type="submit" class="btn btn-primary start"><i class="icon-upload icon-white"></i>Upload</button>
                <button type="reset" class="btn btn-warning cancel"><i class="icon-ban-circle icon-white"></i>Cancel</button>
                <input class="toggle" type="checkbox">
            </div>
            <div class="span5"><div class="progress progress-success progress-striped active fade"><div class="bar" style="width:0%;"></div></div></div>
        </div>
        <div class="fileupload-loading"></div><br>
        <table class="table table-striped"><tbody class="files" data-toggle="modal-gallery" data-target="#modal-gallery"></tbody></table>
    </form>

-->


<script>
var fileUploadErrors = {
    maxFileSize: 'File is too big',
    minFileSize: 'File is too small',
    acceptFileTypes: 'Filetype not allowed',
    maxNumberOfFiles: 'Max number of files exceeded',
    uploadedBytes: 'Uploaded bytes exceed file size',
    emptyResult: 'Empty file upload result'
};
</script>

<script id="template-upload" type="text/html">
{% for (var i=0, files=o.files, l=files.length, file=files[0]; i<l; file=files[++i]) { %}
    <tr class="template-upload fade">
        <td class="preview"><span class="fade"></span></td>
        <td class="name">{%=file.name%}</td>
        <td class="size">{%=o.formatFileSize(file.size)%}</td>
        {% if (file.error) { %}
            <td class="error" colspan="2"><span class="label important">Error</span> {%=fileUploadErrors[file.error] || file.error%}</td>
        {% } else if (o.files.valid && !i) { %}
            <td class="progress"><div class="progressbar"><div style="width:0%;"></div></div></td>
            <td class="start" style="width: 2%">{% if (!o.options.autoUpload) { %}<button class="btn primary">Start</button>{% } %}</td>
        {% } else { %}
            <td colspan="2"></td>
        {% } %}
        <td class="cancel" style="width: 7em;">{% if (!i) { %}<button class="btn info">Cancel</button>{% } %}</td>
        <td style="width: 1%">&nbsp;</td>
    </tr>
{% } %}
</script>

<script id="template-download" type="text/html">
{% for (var i=0, files=o.files, l=files.length, file=files[0]; i<l; file=files[++i]) { %}
    <tr class="template-download fade">
        {% if (file.error) { %}
            <td></td>
            <td class="name">{%=file.name%}</td>
            <td class="size">{%=o.formatFileSize(file.size)%}</td>
            <td class="error" colspan="2"><span class="label important">Error</span> {%=fileUploadErrors[file.error] || file.error%}</td>
        {% } else { %}
            <td class="preview">{% if (file.thumbnail_url) { %}
                <a href="{%=file.url%}" title="{%=file.name%}" rel="gallery"><img src="{%=file.thumbnail_url%}"></a>
            {% } %}</td>
            <td class="name">
                <a href="{%=file.url%}" title="{%=file.name%}" rel="{%=file.thumbnail_url&&'gallery'%}">{%=file.name%}</a>
            </td>
            <td class="size">{%=o.formatFileSize(file.size)%}</td>
            <td colspan="2"></td>
        {% } %}
        <td class="delete" style="width: 7em;">
<!--
            <button class="btn danger" data-type="{%=file.delete_type%}" data-url="{%=file.delete_url%}">Delete</button>
            <input type="checkbox" name="delete" value="1">
-->
            <div style="width: 24px; height: 24px;"><img src="images/ok.png" width="100%" height="100%"/></div>
        </td>
        <td style="width: 1%">&nbsp;</td>
    </tr>
{% } %}
</script>

<script type="text/javascript">
    var progressBar;
    var pbPoller;
    var currentItemNumber = 1;
    var processingFinished = false;
    var fileCount =1;
    var lastItemNumber = 0;
    var lastCalcItemNumber = 0;
    var noProgressCount = 0;

   function moreUploads() {
        formTable = $('uploadFileDiv').getElementsByTagName('table')[0];
        newRow = $(formTable.rows[0].cloneNode(true));
        formTable.tBodies[0].appendChild(newRow);
        newFile = newRow.down('input');
        newFile.value = '';
        newFile.id = 'upload'+fileCount;
        newCheckbox = newFile.next('input');
        newCheckbox.value = fileCount;
        newCheckbox.checked=false;
        newCheckbox.id = 'checkbox'+fileCount;
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

        // itemNumber < 0 means that there is no matching upload in progress on the server.
        // this could be because the upload never began due to the 2GB max upload issue. to check,
        // we allow a grace period of 5 refreshes, and then display an error message.
        if (itemNumber < 0 && lastItemNumber == 0 && noProgressCount++ >= 5) {
            pbPoller.stop();
            uploadFinished("Your files could not be uploaded. Please check whether the files you selected had a combined size of more than 2 GB",
                    "Your files could not be uploaded");
        }

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

    function uploadFinished(messages, alertMessages) {
        processingFinished = true;
        $("uploadingMessage").innerHTML = messages;
        $("uploadProgressFileList").style.display = "none";
        $("closeWindow").style.display = "";
        if (alertMessages) {
            alert(alertMessages);
        } else if (messages.match("not uploaded")) {
            alert('Your file upload is complete, but there were errors. Not all files may have been uploaded successfully.');
        } else if (messages.match("at least 1 file")) {
            // do nothing.
        } else {
            alert('Your file upload is complete');
        }
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