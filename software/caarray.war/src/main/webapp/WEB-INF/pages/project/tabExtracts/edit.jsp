<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Extract" itemId="${currentExtract.id}" itemName="${currentExtract.name}"
        isSubtab="true">
        <s:textfield name="currentExtract.name" key="experiment.extracts.name" size="80" tabindex="1" />
        <s:textarea name="currentExtract.description" key="experiment.extracts.description" rows="3" cols="80"
            tabindex="2" />
        <caarray:annotationAssociationPicker baseId="samplePicker" entityName="Extract" associatedEntityName="Sample" itemId="${currentExtract.id}" />
        <s:hidden name="currentExtract.id" />
        <s:hidden name="project.id" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>