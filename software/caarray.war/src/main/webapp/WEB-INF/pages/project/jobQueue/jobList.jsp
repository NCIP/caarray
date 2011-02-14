<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<meta http-equiv="refresh" content="30">

<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <c:set var="privateExperiment" value="Private Experiment"/>
    <c:set var="privateUser" value="Private User"/>
    <c:set var="jobQueueDatePattern" value="E MMM d h:mm a" />
    
    <display:table class="searchresults" cellspacing="0" list="${jobs}" requestURI="${sortUrl}" id="row" excludedParams="project.id" style="clear: none;">
        <c:set var="canReadExperiment" value="${row.userHasReadAccess}"/>
        <c:set var="canWriteExperiment" value="${row.userHasWriteAccess}"/>
        <c:set var="arrayDesignJob" value="${row.jobType.arrayDesignJob}"/>
        <c:choose>
            <c:when test="${!canReadExperiment && !canWriteExperiment}">
                <c:set var="experimentColValue" value="Private Experiment" />
                <c:set var="userColValue" value="Private User" />
            </c:when>
            <c:when test="${row.userHasOwnership}">
                <c:set var="experimentColValue" value="${row.jobEntityName}" />
                <c:set var="userColValue" value="${row.ownerName}" />
            </c:when>
            <c:when test="${!row.userHasOwnership && canReadExperiment}">
                <c:set var="experimentColValue" value="${row.jobEntityName}" />
                <c:set var="userColValue" value="${row.ownerName}" />
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${!row.originalJob.inProgress && canWriteExperiment}">
                <c:set var="canCancel" value="true" />
            </c:when>
            <c:otherwise>
                <c:set var="canCancel" value="false" />
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${arrayDesignJob}">
                <c:url var="viewEditUrl" value="/protected/arrayDesign/view.action">
                    <c:param name="arrayDesign.id" value="${row.jobEntityId}"/>
                </c:url>
            </c:when>
            <c:when test="${!arrayDesignJob && canWriteExperiment}">
                <c:url var="viewEditUrl" value="/protected/project/edit.action">
                    <c:param name="project.id" value="${row.jobEntityId}"/>
                </c:url>
            </c:when>
            <c:when test="${!arrayDesignJob && !canWriteExperiment && canReadExperiment}">
                <c:url var="viewEditUrl" value="/project/details.action">
                    <c:param name="project.id" value="${row.jobEntityId}"/>
                </c:url>
            </c:when>
        </c:choose>
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="jobs.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="jobs.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="jobs.pageNumber" />
        
        <display:column sortProperty="POSITION" title="Position" sortable="true" >${row.position}</display:column>
        <display:column sortProperty="USER" title="User" sortable="true" maxLength="30">${userColValue}</display:column>
        <display:column sortProperty="EXPERIMENT" title="Experiment/Array Design" sortable="true" >
            <c:choose>
                <c:when test="${canReadExperiment || canWriteExperiment}">
                    <a href="${viewEditUrl}">${experimentColValue}</a>
                </c:when>
                <c:otherwise>${experimentColValue}</c:otherwise>
            </c:choose>
        </display:column>
        <display:column sortProperty="JOB" title="Job" sortable="true" >${row.jobType.displayValue}</display:column>
        <display:column sortProperty="TIME_REQUESTED" title="Time Requested" sortable="true">
          <fmt:formatDate value="${row.timeRequested}" pattern="${jobQueueDatePattern}"/>
        </display:column>
        <display:column sortProperty="TIME_STARTED" title="Time Started" sortable="true" >
          <fmt:formatDate value="${row.timeStarted}" pattern="${jobQueueDatePattern}"/>
        </display:column>
        <display:column sortProperty="STATUS" title="Status" sortable="true" >${row.status.displayValue}</display:column>
        <c:url value="/protected/project/cancelJob.action" var="cancelJobUrl">
            <c:param name="jobId" value="${row.jobId}" />     
        </c:url>
        <display:column title="Action" sortable="false" >
            <c:if test="${canCancel}">
                <a href="${cancelJobUrl}"><img src="<c:url value="/images/ico_cancel.gif"/>" alt="Cancel" /></a>
            </c:if>
        </display:column>
    </display:table>
</ajax:displayTag>
