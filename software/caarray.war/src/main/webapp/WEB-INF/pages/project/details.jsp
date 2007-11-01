<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Experiment Workspace</title>
</head>
<body>
    <h1>Experiment Workspace</h1>
    <caarray:successMessages />
    <p>You have selected <s:property value="project.experiment.title" />.

    <p>This project's browsability status: <s:property value="project.browsable"/>.
    <s:form action="project/toggle">
      <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
      <s:submit key="button.toggle"/>
    </s:form>
</body>
</html>