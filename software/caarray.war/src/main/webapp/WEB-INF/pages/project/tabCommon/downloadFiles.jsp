<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">
<div class="tableboxpad" style="overflow:auto; max-height:500px">
<table class="searchresults">
  <tr>
      <td width="80%">
      <span id="downloadFilesList">
        <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFilesList.jsp" %>
        </span>
      </td>
      <td width="20%" style="padding-left: 2px;">
        <br>
        <br>
        <div id="downloadInProgressMsg" class="confirm_msg" style="display:none"><fmt:message key="experiment.files.download.inProgress"/></div>
        <table class="searchresults" id="downloadTbl">
          <tr>
            <fmt:message key="experiment.files.downloadQueue" var="downloadQueueTitle" />
            <th>${downloadQueueTitle}&nbsp;[<a href="#" id="toggleQueue" onclick="downloadMgr.toggleQueue(); return false" >Show Files</a>]</th>
          </tr>
          <tr>
            <td>
              Job Size: 0 Files, 0 KB
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</div>
<caarray:actions divclass="actionsthin">
    <caarray:action onclick="downloadMgr.resetDownloadInfo();" actionClass="cancel" text="Clear Download Queue" />
    <caarray:action onclick="showDownloadInProgress(); downloadMgr.doDownloadFiles();" actionClass="launch_download" text="Launch Download Job" />
    <c:if test="${!empty exportUrl}">
        <caarray:action url="${exportUrl}" actionClass="launch_download" text="Export Consolidated MAGE-TAB" />
    </c:if>
</caarray:actions>
</caarray:tabPane>
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
