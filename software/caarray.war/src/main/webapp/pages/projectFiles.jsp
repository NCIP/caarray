<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <s:form action="uploadFile" enctype="multipart/form-data" method="post" validate="true" id="uploadForm">
            <table>
            <tr>
                <td>
                    <p>You are managing files for.</p>
                    <s:file name="file" label="%{getText('uploadForm.file')}" required="true"/>
                </td>
            </tr>
            </table>
        </s:form>
    </div>
</body>