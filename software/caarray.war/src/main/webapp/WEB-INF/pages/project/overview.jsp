<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane paneTitleKey="experiment.overview">
    <div class="boxpad">

    <p class="instructions">
        The Overall Experiment Characteristics represent the minimum set of 
        attributes required to submit an experiment for review. 
        Required fields are highlighted and have 
        <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.
        </p>
    <s:form action="ajax_Project_saveTab_overview" cssClass="form" id="projectForm" method="get">
        <s:textfield required="true" name="proposal.project.experiment.title" label="Experiment Title" size="80" tabindex="1"/>
        <tr>
            <td class="tdLabel"><label for="proposalStatus">Status</label></td>
            <td>
                <span id="proposalStatus">
                <s:if test="proposal.status != null">
                    <s:property value="getText(proposal.status.resourceKey)"/>
                </s:if>
                <s:else>
                    <s:text name="proposalStatus.new"/>
                </s:else>
                </span>
            </td>
        </tr>
        <tr>
            <td class="tdLabel"><label for="publicIdentifier">Experiment Identifier</label></td>
            <td>
                <s:property id="publicId" value="proposal.project.experiment.publicIdentifier" default="None Yet (you must save the proposal and assign it a PI before an identifier can be generated)"/>
            </td>
        </tr>
        <s:select required="true" name="proposal.project.experiment.serviceType" label="Service Type" tabindex="4"
                  list="@gov.nih.nci.caarray.domain.project.ServiceType@values()" listValue="%{getText(resourceKey)}"
                  headerKey="" headerValue="--Select a Service Type--"/>
        <s:select required="true" name="proposal.project.experiment.assayType" label="Assay Type" tabindex="5"
                  list="@gov.nih.nci.caarray.domain.project.AssayType@values()" listValue="%{getText(resourceKey)}"
                  headerKey="" headerValue="--Select an Assay Type--"/>
        <s:select name="selectedManufacturer" label="Manufacturer" tabindex="6"
                  list="arrayDesignsByManufacturer.keySet()" listKey="id" listValue="name"
                  headerKey="" headerValue="--Select a Manufacturer--"/>
        <s:select multiple="true" name="selectedArrayDesigns" label="Array Designs" tabindex="7"
                  list="new java.util.ArrayList()" listKey="id" listValue="name"/>
        <s:select required="true" name="selectedOrganism" label="Organism" tabindex="7"
                  list="organisms" listKey="id" listValue="commonName"
                  headerKey="" headerValue="--Select an Organism--"/>
        <s:select multiple="true" name="selectedTissueSites" label="Tissue Sites" tabindex="8"
                  list="tissueSites" listKey="id" listValue="value"/>
        <s:select multiple="true" name="selectedTissueTypes" label="Tissue Types" tabindex="9"
                  list="tissueTypes" listKey="id" listValue="value"/>
        <s:select multiple="true" name="selectedCellTypes" label="Cell Types" tabindex="10"
                  list="cellTypes" listKey="id" listValue="value"/>
        <s:select multiple="true" name="selectedConditions" label="Conditions" tabindex="11"
                  list="conditions" listKey="id" listValue="value"/>
        <s:hidden name="proposal.id" />
        <s:hidden name="ajax" value="%{'true'}"/>
<script type="text/javascript">
var dol = new DynamicOptionList("selectedManufacturer", "selectedArrayDesigns");
var opts = new Array();
<s:iterator value="arrayDesignsByManufacturer.entrySet()" id="designMapping">
    <s:iterator value="value">
dol.forValue('<s:property value="#designMapping.key.id"/>').addOptionsTextValue('<s:property value="name"/>', '<s:property value="id"/>');
    </s:iterator>
    <s:if test="selectedManufacturer == #designMapping.key.id">
        <s:iterator value="selectedArrayDesigns">
dol.forValue('<s:property value="#designMapping.key.id"/>').setDefaultOptions('<s:property/>');
        </s:iterator>
    </s:if>
</s:iterator>
window.setTimeout(function() { console.log("setting up dols"); initDynamicOptionLists(); }, 100);
</script>
    </s:form>
    <a href="javascript:TabUtils.submitTabForm('projectForm', 'tabboxwrapper', 'save_session');" class="save"><img src="<c:url value="/images/btn_save_draft.gif"/>" alt="Save Draft"></a>

    </div>
</caarray:tabPane>
