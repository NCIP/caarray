<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane>
    <div class="boxpad2">
        <h3>Manage <fmt:message key="vocabulary.tabs.${category}" /></h3>
    </div>
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/vocabulary/save" cssClass="form" id="termForm" onsubmit="TabUtils.submitTabForm('termForm', 'tabboxwrapper'); return false;">
            <s:textfield name="currentTerm.value" label="Value" required="true" size="80" tabindex="1"/>
            <s:textfield name="currentTerm.description" label="Description" size="80" tabindex="2"/>
            <s:hidden name="category" />
            <s:hidden name="currentTerm.id" />
            <s:hidden name="returnProjectId" />
            <s:hidden name="returnToProjectOnCompletion" />
        </s:form>
        <caarray:actions>
            <s:if test="returnToProjectOnCompletion">
                <s:if test="returnProjectId == null">
                    <c:url value="/protected/project/create.action" var="returnUrl" />
                </s:if>
                <s:else>
                    <c:url value="/protected/project/edit.action" var="returnUrl" >
                        <c:param name="project.id" value="${returnProjectId}" />
                    </c:url>
                </s:else>
                <caarray:action actionClass="cancel" text="Cancel" url="${returnUrl}" tabindex="3" />
            </s:if>
            <s:else>
                <c:url value="/protected/ajax/vocabulary/list.action" var="vocabListAction">
                    <c:param name="category" value="${category}" />
                </c:url>
                <caarray:action actionClass="cancel" text="Cancel" onclick="executeAjaxTab_tabs(null,'selected', '${vocabListAction}', '');" tabindex="3" />
            </s:else>
            <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('termForm', 'tabboxwrapper'); return false;" tabindex="4" />
        </caarray:actions>
    </div>
</caarray:tabPane>
