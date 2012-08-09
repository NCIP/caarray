<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>View Job Queue</title>
</head>
<body>
    <h1>View Job Queue</h1>
    <caarray:helpPrint/>

    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3><fmt:message key="project.workspace.tabs.myProjects" var="myProjectsTitle" /></h3>
                <div class="addlink">
                    <c:url value="/protected/project/viewJobQueue.action" var="jobQueueUrl" />
                    <caarray:linkButton actionClass="import" text="Refresh" url="${jobQueueUrl}" />
                </div>
            </div>
            <caarray:successMessages />
            <div class="tableboxpad">
                <%@ include file="/WEB-INF/pages/project/jobQueue/jobList.jsp" %>
            </div>
        </div>
    </div>
</body>
</html>
