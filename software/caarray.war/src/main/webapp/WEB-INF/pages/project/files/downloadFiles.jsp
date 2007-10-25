<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">
    <div class="tableboxpad">
    <c:url value="/protected/ajax/project/files/downloadFiles.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

  <table class="searchresults">
    <tr>
      <td>
        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.files}"
                requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
                <caarray:displayTagProperties/>
                <display:column titleKey="experiment.files.name" sortProperty="name" sortable="true">
                    <c:url var="downloadUrl" value="/protected/file/download.action"/>
                  <a href="#" onclick="downloadMgr.addDownloadRow('${row.name}', '${row.id}', ${row.compressedSize})">${row.name}</a>
                </display:column>
                <display:column property="type.name" titleKey="experiment.files.type" sortable="true" />
                <display:column titleKey="experiment.files.extension" sortable="true">
                  .${fn:split(row.name, ".")[fn:length(fn:split(row.name, ".")) - 1]}
                </display:column>
                <display:column titleKey="experiment.files.compressedSize" sortProperty="compressedSize" sortable="true">
                  <fmt:formatNumber value="${row.compressedSize / 1024}" maxFractionDigits="0"/>
                </display:column>
                <display:column titleKey="experiment.files.uncompressedSize" sortProperty="uncompressedSize" sortable="true">
                  <fmt:formatNumber value="${row.uncompressedSize / 1024}" maxFractionDigits="0"/>
                </display:column>
                <display:setProperty name="paging.banner.placement" value="bottom"/>
            </display:table>
        </ajax:displayTag>
        </td>
        <td width="100%" style="padding-left: 10px;">
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
    <a href="javascript:downloadMgr.resetDownloadInfo();" class="save"><img src="<c:url value="/images/btn_cancel.gif"/>" alt="Cancel"></a>
    <a href="javascript:downloadMgr.doDownloadFiles();" class="save"><img src="<c:url value="/images/btn_launchjob.gif"/>" alt="Launch Job"></a>
    </div>
</caarray:tabPane>
<c:url var="downloadUrl" value="/protected/project/files/download.action"/>
<c:url var="removeUrl" value="/images/ico_remove.gif"/>
<script type="text/javascript">
<!--
  downloadMgr = new DownloadMgr('${downloadUrl}', '${removeUrl}');
  downloadMgr.resetDownloadInfo();
-->
</script>
