<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<meta http-equiv="refresh" content="30">

<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <c:set var="privateExperiment" value="Private Experiment"/>
    <c:set var="privateUser" value="Private User"/>
    <c:set var="jobQueueDatePattern" value="E MMM d h:mm a" />
    
    <display:table class="searchresults" cellspacing="0" list="${jobs}" requestURI="${sortUrl}" id="row" excludedParams="project.id" style="clear: none;">
        <c:choose>
            <c:when test="${!row.userHasReadAccess && !row.userHasWriteAccess}">
                <c:set var="experimentColValue" value="Private Experiment" />
                <c:set var="userColValue" value="Private User" />
            </c:when>
            <c:when test="${row.userHasOwnership}">
                <c:set var="experimentColValue" value="${row.jobEntityName}" />
                <c:set var="userColValue" value="${row.ownerName}" />
            </c:when>
            <c:when test="${!row.userHasOwnership && row.userHasReadAccess}">
                <c:set var="experimentColValue" value="${row.jobEntityName}" />
                <c:set var="userColValue" value="${row.ownerName}" />
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${row.jobType.arrayDesignJob}">
                <c:url var="viewEditUrl" value="/protected/arrayDesign/view.action">
                    <c:param name="arrayDesign.id" value="${row.jobEntityId}"/>
                </c:url>
            </c:when>
            <c:when test="${!row.jobType.arrayDesignJob && row.userHasWriteAccess}">
                <c:url var="viewEditUrl" value="/protected/project/edit.action">
                    <c:param name="project.id" value="${row.jobEntityId}"/>
                </c:url>
            </c:when>
            <c:when test="${!row.jobType.arrayDesignJob && !row.userHasWriteAccess && row.userHasReadAccess}">
                <c:url var="viewEditUrl" value="/project/details.action">
                    <c:param name="project.id" value="${row.jobEntityId}"/>
                </c:url>
            </c:when>
        </c:choose>
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="jobs.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="jobs.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="jobs.pageNumber" />
        
        <display:column sortProperty="POSITION" title="Position" sortable="false" >${row.position}</display:column>
        <display:column sortProperty="USER" title="User" sortable="false" maxLength="30">${userColValue}</display:column>
        <display:column sortProperty="EXPERIMENT" title="Experiment/Array Design" sortable="false" >
            <c:choose>
                <c:when test="${row.userHasReadAccess || row.userHasWriteAccess}">
                    <a href="${viewEditUrl}">${experimentColValue}</a>
                </c:when>
                <c:otherwise>${experimentColValue}</c:otherwise>
            </c:choose>
        </display:column>
        <display:column sortProperty="JOB" title="Job" sortable="false" >${row.jobType.displayValue}</display:column>
        <display:column sortProperty="TIME_REQUESTED" title="Time Requested" sortable="false">
          <fmt:formatDate value="${row.timeRequested}" pattern="${jobQueueDatePattern}"/>
        </display:column>
        <display:column sortProperty="TIME_STARTED" title="Time Started" sortable="false" >
          <fmt:formatDate value="${row.timeStarted}" pattern="${jobQueueDatePattern}"/>
        </display:column>
        <display:column sortProperty="STATUS" title="Status" sortable="false" >
          ${row.jobStatus.displayValue}
          <c:if test="${!empty row.parent}">
            <div class="tooltip small">
              (<c:out value="${row.jobsProcessed}"/>/<c:out value="${fn:length(row.children)}"/> Processed)
              <span>
                <c:forEach var="entry" items="${row.statusCounts}">
                  <c:if test="${entry.value > 0}">
                    <c:out value="${entry.value} ${entry.key.displayValue}"/><br/>
                  </c:if>
                </c:forEach>
              </span>
            </div>
          </c:if>
        </display:column>
        <c:url value="/protected/project/cancelJob.action" var="cancelJobUrl">
            <c:param name="jobId" value="${row.jobId}" />     
        </c:url>
        <display:column title="Action" sortable="false" >
            <c:if test="${row.userCanCancelJob}">
                <a href="${cancelJobUrl}">
                	<img src="<c:url value="/images/ico_cancel.gif"/>" 
                		 alt="Cancel" 
                		 title="Cancel"/>
               	</a>
            </c:if>
        </display:column>
    </display:table>
</ajax:displayTag>
