<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" list="${sourceResults}" requestURI="${sortUrl}" id="row" style="clear: none;">
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="sourceResults.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="sourceResults.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="sourceResults.pageNumber" />
        <display:column titleKey="experiment.sources.name" sortable="true" sortProperty="NAME">
            <c:url var="viewSourceUrl" value="/ajax/project/listTab/Sources/view.action">
                <c:param name="project.id" value="${row.experiment.project.id}"/>
                <c:param name="currentSource.id" value="${row.id}"/>
            </c:url>
            <c:url var="projectUrl" value="/project/details.action">
                <c:param name="project.id" value="${row.experiment.project.id}"/>
                <c:param name="initialTab" value="annotations"/>
                <c:param name="initialTab2" value="sources"/>
                <c:param name="initialTab2Url" value="${viewSourceUrl}"/>
             </c:url>
             <a href="${projectUrl}"><caarray:abbreviate value="${row.name}" maxWidth="30"/></a>
        </display:column>
        <display:column property="externalId" titleKey="search.result.externalId" />
        <display:column property="description" titleKey="experiment.sources.description" />
        <display:column sortProperty="ORGANISM" titleKey="search.result.organism" sortable="true">
          <c:choose>
            <c:when test="${empty row.organism}">
              <c:out value="${row.experiment.organism.scientificName}" />
            </c:when>
            <c:otherwise>
              <c:out value="${row.organism.scientificName}" />
            </c:otherwise>
          </c:choose>
        </display:column>
        <display:column property="diseaseState.value" sortProperty="DISEASESTATE" titleKey="search.result.diseaseState" sortable="true"/>
        <display:column property="tissueSite.value" sortProperty="TISSUESITE" titleKey="search.result.tissueSite" sortable="true"/>
        <display:column property="materialType.value" sortProperty="MATERIALTYPE" titleKey="search.result.materialType" sortable="true"/>
        <display:column property="cellType.value" sortProperty="CELLTYPE" titleKey="search.result.cellType" sortable="true"/>
        <display:column titleKey="search.result.provider" sortProperty="PROVIDER_NAME" sortable="true">
            <c:forEach items="${row.providers}" var="curProvider" varStatus="status">
                ${curProvider.name}<c:if test="${!status.last}">,<br></c:if>
            </c:forEach>
        </display:column>
        <display:column titleKey="search.result.experimentTitle" sortable="true" sortProperty="TITLE">
                     <c:url var="viewExpUrl" value="/project/details.action">
                        <c:param name="project.id" value="${row.experiment.project.id}"/>
                     </c:url>
                     <a title="View experiment ${row.experiment.title} in read only mode" href="${viewExpUrl}">${row.experiment.title}</a>
        </display:column>
        <s:set name="showDownloadGroups" value="%{@gov.nih.nci.caarray.web.action.project.AbstractProjectProtocolAnnotationListTabAction@isWillPerformDownloadByGroups(#attr.row.getAllDataFiles())}"/>
        <caarray:projectListTabDownloadColumn entityName="Source" itemId="${row.id}" showDownloadGroups="${showDownloadGroups}"/>
    </display:table>
</ajax:displayTag>
