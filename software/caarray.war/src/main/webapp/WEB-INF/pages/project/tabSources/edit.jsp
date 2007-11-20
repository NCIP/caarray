<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Source" item="${currentSource}" itemName="${currentSource.name}"
        isSubtab="true">
        <s:textfield name="currentSource.name" key="experiment.sources.name" required="true" size="80" tabindex="1" />
        <s:textarea name="currentSource.description" key="experiment.sources.description" rows="3" cols="80"
            tabindex="2" />
        <s:select name="currentSource.tissueSite" label="Tissue Site" tabindex="3" value="currentSource.tissueSite.id"
            list="project.experiment.tissueSites" listKey="id" listValue="value" />
        <caarray:annotationCharacteristics item="${currentSource}"/>                 
        <s:hidden name="currentSource.id" />
        <s:hidden name="project.id" />
    </caarray:projectListTabItemForm>
</caarray:tabPane>