<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/project/files/listTable.action" var="sortUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:set var="checkboxAll" value="<input type=\"checkbox\" name=\"selectAllCheckbox\" onClick=\"javascript: selectAll(this, document.getElementById('projectForm'));\" >" />
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.files}"
        requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
        <caarray:displayTagProperties/>
        <display:column title="${checkboxAll}">
            <s:checkbox name="selectedFiles" fieldValue="${row.id}" value="false" theme="simple" />
        </display:column>
        <display:column sortProperty="name" titleKey="experiment.files.name" sortable="true">
             <ajax:anchors target="tabboxlevel2wrapper">
                <c:url value="/protected/ajax/project/files/validationMessages.action" var="viewMessagesUrl">
                    <c:param name="project.id" value="${project.id}" />
                    <c:param name="selectedFiles" value="${row.id}" />
                </c:url>
                <a href="${viewMessagesUrl}">${row.name}</a>
            </ajax:anchors>
        </display:column>
        <display:column titleKey="experiment.files.type" sortable="true">
            <fmt:message key="experiment.filew.filetype.${row.type.name}" />
        </display:column>
        <display:column titleKey="experiment.files.status" sortable="true" >
            <fmt:message key="experiment.files.filestatus.${row.status}">
                <fmt:param><c:url value="/" /></fmt:param>
            </fmt:message>
        </display:column>
    </display:table>
</ajax:displayTag>