<%@ tag display-name="projectListTabActionColumns"
        description="Renders the action columns (edit, copy, etc) for the list view of a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="actions" required="true"%>
<%@ attribute name="item" required="true" type="gov.nih.nci.caarray.domain.PersistentObject"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:forTokens items="${actions}" delims="," var="action">
    <c:set var="editableOnly" value="${fn:substring(action, 0, 1) == '!'}"/>
    <c:if test="${editableOnly}">
        <c:set var="action" value="${fn:substring(action, 1, fn:length(action))}"/>        
    </c:if>
    <display:column titleKey="button.${action}">
        <c:if test="${!editableOnly || project.saveAllowed && caarrayfn:canWrite(item, caarrayfn:currentUser()) && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
            <caarray:projectListTabActionLink entityName="${entityName}" action="${action}" itemId="${item.id}" isSubtab="${isSubtab}"/>            
        </c:if>
    </display:column>    
</c:forTokens>
