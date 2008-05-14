<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/ajax/project/files/downloadFilesListTable.action" var="sortUrl">
  <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url var="addIco" value="/images/ico_add.gif"/>
<fmt:message key="experiment.files.selectAll" var="addAll">
    <fmt:param value="${addIco}"/>
</fmt:message>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${files}"
        requestURI="${sortUrl}" sort="list" id="row" excludedParams="project.id">
        <caarray:displayTagProperties/>
        <display:column title="${addAll}">
          <a href="#" id="fileRow${row.id}" onclick="downloadMgr.addDownloadRow('${row.id}'); return false;">
            <img src="<c:url value="/images/ico_add.gif"/>" alt="Add ${row.name}"/>
          </a>
        </display:column>
        <display:column titleKey="experiment.files.name" property="name" sortable="true"/>
        <display:column property="fileType.name" titleKey="experiment.files.type" sortable="true" />
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

<script type="text/javascript">
<c:forEach items="${files}" var="file"> 
    downloadMgr.addFile('${file.name}', '${file.id}', ${file.compressedSize});
</c:forEach>
</script>