<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <%@ include file="/common/messages.jsp" %>
        <p>You are managing files for <s:property value="project.experiment.title" />.</p>

        <s:if test='project.files != null && !project.files.isEmpty()'>
            <s:form action="File_import" method="post">
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
                        <td><s:property value="name"/></td>
                        <td><s:select name="file:%{#status.index}:fileType"
                                      listKey="label"
                                      listValue="value"
                                      list="fileTypes"
                                      value="type" />
                        </td>
                        <td>
                            <a name="file:<s:property value='%{#status.index}'/>:status" href="File_messages.action?project.id=<s:property value='project.id'/>&file.id=<s:property value='id'/>">
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
                    </tr>
                </table>
            </s:form>
        </s:if>

        <s:form action="File_upload" enctype="multipart/form-data" method="post">
            <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
            <s:file id="upload" name="upload" label="File" />
            <table>
                <tr>
                    <td>
                        <s:submit name="uploadFile" value="Upload" id="uploadFile"/>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
</body>

