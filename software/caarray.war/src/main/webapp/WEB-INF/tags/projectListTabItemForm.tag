<%@ tag display-name="projectListTabItemForm"
        description="Renders the form for a adding/editing an item for a list-type project tab"
        body-content="scriptless"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="item" required="true" type="com.fiveamsolutions.nci.commons.data.persistent.PersistentObject"%>
<%@ attribute name="itemName" required="true"%>
<%@ attribute name="instructions" required="false"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${empty instructions}">
    <c:set var="instructions">
        Required fields are marked with <span class="required">*asterisks*</span>.
    </c:set>
</c:if>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>

<caarray:projectListTabItemHeader entityName="${entityName}" itemId="${item.id}" itemName="${itemName}" isSubtab="${isSubtab}"/>

<div class="boxpad">
    <p class="instructions"><c:out value="${instructions}" escapeXml="false"/></p>
    <s:form action="ajax/project/listTab/%{#attr.plural}/save" cssClass="form" id="projectForm"
            onsubmit="TabUtils.submitTabForm('projectForm', '%{@attr.tabAnchor}'); return false;">
        <s:token/>
        <jsp:doBody/>
    </s:form>
    <caarray:focusFirstElement formId="projectForm"/>
    <caarray:projectListTabItemButtons entityName="${entityName}" item="${item}" isSubtab="${isSubtab}"/>
</div>