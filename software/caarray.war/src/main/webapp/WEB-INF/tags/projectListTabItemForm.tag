<%@ tag display-name="projectListTabItemForm"
        description="Renders the form for a adding/editing an item for a list-type project tab"
        body-content="scriptless"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="itemName" required="true"%>
<%@ attribute name="instructions" required="false"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${empty instructions}">
    <c:set var="instructions">
        Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.    
    </c:set>
</c:if>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>

<caarray:projectListTabItemHeader entityName="${entityName}" itemId="${itemId}" itemName="${itemName}" isSubtab="${isSubtab}"/>

<div class="boxpad2">
    <s:form action="ajax/project/listTab/${plural}/save" cssClass="form" id="projectForm" 
            onsubmit="TabUtils.submitSubTabForm('projectForm', '${tabAnchor}', 'save_draft'); return false;">
        <jsp:doBody/>
    </s:form>        
</div>

<caarray:projectListTabItemButtons entityName="${entityName}" itemId="${itemId}" isSubtab="${isSubtab}"/>
