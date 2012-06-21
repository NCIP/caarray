<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>My Experiment Workspace</title>
</head>
<body>
    <h1>My Experiment Workspace</h1>
    <caarray:helpPrint/>
    <c:url value="/protected/ajax/project/workspace.action" var="myProjectsUrl" />

    <fmt:message key="project.workspace.tabs.myProjects" var="myProjectsTitle" />
    <caarray:successMessages />
    <div class="padme">
        <c:url value="/protected/ajax/project/workspace.action" var="sortUrl" />
    <%@ include file="/WEB-INF/pages/project/list.jsp" %>
    </div>
</body>
</html>
