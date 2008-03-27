<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div id="leftnav">
    <c:choose>
        <c:when test="${pageContext.request.remoteUser != null}">
            <div class="navheader">Welcome to caArray</div>
            <ul class="caarraymenu">
                <li class="liheader">Home</li>
                <li><a href="<c:url value="/home.action" />">Browse</a></li>
                <li class="liheader">Experiment Management</li>
                <li><a href="<c:url value="/protected/project/workspace.action" />">My Experiment Workspace</a></li>
                <li><a href="<c:url value="/protected/project/create.action" />">Create/Propose Experiment</a></li>

                <li class="liheader">User Management</li>
                <c:if test="${isSystemAdministrator}">
                  <li><a href="<c:out value='${initParam["uptUrl"]}'/>" target="_blank">Manage Users</a></li>
                </c:if>
                <li><a href="<c:url value="/protected/collaborators/listGroups.action" />">Manage Collaboration Groups</a></li>

                <li class="liheader">Curation</li>
                <li><a href="<c:url value="/protected/arrayDesign/list.action" />">Manage Array Designs</a></li>
                <li><a href="<c:url value="/protected/protocol/manage.action" />">Manage Protocols</a></li>
                <li><a href="<c:url value="/protected/vocabulary/manage.action" />">Manage Vocabulary</a></li>
                <li><a href="<c:url value="/protected/antibody/list.action" />">Manage Antibodies</a></li>
            </ul>

         </c:when>
        <c:otherwise>
            <div class="navheader">Welcome to caArray</div>
            <ul class="caarraymenu">
                <li><a href="<c:url value="/home.action" />">Browse</a></li>
                <li><a href="<c:url value="/protected/project/workspace.action" />">Login</a></li>
                <li><a href="<c:url value="/registration/input.action"/>">Register</a></li>
            </ul>
        </c:otherwise>
    </c:choose>
    <ul class="${pageContext.request.remoteUser != null ? 'aboutmenu' : 'welcomemenu' }">
        <li class="liheader">About caArray</li>
        <li><a href="http://caarray.nci.nih.gov/" target="_blank">What is caArray?</a></li>
        <li><a href="https://gforge.nci.nih.gov/frs/shownotes.php?release_id=1984" target="_blank">Release Notes</a></li>
        <li><a href="http://caarray.nci.nih.gov/" target="_blank">Technical Documentation</a></li>
        <li><a href="http://ncicb.nci.nih.gov/training/caarray/" target="_blank">Training</a></li>
        <li><a href="javascript:openHelpWindow('')">Help</a></li>
    </ul>
    <ul class="quicklinks">
        <li class="liheader">Global Quick Links</li>
        <li><a href="http://www.cancer.gov/" target="_blank" class="external">National Cancer Institute (NCI)</a></li>
        <li><a href="http://ncicb.nci.nih.gov/" target="_blank" class="external">NCI Center for Bioinformatics (NCICB)</a></li>
        <li><a href="https://cabig.nci.nih.gov/" target="_blank" class="external">caBIG&trade; - Cancer Biomedical Informatics Grid&trade;</a></li>
    </ul>
</div>
