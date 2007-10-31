<%@ tag display-name="projectListTabHeader"
        description="Displays the header for a list-type project tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<div class="boxpad2">
    <h3><fmt:message key="${resourceKeyPrefix}" /></h3>
    <div class="addlink">
        <c:url value="/protected/ajax/project/listTab/${plural}/edit.action" var="addUrl">
            <c:param name="project.id" value="${project.id}" />
        </c:url>
        <ajax:anchors target="${tabAnchor}">
            <a href="${addUrl}" class="add">
                <fmt:message key="experiment.items.add">
                    <fmt:param><fmt:message key="experiment.${entityNameLower}"/></fmt:param>
                </fmt:message>
            </a>
        </ajax:anchors>
    </div>
</div>

