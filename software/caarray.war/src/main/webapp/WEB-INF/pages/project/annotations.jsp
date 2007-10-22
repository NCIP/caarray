<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/ajax/project/loadTab/experimentalDesign.action" var="experimentalDesignUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/loadGenericTab/factors.action" var="experimentalFactorsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/loadGenericTab/sources.action" var="sourcesUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/loadGenericTab/samples.action" var="samplesUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/loadGenericTab/extracts.action" var="extractsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/loadGenericTab/labeledExtracts.action" var="labeledExtractsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/loadGenericTab/hybridizations.action" var="hybridizationsUrl">
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
    <ajax:tab caption="${hybridizationsTitle}" baseUrl="${hybridizationsUrl}" defaultTab="${param.initialTab == 'hybridizations'}" />
    <ajax:tab caption="${extractsTitle}" baseUrl="${extractsUrl}" defaultTab="${param.initialTab == 'extracts'}" />
    <ajax:tab caption="${labeledExtractsTitle}" baseUrl="${labeledExtractsUrl}" defaultTab="${param.initialTab == 'labeledExtracts'}" />
</ajax:tabPanel>

<script type="text/javascript">
executeAjaxTab_tablevel2(null,'selected', '${experimentalDesignUrl}', '');
</script>
