<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    var counter = 0;

    function init() {
        document.getElementById('moreUploads').onclick = moreUploads;
        moreUploads();
    }

    function moreUploads() {
        counter++;
        var newFields = document.getElementById('uploadDiv').cloneNode(true);
        newFields.id = '';
        newFields.style.display = 'block';
        var newField = newFields.childNodes;
        for (var i=0;i<newField.length;i++) {
            var theName = newField[i].name
            if (theName)
                newField[i].name = theName + counter;
        }
        var insertHere = document.getElementById('writeUploadDiv');
        insertHere.parentNode.insertBefore(newFields,insertHere);
    }
</script>

<head>
</head>

<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <caarray:successMessages />
        <p>You are managing files for <s:property value="project.experiment.title" />.</p>

        <s:if test='project.files != null && !project.files.isEmpty()'>
            <s:form action="file/import" method="post">
                <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
                <table>
                    <tr>
                        <td>Current Files</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr></tr>
                    <tr>
                        <td></td>
                        <td>Select Name</td>
                        <td>File Type</td>
                        <td>Status</td>
                    </tr>
                    <s:iterator value="project.files" status="status">
                    <tr>
                        <td><s:checkbox name="file:%{#status.index}:selected" /></td>
                        <td>
                            <c:url var="downloadUrl" value="/protected/file/download.action">
                                <c:param name="downloadIds"><s:property value="id"/></c:param>
                            </c:url>
                            <a href="${downloadUrl}"><s:property value="name"/></a>
                        </td>
                        <td><s:select name="file:%{#status.index}:fileType"
                                      listKey="label"
                                      listValue="value"
                                      list="fileTypes"
                                      value="type" />
                        </td>
                        <td>
                            <c:url var="messagesUrl" value="/protected/file/messages.action">
                                <c:param name="project.id"><s:property value="project.id"/></c:param>
                                <c:param name="file.id"><s:property value="id"/></c:param>
                            </c:url>
                            <a name="file:<s:property value='%{#status.index}'/>:status" href="${messagesUrl}">
                                <s:property value="status"/>
                            </a>
                        </td>
                    </tr>
                    </s:iterator>
                    <tr>
                        <td>
                            <s:submit name="importFile" value="Import" method="importFile" id="importFile" />
                        </td>
                        <td>
                            <s:submit name="validateFile" value="Validate" method="validateFile" id="validateFile" />
                        </td>
                       <td>
                            <s:submit name="removeFile" value="Delete" method="removeFile" id="removeFile"/>
                        </td>
                        <td>
                            <s:submit name="saveExtension" value="Save Changes" method="saveExtension" id="saveExtension"/>
                        </td>
                    </tr>
                </table>
            </s:form>
        </s:if>

        <div id="uploadDiv" style="display: none">
            <input type="button" value="Remove" onclick="this.parentNode.parentNode.removeChild(this.parentNode);" /><br />
            <s:file id="upload" name="upload" label="File" />
        </div>

        <s:form action="file/upload" enctype="multipart/form-data" method="post">
            <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
            <s:file id="upload" name="upload" label="File" />
            <span id="writeUploadDiv"></span>
            <table>
                <tr>
                    <td>
                        <input type="button" id="moreUploads" value="More" onclick="init()"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <s:submit name="uploadFile" value="Upload" id="uploadFile"/>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
</body>
