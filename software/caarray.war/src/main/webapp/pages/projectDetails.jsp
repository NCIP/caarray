<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Workspace</h1>
        <%@ include file="/common/messages.jsp" %>
        <p>You have selected <s:property value="project.experiment.title" />.
        <p>This project's browsability status: <s:property value="project.browsable"/>.
        <s:form action="Project_toggle" method="post">
          <s:hidden name="projectId" value="%{project.id}"/>
          <s:submit key="button.toggle"/>
        </s:form>
    </div>
</body>