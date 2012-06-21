<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" list="${results}" requestURI="${sortUrl}" id="row" style="clear: none;">
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="results.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="results.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="results.pageNumber" />
        <display:column sortProperty="PUBLIC_ID" title="Experiment ID" sortable="true">
            <c:set var="canReadRow" value="${caarrayfn:canRead(row, caarrayfn:currentUser())}"/>
            <c:choose>
                <c:when test="${canReadRow}">
                    <c:url var="viewUrl" value="/project/details.action">
                        <c:param name="project.id" value="${row.id}"/>
                    </c:url>
                    <a title="View experiment ${row.experiment.publicIdentifier} in read only mode" href="${viewUrl}">${row.experiment.publicIdentifier}</a>
                </c:when>
                <c:otherwise>
                    ${row.experiment.publicIdentifier}
                </c:otherwise>
            </c:choose>
        </display:column>
                <display:column sortProperty="TITLE" titleKey="search.result.experimentTitle" sortable="true" maxLength="30"><c:out value="${row.experiment.title}" escapeXml="true"/></display:column>
        <display:column titleKey="search.result.assayTypes">
            <c:forEach items="${row.experiment.assayTypes}" var="currType" varStatus="status">
                <c:if test="${!status.first}">, </c:if>${currType.name}
            </c:forEach>
        </display:column>
        <display:column titleKey="search.result.pi">
            <c:choose>
                <c:when test="${row.experiment.mainPointOfContact.contact.email != null}">
                    <a href="mailto:${caarrayfn:encodeUrl(row.experiment.mainPointOfContact.contact.email)}?subject=${caarrayfn:encodeUrl(row.experiment.title)}" class="email">${row.experiment.mainPointOfContact.contact.lastName}<img src="<c:url value="/images/ico_sendmail.gif"/>" alt="" style="padding-left:5px" /></a>
                </c:when>
                <c:otherwise>
                    ${row.experiment.mainPointOfContact.contact.lastName}
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column property="experiment.organism.scientificName" sortProperty="ORGANISM" titleKey="search.result.organism" sortable="true"/>
        <display:column titleKey="search.result.diseaseState">
            <c:choose>
                <c:when test="${canReadRow}">
                    <jsp:useBean id="diseaseStates" class="java.util.HashMap"/>
                    <c:forEach var="condition" items="${row.experiment.sources}" varStatus="status">
                        <c:if test="${!empty condition.diseaseState.value}">
                            <c:set target="${diseaseStates}" property="${condition.diseaseState.value}"/>
                        </c:if>
                    </c:forEach>
                    <c:forEach var="diseaseState" items="${diseaseStates}" varStatus="status">
                        <c:if test="${!status.first}">, </c:if>${diseaseState.key}
                    </c:forEach>
                    <c:remove var="diseaseStates"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="browse.notAvailable"/>
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column title="Samples">
            <c:if test="${canReadRow}">
                <c:url value="/project/details.action" var="viewSamplesUrl">
                    <c:param name="project.id" value="${row.id}" />
                    <c:param name="initialTab" value="annotations" />
                    <c:param name="initialTab2" value="samples" />
                </c:url>
                <a href="${viewSamplesUrl}">${row.experiment.sampleCount}</a>
            </c:if>
        </display:column>
        <display:column sortProperty="LAST_UPDATED" titleKey="search.result.updated" sortable="true">
            <fmt:formatDate value="${row.lastUpdated}" pattern="M/d/yyyy"/>
        </display:column>
    </display:table>
</ajax:displayTag>