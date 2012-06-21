<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.file.FileStatus"%>

<s:form action="/ajax/project/files/process" id="selectFilesForm" method="post" theme="simple">
  <fmt:message key="experiment.files.filterby.fileType" var="filterByTypeLabel" />
  <fmt:message key="experiment.files.filterby.status" var="filterByStatusLabel" />
  <table class="searchresults">
    <tr>
      <td>${filterByTypeLabel}:
        <s:select label="Filter By File Type"
        name="fileType"
        list="fileTypes"
        headerKey=" "
        headerValue="(All)"
        onchange="doFilter()"/>
      </td>
      <td>${filterByStatusLabel}:
        <s:select label="Filter By File Status"
          name="fileStatus"
          list="fileStatuses"
          headerKey=" "
          headerValue="(All)"
        onchange="doFilter()"/>
      </td>
    </tr>
  </table>
    <s:hidden name="project.id" value="%{project.id}" />

    <table class="searchresults">
    <tr>
      <td width="80%"><%@ include
        file="/WEB-INF/pages/project/files/listTable.jsp"%></td>
      <td width="20%" style="padding-left: 2px;">
      <table class="searchresults" id="unimportedTbl">
        <tr>
          <fmt:message key="experiment.files.status.count"
            var="statusCountTitle" />
          <th colspan="2">${statusCountTitle}</th>
        </tr>
        <c:forEach items="${fileStatusCountMap}" var="item"
          varStatus="itemStatus">
          <c:if test="${item.value > 0}">
            <c:set var="statusVal" value="${item.key}"/>
            <jsp:useBean id="statusVal" type="gov.nih.nci.caarray.domain.file.FileStatus" />
            <tr>
              <td><fmt:message
                key="experiment.files.filestatus.${statusVal}" var="statusLabel">
                <fmt:param>
                  <c:url value="/" />
                </fmt:param>
              </fmt:message> ${statusLabel}</td>
              <td>${item.value}</td>
            </tr>
          </c:if>
        </c:forEach>
      </table>

	  <br>
      <table class="searchresults">
        <tr>           
          <th><fmt:message key="experiment.files.jobQueue"/></th>
        </tr>
        <tr>
          <td><span id="jobSizeContent">0 Files, 0 KB</span></td>
        </tr>
      </table>

      </td>
    </tr>
  </table>
</s:form>
