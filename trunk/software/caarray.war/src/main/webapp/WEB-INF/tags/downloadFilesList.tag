<%@ tag display-name="downloadFilesList" description="Renders the files available for download" body-content="scriptless"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="actionDownloadFilesList" required="true" type="java.lang.String"%>
<%@ attribute name="fileTypes" required="true" type="java.util.Set"%>
<%@ attribute name="fileStatuses" required="true" type="java.util.List"%>

<span id="downloadFilesList">
  <s:form action="%{#attr.actionDownloadFilesList}" id="fileTypeForm" theme="simple">
  <fmt:message key="experiment.files.filterby.fileType" var="filterByTypeLabel" />
  <fmt:message key="experiment.files.filterby.status" var="filterByStatusLabel" />
    <table class="searchresults">
        <tr>
            <td>${filterByTypeLabel}:
                <s:select label="Filter By File Type"
                    name="fileType"
                    list="#attr.fileTypes"
                    headerKey=" "
                    headerValue="(All)"
                    onchange="Caarray.submitAjaxForm('fileTypeForm', 'downloadFilesList')"/>
            </td>
            <td>${filterByStatusLabel}:
                <s:select label="Filter By File Status"
                    name="fileStatus"
                    list="#attr.fileStatuses"
                    headerKey=" "
                    headerValue="(All)"
                    onchange="Caarray.submitAjaxForm('fileTypeForm', 'downloadFilesList')"/>
            </td>
        </tr>
    </table>
    <s:hidden name="project.id" value="%{project.id}" />
    <s:hidden name="currentSource.id" />
    <s:hidden name="currentSample.id" />
    <s:hidden name="currentExtract.id" />
    <s:hidden name="currentLabeledExtract.id" />
    <s:hidden name="currentHybridization.id" />
    <input type="submit" class="enableEnterSubmit"/>
  </s:form>
  <caarray:focusFirstElement formId="fileTypeForm"/>
  <jsp:doBody/>
</span>
