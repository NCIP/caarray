<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>

<caarray:tabPane paneTitleKey="experiment.experimentalDesign" subtab="true">
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/project/tab/ExperimentalDesign/save" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabboxlevel2wrapper'); return false;">
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
        <caarray:projectTabButtons tab="ExperimentalDesign" isSubtab="true"/>
    </div>
</caarray:tabPane>
