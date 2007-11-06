<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<page:applyDecorator name="login">
<html>
<head>
</head>
<body>
    <div class="homebanner"><img src="<c:url value="/images/banner_caarray.jpg"/>" width="600" height="140" alt="" /></div>
    <h1>Welcome to the caArray Data Portal</h1>
    <p><strong>caArray</strong> is an open-source, role-based, Web and programmatically accessible data management system that guides the annotation and exchange of array data through a federated model of local and centralized installations. It provides browser-based and programmatic access to the data stored locally; enables mechanisms for accessing all local installation data over <a href="http://cabig.nci.nih.gov/workspaces/Architecture/caGrid/" class="external" target"#">caGrid</a>; supports silver compatibility with <a href="http://cabig.cancer.gov/index.asp" class="external" target"#">caBIG</a> guidelines, promotes compatibility with the MIAME 1.1 guidelines and the import of MAGE-TAB and provides a data service for caBIG analytical services.</p>
    <div id="browsesearchwrapper">
        <div id="searchboxhome">
            <h2 class="tanbar">Search caArray</h2>
            <div class="boxpad">
                <s:form action="/search/basicSearch.action">
                    <s:textfield name="keyword" key="search.keyword"/>
                    <s:select name="category" key="search.category"
                              list="@gov.nih.nci.caarray.domain.search.SearchCategory@values()" listValue="%{getText(resourceKey)}"
                              headerKey="" headerValue="(All Categories)"/>
                    <s:select name="location" key="search.location"
                              list="#{'NCICB':'NCICB'}"
                              headerKey="" headerValue="(All Locations)"/>
                    <s:submit value="Search"/>
                </s:form>
            </div>
        </div>
    </div>
    <div class="clear"></div>
</body>
</html>
</page:applyDecorator>