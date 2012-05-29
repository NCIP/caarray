<%@ tag display-name="projectListTabHeader"
        description="Displays the header for a list-type project tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>

<div class="boxpad2">
    <h3><fmt:message key="${resourceKeyPrefix}" /></h3>
    <c:if test="${!project.locked && caarrayfn:canFullWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
        <div class="addlink">
            <fmt:message key="experiment.items.add" var="addCaption">
                <fmt:param><fmt:message key="experiment.${entityNameLower}"/></fmt:param>
            </fmt:message>
            <caarray:projectListTabActionLink entityName="${entityName}" action="edit" itemId="" isSubtab="${isSubtab}">
                <jsp:attribute name="linkRenderer">
                    <caarray:linkButton actionClass="add" text="${addCaption}" onclick="TabUtils.${loadTabFunction}('${tabCaption}', '${actionUrl}'); return false;"/>
                </jsp:attribute>
            </caarray:projectListTabActionLink>
        </div>
    </c:if>
</div>

