<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="ExperimentContact"/>

    <c:url value="/ajax/project/listTab/ExperimentContacts/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <c:set var="canWriteProject" value="${caarrayfn:canWrite(project, caarrayfn:currentUser())}"/>
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
             <display:column titleKey="currentExperimentContact.person.firstName" sortable="true" sortProperty="FIRST_NAME">
                 <caarray:projectListTabActionLink linkContent="${!empty row.person.firstName ? row.person.firstName : 'View'}" entityName="ExperimentContact" action="view" itemId="${row.id}" maxWidth="30"/>
            </display:column>
            <display:column property="person.lastName" sortProperty="LAST_NAME" titleKey="currentExperimentContact.person.lastName" sortable="true" maxLength="30"/>
            <display:column property="person.email" sortProperty="EMAIL" titleKey="currentExperimentContact.person.email" sortable="true" maxLength="30"/>
            <display:column property="person.phone" sortProperty="PHONE" titleKey="currentExperimentContact.person.phone" sortable="true" maxLength="30"/>
            <display:column property="roleNames" titleKey="currentExperimentContact.roleNames" maxLength="30"/>
            <caarray:projectListTabActionColumns entityName="ExperimentContact" item="${row}" actions="!edit,!delete"
                canWriteProject="${canWriteProject}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>