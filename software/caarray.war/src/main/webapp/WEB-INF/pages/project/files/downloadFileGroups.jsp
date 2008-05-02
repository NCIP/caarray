<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<script type="text/javascript">
launchDownload = function(downloadUrl, downloadLink) {
    var downloadLinkDiv = $(downloadLink).up("div");
    downloadLinkDiv.innerHTML = "This download is being assembled and will begin shortly";
    new Effect.Highlight(downloadLinkDiv);
    window.location = downloadUrl;
}
</script>

<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">

<p>
The total size of the files you have selected is too large to download at once. This page allows you to download
these files in groups. Please click the download link for each file group below to download it.

</p>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${downloadFileGroups}"
                   sort="list" id="row">
        <caarray:displayTagProperties/>
        <display:column title="Group">
            ${row_rowNum} of ${fn:length(downloadFileGroups)}
        </display:column>
        <display:column title="Files in Group" property="fileNames"/>
        <display:column titleKey="experiment.files.compressedSize">
            <fmt:formatNumber value="${row.totalCompressedSize / 1024}" maxFractionDigits="0"/>
        </display:column>
        <display:column titleKey="experiment.files.uncompressedSize">
            <fmt:formatNumber value="${row.totalUncompressedSize / 1024}" maxFractionDigits="0"/>
        </display:column>
        <display:column title="Download" >
	        <c:url value="/project/files/download.action" var="actionUrl">
                <c:param name="project.id" value="${project.id}" />
                <c:param name="downloadGroupNumber" value="${row_rowNum}" />
                <c:forEach items="${selectedFileIds}" var="fileId">
                    <c:param name="selectedFileIds" value="${fileId}"/>
                </c:forEach>
    	    </c:url>
            <div>
                <a href="#" onclick="launchDownload('${actionUrl}', this)">
                    <img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>">
                </a>
            </div>
        </display:column>
        <display:setProperty name="paging.banner.placement" value="bottom"/>
    </display:table>
</ajax:displayTag>

</caarray:tabPane>
