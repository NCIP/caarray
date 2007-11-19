<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:form action="/ajax/project/files/process" id="selectFilesForm" method="post" theme="simple">
	<s:select label="Filter By"
			  name="fileType"
			  list="@gov.nih.nci.caarray.domain.file.FileType@values()"
			  headerKey=" "
			  headerValue="(All)"
			  onchange="doFilter()"/> 
    <s:hidden name="project.id" value="${project.id}" />
    <%@ include file="/WEB-INF/pages/project/files/listTable.jsp" %>
</s:form>
