<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Hybridization" item="${currentHybridization}" itemName="${currentHybridization.name}"
        isSubtab="true">
        <s:textfield name="currentHybridization.name" key="experiment.hybridizations.name" required="true" size="80" tabindex="1" />
        <s:textarea name="currentHybridization.description" key="currentHybridization.description" rows="3" cols="80"
            tabindex="2" />
        <caarray:annotationAssociationPicker baseId="labeledExtractPicker" entityName="Hybridization" associatedEntityName="Labeled Extract" itemId="${currentHybridization.id}" tabIndex="3" />
        <s:hidden name="currentHybridization.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>