<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/ajax/project/tab/ExperimentalDesign/load.action" var="experimentalDesignUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/listTab/Factors/load.action" var="experimentalFactorsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/listTab/Sources/load.action" var="sourcesUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/listTab/Samples/load.action" var="samplesUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/listTab/Extracts/load.action" var="extractsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/listTab/LabeledExtracts/load.action" var="labeledExtractsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/listTab/Hybridizations/load.action" var="hybridizationsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>

<fmt:message key="project.tabs.experimentalDesign" var="experimentalDesignTitle" />
<fmt:message key="project.tabs.experimentalFactors" var="experimentalFactorsTitle" />
<fmt:message key="project.tabs.sources" var="sourcesTitle" />
<fmt:message key="project.tabs.samples" var="samplesTitle" />
<fmt:message key="project.tabs.extracts" var="extractsTitle" />
<fmt:message key="project.tabs.labeledExtracts" var="labeledExtractsTitle" />
<fmt:message key="project.tabs.hybridizations" var="hybridizationsTitle" />

<ajax:tabPanel panelStyleId="tablevel2" panelStyleClass="tablevel2" currentStyleClass="selected" contentStyleId="tabboxlevel2wrapper" contentStyleClass="tabboxlevel2wrapper"
        postFunction="TabUtils.setSelectedLevel2Tab" preFunction="TabUtils.showSubtabLoadingText">
    <ajax:tab caption="${experimentalDesignTitle}" baseUrl="${experimentalDesignUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'experimentalDesign'}" />
    <ajax:tab caption="${experimentalFactorsTitle}" baseUrl="${experimentalFactorsUrl}" defaultTab="${param.initialTab == 'experimentalFactors'}" />
    <ajax:tab caption="${sourcesTitle}" baseUrl="${sourcesUrl}" defaultTab="${param.initialTab == 'sources'}" />
    <ajax:tab caption="${samplesTitle}" baseUrl="${samplesUrl}" defaultTab="${param.initialTab == 'samples'}" />
    <ajax:tab caption="${extractsTitle}" baseUrl="${extractsUrl}" defaultTab="${param.initialTab == 'extracts'}" />
    <ajax:tab caption="${labeledExtractsTitle}" baseUrl="${labeledExtractsUrl}" defaultTab="${param.initialTab == 'labeledExtracts'}" />
    <ajax:tab caption="${hybridizationsTitle}" baseUrl="${hybridizationsUrl}" defaultTab="${param.initialTab == 'hybridizations'}" />
</ajax:tabPanel>

<script type="text/javascript">
executeAjaxTab_tablevel2(null,'selected', '${experimentalDesignUrl}', '');
</script>
