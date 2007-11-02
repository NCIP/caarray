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
    <s:form action="ajax/project/listTab/Sources/saveList" cssClass="form" id="projectForm" theme="simple">
        <s:hidden name="project.id" />
    </s:form>
    </div>
    <caarray:actions divclass="actionsthin">
        <caarray:action onclick="downloadMgr.resetDownloadInfo();" actionClass="cancel" text="Cancel" />
        <caarray:action onclick="downloadMgr.doDownloadFiles();" actionClass="launch_download" text="Launch Download Job" />
    </caarray:actions>
</caarray:tabPane>
<script type="text/javascript">
<!--
  downloadMgr.resetDownloadInfo();
-->
</script>