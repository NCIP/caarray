<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<c:url var="thisUrl" value="/ajax/project/listTab/LabeledExtracts/edit.action">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="currentLabeledExtract.id" value="${currentLabeledExtract.id}" />
</c:url>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="LabeledExtract" item="${currentLabeledExtract}" itemName="${currentLabeledExtract.name}"
        isSubtab="true">
        <c:if test="${editMode}">
            <s:textfield name="currentLabeledExtract.name" key="experiment.labeledExtracts.name" requiredLabel="true" size="80" tabindex="1" />
        </c:if>
        <s:textarea name="currentLabeledExtract.description" key="experiment.labeledExtracts.description" rows="3" cols="80"
            tabindex="2" />
        <s:textfield key="currentLabeledExtract.externalId" size="80" tabindex="3" />
        <c:if test="${!empty currentLabeledExtract.externalId}">
            <caarray:outputUrl var="permalinkUrl">
                <jsp:attribute name="url"><c:url value="/project/${project.experiment.publicIdentifier}/labeledExtract/${caarrayfn:encodeUrl(currentLabeledExtract.externalId)}"/></jsp:attribute>
            </caarray:outputUrl>
            <s:textfield theme="readonly" label="Sample URL" value="%{#attr.permalinkUrl}">
                <s:param name="url">true</s:param>
            </s:textfield>
        </c:if>
        <caarray:annotationAssociationPicker baseId="extractPicker" entityName="LabeledExtract" associatedEntityName="Extract" itemId="${currentLabeledExtract.id}" tabIndex="3" />
        <caarray:termSelector baseId="materialType" category="<%= ExperimentOntologyCategory.MATERIAL_TYPE %>" termField="${currentLabeledExtract.materialType}"
            tabIndex="4" termFieldName="currentLabeledExtract.materialType" returnInitialTab1="annotations" returnInitialTab2="labeledExtracts" returnInitialTab2Url="${thisUrl}" />
        <caarray:protocolSelector returnInitialTab1="annotations" returnInitialTab2="labeledExtracts" returnInitialTab2Url="${thisUrl}" tabIndex1="5" tabIndex2="6" multiple="true" />
        <caarray:annotationCharacteristics item="${currentLabeledExtract}"/>
        <s:textfield theme="readonly" label="Label" name="currentLabeledExtract.label.value"/>
        <s:hidden name="currentLabeledExtract.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>

    <c:if test="${!editMode}">
        <div id="tabHeader" class="boxpad2"><h3>Download Data</h3></div>
        <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>
    </c:if>
</caarray:tabPane>
