<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true">
    <h3><fmt:message key="experiment.samples.add.header" /></h3>
    <div class="boxpad2">
        <p class="instructions">Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.</p>
        <s:form action="ajax_Project_saveTab_sampleEdit" cssClass="form" id="projectForm" method="post">
            <s:textfield required="true" name="currentSample.name" key="experiment.samples.name" size="80" tabindex="1"/>
            <s:textarea name="currentSample.description" key="experiment.samples.description" rows="3" cols="80" tabindex="2" />
            <s:hidden name="currentSampleIndex" />
            <s:hidden name="proposalKey" />
        </s:form>
        <a href="javascript:TabUtils.submitSubTabForm('projectForm', 'tabboxlevel2wrapper', 'save_session');" class="save" tabindex="3"><img src="<c:url value="/images/btn_save_draft.gif"/>" alt="Save Draft"></a>
   </div>
</caarray:tabPane>