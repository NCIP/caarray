<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <%@ include file="/common/messages.jsp" %>
        <p>You are managing files for <s:property value="project.experiment.title" />.</p>

        <s:if test='fileEntries != null && !fileEntries.isEmpty()'>
            <s:form id="filesForm" action="doImport" method="post">
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
                    <s:iterator value="fileEntries" status="status">
                    <tr>
                        <td><s:checkbox id="selected" name="fileEntries:%{#status.index}:selected" /></td>
                        <td><s:property value="caArrayFile.name"/></td>
                        <td><s:select id="fileType"
                                      name="fileEntries:%{#status.index}:fileType"
                                      listKey="label"
                                      listValue="value"
                                      list="fileTypes"
                                      value="typeName"/></td>
                        <td>
                            <s:url id="url" action="viewMessages" >
                                <s:param name="fileId" value="caArrayFile.id" />
                            </s:url>
                            <s:a href="%{url}"><s:property value="caArrayFile.status"/></s:a>
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
                    </tr>
                </table>
            </s:form>
        </s:if>

        <s:form id="uploadForm" action="doUpload" method="post" enctype="multipart/form-data" onsubmit="return validateForm(this)">
            <s:file id="upload" name="upload" label="File"/>
            <table>
                <tr>
                    <td>
                        <s:submit name="uploadFile" value="Upload" id="uploadFile"/>
                    </td>
                </tr>
            </table>
        </s:form>
        <%@ include file="/scripts/upload.js"%>
    </div>
    <script type="text/javascript">
        Form.focusFirstElement($('uploadForm'));
    </script>
</body>

