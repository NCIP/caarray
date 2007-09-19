<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1><c:out value="${requestScope.contentLabel}" /></h1>
        <s:form action="saveProject" method="post" validate="false">
            <table>
                <tr>
                    <th>Title for new project:</th>
                    <td>
                        <s:textfield name="projectName" required="true"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <s:submit key="button.save" />
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
 </body>