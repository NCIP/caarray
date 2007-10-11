<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Workspace</h1>
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <s:set name="projects" value="projects" scope="request"/>
        <display:table name="projects" class="table" requestURI="" id="projectList">
            <display:column property="experiment.title" title="Experiment Title" escapeXml="true" href="Project_details.action" paramId="project.id" paramProperty="id" titleKey="project.id"/>
        </display:table>
    </div>
    <script type="text/javascript">
        highlightTableRows("projectList");
    </script>
</body>
