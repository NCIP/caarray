<%@ tag display-name="projectListTabHiddenForm"
        description="Renders the hidden project submission form for a list-type project tab"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="isSubtab" required="false"%>

<%@ include file="projectListTabCommon.tagf"%>

<s:form action="ajax/project/listTab/${plural}/saveList" cssClass="form" id="projectForm" theme="simple">
    <s:hidden name="project.id" />
</s:form>
