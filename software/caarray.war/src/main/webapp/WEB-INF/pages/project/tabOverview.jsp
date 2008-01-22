<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:set var="projectTitle"><c:out value="${project.experiment.title}" default="New Experiment"/></c:set>
<c:if test="${!empty project.id}">
    <c:url value="/project/${project.experiment.publicIdentifier}" var="permalinkUrl"/>
    <c:set var="projectPermalink">(<a href="${permalinkUrl}">Permalink</a>)</c:set>
</c:if>

<script type="text/javascript">
setExperimentTitleHeader('${caarrayfn:escapeJavaScript(projectTitle)} <c:out value="${projectPermalink}" escapeXml="false"/>');
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
        <s:form action="ajax/project/tab/Overview/save" namespace="/protected" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabboxwrapper'); return false;">
            <s:textfield required="true" key="project.experiment.title" size="80" tabindex="1"/>
            <s:textarea key="project.experiment.description" cols="80" rows="5" tabindex="2"/>
            <s:textfield theme="readonly" label="Status" value="%{getText(project.status.resourceKey)}"/>
            <s:textfield theme="readonly" name="project.experiment.publicIdentifier" label="Experiment Identifier"/>
            <s:select required="true" key="project.experiment.assayType" tabindex="5"
                      list="@gov.nih.nci.caarray.domain.project.AssayType@values()" listValue="%{getText(resourceKey)}"
                      listKey="getValue()" headerKey="" headerValue="--Select an Assay Type--"/>
            <s:select key="project.experiment.manufacturer" tabindex="6" required="true"
                      list="manufacturers" listKey="id" listValue="name"
                      headerKey="" headerValue="--Select a Provider--" value="project.experiment.manufacturer.id">
                <s:param name="after">
                    <span id="progressMsg" style="display:none;"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /> Loading.. </span>
                </s:param>
            </s:select>

            <s:select multiple="true" name="project.experiment.arrayDesigns" label="Array Designs" tabindex="7"
                      list="arrayDesigns" listKey="id" listValue="name" value="%{project.experiment.arrayDesigns.{id}}" />
            <s:select required="true" key="project.experiment.organism" tabindex="7"
                      list="organisms" listKey="id" listValue="nameAndSource" value="project.experiment.organism.id"
                      headerKey="" headerValue="--Select an Organism--"/>
            <c:if test="${!editMode}">
                <s:select key="tissueSites" list="tissueSites" listKey="id" listValue="value" value="%{tissueSites.{id}}" multiple="true" label="Tissue Sites"/>
                <s:select key="materialTypes" list="materialTypes" listKey="id" listValue="value" value="%{materialTypes.{id}}" multiple="true" label="Material Types"/>
                <s:select key="cellTypes" list="cellTypes" listKey="id" listValue="value" value="%{cellTypes.{id}}" multiple="true" label="Cell Types"/>
                <s:select key="diseaseState" list="diseaseState" listKey="id" listValue="value" value="%{diseaseState.{id}}" multiple="true" label="Disease States"/>
            </c:if>
            <s:hidden name="project.id" />
            <s:hidden name="editMode" />
            <input type="submit" class="enableEnterSubmit"/>
        </s:form>

        <script type="text/javascript">
            startArrayDesignLookup = function() {
                $("progressMsg").show();
            }

            finishArrayDesignLookup = function() {
                Effect.Fade("progressMsg");
            }
        </script>

        <c:url var="getArrayDesignsUrl" value="/ajax/project/tab/Overview/retrieveArrayDesigns.action" />
        <c:if test="${editMode}">
            <ajax:select baseUrl="${getArrayDesignsUrl}"
                source="projectForm_project_experiment_manufacturer" target="projectForm_project_experiment_arrayDesigns"
                parameters="manufacturerId={projectForm_project_experiment_manufacturer},assayTypeValue={projectForm_project_experiment_assayType}"
                preFunction="startArrayDesignLookup" postFunction="finishArrayDesignLookup"/>
            <ajax:select baseUrl="${getArrayDesignsUrl}"
                source="projectForm_project_experiment_assayType" target="projectForm_project_experiment_arrayDesigns"
                parameters="manufacturerId={projectForm_project_experiment_manufacturer},assayTypeValue={projectForm_project_experiment_assayType}"
                preFunction="startArrayDesignLookup" postFunction="finishArrayDesignLookup"/>
        </c:if>

        <caarray:projectTabButtons tab="Overview"/>
    </div>
</caarray:tabPane>
