<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:if test="${experiment.id != null}">
    <c:set var="formAction" value="ajax/experiment/management/save/overview"/>
</s:if>
<s:else>
    <c:set var="formAction" value="experiment/management/save"/>
</s:else>

<caarray:tabPane paneTitleKey="experiment.contacts">
    <div class="boxpad">
    <p class="instructions">Contact information for this experiment is below. Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.</p>
    <s:form action="ajax_Project_saveTab_contacts" cssClass="form" id="projectForm" method="post">
        <tr><th colspan="2">Principal Investigator (P.I.)</th></tr>        
        <s:textfield required="true" name="proposal.project.experiment.title" label="P.I. First Name" value="%{user.firstName}" size="80" tabindex="1"/>
        <s:textfield required="true" name="proposal.project.experiment.title" label="P.I. Last Name" value="%{user.lastName}" size="80" tabindex="2"/>
        <s:textfield required="true" name="proposal.project.experiment.title" label="Title" value="%{user.title}" size="80" tabindex="3"/>
        <s:textfield required="true" name="proposal.project.experiment.title" label="Email" value="%{user.emailId}" size="80" tabindex="4"/>
        <s:hidden name="proposalKey" />
        <s:hidden name="proposal.id" />
        <s:hidden name="cancelResult" />
    </s:form>
    </div>
</caarray:tabPane>
