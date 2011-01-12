<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <c:set var="privateExperiment" value="Private Experiment"/>
    <c:set var="privateUser" value="Private User"/>
    
    <display:table class="searchresults" cellspacing="0" list="${jobs}" requestURI="${sortUrl}" id="row" excludedParams="project.id" style="clear: none;">
        <c:set var="canReadExperiment" value="${row.userHasReadAccess}"/>
        <c:set var="canWriteExperiment" value="${row.userHasWriteAccess}"/>
        <c:choose>
            <c:when test="${!canReadExperiment && !canWriteExperiment}">
                <c:set var="experimentColValue" value="Private Experiment" />
                <c:set var="userColValue" value="Private User" />
            </c:when>
            <c:when test="${row.userHasOwnership}">
                <c:set var="experimentColValue" value="${row.experimentName}" />
                <c:set var="userColValue" value="${row.ownerName}" />
            </c:when>
            <c:when test="${!row.userHasOwnership && canReadExperiment}">
                <c:set var="experimentColValue" value="${row.experimentName}" />
                <c:set var="userColValue" value="${row.ownerName}" />
            </c:when>
        </c:choose>
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="jobs.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="jobs.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="jobs.pageNumber" />
        
        <display:column sortProperty="POSITION" title="Position" sortable="true" >${row.position}</display:column>
        <display:column sortProperty="USER" title="User" sortable="true" maxLength="30">${userColValue}</display:column>
        <display:column sortProperty="EXPERIMENT" title="Experiment" sortable="true" >${experimentColValue}</display:column>
        <display:column sortProperty="JOB" title="Job" sortable="true" >${row.jobType}</display:column>
        <display:column sortProperty="TIME_REQUESTED" title="Time Requested" sortable="true" >${row.timeRequested}</display:column>
        <display:column sortProperty="TIME_STARTED" title="Time Started" sortable="true" >${row.timeStarted}</display:column>
        <display:column sortProperty="STATUS" title="Status" sortable="true" >${row.status}</display:column>
    </display:table>
</ajax:displayTag>
