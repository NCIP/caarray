<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/admin/import/projects.action" var="sortUrl" />
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${projects}" requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
        <caarray:displayTagProperties/>
        <display:column sortProperty="PUBLIC_ID" title="Experiment ID" >
            <c:url var="reimportProjectUrl" value="/project/details.action">
                <c:param name="project.id" value="${row.id}"/>
                <c:param name="initialTab" value="data"/>
                <c:param name="initialTab2" value="importedData"/>
            </c:url>
            <a href="${reimportProjectUrl}" title="View imported files for ${row.experiment.publicIdentifier}">${row.experiment.publicIdentifier}</a>
         </display:column>
        <display:column sortProperty="TITLE" title="Experiment Title"maxLength="30"><c:out value="${row.experiment.title}" escapeXml="true"/></display:column>
    </display:table>
</ajax:displayTag>