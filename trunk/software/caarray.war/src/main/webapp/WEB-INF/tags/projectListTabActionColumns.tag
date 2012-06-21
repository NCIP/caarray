<%@ tag display-name="projectListTabActionColumns"
        description="Renders the action columns (edit, copy, etc) for the list view of a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="actions" required="true" %>
<%@ attribute name="item" required="true" type="com.fiveamsolutions.nci.commons.data.persistent.PersistentObject"%>
<%@ attribute name="isSubtab" required="false"%>
<%@ attribute name="confirmText" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${pageContext.request.remoteUser != null}">
    <c:set var="canWriteProject" value="${caarrayfn:canWrite(project, caarrayfn:currentUser())}"/>
    <c:set var="canFullWriteProject" value="${caarrayfn:canFullWrite(project, caarrayfn:currentUser())}"/>
    <c:set var="canWriteItem" value="${!project.locked && caarrayfn:canWrite(item, caarrayfn:currentUser()) && canWriteProject && (!project.importingData)}"/>
    <c:forTokens items="${actions}" delims="," var="action">
        <c:set var="needWriteAccess" value="${fn:substring(action, 0, 1) == '!'}"/>
        <c:if test="${needWriteAccess}">
            <c:set var="needFullWriteAccess" value="${fn:substring(action, 1, 2) == '!'}"/>
            <c:set var="action" value="${fn:substring(action, needFullWriteAccess?2:1, fn:length(action))}"/>
        </c:if>
        <display:column titleKey="button.${action}">
            <c:if test="${!needWriteAccess || (canWriteItem && (!needFullWriteAccess || canFullWriteProject))}">
                <caarray:projectListTabActionLink entityName="${entityName}" action="${action}" itemId="${item.id}" isSubtab="${isSubtab}" confirmText="${confirmText}"/>
            </c:if>
        </display:column>
    </c:forTokens>
</c:if>