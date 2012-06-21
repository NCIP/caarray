<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:if test="returnProjectId == null">
    <c:url value="/protected/project/create.action" var="editProjectUrl" />
</s:if>
<s:else>
    <c:url value="/protected/project/edit.action" var="editProjectUrl" >
        <c:param name="project.id" value="${returnProjectId}"/>
        <c:param name="initialTab" value="${returnInitialTab1}" />
        <c:param name="initialTab2" value="${returnInitialTab2}" />
        <c:param name="initialTab2Url" value="${returnInitialTab2Url}" />
    </c:url>
</s:else>
<script type="text/javascript">
window.location='${editProjectUrl}';
</script>