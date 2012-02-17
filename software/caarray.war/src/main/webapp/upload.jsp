<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


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

            <c:if test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
                <s:form action="project/files/upload" id="fileupload" namespace="" enctype="multipart/form-data" method="post"  target="target_upload">
                    <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
                    <input type=hidden name="selectedFilesToUnpack" value="-1" />
                    <div class="row fileupload-buttonbar">
            <div class="span7">
                <!-- The fileinput-button span is used to style the file input field as button -->
                <span class="btn btn-success fileinput-button">
                    <span><i class="icon-plus icon-white"></i> Add files...</span>
                    <input type="file" multiple="" name="files[]">
                </span>
                <button class="btn btn-primary start" type="submit">
                    <i class="icon-upload icon-white"></i> Start upload
                </button>
                <button class="btn btn-warning cancel" type="reset">
                    <i class="icon-ban-circle icon-white"></i> Cancel upload
                </button>
                <button class="btn btn-danger delete" type="button">
                    <i class="icon-trash icon-white"></i> Delete
                </button>
                <input type="checkbox" class="toggle">
            </div>
            <div class="span5">
                <!-- The global progress bar -->
                <div class="progress progress-success progress-striped active fade">
                    <div style="width:0%;" class="bar"></div>
                </div>
            </div>
        </div>
        <!-- The loading indicator is shown during image processing -->
        <div class="fileupload-loading"></div>
        <br>
        <!-- The table listing the files available for upload/download -->
        <table class="table table-striped"><tbody data-target="#modal-gallery" data-toggle="modal-gallery" class="files"></tbody></table>


                    <!--
                    <s:file id="upload0" name="upload" label="File" onchange="setCheckboxVal(this.value, this.id)">
                         <s:param name="after">
                             <s:checkbox id="checkbox0" name="selectedFilesToUnpack" fieldValue="0" value="false" theme="simple"/>
                             <s:label for="uploadForm_selectedFilesToUnpack" value="Unpack Compressed Archive" theme="simple"/>
                         </s:param>
                    </s:file>
                    -->
                </s:form>
                <caarray:actions>
                    <div class="span16 fileupload-buttonbar">
                        <div class="progressbar fileupload-progressbar fade"><div style="width:0%;"></div></div>
                        <span id="button_add" class="btn success fileinput-button">
                            <span>Add files...</span>
                        </span>
                        <button type="submit" class="btn primary start">Start upload</button>
                        <button type="reset" class="btn info cancel">Cancel upload</button>
                    </div>
                </caarray:actions>
                <!--
                <caarray:actions>
                    <caarray:linkButton actionClass="cancel" text="Cancel" onclick="window.close()"/>
                    <caarray:linkButton actionClass="add" text="Add More Files" onclick="moreUploads();"/>
                    <caarray:linkButton actionClass="save" text="Upload" onclick="beginUpload()"/>
                </caarray:actions>
                -->
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



    </body>
</html>

