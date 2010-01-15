<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:set var="initTab" value="${sessionScope.initialTab2 == null ? param.initialTab2 : sessionScope.initialTab2}" />
<c:remove var="initialTab2" scope="session" />

<c:url value="/protected/ajax/project/files/listUnimported.action" var="unimportedDataUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>
<c:url value="/ajax/project/files/listImported.action" var="importedDataUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/ajax/project/files/listSupplemental.action" var="supplementalFilesUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<c:url value="/ajax/project/files/downloadOptions.action" var="downloadDataUrl">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="editMode" value="${editMode}" />
</c:url>

<fmt:message key="project.tabs.unimportedFiles" var="unimportedDataTitle" />
<fmt:message key="project.tabs.importedFiles" var="importedDataTitle" />
<fmt:message key="project.tabs.supplementalFiles" var="supplementalDataTitle" />
<fmt:message key="project.tabs.downloadData" var="downloadDataTitle" />

<c:choose>
    <c:when test="${!empty param.initialTab2Url}">
        <c:set var="initUrl" value="${param.initialTab2Url}"/>
        <c:remove var="initialTab2Url" scope="session"/>
    </c:when>
    <c:when test="${!empty initialTab2Url}">
        <c:set var="initUrl" value="${initialTab2Url}"/>
        <c:remove var="initialTab2Url" scope="session"/>
    </c:when>
    <c:when test="${initTab == 'unimportedData'}">
        <c:set var="initUrl" value="${unimportedDataUrl}"/>
    </c:when>
    <c:when test="${initTab == 'importedData'}">
        <c:set var="initUrl" value="${importedDataUrl}"/>
    </c:when>
    <c:when test="${initTab == 'supplementalFiles'}">
        <c:set var="initUrl" value="${supplementalFilesUrl}"/>
    </c:when>
    <c:when test="${initTab == 'downloadData'}">
        <c:set var="initUrl" value="${downloadDataUrl}"/>
    </c:when>
    <c:when test="${pageContext.request.remoteUser != null}">
        <c:set var="initUrl" value="${unimportedDataUrl}"/>
    </c:when>
    <c:otherwise>
        <%-- handles ${pageContext.request.remoteUser == null} and all defaults --%>
        <c:set var="initUrl" value="${downloadDataUrl}"/>
    </c:otherwise>
</c:choose>

<ajax:tabPanel panelStyleId="tablevel2" panelStyleClass="tablevel2" currentStyleClass="selected" contentStyleId="tabboxlevel2wrapper" contentStyleClass="tabboxlevel2wrapper"
        postFunction="TabUtils.setSelectedLevel2Tab" preFunction="TabUtils.preFunction">
    <c:if test="${pageContext.request.remoteUser != null}">
        <caarray:tab caption="${unimportedDataTitle}" baseUrl="${unimportedDataUrl}" defaultTab="${initTab == null || initTab == 'unimportedData'}" />
    </c:if>
    <caarray:tab caption="${importedDataTitle}" baseUrl="${importedDataUrl}" defaultTab="${initTab == 'importedData'}" />
    <caarray:tab caption="${supplementalDataTitle}" baseUrl="${supplementalFilesUrl}" defaultTab="${initTab == 'supplementalFiles'}" />
    <c:if test="${pageContext.request.remoteUser != null}">
        <caarray:tab caption="${downloadDataTitle}" baseUrl="${downloadDataUrl}" defaultTab="${initTab == 'downloadData'}" />
    </c:if>
    <c:if test="${pageContext.request.remoteUser == null}">
        <caarray:tab caption="${downloadDataTitle}" baseUrl="${downloadDataUrl}" defaultTab="${initTab == null || initTab == 'downloadData'}" />
    </c:if>
    
</ajax:tabPanel>

<script type="text/javascript">
<%-- wrapped the '${initUrl}' with a decodeURIComponent call to decode the initialTab2Url that is encoded otherwise the page context is prefixed (url activated is '/caarray/project/%2Fcaarray%2Fajax%2Fproject%2...' notice the incorrect path)--%>
executeAjaxTab_tablevel2(null,'selected', decodeURIComponent('${initUrl}'), '');
</script>
