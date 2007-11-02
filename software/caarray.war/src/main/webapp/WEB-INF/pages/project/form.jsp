<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Experiment Details</title>
<%--
  IE can't seem to handle defining downloadMgr on the downloadFiles page, so it's here,
  since this is the outer (non-ajax) page.
--%>
<c:url var="downloadUrl" value="/protected/project/files/download.action"/>
<c:url var="removeUrl" value="/images/ico_remove.gif"/>
<script type="text/javascript">
  downloadMgr = new DownloadMgr('${downloadUrl}', '${removeUrl}');
  setExperimentTitleHeader = function(value) {
    $('experimentTitleHeader').innerHTML = value || 'New Experiment';
  }
</script>
</head>
<body>
    <h1>
        <caarray:linkButton onclick="TabUtils.submitTabOrSubTabForm('projectForm', 'tabboxwrapper', 'tabboxlevel2wrapper', 'save_submit');"
            actionClass="submit_experiment" text="Submit Experiment Proposal" style="display: block; float: right"/>
        Experiment Details
    </h1>

    <c:url value="/protected/ajax/project/tab/Overview/load.action" var="overviewUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>
    <c:url value="/protected/ajax/project/tab/Contacts/load.action" var="contactsUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>
    <c:url value="/protected/ajax/project/tab/Annotations/load.action" var="annotationsUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>
    <c:url value="/protected/ajax/project/tab/Data/load.action" var="dataUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>
    <c:url value="/ajax/notYetImplemented.jsp" var="supplementalUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>
    <c:url value="/protected/ajax/project/listTab/Publications/load.action" var="publicationsUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <fmt:message key="project.tabs.overview" var="overviewTitle" />
    <fmt:message key="project.tabs.contacts" var="contactsTitle" />
    <fmt:message key="project.tabs.annotations" var="annotationsTitle" />
    <fmt:message key="project.tabs.data" var="dataTitle" />
    <fmt:message key="project.tabs.supplemental" var="supplementalTitle" />
    <fmt:message key="project.tabs.publications" var="publicationsTitle" />

    <c:if test="${param.initialTab == 'annotations' && param.initialTab2 != null}">
        <c:set value="${param.initialTab2}" scope="session" var="initialTab2" />
        <c:if test="${param.initialTab2Url != null}">
            <c:set value="${param.initialTab2Url}" scope="session" var="initialTab2Url" />
        </c:if>    
    </c:if>
        
    <div class="padme">
        <h2><span class="dark">Experiment:</span>   <span id="experimentTitleHeader">${project.experiment.title}</span></h2>
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
</body>
</html>