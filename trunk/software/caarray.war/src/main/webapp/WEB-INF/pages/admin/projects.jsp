<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/admin/import/projects.action" var="sortUrl" />
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${projects}" requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
        <caarray:displayTagProperties/>
        <display:column title="Experiment ID" url="/project/details.action?initialTab=data&initialTab2=importedData" paramId="project.id" paramProperty="id" property="experiment.publicIdentifier"/>
        <display:column title="Experiment Title"maxLength="30" property="experiment.title" escapeXml="true"/>
    </display:table>
</ajax:displayTag>