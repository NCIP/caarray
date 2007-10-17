<div class="actions">
   <c:url value="Project_cancel.action" var="cancelUrl">
        <c:param name="proposalKey" value="${proposalKey}" />   
   </c:url>
   <a href="${cancelUrl}" class="cancel"><img src="<c:url value="/images/btn_cancel.gif"/>" alt="<fmt:message key="button.cancel" />"></a>
   <a href="javascript:TabUtils.submitTabOrSubTabForm('projectForm', 'tabboxwrapper', 'tabboxlevel2wrapper', 'save_draft');" class="save"><img src="<c:url value="/images/btn_save_draft.gif"/>" alt="Save Draft"></a>
   <a href="javascript:TabUtils.submitTabOrSubTabForm('projectForm', 'tabboxwrapper', 'tabboxlevel2wrapper', 'save_submit');"  class="submit_experiment"><img src="<c:url value="/images/btn_submit_experiment.gif"/>" alt="Submit Experiment Proposal"></a>
</div>

                                    