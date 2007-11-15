<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="editProjectUrl" value="/protected/project/edit.action">
    <c:param name="project.id" value="${returnProjectId}"/>
    <c:param name="initialTab" value="${returnInitialTab}" />
</c:url>
<script type="text/javascript">
window.location='${editProjectUrl}';
</script>