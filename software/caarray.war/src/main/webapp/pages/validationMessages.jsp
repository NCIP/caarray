<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Workspace</h1>
        <%@ include file="/common/messages.jsp" %>
        <p>Validation Messages for <s:property value="fileEntry.caArrayFile.name"/></p>
        <table>
        <tr>
            <td>Messages</td>
            <td>Types</td>
        </tr>
        <s:iterator value="fileEntry.caArrayFile.validationResult.messages">
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