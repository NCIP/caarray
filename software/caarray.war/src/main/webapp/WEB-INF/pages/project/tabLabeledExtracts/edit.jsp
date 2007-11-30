<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<c:url var="thisUrl" value="/ajax/project/listTab/LabeledExtracts/edit.action">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="currentLabeledExtract.id" value="${currentLabeledExtract.id}" />
</c:url>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="LabeledExtract" item="${currentLabeledExtract}" itemName="${currentLabeledExtract.name}"
        isSubtab="true">
        <s:textfield name="currentLabeledExtract.name" key="experiment.labeledExtracts.name" required="true" size="80" tabindex="1" />
        <s:textarea name="currentLabeledExtract.description" key="experiment.labeledExtracts.description" rows="3" cols="80"
            tabindex="2" />
        <caarray:annotationAssociationPicker baseId="extractPicker" entityName="LabeledExtract" associatedEntityName="Extract" itemId="${currentLabeledExtract.id}" tabIndex="3" />
        <caarray:termSelector baseId="materialType" category="<%= ExperimentOntologyCategory.MATERIAL_TYPE %>" termField="${currentLabeledExtract.materialType}"
            tabIndex="4" termFieldName="currentLabeledExtract.materialType" returnInitialTab1="annotations" returnInitialTab2="labeledExtracts" returnInitialTab2Url="${thisUrl}" />
        <caarray:annotationCharacteristics item="${currentSource}"/>
        <s:hidden name="currentLabeledExtract.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>