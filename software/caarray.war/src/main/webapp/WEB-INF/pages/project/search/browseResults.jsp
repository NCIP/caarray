<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Browse</title>
</head>
<body>
    <h1>Browse caArray</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <h2>Browse by: "<strong><s:text name="${category.resourceKey}"/></strong>"</h2>
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.showLoadingText">
            <c:forEach var="tab" items="${tabs}" varStatus="myStatus">
                <c:url value="/ajax/browse/list.action" var="tabUrl">
                    <c:param name="id" value="${tab.id}"/>
                    <c:param name="category" value="${category}"/>
                </c:url>
                <ajax:tab caption="${tab.name} (${tab.count})" baseUrl="${tabUrl}" defaultTab="${myStatus.first}" />
            </c:forEach>
        </ajax:tabPanel>
    </div>
</body>
</html>