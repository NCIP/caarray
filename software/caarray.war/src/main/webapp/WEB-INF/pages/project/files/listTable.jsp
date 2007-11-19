<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/project/files/${listAction}Table.action" var="sortUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:set var="listingImported" value="${listAction == 'listImported'}"/>
<c:choose>
    <c:when test="${listingImported}">
        <c:set var="pageSize" value="20" />
    </c:when>
    <c:otherwise>
        <c:set var="pageSize" value="-1" />
    </c:otherwise>
</c:choose>
<fmt:message key="experiment.files.selectAllCheckBox" var="checkboxAll">
    <fmt:param value="selectAllCheckbox" />
    <fmt:param value="'selectFilesForm'" />
</fmt:message>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${files}" pagesize="${pageSize}"
        requestURI="${sortUrl}" sort="list" id="row" excludedParams="project.id">
        <caarray:displayTagProperties/>
        <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser()) && !listingImported}">
            <display:column title="${checkboxAll}">
                <s:checkbox name="selectedFiles" fieldValue="${row.id}" value="false" theme="simple" />
            </display:column>
        </c:if>
        <display:column property="name" titleKey="experiment.files.name" sortable="true" />
        <display:column titleKey="experiment.files.type" sortable="true">
            <c:if test="${row.fileType != null}">
                <fmt:message key="experiment.files.filetype.${row.fileType.name}" />
            </c:if>
            <c:if test="${row.fileType == null}">
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
                            <c:param name="returnAction" value="${listAction}" />
                        </c:url>
                        <a href="${viewMessagesUrl}">${statusVal}</a>
                    </c:otherwise>
                </c:choose>
            </ajax:anchors>
        </display:column>
    </display:table>
</ajax:displayTag>