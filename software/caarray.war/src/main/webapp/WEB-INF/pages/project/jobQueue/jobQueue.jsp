<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>View Job Queue</title>
</head>
<body>
    <h1>View Job Queue</h1>
    <caarray:helpPrint/>
    <c:url value="/protected/ajax/project/jobQueue.action" var="jobQueueUrl" />

    <fmt:message key="project.workspace.tabs.myProjects" var="myProjectsTitle" />
    <caarray:successMessages />
    <div class="padme">
        <c:url value="/protected/ajax/project/jobQueue.action" var="sortUrl" />
        <%@ include file="/WEB-INF/pages/project/jobQueue/jobList.jsp" %>
    </div>
</body>
</html>
