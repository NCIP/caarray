<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">
  <div class="tableboxpad">
  <table class="searchresults">
    <tr>
        <td>
      		<%@ include file="/WEB-INF/pages/project/files/downloadFilesList.jsp" %>
        </td>
        <td width="100%" style="padding-left: 10px;">
          <div id="downloadInProgressMsg" class="confirm_msg" style="display:none"><fmt:message key="experiment.files.download.inProgress"/></div>
          <table class="searchresults" id="downloadTbl">
            <tr>
              <fmt:message key="experiment.files.downloadQueue" var="downloadQueueTitle" />
              <th>${downloadQueueTitle}</th>
            </tr>
            <tr>
              <td>
                Job Size: 0 KB
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    <s:form action="ajax/project/listTab/Sources/saveList" cssClass="form" id="projectForm" theme="simple">
        <s:hidden name="project.id" />
    </s:form>
    </div>
    <caarray:actions divclass="actionsthin">
        <caarray:action onclick="downloadMgr.resetDownloadInfo();" actionClass="cancel" text="Cancel" />
        <caarray:action onclick="$('downloadInProgressMsg').show(); downloadMgr.doDownloadFiles();" actionClass="launch_download" text="Launch Download Job" />
    </caarray:actions>
</caarray:tabPane>
<script type="text/javascript">
<!--
  downloadMgr.resetDownloadInfo();
-->
</script>
