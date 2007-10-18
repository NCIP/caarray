<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="ajax_Project_loadTab_manageData.action" var="manageDataUrl">
    <c:param name="proposalKey" value="${proposalKey}" />
    <c:param name="ajax" value="true" />
</c:url>
<c:url value="ajax_Project_loadGenericTab_downloadData.action" var="downloadDataUrl">
    <c:param name="proposalKey" value="${proposalKey}" />
    <c:param name="ajax" value="true" />
</c:url>

<fmt:message key="project.tabs.manageData" var="manageDataTitle" />
<fmt:message key="project.tabs.downloadData" var="downloadDataTitle" />

<ajax:tabPanel panelStyleId="tablevel2" panelStyleClass="tablevel2" currentStyleClass="selected" contentStyleId="tabboxlevel2wrapper" contentStyleClass="tabboxlevel2wrapper"
        postFunction="TabUtils.setSelectedLevel2Tab" preFunction="TabUtils.showSubtabLoadingText">
    <ajax:tab caption="${manageDataTitle}" baseUrl="${manageDataUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'manageData'}" />
    <ajax:tab caption="${downloadDataTitle}" baseUrl="${downloadDataUrl}" defaultTab="${param.initialTab == 'downloadData'}" />
</ajax:tabPanel>

<script type="text/javascript">
executeAjaxTab_tablevel2(this,'selected', '${manageDataUrl}', '');
</script>
