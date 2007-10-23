<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane paneTitleKey="experiment.overview">
    <div class="boxpad">

    <p class="instructions">
        The Overall Experiment Characteristics represent the minimum set of
        attributes required to submit an experiment for review.
        Required fields are highlighted and have
        <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.
        </p>
    <s:form action="ajax/project/saveTab/overview" cssClass="form" id="projectForm" method="get">
        <s:textfield required="true" name="project.experiment.title" label="Experiment Title" size="80" tabindex="1"/>
        <tr>
            <td class="tdLabel"><label for="proposalStatus">Status</label></td>
            <td><span id="proposalStatus"><s:property value="getText(project.status.resourceKey)"/></span></td>
        </tr>
        <tr>
            <td class="tdLabel"><label for="publicIdentifier">Experiment Identifier</label></td>
            <td>
                <s:property id="publicId" value="project.experiment.publicIdentifier" default="None Yet (you must save the project and assign it a PI before an identifier can be generated)"/>
            </td>
        </tr>
        <s:select required="true" name="project.experiment.serviceType" label="Service Type" tabindex="4"
                  list="@gov.nih.nci.caarray.domain.project.ServiceType@values()" listValue="%{getText(resourceKey)}"
                  headerKey="" headerValue="--Select a Service Type--"/>
        <s:select required="true" name="project.experiment.assayType" label="Assay Type" tabindex="5"
                  list="@gov.nih.nci.caarray.domain.project.AssayType@values()" listValue="%{getText(resourceKey)}"
                  headerKey="" headerValue="--Select an Assay Type--"/>
        <s:select name="project.experiment.manufacturer" label="Manufacturer" tabindex="6"
                  list="arrayDesignsByManufacturer.keySet()" listKey="id" listValue="name"
                  headerKey="" headerValue="--Select a Manufacturer--" value="project.experiment.manufacturer.id" />
        <s:select multiple="true" name="project.experiment.arrayDesigns" label="Array Designs" tabindex="7"
                  list="new java.util.ArrayList()" listKey="id" listValue="name" value="%{project.experiment.arrayDesigns.{id}}" />
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
<script type="text/javascript">
var dol = new DynamicOptionList("project.experiment.manufacturer", "project.experiment.arrayDesigns");
var opts = new Array();
<s:iterator value="arrayDesignsByManufacturer.entrySet()" id="designMapping">
    <s:iterator value="value">
dol.forValue('<s:property value="#designMapping.key.id"/>').addOptionsTextValue('<s:property value="name"/>', '<s:property value="id"/>');
    </s:iterator>
    <s:if test="project.experiment.manufacturer == #designMapping.key.id">
        <s:iterator value="project.experiment.arrayDesigns">
dol.forValue('<s:property value="#designMapping.key.id"/>').setDefaultOptions('<s:property/>');
        </s:iterator>
    </s:if>
</s:iterator>
window.setTimeout(function() {initDynamicOptionLists(); }, 100);
</script>
    </s:form>
    <a href="javascript:TabUtils.submitTabForm('projectForm', 'tabboxwrapper', 'save_draft');" class="save"><img src="<c:url value="/images/btn_save_draft.gif"/>" alt="Save Draft"></a>

    </div>
</caarray:tabPane>
