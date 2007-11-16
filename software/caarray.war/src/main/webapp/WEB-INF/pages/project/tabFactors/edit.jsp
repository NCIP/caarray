<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Factor" item="${currentFactor}" itemName="${currentFactor.name}"
        isSubtab="true">
            <s:textfield required="true" name="currentFactor.name" key="experiment.factors.name" size="80" tabindex="1"/>
            <s:hidden name="currentFactor.id" />
            <s:hidden name="project.id" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>