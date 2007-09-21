<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Workspace</h1>
        <%@ include file="/common/messages.jsp" %>
        <p>Validation Messages for <c:out value="${requestScope.fileEntry.caArrayFile.name}"/></p>
        <table>
        <tr>
            <td>Messages</td>
            <td>Types</td>
        </tr>
        <c:forEach items="${requestScope.fileEntry.caArrayFile.validationResult.messages}" var="message" varStatus="status">
        <tr>
            <td><c:out value="${message.type}"/></td>
            <td><c:out value="${message.line}"/></td>
            <td><c:out value="${message.column}"/></td>
            <td><c:out value="${message.message}"/></td>
        </tr>
        </c:forEach>
        </table>
    </div>
 </body>