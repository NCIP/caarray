<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Hybridization" itemId="${currentHybridization.id}" itemName="${currentHybridization.name}"
        isSubtab="true">
        <s:textfield name="currentHybridization.name" key="experiment.hybridizations.name" size="80" tabindex="1" />
        <s:hidden name="currentHybridization.id" />
        <s:hidden name="project.id" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>