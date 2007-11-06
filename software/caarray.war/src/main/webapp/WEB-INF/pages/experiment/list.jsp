<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${results}" requestURI="${sortUrl}"
        sort="list" id="row" pagesize="20" style="clear: none;">
        <caarray:displayTagProperties/>
        <display:column sortProperty="title" titleKey="search.result.experimentTitle" sortable="true">
            <a href="#">${row.title}</a>
        </display:column>
        <display:column property="publicIdentifier" titleKey="search.result.experimentId" sortable="true"/>
        <display:column property="assayType" titleKey="search.result.assayType" sortable="true" />
        <display:column sortProperty="mainPointOfContact.contact.email" titleKey="search.result.pi" sortable="true">
            <a href="mailto:${row.mainPointOfContact.contact.email}" class="email">${row.mainPointOfContact.contact.lastName}<img src="images/ico_sendmail.gif" alt="" style="padding-left:5px" /></a>
        </display:column>
        <display:column property="organism.commonName" titleKey="search.result.organism" sortable="true"/>
        <display:column titleKey="search.result.diseaseState" sortable="true">
            <c:forEach var="condition" items="${row.conditions}" varStatus="status">
                ${condition.value}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        </display:column>
        <display:column sortProperty="sampleCount" titleKey="search.result.numSamples" sortable="true">
            <c:url value="/protected/project/edit.action" var="editSamplesUrl">
                <c:param name="project.id" value="${row.id}" />
                <c:param name="initialTab" value="annotations" />
                <c:param name="initialTab2" value="samples" />
            </c:url>
            <a href="${editSamplesUrl}">${row.sampleCount}</a>
        </display:column>
        <display:column property="publicReleaseDate" titleKey="search.result.updated" sortable="true"/>
    </display:table>
</ajax:displayTag>