<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>My Experiment Workspace</title>
</head>
<body>
    <h1>My Experiment Workspace</h1>
    <caarray:helpPrint/>
    <c:url value="/protected/ajax/project/workspace/myProjects.action" var="myProjectsUrl" />
    <c:url value="/protected/ajax/project/workspace/publicProjects.action" var="publicProjectsUrl" />

    <fmt:message key="project.workspace.tabs.myProjects" var="myProjectsTitle" />
    <fmt:message key="project.workspace.tabs.publicProjects" var="publicProjectsTitle" />

    <div class="padme">
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.showLoadingText">
            <ajax:tab caption="${myProjectsTitle} (<span id='work_queue_count'>${workQueueCount}</span>)" baseUrl="${myProjectsUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'myProjects'}" />
            <ajax:tab caption="${publicProjectsTitle} (<span id='public_count'>${publicCount}</span>)" baseUrl="${publicProjectsUrl}" defaultTab="${param.initialTab == 'publicProjects'}" />
        </ajax:tabPanel>
    </div>
</body>
</html>