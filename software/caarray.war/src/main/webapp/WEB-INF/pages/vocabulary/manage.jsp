<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Vocabularies</title>
</head>
<body>
    <h1>
        Manage Vocabularies
    </h1>

    <c:url value="/protected/ajax/vocabulary/list.action" var="tissueSitesUrl">
        <c:param name="category"><s:property value="@gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory@ORGANISM_PART" /></c:param>
    </c:url>
    <c:url value="/protected/ajax/vocabulary/list.action" var="tissueTypesUrl">
        <c:param name="category"><s:property value="@gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory@MATERIAL_TYPE" /></c:param>
    </c:url>
    <c:url value="/protected/ajax/vocabulary/list.action" var="cellTypesUrl">
        <c:param name="category"><s:property value="@gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory@CELL_TYPE" /></c:param>
    </c:url>
    <c:url value="/protected/ajax/vocabulary/list.action" var="conditionsUrl">
        <c:param name="category"><s:property value="@gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory@DISEASE_STATE" /></c:param>
    </c:url>

    <fmt:message key="vocabulary.tabs.ORGANISM_PART" var="tissueSitesTitle" />
    <fmt:message key="vocabulary.tabs.MATERIAL_TYPE" var="tissueTypesTitle" />
    <fmt:message key="vocabulary.tabs.CELL_TYPE" var="cellTypesTitle" />
    <fmt:message key="vocabulary.tabs.DISEASE_STATE" var="conditionsTitle" />

    <c:if test="${param.initialTab != null && param.startWithCreate != null}">
        <c:set value="${param.startWithCreate}" scope="session" var="startWithCreate" />
        <c:set value="${param.returnProjectId}" scope="session" var="returnProjectId" />
    </c:if>

    <div class="padme">
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.preFunction">
            <ajax:tab caption="${tissueSitesTitle}" baseUrl="${tissueSitesUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'ORGANISM_PART'}" />
            <ajax:tab caption="${tissueTypesTitle}" baseUrl="${tissueTypesUrl}" defaultTab="${param.initialTab == 'MATERIAL_TYPE'}" />
            <ajax:tab caption="${cellTypesTitle}" baseUrl="${cellTypesUrl}" defaultTab="${param.initialTab == 'CELL_TYPE'}" />
            <ajax:tab caption="${conditionsTitle}" baseUrl="${conditionsUrl}" defaultTab="${param.initialTab == 'DISEASE_STATE'}" />
        </ajax:tabPanel>
    </div>
</body>
</html>