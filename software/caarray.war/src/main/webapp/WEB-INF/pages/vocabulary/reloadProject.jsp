<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:if test="returnProjectId == null">
    <c:url value="/project/create.action" var="editProjectUrl" />
</s:if>
<s:else>
    <c:url value="/project/edit.action" var="editProjectUrl" >
        <c:param name="project.id" value="${returnProjectId}"/>
        <c:param name="initialTab" value="${returnInitialTab}" />
    </c:url>
</s:else>
<script type="text/javascript">
window.location='${editProjectUrl}';
</script>