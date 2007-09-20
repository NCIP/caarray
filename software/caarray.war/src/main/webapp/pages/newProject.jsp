<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1><c:out value="${requestScope.contentLabel}" /></h1>
        <%@ include file="/common/messages.jsp" %>
        <s:form action="saveProject" method="post" validate="false">
            <table>
                <tr>
                    <th>Title for new project:</th>
                    <td>
                        <s:textfield id="projectName" name="projectName" required="true"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <s:submit id="submit" key="button.save" />
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
 </body>