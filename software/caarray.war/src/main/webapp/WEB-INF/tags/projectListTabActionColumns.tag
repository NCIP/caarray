<%@ tag display-name="projectListTabActionColumns"
        description="Renders the action columns (edit, copy, etc) for the list view of a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="actions" required="true"%>
<%@ attribute name="item" required="true" type="com.fiveamsolutions.nci.commons.data.persistent.PersistentObject"%>
<%@ attribute name="isSubtab" required="false"%>
<%@ attribute name="confirmText" required="false"%>
<%@ attribute name="canWriteProject" required="true" %>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${pageContext.request.remoteUser != null}">
    <c:if test="${canWriteProject}">    
        <c:set var="canWriteItem" value="${caarrayfn:canWrite(item, caarrayfn:currentUser())}"/>
    </c:if>
	<c:forTokens items="${actions}" delims="," var="action">
	    <c:set var="editableOnly" value="${fn:substring(action, 0, 1) == '!'}"/>
	    <c:if test="${editableOnly}">
	        <c:set var="action" value="${fn:substring(action, 1, fn:length(action))}"/>        
	    </c:if>
	    <display:column titleKey="button.${action}">
	        <c:if test="${!editableOnly || project.saveAllowed && canWriteProject && canWriteItem}">
	            <caarray:projectListTabActionLink entityName="${entityName}" action="${action}" itemId="${item.id}" isSubtab="${isSubtab}" confirmText="${confirmText}"/>            
	        </c:if>
	    </display:column>    
	</c:forTokens>
</c:if>