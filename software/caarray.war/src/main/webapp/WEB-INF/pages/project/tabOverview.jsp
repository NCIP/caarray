<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
setExperimentTitleHeader('${project.experiment.title}');
</script>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>

<caarray:tabPane paneTitleKey="experiment.overview">
    <div class="boxpad">
        <p class="instructions">
            The Overall Experiment Characteristics represent the minimum set of
            attributes required to submit an experiment for review.
            Required fields are marked with <span class="required">*asterisks*</span>.
        </p>
        <s:form action="ajax/project/tab/Overview/save" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabboxwrapper'); return false;">
            <s:textfield required="true" name="project.experiment.title" label="Experiment Title" size="80" tabindex="1"/>
            <s:textfield theme="readonly" label="Status" value="%{getText(project.status.resourceKey)}"/>
            <s:textfield theme="readonly" name="project.experiment.publicIdentifier" label="Experiment Identifier"/>
            <s:select required="true" name="project.experiment.serviceType" label="Service Type" tabindex="4"
                      list="@gov.nih.nci.caarray.domain.project.ServiceType@values()" listValue="%{getText(resourceKey)}"
                      headerKey="" headerValue="--Select a Service Type--"/>
            <s:select required="true" name="project.experiment.assayType" label="Assay Type" tabindex="5"
                      list="@gov.nih.nci.caarray.domain.project.AssayType@values()" listValue="%{getText(resourceKey)}"
                      headerKey="" headerValue="--Select an Assay Type--"/>
            <s:select name="project.experiment.manufacturer" label="Provider" tabindex="6"
                      list="manufacturers" listKey="id" listValue="name"
                      headerKey="" headerValue="--Select a Provider--" value="project.experiment.manufacturer.id">
                <s:param name="after">
                    <span id="progressMsg" style="display:none;"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /> Loading.. </span>
                </s:param>
            </s:select>

            <s:select multiple="true" name="project.experiment.arrayDesigns" label="Array Designs" tabindex="7"
                      list="arrayDesigns" listKey="id" listValue="name" value="%{project.experiment.arrayDesigns.{id}}" />
            <s:select required="true" name="project.experiment.organism" label="Organism" tabindex="7"
                      list="organisms" listKey="id" listValue="commonName" value="project.experiment.organism.id"
                      headerKey="" headerValue="--Select an Organism--"/>
            <s:select multiple="true" name="project.experiment.tissueSites" label="Tissue Sites" tabindex="8"
                      list="tissueSites" listKey="id" listValue="value" value="%{project.experiment.tissueSites.{id}}" />
            <s:select multiple="true" name="project.experiment.tissueTypes" label="Tissue Types" tabindex="9"
                      list="tissueTypes" listKey="id" listValue="value" value="%{project.experiment.tissueTypes.{id}}" />
            <s:select multiple="true" name="project.experiment.cellTypes" label="Cell Types" tabindex="10"
                      list="cellTypes" listKey="id" listValue="value" value="%{project.experiment.cellTypes.{id}}" />
            <s:select multiple="true" name="project.experiment.conditions" label="Conditions" tabindex="11"
                      list="conditions" listKey="id" listValue="value" value="%{project.experiment.conditions.{id}}" />
            <s:hidden name="project.id" />
        </s:form>

        <script type="text/javascript">
            startArrayDesignLookup = function() {
                $("progressMsg").show();
            }

            finishArrayDesignLookup = function() {
                Effect.Fade("progressMsg");
            }
        </script>

        <c:url var="getArrayDesignsUrl" value="/protected/ajax/project/tab/Overview/retrieveArrayDesigns.action" />
        <ajax:select baseUrl="${getArrayDesignsUrl}"
            source="projectForm_project_experiment_manufacturer" target="projectForm_project_experiment_arrayDesigns"
            parameters="manufacturerId={projectForm_project_experiment_manufacturer}"
            preFunction="startArrayDesignLookup" postFunction="finishArrayDesignLookup"/>

        <caarray:projectTabButtons tab="Overview"/>
    </div>
</caarray:tabPane>
