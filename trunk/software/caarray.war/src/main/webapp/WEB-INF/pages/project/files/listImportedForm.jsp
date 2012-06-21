<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
    <fmt:message key="experiment.files.filterby.fileType" var="filterByTypeLabel" />
    <s:form action="/ajax/project/files/process" id="selectFilesForm" method="post" theme="simple">
    <div class="tableboxpad" style="overflow:auto; max-height:500px">
        <table class="searchresults">
            <tr>
                <td>${filterByTypeLabel}:
                  <s:select label="Filter By File Type"
                    name="fileType"
                    list="fileTypes"
                    headerKey=" "
                    headerValue="(All)"
                    onchange="doImportedFilter()"/>
                </td>
            </tr>
        </table>
        <s:hidden name="project.id" value="%{project.id}" />
        <table class="searchresults">
            <tr>
                <td>
                    <%@ include file="/WEB-INF/pages/project/files/listTable.jsp" %>
                </td>
            </tr>
        </table>
    </div>
</s:form>