<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    selectAll = function(selectAllBox, theform) {
       var state = selectAllBox.checked;
        for (i = 0; i < theform.elements.length; i++) {
            var element = theform.elements[i];
            if ("checkbox" == element.type) {
                element.checked = state;
            }
        }
    }
</script>

<c:url value="/protected/ajax/project/files/listUnimported.action" var="unimportedDataUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/protected/ajax/project/files/listImported.action" var="importedDataUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/protected/ajax/project/files/downloadFiles.action" var="downloadDataUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>

<fmt:message key="project.tabs.unimportedFiles" var="unimportedDataTitle" />
<fmt:message key="project.tabs.importedFiles" var="importedDataTitle" />
<fmt:message key="project.tabs.downloadData" var="downloadDataTitle" />

<ajax:tabPanel panelStyleId="tablevel2" panelStyleClass="tablevel2" currentStyleClass="selected" contentStyleId="tabboxlevel2wrapper" contentStyleClass="tabboxlevel2wrapper"
        postFunction="TabUtils.setSelectedLevel2Tab" preFunction="TabUtils.showTabLoadingText">
    <ajax:tab caption="${unimportedDataTitle}" baseUrl="${unimportedDataUrl}" defaultTab="${param.initialTab2 == null || param.initialTab2 == 'unimportedData'}" />
    <ajax:tab caption="${importedDataTitle}" baseUrl="${importedDataUrl}" defaultTab="${param.initialTab2 == 'importedData'}" />
    <ajax:tab caption="${downloadDataTitle}" baseUrl="${downloadDataUrl}" defaultTab="${param.initialTab2 == 'downloadData'}" />
</ajax:tabPanel>

<script type="text/javascript">
executeAjaxTab_tablevel2(null,'selected', '${unimportedDataUrl}', '');
</script>
