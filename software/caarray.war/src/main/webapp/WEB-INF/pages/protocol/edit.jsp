<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<script type="text/javascript">
    displayCorrectSourceEditingUi = function() {
        $('selectSource')[$('protocolForm_createNewSourcetrue').checked ? 'hide' : 'show']();
        $('newSource')[$('protocolForm_createNewSourcetrue').checked ? 'show' : 'hide']();
        if ($('protocolForm_createNewSourcetrue').checked)
             $('protocolForm_protocol_source').selectedIndex = 0;
    }
    submitTermForm = function() {
        $('protocolForm_protocol_source').disabled = $('protocolForm_createNewSourcetrue').checked;
        TabUtils.submitTabForm('protocolForm', 'tabboxwrapper');
        return false;
    }
</script>
<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>
<caarray:tabPane>
    <div class="boxpad2">
        <h3><fmt:message key="protocols.mamage" /></h3>
    </div>
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/protocol/save" cssClass="form" id="protocolForm" onsubmit="submitTermForm(); return false;">
            <s:token/>
            <tr><th colspan="2">Protocol</th></tr>
            <s:textfield key="protocol.name" required="true" size="80" tabindex="1" maxlength="254"/>
            <s:textfield key="protocol.description" size="80" tabindex="2"/>
            <caarray:termSelector baseId="protocolType" category="<%= ExperimentOntologyCategory.PROTOCOL_TYPE %>" hideAddButton="true"
                tabIndex="3" termFieldName="protocol.type" termField="${protocol.type}" required="true" />
            <s:textfield key="protocol.contact" size="80" tabindex="4"/>
            <s:textfield key="protocol.software" size="80" tabindex="5"/>
            <s:textfield key="protocol.hardware" size="80" tabindex="6"/>
            <s:textfield key="protocol.url" size="80" tabindex="7">
                <s:param name="url">true</s:param>
                <s:param name="target">_blank</s:param>
            </s:textfield>
            <tr><th colspan="2">Source</th></tr>
            <c:if test="${editMode}">
            <s:radio name="createNewSource" label="Create a new Source?" list="#{true: 'Yes', false: 'No'}"
                tabindex="8" onclick="displayCorrectSourceEditingUi();" />
            </c:if>
            <tbody id="selectSource" <s:if test="createNewSource == true">style="display: none"</s:if>>
                <s:select list="sources" key="protocol.source" headerKey="" headerValue="-- Select A Source --"
                    listKey="id" listValue="nameAndVersion" value="protocol.source.id" tabindex="9" required="true" />
            </tbody>
            <tbody id="newSource" <s:if test="createNewSource == false">style="display: none"</s:if>>
                <tr>
                  <td colspan="2" valign="top" align="center">
                    <s:fielderror>
                      <s:param>newSource</s:param>
                    </s:fielderror>
                  </td>
                </tr>
                <s:textfield key="newSource.name" size="80" tabindex="10" required="true" />
                <s:textfield key="newSource.url" size="80" tabindex="11" />
                <s:textfield key="newSource.version" size="80" tabindex="12"/>
            </tbody>
            <s:hidden name="protocol.id" />
            <s:hidden name="returnProjectId" />
            <s:hidden name="returnInitialTab1" />
            <s:hidden name="returnInitialTab2" />
            <s:hidden name="returnInitialTab2Url" />
            <s:hidden name="returnToProjectOnCompletion" />
            <s:hidden name="editMode" />
            <input type="submit" class="enableEnterSubmit"/>
        </s:form>
        <caarray:focusFirstElement formId="protocolForm"/>
        <caarray:actions>
            <s:if test="returnToProjectOnCompletion && returnProjectId == null">
                <c:url value="/protected/project/create.action" var="returnUrl" />
            </s:if>
            <s:elseif test="returnToProjectOnCompletion">
                <c:url value="/protected/ajax/protocol/projectEdit.action" var="returnUrl" />
            </s:elseif>
            <s:else>
                <c:url value="/protected/ajax/protocol/list.action" var="returnUrl" />
            </s:else>
            <caarray:action actionClass="cancel" text="Cancel" onclick="TabUtils.submitTabFormToUrl('protocolForm', '${returnUrl}','tabboxwrapper'); return false;" tabindex="13" />
            <c:choose>
                <c:when test="${editMode}">
                    <caarray:action actionClass="save" text="Save" onclick="submitTermForm();" tabindex="9" />
                </c:when>
                <c:when test="${caarrayfn:canWrite(protocol, caarrayfn:currentUser())}">
                    <c:url value="/protected/ajax/protocol/edit.action" var="actionUrl">
                        <c:param name="protocol.id" value="${protocol.id}" />
                    </c:url>
                    <ajax:anchors target="tabboxwrapper">
                        <caarray:action actionClass="edit" text="Edit" url="${actionUrl}" tabindex="14" />
                    </ajax:anchors>
                </c:when>
            </c:choose>
        </caarray:actions>
    </div>
</caarray:tabPane>
