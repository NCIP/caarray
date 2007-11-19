<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>

<caarray:tabPane paneTitleKey="experiment.contacts">
    <div class="boxpad">
        <p class="instructions">Contact information for this experiment is below. Required fields are marked with <span class="required">*asterisks*</span>.
        </p>
        <s:form action="ajax/project/tab/Contacts/save" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabboxwrapper'); return false;">
            <tbody>
            <tr><th colspan="2">Principal Investigator (P.I.)</th></tr>
            <s:textfield required="true" key="primaryInvestigator.firstName" size="80" tabindex="1"/>
            <s:textfield required="true" key="primaryInvestigator.lastName" size="80" tabindex="2"/>
            <s:textfield required="true" key="primaryInvestigator.email" size="80" tabindex="3"/>
            <s:textfield name="primaryInvestigator.phone" label="Phone" size="80" tabindex="4"/>
            <s:radio name="piIsMainPoc" label="Is the P.I. the P.O.C" list="#{true: 'Yes', false:'No'}" tabindex="5" onclick="$('poc')[$('projectForm_piIsMainPoctrue').checked ? 'hide' : 'show']();"/>
            </tbody>
            <tbody id="poc"     <s:if test="piIsMainPoc == true">style="display: none"</s:if> >
            <tr><th colspan="2">Main Point of Contact (P.O.C.)</th></tr>
            <s:textfield required="true" name="mainPointOfContact.firstName" label="P.O.C First Name" size="80" tabindex="6"/>
            <s:textfield required="true" name="mainPointOfContact.lastName" label="P.O.C Last Name" size="80" tabindex="7"/>
            <s:textfield required="true" name="mainPointOfContact.email" label="Email" size="80" tabindex="8"/>
            <s:textfield name="mainPointOfContact.phone" label="Phone" size="80" tabindex="9"/>
            </tbody>
            <s:hidden name="project.id" />
            <s:hidden name="editMode" />
        </s:form>
        <caarray:projectTabButtons tab="Contacts"/>
    </div>
</caarray:tabPane>
