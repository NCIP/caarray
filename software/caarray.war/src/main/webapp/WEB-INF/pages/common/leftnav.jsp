<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div id="leftnav">
    <c:choose>
        <c:when test="${pageContext.request.remoteUser != null}">
            <div class="navheader">caArray Actions</div>
            <ul class="caarraymenu">
                <li class="liheader">Experiments</li>
                <li><a href="<c:url value="/protected/project/workspace.action" />">My Experiment Workspace</a></li>
                <li><a href="<c:url value="/protected/project/create.action" />">Create/Propose Experiment</a></li>

                <li class="liheader">Users &amp; Groups</li>
                <li><a href="<c:url value="/notYetImplemented.jsp" />">Manage Users</a></li>
                <li><a href="<c:url value="/protected/collaborators/listGroups.action" />">Manage Collaboration Groups</a></li>

                <li class="liheader">Curation</li>
                <li><a href="<c:url value="/notYetImplemented.jsp" />">Import Array Designs</a></li>
            </ul>

            <ul class="welcomemenu">
                <li class="liheader">Welcome to caArray</li>
                <li><a href="<c:url value="/" />">Public Home</a></li>
            </ul>
        </c:when>
        <c:otherwise>
            <div class="navheader">Welcome to caArray</div>
            <ul class="caarraymenu">
                <li><a href="<c:url value="/" />">Public Home</a></li>
                <li><a href="<c:url value="/registration/input.action"/>">Register</a></li>
                <li><a href="<c:url value="/protected/project/workspace.action" />">Login</a></li>
            </ul>
        </c:otherwise>
    </c:choose>
    <ul class="${pageContext.request.remoteUser != null ? 'aboutmenu' : 'welcomemenu' }">
        <li class="liheader">About caArray</li>
        <li><a href="<c:url value="/notYetImplemented.jsp" />">What is caArray?</a></li>
        <li><a href="<c:url value="/notYetImplemented.jsp" />">Install caArray</a></li>
        <li><a href="<c:url value="/notYetImplemented.jsp" />">User Guide</a></li>
        <li><a href="<c:url value="/notYetImplemented.jsp" />">Release Notes</a></li>
        <li><a href="<c:url value="/notYetImplemented.jsp" />">Technical Documentation</a></li>
    </ul>
    <ul class="quicklinks">
        <li class="liheader">Global Quick Links</li>
        <li><a href="http://www.cancer.gov/" class="external">National Cancer Institute (NCI)</a></li>
        <li><a href="http://ncicb.nci.nih.gov/" class="external">NCI Center for Bioinformatics (NCICB)</a></li>
        <li><a href="https://cabig.nci.nih.gov/" class="external">caBIG&trade; - Cancer Biomedical Informatics Grid&trade;</a></li>
    </ul>
</div>
