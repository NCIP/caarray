<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">
<div class="tableboxpad" style="overflow:auto;">
<table class="searchresults">
  <tr>
      <td width="80%">
        <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFilesList.jsp" %>
      </td>
      <td width="20%" style="padding-left: 2px;">
        <br>
        <br>
        <div id="downloadInProgressMsg" class="confirm_msg" style="display:none"><fmt:message key="experiment.files.download.inProgress"/></div>
        <table class="searchresults" id="downloadTbl">
          <tr>
            <fmt:message key="experiment.files.downloadQueue" var="downloadQueueTitle" />
            <th>${downloadQueueTitle}&nbsp;[<a href="#" id="toggleQueue" onclick="downloadMgr.toggleQueue()" >Show Files</a>]</th>
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
    <caarray:action onclick="showDownloadInProgress(); downloadMgr.doDownloadFiles();" actionClass="launch_download" text="Launch Download Job" />
</caarray:actions>    
</caarray:tabPane>
