<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<page:applyDecorator name="login">
<html>
<head>
</head>
<body>
    <div id="content" class="homepage">
        <div class="homebanner"><img src="<c:url value="/images/banner_caarray.jpg"/>" width="600" height="140" alt="" /></div>
        <h1>Welcome to the caArray Data Portal</h1>
        <p><strong>caArray</strong> is an open-source, role-based, Web and programmatically accessible data management system that guides the annotation and exchange of array data through a federated model of local and centralized installations. It provides browser-based and programmatic access to the data stored locally; enables mechanisms for accessing all local installation data over <a href="http://cabig.nci.nih.gov/workspaces/Architecture/caGrid/" class="external" target"#">caGrid</a>; supports silver compatibility with <a href="http://cabig.cancer.gov/index.asp" class="external" target"#">caBIG</a> guidelines, promotes compatibility with the MIAME 1.1 guidelines and the import of MAGE-TAB and provides a data service for caBIG analytical services.</p>
        <div class="clear"></div>
    </div>
</body>
</html>
</page:applyDecorator>