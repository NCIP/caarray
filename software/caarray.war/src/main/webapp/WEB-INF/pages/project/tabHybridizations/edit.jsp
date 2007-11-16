<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Hybridization" item="${currentHybridization}" itemName="${currentHybridization.name}"
        isSubtab="true">
        <s:textfield name="currentHybridization.name" key="experiment.hybridizations.name" size="80" tabindex="1" />
        <caarray:annotationAssociationPicker baseId="labeledExtractPicker" entityName="Hybridization" associatedEntityName="Labeled Extract" itemId="${currentHybridization.id}" />
        <s:hidden name="currentHybridization.id" />
        <s:hidden name="project.id" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>