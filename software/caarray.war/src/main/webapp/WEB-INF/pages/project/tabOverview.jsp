<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:set var="projectTitle"><c:out value="${project.experiment.title}" default="New Experiment"/></c:set>

<script type="text/javascript">
setExperimentTitleHeader('${caarrayfn:escapeJavaScript(projectTitle)}');
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
            <s:token/>
            <c:if test="${editMode}">
                <s:textfield requiredLabel="true" key="project.experiment.title" size="80" tabindex="1" maxlength="254"/>
            </c:if>
            <s:textarea key="project.experiment.description" cols="80" rows="5" tabindex="2"/>
            <s:textfield theme="readonly" label="Status" value="%{project.locked ? 'Locked' : 'In Progress'}"/>
            <s:textfield theme="readonly" name="project.experiment.publicIdentifier" label="Experiment Identifier"/>
            <c:if test="${!empty project.id}">
                <caarray:outputUrl var="permalinkUrl">
                    <jsp:attribute name="url"><c:url value="/project/${caarrayfn:encodeUrl(project.experiment.publicIdentifier)}"/></jsp:attribute>
                </caarray:outputUrl>
                <s:textfield theme="readonly" label="Experiment URL" value="%{#attr.permalinkUrl}">
                    <s:param name="url">true</s:param>
                </s:textfield>
            </c:if>
            <c:url var="autocompleteUrl" value="/protected/ajax/project/listTab/Overview/generateAssayList.action" />
            <caarray:listSelector baseId="assayTypes" listField="${project.experiment.assayTypes}" listFieldName="project.experiment.assayTypes"
                tabIndex="1" showFilter="false" objectValue="id" required="true" multiple="true" hideAddButton="true"
                autocompleteUrl="${autocompleteUrl}"/>
            <s:select key="project.experiment.manufacturer" tabindex="6" requiredLabel="true"
                      list="manufacturers" listKey="id" listValue="name"
                      headerKey="" headerValue="--Select a Provider--" value="project.experiment.manufacturer.id">
                <s:param name="after">
                    <span id="progressMsg" style="display:none;"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /> Loading... </span>
                </s:param>
            </s:select>

            <s:select multiple="true" name="project.experiment.arrayDesigns" label="Array Designs" tabindex="7"
                      list="arrayDesigns" listKey="id" listValue="name" value="%{project.experiment.arrayDesigns.{id}}" />
            <s:select requiredLabel="true" key="project.experiment.organism" tabindex="7"
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
    <caarray:focusFirstElement formId="projectForm"/>
        <script type="text/javascript">
            var selectedDesigns;
        
            startArrayDesignLookup = function() {
                $("progressMsg").show();
                selectedDesigns=$F('projectForm_project_experiment_arrayDesigns');
            }

            finishArrayDesignLookup = function() {
                Effect.Fade("progressMsg");
                $A($('projectForm_project_experiment_arrayDesigns').options).each(function(opt) {
                    if (selectedDesigns.indexOf(opt.value) >= 0) {
                        opt.selected = true;
                    }
                });
            }
        </script>

        <c:url var="getArrayDesignsUrl" value="/ajax/project/tab/Overview/retrieveArrayDesigns.action" />
        <c:if test="${editMode}">
            <c:set var="execOnLoad" value="false"/>
            <s:if test="fieldErrors['project.experiment.arrayDesigns'] != null">
                <c:set var="execOnLoad" value="true"/>
            </s:if>
            <ajax:select baseUrl="${getArrayDesignsUrl}"
                source="projectForm_project_experiment_manufacturer" target="projectForm_project_experiment_arrayDesigns"
                parameters="manufacturerId={projectForm_project_experiment_manufacturer}, assayTypeValues={assayTypesSelectedItemValues}"
                preFunction="startArrayDesignLookup" postFunction="finishArrayDesignLookup" executeOnLoad="${execOnLoad}"/>
            <ajax:select baseUrl="${getArrayDesignsUrl}"
                source="assayTypesSelectedItemValues" target="projectForm_project_experiment_arrayDesigns"
                parameters="manufacturerId={projectForm_project_experiment_manufacturer}, assayTypeValues={assayTypesSelectedItemValues}"
                preFunction="startArrayDesignLookup" postFunction="finishArrayDesignLookup"/>
        </c:if>

        <caarray:projectTabButtons tab="Overview"/>
    </div>
</caarray:tabPane>