<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/ajax/project/files/list.action" var="manageDataUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/files/downloadFiles.action" var="downloadDataUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>

<fmt:message key="project.tabs.manageData" var="manageDataTitle" />
<fmt:message key="project.tabs.downloadData" var="downloadDataTitle" />

<ajax:tabPanel panelStyleId="tablevel2" panelStyleClass="tablevel2" currentStyleClass="selected" contentStyleId="tabboxlevel2wrapper" contentStyleClass="tabboxlevel2wrapper"
        postFunction="TabUtils.setSelectedLevel2Tab" preFunction="TabUtils.showSubtabLoadingText">
    <ajax:tab caption="${manageDataTitle}" baseUrl="${manageDataUrl}" defaultTab="${param.initialTab2 == null || param.initialTab2 == 'manageData'}" />
    <ajax:tab caption="${downloadDataTitle}" baseUrl="${downloadDataUrl}" defaultTab="${param.initialTab2 == 'downloadData'}" />
</ajax:tabPanel>

<script type="text/javascript">
executeAjaxTab_tablevel2(null,'selected', '${manageDataUrl}', '');
</script>
