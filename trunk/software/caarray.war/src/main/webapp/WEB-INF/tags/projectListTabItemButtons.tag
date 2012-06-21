<%@ tag display-name="projectListTabItemButtons"
        description="Renders the buttons for the single item view for a list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="item" required="true" type="com.fiveamsolutions.nci.commons.data.persistent.PersistentObject"%>
<%@ attribute name="isSubtab" required="false" %>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>
<caarray:actions>
    <c:choose>
        <c:when test="${editMode && (!project.importingData)}">
            <caarray:projectListTabActionLink entityName="${entityName}" action="load" itemId="${item.id}" isSubtab="${isSubtab}">
                <jsp:attribute name="linkRenderer">
                    <caarray:action actionClass="cancel" text="Cancel" onclick="TabUtils.updateSavedFormData(); TabUtils.${loadTabFunction}('${tabCaption}', '${actionUrl}'); return false;"/>
                </jsp:attribute>
            </caarray:projectListTabActionLink>
            <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('projectForm', '${tabAnchor}'); return false;"/>
        </c:when>
        <c:when test="${!project.locked && caarrayfn:canWrite(item, caarrayfn:currentUser()) && caarrayfn:canWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
            <caarray:projectListTabActionLink entityName="${entityName}" action="edit" itemId="${item.id}" isSubtab="${isSubtab}">
                <jsp:attribute name="linkRenderer">
                    <caarray:action actionClass="edit" text="Edit" onclick="TabUtils.${loadTabFunction}('${tabCaption}', '${actionUrl}'); return false;"/>
                </jsp:attribute>
            </caarray:projectListTabActionLink>
        </c:when>
    </c:choose>
</caarray:actions>
