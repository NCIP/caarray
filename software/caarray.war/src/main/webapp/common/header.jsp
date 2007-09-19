<%@ include file="/common/taglibs.jsp"%>

<a href="#content" id="navskip">Skip to Page Content</a>
<div id="nciheader">
    <div id="ncilogo"><a href="http://www.cancer.gov"><img src="<%= request.getContextPath() %>/images/logotype.gif" width="283" height="37" alt="Logo: National Cancer Institute" /></a></div>
    <div id="nihtag"><a href="http://www.cancer.gov"><img src="<%= request.getContextPath() %>/images/tagline.gif" width="295" height="37" alt="Logo: U.S. National Institutes of Health | www.cancer.gov" /></a></div>
</div>
<div id="caarrayheader">
    <div id="caarraylogo"><a href="./"><img src="<%= request.getContextPath() %>/images/logo_caarray.gif" width="172" height="46" alt="Logo: caArray - Array Data Management System" /></a></div>
</div>
<div id="infobar">
    <div id="rightinfo"> <caarray:version/> |  Node: <span>NCICB</span></div>
</div>
