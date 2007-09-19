<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1><c:out value="${requestScope.contentLabel}" /></h1>
        <%@ include file="/common/messages.jsp" %>
        <display:table name="projects" id="project">
            <display:column property="experiment.title" title="Experiment Title" escapeXml="true" href="editProject.action" paramId="projectName" paramProperty="experiment.title"/>
        </display:table>
    </div>
</body>
