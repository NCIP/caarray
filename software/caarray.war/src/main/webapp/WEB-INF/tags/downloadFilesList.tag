<%@ tag display-name="downloadFilesList" description="Renders the files available for download" body-content="scriptless"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<%@ attribute name="actionDownloadFilesList" required="true" type="java.lang.String"%>
<%@ attribute name="fileTypes" required="true" type="java.util.Collection"%>

<div id="downloadFilesList" style="display: inline;">
  <s:form action="${actionDownloadFilesList}" id="extensionForm" theme="xhtml">
    <s:select label="Filter By"
          name="extensionFilter"
          list="#attr.fileTypes"
          headerKey=" "
          headerValue="(All)"
          onchange="Caarray.submitAjaxForm('extensionForm', 'downloadFilesList')"/>
    <s:hidden name="project.id" value="${project.id}" />
    <s:hidden name="currentSource.id" />
    <s:hidden name="currentSample.id" />
    <s:hidden name="currentExtract.id" />
    <s:hidden name="currentLabeledExtract.id" />
    <s:hidden name="currentHybridization.id" />
    <input type="submit" class="enableEnterSubmit"/>
  </s:form>
  <caarray:focusFirstElement formId="extensionForm"/>
  <jsp:doBody/>
</div>