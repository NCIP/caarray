<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<c:url var="thisUrl" value="/ajax/project/listTab/Sources/edit.action">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="currentSource.id" value="${currentSource.id}" />
</c:url>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Source" item="${currentSource}" itemName="${currentSource.name}"
        isSubtab="true">
        <c:if test="${editMode}">
            <s:textfield name="currentSource.name" key="experiment.sources.name" requiredLabel="true" size="80" tabindex="1" />
        </c:if>
        <s:textarea name="currentSource.description" key="experiment.sources.description" rows="3" cols="80" tabindex="2" />
        <s:textfield key="currentSource.externalId" size="80" tabindex="3" />
        <c:if test="${!empty currentSource.externalId}">
            <caarray:outputUrl var="permalinkUrl">
                <jsp:attribute name="url"><c:url value="/project/${project.experiment.publicIdentifier}/source/${caarrayfn:encodeUrl(currentSource.externalId)}"/></jsp:attribute>
            </caarray:outputUrl>
            <s:textfield theme="readonly" label="Source URL" value="%{#attr.permalinkUrl}">
                <s:param name="url">true</s:param>
            </s:textfield>
        </c:if>
        <caarray:termSelector baseId="tissueSite" category="<%= ExperimentOntologyCategory.ORGANISM_PART %>" tabIndex="3" termField="${currentSource.tissueSite}"
            termFieldName="currentSource.tissueSite" required="true" returnInitialTab1="annotations" returnInitialTab2="sources" returnInitialTab2Url="${thisUrl}" />
        <caarray:termSelector baseId="materialType" category="<%= ExperimentOntologyCategory.MATERIAL_TYPE %>" termField="${currentSource.materialType}"
            tabIndex="4" termFieldName="currentSource.materialType" returnInitialTab1="annotations" returnInitialTab2="sources" returnInitialTab2Url="${thisUrl}" />
        <caarray:termSelector baseId="cellType" category="<%= ExperimentOntologyCategory.CELL_TYPE %>" termField="${currentSource.cellType}"
            tabIndex="5" termFieldName="currentSource.cellType" returnInitialTab1="annotations" returnInitialTab2="sources" returnInitialTab2Url="${thisUrl}" />
        <caarray:termSelector baseId="diseaseState" category="<%= ExperimentOntologyCategory.DISEASE_STATE %>" termField="${currentSource.diseaseState}"
            tabIndex="6" termFieldName="currentSource.diseaseState" returnInitialTab1="annotations" returnInitialTab2="sources" returnInitialTab2Url="${thisUrl}" />
        <caarray:protocolSelector returnInitialTab1="annotations" returnInitialTab2="sources" returnInitialTab2Url="${thisUrl}" tabIndex1="7" tabIndex2="8" multiple="true" />
        <caarray:annotationCharacteristics item="${currentSource}"/>
        <s:hidden name="currentSource.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>

    <c:if test="${!editMode}">
        <div id="tabHeader" class="boxpad2"><h3>Download Data</h3></div>
        <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>
    </c:if>
</caarray:tabPane>
