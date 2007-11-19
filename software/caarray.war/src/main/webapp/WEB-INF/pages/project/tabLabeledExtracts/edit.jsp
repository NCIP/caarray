<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="LabeledExtract" item="${currentLabeledExtract}" itemName="${currentLabeledExtract.name}"
        isSubtab="true">
        <s:textfield name="currentLabeledExtract.name" key="experiment.labeledExtracts.name" required="true" size="80" tabindex="1" />
        <s:textarea name="currentLabeledExtract.description" key="experiment.labeledExtracts.description" rows="3" cols="80"
            tabindex="2" />
        <caarray:annotationAssociationPicker baseId="extractPicker" entityName="LabeledExtract" associatedEntityName="Extract" itemId="${currentLabeledExtract.id}" />
        <s:hidden name="currentLabeledExtract.id" />
        <s:hidden name="project.id" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>