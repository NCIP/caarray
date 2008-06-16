<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:set var="downloadFileListAction" value="/ajax/project/files/downloadFilesList.action" />
<%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>
<script type="text/javascript">
    downloadMgr.resetDownloadInfo();

    showDownloadInProgress = function() {
        if (Object.values(downloadMgr.downloadFiles).length > 0) {
            $('downloadInProgressMsg').show();
            setTimeout("hideMsg()",5000);
        }
    }

    hideMsg = function() {
        $('downloadInProgressMsg').hide();
    }
</script>
