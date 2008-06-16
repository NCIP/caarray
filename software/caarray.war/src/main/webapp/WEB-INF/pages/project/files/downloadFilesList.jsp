<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:downloadFilesList
    actionDownloadFilesList="/ajax/project/files/downloadFilesList.action" 
    fileTypes="${allExtensions}">
  <%@ include file="/WEB-INF/pages/project/files/downloadFilesListTable.jsp" %>
</caarray:downloadFilesList>