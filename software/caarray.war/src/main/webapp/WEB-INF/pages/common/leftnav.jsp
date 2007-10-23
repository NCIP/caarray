<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div id="leftnav">
    <ul class="caarraymenu">
        <li class="liheader">caArray</li>
        <li><a href="<c:url value="/notYetImplemented.jsp" />">Public Home</a></li>
        <c:if test="${pageContext.request.remoteUser == null}">
        <li><a href="<c:url value="/notYetImplemented.jsp" />">Register</a></li>
        <li><a href="<c:url value="/protected/project/list.action" />">Login</a></li>
        </c:if>
    </ul>
    <ul class="aboutmenu">
        <li class="liheader">caArray 2.0 Software</li>
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
