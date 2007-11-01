<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane paneTitleKey="experiment.experimentalDesign" subtab="true">
    <div class="boxpad">
        <p class="instructions">Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.</p>
        <s:form action="ajax/project/tab/ExperimentalDesign/save" cssClass="form" id="projectForm" onsubmit="TabUtils.submitSubTabForm('projectForm', 'tabboxlevel2wrapper', 'save_draft'); return false;">
            <s:select required="true" name="project.experiment.experimentDesignType" label="Experiment Design Type" tabindex="1"
                      list="experimentDesignTypes" listKey="id" listValue="value" value="project.experiment.experimentDesignType.id"
                      headerKey="" headerValue="--Select an Experiment Design Type--"/>
            <s:textarea name="project.experiment.experimentDesignDescription" label="Experiment Design" cols="80" rows="8" tabindex="2"/>
            <s:textarea name="project.experiment.qualityControlDescription" label="Experimental Quality Control Description" cols="80" rows="8" tabindex="3"/>
            <s:select multiple="true" name="project.experiment.qualityControlTypes" label="Quality Control Types" tabindex="4"
                      list="qualityControlTypes" listKey="id" listValue="value" value="%{project.experiment.qualityControlTypes.{id}}" />
            <s:textarea name="project.experiment.replicateDescription" label="Replicate Description" cols="80" rows="8" tabindex="5"/>
            <s:select multiple="true" name="project.experiment.replicateTypes" label="Replicate Types" tabindex="6"
                      list="replicateTypes" listKey="id" listValue="value" value="%{project.experiment.replicateTypes.{id}}" />
            <s:hidden name="project.id" />
        </s:form>
        <caarray:actions>
           <caarray:action onclick="TabUtils.submitSubTabForm('projectForm', 'tabboxlevel2wrapper', 'save_draft');" actionClass="save" text="Save" />
        </caarray:actions>
    </div>
</caarray:tabPane>
