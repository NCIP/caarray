<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Source" item="${currentSource}" itemName="${currentSource.name}"
        isSubtab="true">
        <s:textfield name="currentSource.name" key="experiment.sources.name" required="true" size="80" tabindex="1" />
        <s:textarea name="currentSource.description" key="experiment.sources.description" rows="3" cols="80" tabindex="2" />
        <caarray:termSelector baseId="tissueSite" category="<%= ExperimentOntologyCategory.ORGANISM_PART %>" tabIndex="3" termField="${currentSource.tissueSite}"
            termFieldName="currentSource.tissueSite" required="true" />
        <caarray:termSelector baseId="materialType" category="<%= ExperimentOntologyCategory.MATERIAL_TYPE %>" termField="${currentSource.materialType}"
            tabIndex="4" termFieldName="currentSource.materialType" />
        <caarray:termSelector baseId="cellType" category="<%= ExperimentOntologyCategory.CELL_TYPE %>" termField="${currentSource.cellType}"
            tabIndex="5" termFieldName="currentSource.cellType" />
        <caarray:termSelector baseId="diseaseState" category="<%= ExperimentOntologyCategory.DISEASE_STATE %>" termField="${currentSource.diseaseState}"
            tabIndex="6" termFieldName="currentSource.diseaseState" />
        <caarray:annotationCharacteristics item="${currentSource}"/>
        <s:hidden name="currentSource.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>