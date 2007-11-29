<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Sample" item="${currentSample}" itemName="${currentSample.name}"
        isSubtab="true">
        <s:textfield required="true" name="currentSample.name" key="experiment.samples.name" size="80" tabindex="1" />
        <s:textarea name="currentSample.description" key="experiment.samples.description" rows="3" cols="75"
            tabindex="2" />
        <s:textfield key="currentSource.externalSampleId" size="80" tabindex="3" />
        <caarray:annotationAssociationPicker baseId="sourcePicker" entityName="Sample" associatedEntityName="Source" itemId="${currentSample.id}" tabIndex="4" />
        <caarray:termSelector baseId="materialType" category="<%= ExperimentOntologyCategory.MATERIAL_TYPE %>" termField="${currentSample.materialType}"
            tabIndex="5" termFieldName="currentSample.materialType" />
        <caarray:annotationCharacteristics item="${currentSource}"/>
        <s:hidden name="currentSample.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>