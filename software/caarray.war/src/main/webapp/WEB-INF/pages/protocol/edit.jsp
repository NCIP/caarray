<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<caarray:tabPane>
    <div class="boxpad2">
        <h3><fmt:message key="protocols.mamage" /></h3>
    </div>
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/protocol/save" cssClass="form" id="protocolForm" onsubmit="TabUtils.submitTabForm('protocolForm', 'tabboxwrapper'); return false;">
            <s:textfield key="protocol.name" required="true" size="80" tabindex="1"/>
            <s:textfield key="protocol.description" size="80" tabindex="2"/>
            <caarray:termSelector baseId="protocolType" category="<%= ExperimentOntologyCategory.PROTOCOL_TYPE %>" hideAddButton="true"
                tabIndex="3" termFieldName="protocol.type" termField="${protocol.type}" required="true" />
            <s:textfield key="protocol.contact" size="80" tabindex="4"/>
            <s:textfield key="protocol.software" size="80" tabindex="5"/>
            <s:textfield key="protocol.hardware" size="80" tabindex="6"/>
            <s:textfield key="protocol.url" size="80" tabindex="7"/>
            <s:hidden name="protocol.id" />
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
                <c:url value="/protected/ajax/protocol/projectEdit.action" var="returnUrl" />
            </s:elseif>
            <s:else>
                <c:url value="/protected/ajax/protocol/list.action" var="returnUrl" />
            </s:else>
            <caarray:action actionClass="cancel" text="Cancel" onclick="TabUtils.submitTabFormToUrl('protocolForm', '${returnUrl}','tabboxwrapper'); return false;" tabindex="10" />
            <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('protocolForm', 'tabboxwrapper'); return false;" tabindex="9" />
        </caarray:actions>
    </div>
</caarray:tabPane>
