<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Workspace</h1>
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <p>Validation Messages for <s:property value="file.name"/></p>
        <table>
        <tr>
            <td>Messages</td>
            <td>Types</td>
        </tr>
        <s:iterator value="file.validationResult.messages">
        <tr>
            <td><s:property value="type"/></td>
            <td><s:property value="line"/></td>
            <td><s:property value="column"/></td>
            <td><s:property value="message"/></td>
        </tr>
        </s:iterator>
        </table>
    </div>
 </body>