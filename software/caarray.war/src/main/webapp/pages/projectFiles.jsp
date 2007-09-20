<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <%@ include file="/common/messages.jsp" %>
        <s:form action="uploadFile" enctype="multipart/form-data" method="post" validate="true">
            <s:file name="file" required="true"/>
            <table>
                <tr>
                    <td>
                        <s:submit name="import" value="Import" method="importFile" key="button.import" id="import" />
                    </td>
                     <td>
                        <s:submit name="validate" value="Validate" method="validate" key="button.validate" id="validate" />
                    </td>
                    <td>
                        <s:submit name="upload" value="Upload" method="upload" key="button.upload" id="upload"/>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    <script type="text/javascript">
        Form.focusFirstElement($('uploadForm'));
    </script>
</body>

