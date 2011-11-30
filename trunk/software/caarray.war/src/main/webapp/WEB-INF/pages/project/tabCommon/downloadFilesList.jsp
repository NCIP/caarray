<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:downloadFilesList
    actionDownloadFilesList="${downloadFileListAction}"
    fileTypes="${fileTypes}"
    fileStatuses="${fileStatuses}">
  <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFilesListTable.jsp" %>
</caarray:downloadFilesList>