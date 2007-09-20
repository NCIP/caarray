<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <s:form action="uploadFile" enctype="multipart/form-data" method="post" validate="true">
            <s:file name="file" required="true"/>
            <table>
                <tr>
                    <td>
                        <s:submit method="importFile" key="button.import" id="import" name="import" />
                    </td>
                     <td>
                        <s:submit method="validate" key="button.validate" id="validate" name="validate" />
                    </td>
                    <td>
                        <s:submit method="upload" key="button.upload" id="upload" name="upload" />
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    <script type="text/javascript">
        Form.focusFirstElement($('uploadForm'));
    </script>
</body>

