<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<a href="#content" id="navskip">Skip to Page Content</a>
<div id="nciheader">
    <div id="ncilogo"><a href="http://www.cancer.gov"><img src="<c:url value="/images/logotype.gif"/>" width="283" height="37" alt="Logo: National Cancer Institute" /></a></div>
    <div id="nihtag"><a href="http://www.cancer.gov"><img src="<c:url value="/images/tagline.gif"/>" width="295" height="37" alt="Logo: U.S. National Institutes of Health | www.cancer.gov" /></a></div>
</div>
<div id="caarrayheader">
    <div id="caarraylogo"><a href="./"><img src="<c:url value="/images/logo_caarray.gif"/>" width="172" height="46" alt="Logo: caArray - Array Data Management System" /></a></div>
</div>
<div id="infobar">
    <div id="rightinfo"> <span title="Subsersion URL: <c:out value='${initParam["svnUrl"]}'/>, revision: <c:out value='${initParam["svnRevision"]}'/>">caArray <c:out value='${initParam["caarrayVersion"]}'/></span> |  Node: <span>NCICB</span> | <a href="Logout.action"><span>Logout</span></a></div>
</div>
