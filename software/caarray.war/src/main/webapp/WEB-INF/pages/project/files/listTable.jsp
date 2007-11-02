<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/project/files/listTable.action" var="sortUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:set var="checkboxAll" value="<input type=\"checkbox\" name=\"selectAllCheckbox\" onClick=\"javascript: selectAll(this, document.getElementById('selectFilesForm'));\" >" />
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.files}"
        requestURI="${sortUrl}" sort="list" id="row" excludedParams="project.id">
        <caarray:displayTagProperties/>
        <display:column title="${checkboxAll}">
            <s:checkbox name="selectedFiles" fieldValue="${row.id}" value="false" theme="simple" />
        </display:column>
        <display:column property="name" titleKey="experiment.files.name" sortable="true" />
        <display:column titleKey="experiment.files.type" sortable="true">
            <c:if test="${row.type != null}">
                <fmt:message key="experiment.files.filetype.${row.type.name}" />
            </c:if>
            <c:if test="${row.type == null}">
                <fmt:message key="experiment.files.filetype.unknown" />
            </c:if>
        </display:column>
        <display:column sortProperty="status" titleKey="experiment.files.status" sortable="true" >
            <ajax:anchors target="tabboxlevel2wrapper">
                <fmt:message key="experiment.files.filestatus.${row.status}" var="statusVal">
                    <fmt:param><c:url value="/" /></fmt:param>
                </fmt:message>
                <c:choose>
                    <c:when test="${empty row.validationResult.messages}">
                        ${statusVal}
                    </c:when>
                    <c:otherwise>
                        <c:url value="/protected/ajax/project/files/validationMessages.action" var="viewMessagesUrl">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="selectedFiles" value="${row.id}" />
                        </c:url>
                        <a href="${viewMessagesUrl}">${statusVal}</a>
                    </c:otherwise>
                </c:choose>
            </ajax:anchors>
        </display:column>
    </display:table>
</ajax:displayTag>