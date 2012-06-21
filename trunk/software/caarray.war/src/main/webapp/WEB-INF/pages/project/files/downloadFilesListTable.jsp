<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:downloadFilesListTable actionDownloadFilesTableListSortUrl="/ajax/project/files/downloadFilesListTable.action" files="${files}"/>