<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Search Results</title>
</head>
<body>
    <h1>Search Results</h1>
    <div class="padme">
        <h2>Results for: "<strong>${keyword}</strong>"</h2>
        <p class="small">
            <strong>X items</strong> found in <strong>X categories</strong> across <strong>X locations</strong>.
            Display in list:
            <s:select name="location" list="#{'NCICB':'NCICB'}" headerKey="" headerValue="(All Locations)"/>
        </p>
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.showLoadingText">
            <s:iterator value="tabs.entrySet()" status="myStatus">
                <c:url value="/ajax/search/${key}.action" var="tabUrl">
                    <c:param name="keyword" value="${keyword}"/>
                    <c:param name="category" value="${category}"/>
                    <c:param name="location" value="${location}"/>
                </c:url>
                <fmt:message key="search.tab.${key}" var="tabTitle">
                    <fmt:param>${value}</fmt:param>
                </fmt:message>
                <ajax:tab caption="${tabTitle}" baseUrl="${tabUrl}" defaultTab="${myStatus.first}" />
            </s:iterator>
        </ajax:tabPanel>
    </div>
</body>
</html>