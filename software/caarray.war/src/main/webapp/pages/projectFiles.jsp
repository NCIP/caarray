<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <%@ include file="/common/messages.jsp" %>
        <p>You are managing files for <s:property value="project.experiment.title" />.</p>

        <s:if test='fileEntries != null && !fileEntries.isEmpty()'>
            <s:form action="File_import" method="post">
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
                        <td><s:checkbox id="selected" name="fileEntries:%{caArrayFile.id}:selected" /></td>
                        <td><s:property value="caArrayFile.name"/></td>
                        <td><s:select id="fileType"
                                      name="fileEntries:%{caArrayFile.id}:fileType"
                                      listKey="label"
                                      listValue="value"
                                      list="fileTypes"
                                      value="typeName"/></td>
                        <td>
                            <a id="fileEntries:<s:property value='caArrayFile.id'/>:status" name="fileEntries:<s:property value='caArrayFile.id'/>:status" href="File_messages.action?fileId=<s:property value='caArrayFile.id'/>">
                                <s:property value="caArrayFile.status"/>
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
                    </tr>
                </table>
            </s:form>
        </s:if>

        <s:form action="File_upload" enctype="multipart/form-data" method="post" validate="true">
            <s:file id="upload" name="upload" label="File" required="true"/>
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

