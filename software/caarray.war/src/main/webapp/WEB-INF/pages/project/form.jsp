<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@ page import="gov.nih.nci.caarray.domain.project.ProposalStatus"%>

<html>
<head>
    <title>Experiment Details</title>
<%--
  IE can't seem to handle defining downloadMgr on the downloadFiles page, so it's here,
  since this is the outer (non-ajax) page.
--%>
<c:url var="downloadUrl" value="/project/files/download.action"/>
<c:url var="removeUrl" value="/images/ico_remove.gif"/>
<script type="text/javascript">
  downloadMgr = new DownloadMgr('${downloadUrl}', '${removeUrl}');
  setExperimentTitleHeader = function(value) {
    $('experimentTitleHeader').innerHTML = value || 'New Experiment';
  }

  submitWorkflowForm = function() {
    var confirmMsg = "Are you sure you want to change the project's status?";
    if (TabUtils.hasFormChanges()) {
        confirmMsg = "There are unsaved changed in your form that will be lost. Are you sure you want to proceed to change the project's status?";
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

	<div class="pagehelp" style="width:auto;">
        <c:if test="${!empty project.id && caarrayfn:isOwner(project, caarrayfn:currentUser())}">
            <c:choose>
                <c:when test="${project.submissionAllowed}">
                    <c:set var="newWorkflowStatus" value="<%= ProposalStatus.IN_PROGRESS %>"/>
                    <c:set var="buttonTitle" value="Submit Experiment Proposal"/>
                </c:when>
                <c:when test="${project.makingPublicAllowed}">
                    <c:set var="newWorkflowStatus" value="<%= ProposalStatus.PUBLIC %>"/>
                    <c:set var="buttonTitle" value="Make Experiment Public"/>
                </c:when>
                <c:when test="${project.public}">
                    <c:set var="newWorkflowStatus" value="<%= ProposalStatus.IN_PROGRESS %>"/>
                    <c:set var="buttonTitle" value="Make Experiment Not Public"/>
                </c:when>
            </c:choose>
            <c:if test="${!empty newWorkflowStatus}">
                <s:form action="project/changeWorkflowStatus" id="workflowForm" cssStyle="display: inline">
                    <s:hidden name="project.id"/>
                    <s:hidden name="workflowStatus" value="${newWorkflowStatus}"/>
                </s:form>
                <caarray:linkButton onclick="submitWorkflowForm();"
                        actionClass="submit_experiment" text="${buttonTitle}" style="margin-top:-.6em; margin-right:15px;"/>
            </c:if>
        </c:if>
		<a href="javascript:openHelpWindow('')" class="help">Help</a>
		<a href="javascript:printpage()" class="print">Print</a>
	</div>
    <c:url value="/ajax/project/tab/Overview/load.action" var="overviewUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    <c:url value="/ajax/project/tab/Contacts/load.action" var="contactsUrl">
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
    <c:url value="/ajax/notYetImplemented.jsp" var="supplementalUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>
    <c:url value="/ajax/project/listTab/Publications/load.action" var="publicationsUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>

    <fmt:message key="project.tabs.overview" var="overviewTitle" />
    <fmt:message key="project.tabs.contacts" var="contactsTitle" />
    <fmt:message key="project.tabs.annotations" var="annotationsTitle" />
    <fmt:message key="project.tabs.data" var="dataTitle" />
    <fmt:message key="project.tabs.publications" var="publicationsTitle" />

    <c:if test="${param.initialTab == 'annotations' && param.initialTab2 != null}">
        <c:set value="${param.initialTab2}" scope="session" var="initialTab2" />
        <c:if test="${param.initialTab2Url != null}">
            <c:set value="${param.initialTab2Url}" scope="session" var="initialTab2Url" />
        </c:if>
    </c:if>
    <div class="padme">
        <h2>
            <span class="dark">Experiment:</span>
            <span id="experimentTitleHeader"><c:out value="${project.experiment.title}" default="New Experiment"/></span>
        </h2>
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.preFunction">
            <ajax:tab caption="${overviewTitle}" baseUrl="${overviewUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'overview'}" />
            <c:if test="${!empty project.id}">
                <ajax:tab caption="${contactsTitle}" baseUrl="${contactsUrl}" defaultTab="${param.initialTab == 'contacts'}" />
                <ajax:tab caption="${annotationsTitle}" baseUrl="${annotationsUrl}" defaultTab="${param.initialTab == 'annotations'}" />
                <ajax:tab caption="${dataTitle}" baseUrl="${dataUrl}" defaultTab="${param.initialTab == 'data'}" />
                <ajax:tab caption="${publicationsTitle}" baseUrl="${publicationsUrl}" defaultTab="${param.initialTab == 'publications'}" />
            </c:if>
        </ajax:tabPanel>
    </div>
</body>
</html>