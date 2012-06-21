<%@ tag display-name="downloadFilesListTable" description="Renders the files available for download" body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/caarray-functions.tld" prefix="caarrayfn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<%@ attribute name="actionDownloadFilesTableListSortUrl" required="true" type="java.lang.String"%>
<%@ attribute name="files" required="true" type="java.util.Collection"%>

<c:url value="${actionDownloadFilesTableListSortUrl}" var="sortUrl">
  <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url var="addIco" value="/images/ico_add.gif"/>
<c:url var="removeIco" value="/images/ico_remove.gif"/>
<fmt:message key="experiment.files.selectAll" var="addAll">
    <fmt:param value="${addIco}"/>
</fmt:message>
<ajax:displayTag id="datatable_downloadFilesListTable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="displayTablePostFuncCallback">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${files}"
        requestURI="${sortUrl}" sort="list" id="row" excludedParams="project.id">
        <caarray:displayTagProperties/>
        <display:column title="${addAll}">
           <a href="#" id="fileRow${row.id}">
               <img id="rIcon${row.id}" 
               	 	src="<c:url value="/images/ico_add.gif"/>" 
               	 	onclick="downloadMgr.addDownloadRow('${row.id}'); return false;" 
               	 	alt="Add ${row.name}"
               	 	title="Add ${row.name}"/>
           </a>
        </display:column>
        <display:column titleKey="experiment.files.name" property="name" sortable="true"/>
        <display:column property="fileType.name" titleKey="experiment.files.type" sortable="true" />
        <display:column titleKey="experiment.files.extension" sortable="true">
          .${fn:split(row.name, ".")[fn:length(fn:split(row.name, ".")) - 1]}
        </display:column>
        <display:column titleKey="experiment.files.compressedSize" sortProperty="compressedSize" sortable="true">
            <caarray:formatFileSize value="${row.compressedSize}"/>
        </display:column>
        <display:column titleKey="experiment.files.uncompressedSize" sortProperty="uncompressedSize" sortable="true">
          <caarray:formatFileSize value="${row.uncompressedSize}"/>
        </display:column>
        <display:setProperty name="paging.banner.placement" value="bottom"/>
    </display:table>
</ajax:displayTag>

<script type="text/javascript">
displayTablePostFuncCallback = function() {
    	TabUtils.hideLoadingText();
    	downloadMgr.reApplySelection();
}
downloadMgr.resetAllFiles();
<c:forEach items="${files}" var="file">
    downloadMgr.addFile('${caarrayfn:escapeJavaScript(file.name)}', '${file.id}', ${file.compressedSize});
    if (downloadMgr.inQueue('${file.id}')) {
        var fileCell = $('fileRow' + '${file.id}')
        if (fileCell) {
            fileCell.innerHTML = '<img id="rIcon${file.id}" src="${removeIco}" alt="remove" title="remove" onclick="downloadMgr.removeRow(${file.id}); return false;"/>';
        }
    }
</c:forEach>
</script>