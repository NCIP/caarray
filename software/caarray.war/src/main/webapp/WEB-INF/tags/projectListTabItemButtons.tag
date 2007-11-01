<%@ tag display-name="projectListTabEditLink"
        description="Renders the edit link for a project list-type tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="isSubtab" required="false" %>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>
<c:set var="submitTabFormMethod" value="${isSubtab ? 'submitSubTabForm' : 'submitTabForm'}"/>
<caarray:actions>
    <s:if test="editMode">
        <caarray:projectListTabActionLink entityName="${entityName}" action="load" itemId="${row.id}" isSubtab="${isSubtab}">
            <jsp:attribute name="linkRenderer">
                <caarray:action actionClass="cancel" text="Cancel" onclick="TabUtils.${loadTabFunction}('${tabCaption}', '${actionUrl}'); return false;"/>
            </jsp:attribute>
        </caarray:projectListTabActionLink>
        <caarray:action actionClass="save" text="Save" onclick="TabUtils.${submitTabFormMethod}('projectForm', '${tabAnchor}', 'save_draft'); return false;"/>
    </s:if>
    <s:else>
        <caarray:projectListTabActionLink entityName="${entityName}" action="edit" itemId="${itemId}" isSubtab="true">
            <jsp:attribute name="linkRenderer">
                <caarray:action actionClass="edit" text="Edit" onclick="TabUtils.${loadTabFunction}('${tabCaption}', '${actionUrl}'); return false;"/>
            </jsp:attribute>
        </caarray:projectListTabActionLink>
    </s:else>
</caarray:actions>
