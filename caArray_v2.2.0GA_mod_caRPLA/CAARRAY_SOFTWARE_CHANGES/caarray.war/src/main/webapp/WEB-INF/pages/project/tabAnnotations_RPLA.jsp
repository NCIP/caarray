<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:set var="initTab" value="${sessionScope.initialTab2 == null ? param.initialTab2 : sessionScope.initialTab2}" />
<c:remove var="initialTab2" scope="session" />

<c:url value="/ajax/project/tab/ExperimentalDesign/load.action" var="experimentalDesignUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/Factors/load.action" var="factorsUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/Sources/load.action" var="sourcesUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/Samples/load.action" var="samplesUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/Extracts/load.action" var="extractsUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/LabeledExtracts/load.action" var="labeledExtractsUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/Hybridizations/load.action" var="hybridizationsUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>



<c:url value="/ajax/project/listTab/RplArrays/load.action" var="rplarraysUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/RplaSamples/load.action" var="rplasamplesUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/Antibodies/load.action" var="antibodiesUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/listTab/RplaHybridizations/load.action" var="rplahybridizationsUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>

















<fmt:message key="project.tabs.experimentalDesign" var="experimentalDesignTitle" />
<fmt:message key="project.tabs.factors" var="factorsTitle" />
<fmt:message key="project.tabs.sources" var="sourcesTitle" />
<fmt:message key="project.tabs.samples" var="samplesTitle" />
<fmt:message key="project.tabs.extracts" var="extractsTitle" />
<fmt:message key="project.tabs.labeledExtracts" var="labeledExtractsTitle" />
<fmt:message key="project.tabs.hybridizations" var="hybridizationsTitle" />


<fmt:message key="project.tabs.rplarrays" var="rplarraysTitle" />
<fmt:message key="project.tabs.rplasamples" var="rplasamplesTitle" />
<fmt:message key="project.tabs.antibodies" var="antibodiesTitle" />
<fmt:message key="project.tabs.rplahybridizations" var="rplahybridizationsTitle" />










<c:choose>
    <c:when test="${!empty initialTab2Url}">
        <c:set var="initUrl" value="${initialTab2Url}"/>
        <c:remove var="initialTab2Url" scope="session"/>
    </c:when>
    <c:when test="${initTab == 'factors'}">
        <c:set var="initUrl" value="${factorsUrl}"/>
    </c:when>
    <c:when test="${initTab == 'sources'}">
        <c:set var="initUrl" value="${sourcesUrl}"/>
    </c:when>
    <c:when test="${initTab == 'samples'}">
        <c:set var="initUrl" value="${samplesUrl}"/>
    </c:when>
    <c:when test="${initTab == 'extracts'}">
        <c:set var="initUrl" value="${extractsUrl}"/>
    </c:when>
    <c:when test="${initTab == 'labeledExtracts'}">
        <c:set var="initUrl" value="${labeledExtractsUrl}"/>
    </c:when>
    <c:when test="${initTab == 'hybridizations'}">
        <c:set var="initUrl" value="${hybridizationsUrl}"/>
    </c:when>
   
    
    
     <c:when test="${initTab == 'rplarrays'}">
        <c:set var="initUrl" value="${rplarraysUrl}"/>
    </c:when>
     <c:when test="${initTab == 'rplasamples'}">
        <c:set var="initUrl" value="${rplasamplesUrl}"/>
    </c:when>
     <c:when test="${initTab == 'antibodies'}">
        <c:set var="initUrl" value="${antibodiesUrl}"/>
    </c:when>
     <c:when test="${initTab == 'rplahybridizations'}">
        <c:set var="initUrl" value="${rplahybridizationsUrl}"/>
    </c:when>
    
     <c:otherwise>
        <c:set var="initUrl" value="${experimentalDesignUrl}"/>
    </c:otherwise>
    
    
    
    
    
    
    
    
</c:choose>

<ajax:tabPanel panelStyleId="tablevel2" panelStyleClass="tablevel2" currentStyleClass="selected" contentStyleId="tabboxlevel2wrapper" contentStyleClass="tabboxlevel2wrapper"
        postFunction="TabUtils.setSelectedLevel2Tab" preFunction="TabUtils.preFunction">
    <caarray:tab caption="${experimentalDesignTitle}" baseUrl="${experimentalDesignUrl}" defaultTab="${initTab == null || initTab == 'experimentalDesign'}" />
    <caarray:tab caption="${factorsTitle}" baseUrl="${factorsUrl}" defaultTab="${initTab == 'factors'}" />
    <caarray:tab caption="${sourcesTitle}" baseUrl="${sourcesUrl}" defaultTab="${initTab == 'sources'}" />
    
    
    <caarray:tab caption="RplaSamples" baseUrl="${rplasamplesUrl}" defaultTab="${initTab == 'rplasamples'}" />
    <caarray:tab caption="RplArrays" baseUrl="${rplarraysUrl}" defaultTab="${initTab == 'rplarrays'}" />
    <caarray:tab caption="Antibodies" baseUrl="${antibodiesUrl}" defaultTab="${initTab == 'antibodies'}" />
    <caarray:tab caption="RplaHybridizations" baseUrl="${rplahybridizationsUrl}" defaultTab="${initTab == 'rplahybridizations'}" />
    
</ajax:tabPanel>

<script type="text/javascript">
executeAjaxTab_tablevel2(null,'selected', '${initUrl}', '');
</script>
