<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<script type="text/javascript">
    displayCorrectSourceEditingUi = function() {
        $('selectSource')[$('termForm_createNewSourcetrue').checked ? 'hide' : 'show']();
        $('newSource')[$('termForm_createNewSourcetrue').checked ? 'show' : 'hide']();
        if ($('termForm_createNewSourcetrue').checked)
             $('termForm_currentTerm_source').selectedIndex = 0;
    }
</script>
<caarray:tabPane>
    <div class="boxpad2">
        <h3>Manage <fmt:message key="vocabulary.tabs.${category}" /></h3>
    </div>
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/vocabulary/save" cssClass="form" id="termForm" onsubmit="TabUtils.submitTabForm('termForm', 'tabboxwrapper'); return false;">
            <tr><th colspan="2">Term</th></tr>
            <s:textfield key="currentTerm.value" required="true" size="80" tabindex="1"/>
            <s:textfield key="currentTerm.description" size="80" tabindex="2"/>
            <tr><th colspan="2">Source</th></tr>
            <s:radio name="createNewSource" label="Create a new Source?" list="#{true: 'Yes', false: 'No'}"
                tabindex="3" onclick="displayCorrectSourceEditingUi();" />
            <tbody id="selectSource" <s:if test="createNewSource == true">style="display: none"</s:if>>
                <s:select list="sources" key="currentTerm.source" headerKey="" headerValue="-- Select A Source --"
                    listKey="id" listValue="name" value="currentTerm.source.id" tabindex="4" required="true" />
            </tbody>
            <tbody id="newSource" <s:if test="createNewSource == false">style="display: none"</s:if>>
            <s:textfield key="newSource.name" size="80" tabindex="5" required="true" />
            <s:textfield key="newSource.url" size="80" tabindex="5" />
            <s:textfield key="newSource.version" size="80" tabindex="7"/>
            </tbody>
            <tr><th colspan="2">Accession</th></tr>
            <s:textfield name="currentTerm.accession.url" label="Accession URL" size="80" tabindex="8"/>
            <s:textfield name="currentTerm.accession.value" label="Accession Value" size="80" tabindex="9"/>
            <s:hidden name="category" />
            <s:hidden name="currentTerm.id" />
            <s:hidden name="returnProjectId" />
            <s:hidden name="returnInitialTab1" />
            <s:hidden name="returnInitialTab2" />
            <s:hidden name="returnInitialTab2Url" />
            <s:hidden name="returnToProjectOnCompletion" />
            <input type="submit" class="enableEnterSubmit"/>
        </s:form>
        <caarray:actions>
            <s:if test="returnToProjectOnCompletion && returnProjectId == null">
                <c:url value="/protected/project/create.action" var="returnUrl" />
            </s:if>
            <s:elseif test="returnToProjectOnCompletion">
                <c:url value="/protected/ajax/vocabulary/projectEdit.action" var="returnUrl" />
            </s:elseif>
            <s:else>
                <c:url value="/protected/ajax/vocabulary/list.action" var="returnUrl">
                    <c:param name="category" value="${category}" />
                </c:url>
            </s:else>
            <caarray:action actionClass="cancel" text="Cancel" onclick="TabUtils.submitTabFormToUrl('termForm', '${returnUrl}','tabboxwrapper'); return false;" tabindex="10" />
            <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('termForm', 'tabboxwrapper'); return false;" tabindex="11" />
        </caarray:actions>
    </div>
</caarray:tabPane>
