<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/admin/import/projects.action" var="sortUrl" />
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${projects}" requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
        <caarray:displayTagProperties/>
        <c:url var="reimportProjectUrl" value="/project/details.action">
            <c:param name="project.id" value="${row.id}"/>
            <c:param name="initialTab" value="data"/>
            <c:param name="initialTab2" value="importedData"/>
        </c:url>
        <display:column title="Experiment ID" href="${reimportProjectUrl}" property="experiment.publicIdentifier"/>
        <display:column title="Experiment Title"maxLength="30" property="experiment.title" escapeXml="true"/>
    </display:table>
</ajax:displayTag>