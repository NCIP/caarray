<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Search Results</title>
</head>
<body>
    <h1>Search Results</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <h2>Results for: "<strong>${keyword}</strong>"</h2>
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.showLoadingText">
            <s:iterator value="tabs.entrySet()" status="myStatus">
                <c:url value="/ajax/search/${key}.action" var="tabUrl">
                    <c:param name="keyword" value="${keyword}"/>
                    <c:param name="searchType" value="${searchType}"/>
                    <c:param name="categoryExp" value="${categoryExp}"/>
                    <c:param name="categorySample" value="${categorySample}"/>
                    <c:param name="categoryCombo" value="${categoryCombo}"/>
                    <c:param name="location" value="${location}"/>
                    <c:param name="resultExpCount" value="${value}"/>
                    <c:param name="resultSampleCount" value="${value}"/>
                    <c:param name="resultSourceCount" value="${value}"/>
                    <c:param name="selectedCategory" value="${selectedCategory.id}"/>
                </c:url>
                <fmt:message key="search.tab.${key}" var="tabTitle">
                    <fmt:param>${value}</fmt:param>
                </fmt:message>
                <caarray:tab caption="${tabTitle}" baseUrl="${tabUrl}" defaultTab="${myStatus.first}" />
            </s:iterator>
        </ajax:tabPanel>
    </div>
</body>
</html>