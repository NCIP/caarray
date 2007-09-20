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
                        <s:submit method="importFile" key="button.import" name="importFile" />
                    </td>
                     <td>
                        <s:submit method="validateFile" key="button.validate" name="validateFile" />
                    </td>
                    <td>
                        <s:submit method="uploadFile" key="button.upload" name="uploadFile" />
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    <script type="text/javascript">
        Form.focusFirstElement($('uploadForm'));
    </script>
</body>

