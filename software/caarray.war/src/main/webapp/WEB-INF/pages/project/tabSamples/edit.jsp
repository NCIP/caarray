<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Sample" itemId="${currentSample.id}" itemName="${currentSample.name}"
        isSubtab="true">
        <s:textfield required="true" name="currentSample.name" key="experiment.samples.name" size="80" tabindex="1" />
        <s:textarea name="currentSample.description" key="experiment.samples.description" rows="3" cols="75"
            tabindex="2" />
        <caarray:annotationAssociationPicker baseId="sourcePicker" entityName="Sample" associatedEntityName="Source" itemId="${currentSample.id}" />
        <s:hidden name="currentSample.id" />
        <s:hidden name="project.id" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>