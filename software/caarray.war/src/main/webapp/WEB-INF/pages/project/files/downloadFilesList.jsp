<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<div id="downloadFilesList" style="display: inline;">
  <s:form action="/ajax/project/files/downloadFilesList.action" id="extensionForm">
    <s:select label="Filter By"
          name="extensionFilter"
          list="allExtensions"
          headerKey=" "
          headerValue="(All)"
          onchange="downloadMgr.resetAddAll(); Caarray.submitAjaxForm('extensionForm', 'downloadFilesList')"/>
    <s:hidden name="project.id" value="${project.id}" />
        <input type="submit" class="enableEnterSubmit"/>
  </s:form>
  <%@ include file="/WEB-INF/pages/project/files/downloadFilesListTable.jsp" %>
</div>