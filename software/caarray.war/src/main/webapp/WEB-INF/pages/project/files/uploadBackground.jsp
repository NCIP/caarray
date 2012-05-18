<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<page:applyDecorator name="upload">
    <html>
        <head>
            <title>Experiment Data Upload</title>
        </head>
        <body>
            <h1>Experiment Data Upload</h1>

             <c:url value="/project/details.action" var="projectDetailsUrl">
                <c:param name="project.id" value="${project.id}"/>
                <c:param name="initialTab" value="data" />
            </c:url>

            <!-- JQuery required upload and download templates for this UI -->
            <jsp:include page="/WEB-INF/pages/project/files/dataFileUploadDownloadTemplates.jsp"/>

            <iframe id='target_upload' name='target_upload' src='' style='display: none'> </iframe>
            <div id="uploadFileDiv">
                <div class="boxpad2extend">
                    <p id="ie_limitation_text"></p>
                    <c:if test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
                        <s:form id="fileupload" action="upload.action" enctype="multipart/form-data" method="post" target="target_upload">
                            <input type=hidden name="project.id" id="uploadProjectId" value="<s:property value='%{project.id}'/>"/>
                            <input type=hidden name="selectedFilesToUnpack" value="-1" />
                            <div class="fileupload-loading"></div><br>
                            <div class="span5"><div class="progress progress-success progress-striped active fade"><div class="bar" style="width:0%;"></div></div></div>
                            <table class="table table-striped"><tbody class="files" data-toggle="modal-gallery" data-target="#modal-gallery"></tbody></table>
                            <div class="row fileupload-buttonbar">
                                <div class="span7">
                                    <span class="btn btn-gray fileinput-button">
                                        <span><i class="icon-plus icon-white"></i>Add Files</span>
                                        <input id="upload0" name="upload" multiple="multiple" type="file">
                                    </span>
                                    <button type="submit" class="btn btn-gray start" onclick="beginUpload()"><i class="icon-upload icon-white"></i>Upload All</button>
                                    <button type="reset" class="btn btn-gray cancel"><i class="icon-ban-circle icon-white"></i>Cancel All Uploads</button>
                                    <button type="reset" class="btn btn-gray cancel" onclick="closeAndGoToProjectData('${projectDetailsUrl}')"><i class="icon-upload icon-white"></i>Close Window</button>
                                </div>
                            </div>
                        </s:form>
                    </c:if>
                </div>
            </div>
        </body>
    </html>
</page:applyDecorator>
