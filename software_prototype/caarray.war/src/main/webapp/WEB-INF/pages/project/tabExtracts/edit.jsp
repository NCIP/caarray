<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<c:url var="thisUrl" value="/ajax/project/listTab/Extracts/edit.action">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="currentExtract.id" value="${currentExtract.id}" />
</c:url>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Extract" item="${currentExtract}" itemName="${currentExtract.name}"
        isSubtab="true">
        <c:if test="${editMode}">
            <s:textfield name="currentExtract.name" key="experiment.extracts.name" required="true" size="80" tabindex="1" />
        </c:if>
        <s:textarea name="currentExtract.description" key="experiment.extracts.description" rows="3" cols="80"
            tabindex="2" />
        <caarray:annotationAssociationPicker baseId="samplePicker" entityName="Extract" associatedEntityName="Sample" itemId="${currentExtract.id}" tabIndex="3" />
        <caarray:termSelector baseId="materialType" category="<%= ExperimentOntologyCategory.MATERIAL_TYPE %>" termField="${currentExtract.materialType}"
            tabIndex="4" termFieldName="currentExtract.materialType" returnInitialTab1="annotations" returnInitialTab2="extracts" returnInitialTab2Url="${thisUrl}" />
        <caarray:protocolSelector returnInitialTab1="annotations" returnInitialTab2="extracts" returnInitialTab2Url="${thisUrl}" tabIndex1="5" tabIndex2="6" />
        <caarray:annotationCharacteristics item="${currentExtract}"/>
        <s:hidden name="currentExtract.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>
    <caarray:protocolSelectorAjaxInit />
</caarray:tabPane>