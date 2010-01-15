<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:if test="${empty files}">
    <p class="instructions">There are no files to download.</p>
</s:if>
<s:else>

<c:set var="downloadFileListAction" value="/ajax/project/files/downloadFilesList.action" />
<c:set var="downloadFilesTableListSortUrlAction" value="/ajax/project/files/downloadFilesListTable.action" />


<%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>

</s:else>