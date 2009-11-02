<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:set var="downloadFileListAction" value="/ajax/project/files/downloadFilesList.action" />
<c:set var="downloadFilesTableListSortUrlAction" value="/ajax/project/files/downloadFilesListTable.action" />

<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">
<%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>
</caarray:tabPane>

