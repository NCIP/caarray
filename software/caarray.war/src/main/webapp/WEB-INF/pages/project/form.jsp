<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<html>
<head>
    <title>Experiment Details</title>
<%--
  IE can't seem to handle defining downloadMgr on the downloadFiles page, so it's here,
  since this is the outer (non-ajax) page.
--%>
<c:url var="downloadUrl" value="/project/files/download.action">
    <c:param name="project.id" value="${project.id}"/>
</c:url>
<c:url var="downloadGroupsUrl" value="/ajax/project/files/download.action">
    <c:param name="project.id" value="${project.id}"/>
</c:url>
<c:url value="/ajax/project/tab/Data/load.action" var="showDataTabDownloadDataSubTabWithDownloadGroupsUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
    <c:param name="initialTab" value="data"/>
    <c:param name="initialTab2" value="downloadData"/>
    <c:param name="initialTab2Url" value="${downloadGroupsUrl}"/>
</c:url>
<c:url var="removeUrl" value="/images/ico_remove.gif"/>
<c:url var="addUrl" value="/images/ico_add.gif"/>

<script type="text/javascript">
  downloadMgr = new DownloadMgr('${downloadUrl}', '${showDataTabDownloadDataSubTabWithDownloadGroupsUrl}','${removeUrl}','${addUrl}');
  setExperimentTitleHeader = function(value) {
    $('experimentTitleHeader').innerHTML = value || 'New Experiment';
  }

  submitWorkflowForm = function() {
    var confirmMsg = "Are you sure you want to lock this experiment from edits?";
    <c:if test="${project.locked}">
        confirmMsg = "Are you sure you want to unlock this experiment and make it editable?";
    </c:if>
    if (TabUtils.hasFormChanges()) {
        confirmMsg = "There are unsaved changed in your form that will be lost. Are you sure you want to proceed to lock the project from edits?";
    }
    if (confirm(confirmMsg)) {
        $('workflowForm').submit();
    }

    return false;
  }
</script>
</head>
<body>
    <h1>
        Experiment Details
    </h1>

    <caarray:helpPrint>
        <jsp:attribute name="extraContent">
        <c:if test="${!empty project.id && caarrayfn:isOwner(project, caarrayfn:currentUser())}">
            <c:choose>
                <c:when test="${!project.locked}">
                    <c:set var="newWorkflowStatus" value="true"/>
                    <c:set var="buttonTitle" value="Lock Experiment from Edits"/>
                </c:when>
                <c:otherwise>
                    <c:set var="newWorkflowStatus" value="false"/>
                    <c:set var="buttonTitle" value="Unlock Experiment"/>
                </c:otherwise>
            </c:choose>
            <c:if test="${!empty newWorkflowStatus}">
                <s:form namespace="/protected" action="project/changeWorkflowStatus" id="workflowForm" cssStyle="display: inline">
                    <s:hidden name="project.id"/>
                    <s:hidden name="workflowStatus" value="%{#attr.newWorkflowStatus}"/>
                </s:form>
                <caarray:linkButton onclick="this.blur(); submitWorkflowForm();"
                        actionClass="submit_experiment" text="${buttonTitle}" shouldWrapInListItem="false" style="float: right; padding-top: 0px; margin-top: -0.3em"/>
            </c:if>
        </c:if>
        </jsp:attribute>
    </caarray:helpPrint>


    <c:url value="/ajax/project/tab/Overview/load.action" var="overviewUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    <c:url value="/ajax/project/listTab/ExperimentContacts/load.action" var="contactsUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    <c:url value="/ajax/project/tab/Annotations/load.action" var="annotationsUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    <c:url value="/ajax/project/tab/Data/load.action" var="dataUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    <c:url value="/ajax/project/listTab/Publications/load.action" var="publicationsUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>

    <fmt:message key="project.tabs.overview" var="overviewTitle" />
    <fmt:message key="project.tabs.experimentContacts" var="contactsTitle" />
    <fmt:message key="project.tabs.annotations" var="annotationsTitle" />
    <fmt:message key="project.tabs.data" var="dataTitle" />
    <fmt:message key="project.tabs.publications" var="publicationsTitle" />

    <c:if test="${param.initialTab == 'annotations' && param.initialTab2 != null}">
        <c:set value="${param.initialTab2}" scope="session" var="initialTab2" />
        <c:if test="${param.initialTab2Url != null}">
            <c:set value="${param.initialTab2Url}" scope="session" var="initialTab2Url" />
        </c:if>
    </c:if>
    <c:if test="${param.initialTab == 'data' && param.initialTab2 != null}">
        <c:set value="${param.initialTab2}" scope="session" var="initialTab2" />
        <c:if test="${param.initialTab2Url != null}">
            <c:set value="${param.initialTab2Url}" scope="session" var="initialTab2Url" />
        </c:if>
    </c:if>
    <div class="padme">
        <h2>
            <span class="dark">Experiment:</span>
            <span id="experimentTitleHeader" style="word-wrap:break-word">
                <c:out value="${project.experiment.title}" default="New Experiment"/>
            </span>
        </h2>
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.preFunction">
            <caarray:tab caption="${overviewTitle}" baseUrl="${overviewUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'overview'}" />
            <c:if test="${!empty project.id}">
                <caarray:tab caption="${contactsTitle}" baseUrl="${contactsUrl}" defaultTab="${param.initialTab == 'contacts'}" />
                <caarray:tab caption="${annotationsTitle}" baseUrl="${annotationsUrl}" defaultTab="${param.initialTab == 'annotations'}" />
                <caarray:tab caption="${dataTitle}" baseUrl="${dataUrl}" defaultTab="${param.initialTab == 'data'}" />
                <caarray:tab caption="${publicationsTitle}" baseUrl="${publicationsUrl}" defaultTab="${param.initialTab == 'publications'}" />
                <c:forEach items="${projectTabs}" var="projectTab">
    				<c:url value="${projectTab.url}" var="tabUrl">
        				<c:param name="project.id" value="${project.id}" />
        				<c:param name="editMode" value="${editMode}" />
    				</c:url>
                	<caarray:tab caption="${projectTab.label}" baseUrl="${tabUrl}" defaultTab="${param.initialTab == projectTab.key}" />
    			</c:forEach>
            </c:if>
        </ajax:tabPanel>
    </div>
</body>
</html>
