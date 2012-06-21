<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage New Imports</title>
</head>
<body>
    <h1>Manage New Imports</h1>
    <caarray:helpPrint/>

    <c:url value="/protected/ajax/admin/import/projects.action" var="projectsUrl" />
    <c:url value="/protected/ajax/admin/import/designs.action" var="designsUrl" />
    
    <div class="padme">
        <p class="instructions">
        The Experiments and Array Designs listed here have data files that
        should be parsed in order to take advantage of features recently added to
        CaArray. Click the Reparse (<img src="<c:url value="/images/ico_import.gif"/>" />)
        button to start the process.
        </p>

        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.preFunction">
            <caarray:tab caption="Experiments" baseUrl="${projectsUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'projects'}" />
            <caarray:tab caption="Array Designs" baseUrl="${designsUrl}" defaultTab="${param.initialTab == 'designs'}" />
        </ajax:tabPanel>
    </div>
    
</body>
</html>