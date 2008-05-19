<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<script type="text/javascript">
groupDownloadAllFiles = new Array();

<c:forEach items="${selectedFiles}" var="file">
groupDownloadAllFiles.push('${file.id}');
</c:forEach>

launchGroupDownload = function(downloadUrl, downloadLink) {
    var downloadLinkDiv = $(downloadLink).up("div");
    downloadLinkDiv.innerHTML = "This download is being assembled and will begin shortly";
    new Effect.Highlight(downloadLinkDiv);

    var form = document.createElement("form");
    form.method="post";
    form.style.display="none";
    form.action=downloadUrl;
    for (i = 0; i < groupDownloadAllFiles.length; ++i) {
        var elt = document.createElement("input");
        elt.type="hidden";
        elt.name="selectedFileIds";
        elt.value=groupDownloadAllFiles[i];
        form.appendChild(elt);
    }

    document.body.appendChild(form);
    form.submit();
    $(form).remove();
}
</script>

<caarray:tabPane paneTitleKey="project.tabs.downloadData" subtab="true">

<p>
The total size of the files you have selected is too large to download at once. This page allows you to download
these files in groups. Please click the download link for each file group below to download it.

</p>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" list="${downloadFileGroups}" id="row">
        <caarray:displayTagProperties/>
        <display:column title="Group">
            ${row_rowNum} of ${fn:length(downloadFileGroups)}
        </display:column>
        <display:column title="Files in Group" property="fileNames"/>
        <display:column titleKey="experiment.files.compressedSize">
            <caarray:formatFileSize value="${row.totalCompressedSize}"/>
        </display:column>
        <display:column titleKey="experiment.files.uncompressedSize">
            <caarray:formatFileSize value="${row.totalUncompressedSize}"/>
        </display:column>
        <display:column title="Download" >
          <c:url value="/project/files/download.action" var="actionUrl">
                <c:param name="project.id" value="${project.id}" />
                <c:param name="downloadGroupNumber" value="${row_rowNum}" />
          </c:url>
            <div>
                <a href="#" onclick="launchGroupDownload('${actionUrl}', this)">
                    <img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>">
                </a>
            </div>
        </display:column>
        <display:setProperty name="paging.banner.placement" value="bottom"/>
    </display:table>
</ajax:displayTag>

</caarray:tabPane>
