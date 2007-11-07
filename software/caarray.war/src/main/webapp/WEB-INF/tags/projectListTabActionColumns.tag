<%@ tag display-name="projectListTabActionColumns"
        description="Renders the action columns (edit, copy, etc) for the list view of a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="actions" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:forTokens items="${actions}" delims="," var="action">
    <c:set var="editableOnly" value="${fn:substring(action, 0, 1) == '!'}"/>
    <c:if test="${editableOnly}">
        <c:set var="action" value="${fn:substring(action, 1, fn:length(action))}"/>        
    </c:if>
    <c:if test="${!editableOnly || project.saveAllowed}">
        <display:column titleKey="button.${action}">
            <caarray:projectListTabActionLink entityName="${entityName}" action="${action}" itemId="${itemId}" isSubtab="${isSubtab}"/>            
        </display:column>    
    </c:if>
</c:forTokens>
