<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>

<caarray:tabPane>
    <caarray:projectListTabItemForm entityName="ExperimentContact" item="${currentExperimentContact}"
        itemName="${currentExperimentContact.contact.firstName} ${currentExperimentContact.contact.lastName}">

	    <s:textfield required="true" key="currentExperimentContact.person.firstName" size="80"
               tabindex="1" maxlength="254"/>
       	<s:textfield required="true" key="currentExperimentContact.person.lastName" size="80"
               tabindex="1" maxlength="254" />
        <s:textfield  required="true" key="currentExperimentContact.person.email" size="80"
                tabindex="1" maxlength="254"/>
        <s:textfield key="currentExperimentContact.person.phone" size="80"
                tabindex="1" maxlength="254" />
   		<caarray:termSelector baseId="currentExperimentContact" category="<%= ExperimentOntologyCategory.ROLES %>" termField="${currentExperimentContact.roles}"
                tabIndex="1" termFieldName="currentExperimentContact.roles" returnInitialTab1="contacts"
                hideAddButton="true" required="true" multiple="true"/>

        <s:hidden name="currentExperimentContact.id" />
        <s:hidden name="project.id" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>
</caarray:tabPane>
