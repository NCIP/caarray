<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/ajax/project/files/${listAction}Table.action" var="sortUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:set var="listingImported" value="${listAction == 'listImported'}"/>

<c:set var="canWriteProject" value="${caarrayfn:canWrite(project, caarrayfn:currentUser())}"/>
<c:choose>
    <c:when test="${!project.locked && canWriteProject}">
        <c:set var="defaultSortVal" value="2" />
    </c:when>
    <c:otherwise>
        <c:set var="defaultSortVal" value="1" />
    </c:otherwise>
</c:choose>
<s:hidden name="selectedFileIds" value=""/>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="${defaultSortVal}" list="${files}" requestURI="${sortUrl}"
        sort="list" id="row" excludedParams="project.id selectedFileIds __checkbox_selectedFileIds">
        <caarray:displayTagProperties/>
        <c:if test="${!project.locked && canWriteProject}">
           <display:column title="<input type='checkbox' name='selectAllCheckbox' onclick='toggleAllFiles(this.checked, $(&quot;selectFilesForm&quot;));' >">
             <c:set var="boxvalue" value="false"/>
             <c:forEach items="${selectedFileIds}" var="sid">
              <c:if test="${sid == row.id}">
                <c:set var="boxvalue" value="true"/>
              </c:if>
            </c:forEach>
            <s:checkbox id="chk%{#attr.row.id}" name="selectedFileIds" disabled="%{!(#attr.row.importable || #attr.row.validatable || deletableFiles[#attr.row] == true)}"
                   fieldValue="%{#attr.row.id}" value="%{#attr.boxvalue}" theme="simple"
                   onclick="toggleFileInJob(this.checked, this.value);"/>

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
                        <c:url value="/ajax/project/files/validationMessages.action" var="viewMessagesUrl">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="selectedFiles" value="${row.id}" />
                            <c:param name="returnAction" value="${listAction}" />
                        </c:url>
                        <a href="${viewMessagesUrl}">${statusVal}</a>
                    </c:otherwise>
                </c:choose>
            </ajax:anchors>
        </display:column>
        <display:column titleKey="experiment.files.compressedSize" sortProperty="compressedSize" sortable="true">
            <caarray:formatFileSize value="${row.compressedSize}"/>
        </display:column>
        <display:column titleKey="experiment.files.uncompressedSize" sortProperty="uncompressedSize" sortable="true">
          <caarray:formatFileSize value="${row.uncompressedSize}"/>
        </display:column>
    </display:table>
</ajax:displayTag>