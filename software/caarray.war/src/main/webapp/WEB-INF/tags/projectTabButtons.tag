<%@ tag display-name="projectTabButtons"
        description="Renders the buttons for a non-list tab"
        body-content="empty"%>

<%@ attribute name="tab" required="true"%>
<%@ attribute name="isSubtab" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>
<%@ taglib uri="/WEB-INF/caarray-functions.tld" prefix="caarrayfn" %>

<c:set var="tabLower" value="${fn:toLowerCase(fn:substring(tab, 0, 1))}${fn:substring(tab, 1, fn:length(tab))}"/>
<c:set var="tabAnchor" value="${isSubtab ? 'tabboxlevel2wrapper' : 'tabboxwrapper'}"/>
<c:set var="loadTabFunction" value="${isSubtab ? 'loadLinkInSubTab' : 'loadLinkInTab' }"/>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>

<caarray:actions>
    <c:choose>
        <c:when test="${editMode && (!project.importingData)}">
            <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('projectForm', '${tabAnchor}'); return false;"/>
        </c:when>
        <c:when test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
            <c:url value="/protected/ajax/project/tab/${tab}/load.action" var="actionUrl">
                <c:param name="project.id" value="${project.id}" />
                <c:param name="editMode" value="true" />
            </c:url>
            <fmt:message key="project.tabs.${tabLower}" var="tabCaption" />
            <caarray:action actionClass="edit" text="Edit" onclick="TabUtils.${loadTabFunction}('${tabCaption}', '${actionUrl}'); return false;"/>
        </c:when>
    </c:choose>
</caarray:actions>
