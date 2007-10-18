<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true">
    <s:if test="currentSourceIndex == null">
        <h3><fmt:message key="experiment.sources.add.header" /></h3>
    </s:if>
    <s:else>
        <h3><fmt:message key="experiment.sources" /> > ${currentSource.name}</h3>
    </s:else>

    <div class="boxpad2">
        <p class="instructions">Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.</p>
        <s:form action="ajax_Project_saveTab_sourceEdit" cssClass="form" id="projectForm" method="post">
            <s:textfield required="true" name="currentSource.name" key="experiment.sources.name" size="80" tabindex="1"/>
            <s:textarea name="currentSource.description" key="experiment.sources.description" rows="3" cols="80" tabindex="2" />
            <s:select name="currentSource.tissueSite" label="Tissue Site" tabindex="3" value="currentSource.tissueSite.id"
                  list="proposal.project.experiment.tissueSites" listKey="id" listValue="value"/>
            <s:hidden name="currentSourceIndex" />
            <s:hidden name="proposalKey" />
        </s:form>
        <a href="javascript:TabUtils.submitSubTabForm('projectForm', 'tabboxlevel2wrapper', 'save_session');" class="save" tabindex="4"><img src="<c:url value="/images/btn_save_draft.gif"/>" alt="Save Draft"></a>
   </div>
</caarray:tabPane>