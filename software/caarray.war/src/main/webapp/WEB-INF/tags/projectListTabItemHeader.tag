<%@ tag display-name="projectListTabItemHeader"
        description="Displays the header for showing a single item in a list-type project tab and configures readonly mode"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="itemName" required="true"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>

<c:set var="linkToList">
    <caarray:projectListTabActionLink entityName="${entityName}" action="load" itemId="${row.id}" isSubtab="${isSubtab}">
        <jsp:attribute name="linkContent"><fmt:message key="experiment.${pluralLower}"/></jsp:attribute>
    </caarray:projectListTabActionLink>
</c:set>

<c:if test="${isSubtab}"><div class="boxpad2"></c:if>
<c:choose>
    <c:when test="${empty itemId}">
        <h3>
            <c:out value="${linkToList}" escapeXml="false"/>
             >
            <fmt:message key="experiment.items.add">
                <fmt:param><fmt:message key="experiment.${entityNameLower}"/></fmt:param>
            </fmt:message>
        </h3>
    </c:when>
    <c:otherwise>
        <h3 style="word-wrap:break-word">
            <c:out value="${linkToList}" escapeXml="false"/>
             > ${itemName}
        </h3>
    </c:otherwise>
</c:choose>
<c:if test="${isSubtab}"></div></c:if>