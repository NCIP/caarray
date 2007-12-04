<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>

<caarray:tabPane paneTitleKey="experiment.experimentalDesign" subtab="true">
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/project/tab/ExperimentalDesign/save" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabboxlevel2wrapper'); return false;">
            <s:select required="true" key="project.experiment.experimentDesignType" tabindex="1"
                      list="experimentDesignTypes" listKey="id" listValue="value" value="project.experiment.experimentDesignType.id"
                      headerKey="" headerValue="--Select an Experiment Design Type--"/>
            <s:textarea required="true" key="project.experiment.experimentDesignDescription" cols="80" rows="8" tabindex="2"/>
            <s:select multiple="true" key="project.experiment.qualityControlTypes" tabindex="4"
                      list="qualityControlTypes" listKey="id" listValue="value" value="%{project.experiment.qualityControlTypes.{id}}" />
            <s:textarea key="project.experiment.qualityControlDescription" cols="80" rows="8" tabindex="3"/>
            <s:select multiple="true" key="project.experiment.replicateTypes" tabindex="4"
                      list="replicateTypes" listKey="id" listValue="value" value="%{project.experiment.replicateTypes.{id}}" />
            <s:textarea key="project.experiment.replicateDescription" cols="80" rows="8" tabindex="5"/>
            <s:hidden name="project.id" />
            <s:hidden name="editMode" />
            <input type="submit" class="enableEnterSubmit"/>
        </s:form>
        <caarray:projectTabButtons tab="ExperimentalDesign" isSubtab="true"/>
    </div>
</caarray:tabPane>
