<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
    <display:table class="searchresults" cellspacing="0" list="${results}" requestURI="${sortUrl}" id="row" style="clear: none;">
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="results.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="results.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="results.pageNumber" />
        <display:column sortProperty="experiment.publicIdentifier" title="Experiment ID" sortable="true">
            <c:choose>
                <c:when test="${caarrayfn:canRead(row, caarrayfn:currentUser())}">
                    <c:url var="viewUrl" value="/project/details.action">
                        <c:param name="project.id" value="${row.id}"/>
                    </c:url>                
                </c:when>
                <c:otherwise>
                    <c:url var="viewUrl" value="/project/browse.action">
                        <c:param name="project.id" value="${row.id}"/>
                    </c:url>                                
                </c:otherwise>
            </c:choose>        
            <a href="${viewUrl}">${row.experiment.publicIdentifier}</a>
        </display:column>
        <display:column property="experiment.title" titleKey="search.result.experimentTitle" sortable="true"/>
        <display:column property="experiment.assayType" titleKey="search.result.assayType" sortable="true" />
        <display:column titleKey="search.result.pi">
            <a href="mailto:${row.experiment.mainPointOfContact.contact.email}" class="email">${row.experiment.mainPointOfContact.contact.lastName}<img src="images/ico_sendmail.gif" alt="" style="padding-left:5px" /></a>
        </display:column>
        <display:column property="experiment.organism.commonName" titleKey="search.result.organism" sortable="true"/>
        <display:column titleKey="search.result.diseaseState">
            <c:forEach var="condition" items="${row.experiment.conditions}" varStatus="status">
                ${condition.value}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        </display:column>
        <display:column titleKey="search.result.numSamples">
            <c:url value="/project/details.action" var="editSamplesUrl">
                <c:param name="project.id" value="${row.id}" />
                <c:param name="initialTab" value="annotations" />
                <c:param name="initialTab2" value="samples" />
            </c:url>
            <a href="${editSamplesUrl}">${row.experiment.sampleCount}</a>
        </display:column>
        <display:column sortProperty="lastUpdated" titleKey="search.result.updated" sortable="true">
            <fmt:formatDate value="${row.lastUpdated}" pattern="M/d/yyyy"/>
        </display:column>
    </display:table>
</ajax:displayTag>