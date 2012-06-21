<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>

<caarray:tabPane paneTitleKey="experiment.experimentalDesign" subtab="true">
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/project/tab/ExperimentalDesign/save" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabboxlevel2wrapper'); return false;">
            <s:token/>
            <caarray:termSelector baseId="experimentDesignTypes" category="<%= ExperimentOntologyCategory.EXPERIMENT_DESIGN_TYPE %>" termField="${project.experiment.experimentDesignTypes}"
                tabIndex="1" termFieldName="project.experiment.experimentDesignTypes" returnInitialTab1="annotations" returnInitialTab2="experimentalDesign"
                hideAddButton="true" required="true" multiple="true"/>
            <s:textarea required="true" key="project.experiment.designDescription" cols="80" rows="4" tabindex="2"/>
            <caarray:termSelector baseId="qualityControlTypes" category="<%= ExperimentOntologyCategory.QUALITY_CONTROL_TYPE %>" termField="${project.experiment.qualityControlTypes}"
                tabIndex="3" termFieldName="project.experiment.qualityControlTypes" returnInitialTab1="annotations" returnInitialTab2="experimentalDesign"
                hideAddButton="true" multiple="true"/>
            <s:textarea key="project.experiment.qualityControlDescription" cols="80" rows="4" tabindex="4"/>
            <caarray:termSelector baseId="replicateTypes" category="<%= ExperimentOntologyCategory.REPLICATE_TYPE %>" termField="${project.experiment.replicateTypes}"
                tabIndex="5" termFieldName="project.experiment.replicateTypes" returnInitialTab1="annotations" returnInitialTab2="experimentalDesign"
                hideAddButton="true" multiple="true"/>
            <s:textarea key="project.experiment.replicateDescription" cols="80" rows="4" tabindex="6"/>
            <s:hidden name="project.id" />
            <s:hidden name="editMode" />
            <input type="submit" class="enableEnterSubmit"/>
        </s:form>
        <caarray:focusFirstElement formId="projectForm"/>
        <caarray:projectTabButtons tab="ExperimentalDesign" isSubtab="true"/>
    </div>
</caarray:tabPane>
