<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content">
        <h1>
           <a href="javascript:TabUtils.submitTabOrSubTabForm('projectForm', 'tabboxwrapper', 'tabboxlevel2wrapper', 'save_submit');"  class="submit_experiment" style="display: block; float: right"><img src="<c:url value="/images/btn_submit_experiment.gif"/>" alt="Submit Experiment Proposal"></a>
            Propose Experiment
        </h1>

        <c:url value="ajax_Project_loadTab_overview.action" var="overviewUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="ajax" value="true" />
        </c:url>
        <c:url value="ajax_Project_loadTab_contacts.action" var="contactsUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="ajax" value="true" />
        </c:url>
        <c:url value="ajax_Project_loadGenericTab_annotations.action" var="annotationsUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="ajax" value="true" />
        </c:url>
        <c:url value="ajax_Project_loadGenericTab_data.action" var="dataUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="ajax" value="true" />
        </c:url>
        <c:url value="ajax_Project_loadGenericTab_data.action" var="supplementalUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="ajax" value="true" />
        </c:url>
        <c:url value="ajax_Project_loadGenericTab_publications.action" var="publicationsUrl">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="ajax" value="true" />
        </c:url>

        <fmt:message key="project.tabs.overview" var="overviewTitle" />
        <fmt:message key="project.tabs.contacts" var="contactsTitle" />
        <fmt:message key="project.tabs.annotations" var="annotationsTitle" />
        <fmt:message key="project.tabs.data" var="dataTitle" />
        <fmt:message key="project.tabs.supplemental" var="supplementalTitle" />
        <fmt:message key="project.tabs.publications" var="publicationsTitle" />

        <div class="padme">
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.showLoadingText">
            <ajax:tab caption="${overviewTitle}" baseUrl="${overviewUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'overview'}" />
            <ajax:tab caption="${contactsTitle}" baseUrl="${contactsUrl}" defaultTab="${param.initialTab == 'contacts'}" />
            <ajax:tab caption="${annotationsTitle}" baseUrl="${annotationsUrl}" defaultTab="${param.initialTab == 'annotations'}" />
            <ajax:tab caption="${dataTitle}" baseUrl="${dataUrl}" defaultTab="${param.initialTab == 'data'}" />
            <ajax:tab caption="${supplementalTitle}" baseUrl="${supplementalUrl}" defaultTab="${param.initialTab == 'supplemental'}" />
            <ajax:tab caption="${publicationsTitle}" baseUrl="${publicationsUrl}" defaultTab="${param.initialTab == 'publications'}" />
        </ajax:tabPanel>
        </div>
    </div>
 </body>