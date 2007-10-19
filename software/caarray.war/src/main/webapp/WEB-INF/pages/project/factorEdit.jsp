<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true">
    <s:if test="currentFactorIndex == null">
        <h3><fmt:message key="experiment.factors.add.header" /></h3>
    </s:if>
    <s:else>
        <h3><fmt:message key="experiment.experimentalFactors" /> > ${currentFactor.name}</h3>
    </s:else>

    <div class="boxpad2">
        <p class="instructions">Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.</p>
        <s:form action="ajax_Project_saveTab_factorEdit" cssClass="form" id="projectForm" method="post">
            <s:textfield required="true" name="currentFactor.name" key="experiment.experimentalFactors.name" size="80" tabindex="1"/>
            <s:hidden name="currentFactor.id" />
            <s:hidden name="project.id" />
            <s:hidden name="ajax" value="true"/>
        </s:form>
        <a href="javascript:TabUtils.submitSubTabForm('projectForm', 'tabboxlevel2wrapper', 'save_draft');" class="save" tabindex="2"><img src="<c:url value="/images/btn_save_draft.gif"/>" alt="Save Draft"></a>
   </div>
</caarray:tabPane>