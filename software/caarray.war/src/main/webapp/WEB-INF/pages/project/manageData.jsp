<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <c:url var="addUrl" value="/protected/File_manage.action">
   	    <c:param name="project.id" value="${proposal.project.id}"/>
    </c:url>
	<div class="boxpad2">
		<h3><fmt:message key="project.tabs.manageData"/></h3>
        <div class="addlink">
            <ajax:anchors target="tabboxlevel2wrapper">
                <a href="${addUrl}" class="add">
                    <fmt:message key="experiment.data.upload" />
                </a>
            </ajax:anchors>
        </div>
	</div>
	
	<c:url value="ajax_Project_loadGenericTab_manageData.action" var="sortUrl">
        <c:param name="proposalKey" value="${proposalKey}" />
        <c:param name="ajax" value="true" />
    </c:url>
	
    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${proposal.project.files}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="proposalKey">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.files.name" sortable="true"/>
            <display:column property="type.name" titleKey="experiment.files.type" sortable="true" />
            <display:column property="status" titleKey="experiment.files.status" sortable="true" />
        </display:table>
    </ajax:displayTag>
    </div>
</caarray:tabPane>
