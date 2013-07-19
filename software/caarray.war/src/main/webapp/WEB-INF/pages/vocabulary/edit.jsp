<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>
<caarray:tabPane>
    <div class="boxpad2">
        <fmt:message key="vocabulary.tabs.${category}" var="tabTitle"/>
        <h3>Manage <c:out value="${tabTitle}"/></h3>
    </div>
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/vocabulary/save" cssClass="form" id="termForm" onsubmit="submitTermForm(); return false;">
            <s:token/>
            <tr><th colspan="2">Term</th></tr>
            <tr>
              <td colspan="2">
                <s:fielderror>
                  <s:param>currentTerm</s:param>
                </s:fielderror>
              </td>
            </tr>
            <s:textfield key="currentTerm.value" requiredLabel="true" size="80" tabindex="1"/>
            <s:textfield key="currentTerm.description" size="80" tabindex="2"/>
            <tr><th colspan="2">Source</th></tr>
            <c:if test="${editMode}">
            <s:radio name="createNewSource" label="Create a new Source?" list="#{true: 'Yes', false: 'No'}"
                tabindex="3" onclick="displayCorrectSourceEditingUi();" />
            </c:if>
            <tbody id="selectSource" <s:if test="createNewSource == true">style="display: none"</s:if>>
                <s:select list="sources" key="currentTerm.source" headerKey="" headerValue="-- Select A Source --"
                    listKey="id" listValue="nameAndVersion" value="currentTerm.source.id" tabindex="4" requiredLabel="true" />
            </tbody>
            <tbody id="newSource" <s:if test="createNewSource == false">style="display: none"</s:if>>
            <tr>
              <td colspan="2" valign="top" align="center">
                <s:fielderror>
                  <s:param>newSource</s:param>
                </s:fielderror>
              </td>
            </tr>
            <s:textfield key="newSource.name" size="80" tabindex="5" requiredLabel="true" />
            <s:textfield key="newSource.url" size="80" tabindex="5" />
            <s:textfield key="newSource.version" size="80" tabindex="7"/>
            </tbody>
            <tr><th colspan="2">Accession</th></tr>
            <s:textfield key="currentTerm.url" size="80" tabindex="8">
                <s:param name="url">true</s:param>
                <s:param name="target">_blank</s:param>
            </s:textfield>
            <s:textfield name="currentTerm.accession" label="Accession Value" size="80" tabindex="9"/>
            <s:hidden name="category" />
            <s:hidden name="currentTerm.id" />
            <s:hidden name="returnProjectId" />
            <s:hidden name="returnInitialTab1" />
            <s:hidden name="returnInitialTab2" />
            <s:hidden name="returnInitialTab2Url" />
            <s:hidden name="returnToProjectOnCompletion" />
            <s:hidden name="editMode" />
            <input type="submit" class="enableEnterSubmit"/>
        </s:form>
        <caarray:focusFirstElement formId="termForm"/>
        <caarray:actions>
            <s:if test="returnToProjectOnCompletion && returnProjectId == null">
                <c:url value="/protected/project/create.action" var="returnUrl" />
                <caarray:action actionClass="cancel" text="Cancel" onclick="TabUtils.submitTabFormToUrl('termForm', '${returnUrl}','tabboxwrapper'); return false;" tabindex="10" />
            </s:if>
            <s:elseif test="returnToProjectOnCompletion">
                <c:url value="/protected/ajax/vocabulary/projectEdit.action" var="returnUrl" />
                <caarray:action actionClass="cancel" text="Cancel" onclick="TabUtils.submitTabFormToUrl('termForm', '${returnUrl}','tabboxwrapper'); return false;" tabindex="10" />
            </s:elseif>
            <s:else>
                <c:url value="/protected/ajax/vocabulary/list.action" var="returnUrl">
                    <c:param name="category" value="${category}" />
                </c:url>
                <ajax:anchors target="tabboxwrapper">
                    <caarray:action actionClass="cancel" text="Cancel" url="${returnUrl}" tabindex="10" />
                </ajax:anchors>
            </s:else>
            <c:choose>
                <c:when test="${editMode}">
                    <caarray:action actionClass="save" text="Save" onclick="return submitTermForm();" tabindex="11" />
                </c:when>
                <c:otherwise>
                    <c:url value="/protected/ajax/vocabulary/edit.action" var="actionUrl">
                        <c:param name="category" value="${category}" />
                        <c:param name="currentTerm.id" value="${currentTerm.id}" />
                    </c:url>
                    <ajax:anchors target="tabboxwrapper">
                        <caarray:action actionClass="edit" text="Edit" url="${actionUrl}" />
                    </ajax:anchors>
                </c:otherwise>
            </c:choose>
        </caarray:actions>
    </div>
<script type="text/javascript">
    displayCorrectSourceEditingUi = function() {
        $('selectSource')[$('termForm_createNewSourcetrue').checked ? 'hide' : 'show']();
        $('newSource')[$('termForm_createNewSourcetrue').checked ? 'show' : 'hide']();
        if ($('termForm_createNewSourcetrue').checked) {
             $('termForm_currentTerm_source').selectedIndex = 0;
        }
    }

    submitTermForm = function() {
        $('termForm_currentTerm_source').disabled = $('termForm_createNewSourcetrue').checked;
        TabUtils.submitTabForm('termForm', 'tabboxwrapper');
        return false;
    }
</script>
</caarray:tabPane>
