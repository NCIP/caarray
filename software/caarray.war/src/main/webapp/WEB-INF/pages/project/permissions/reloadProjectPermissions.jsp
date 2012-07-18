<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/project/permissions/editPermissions.action" var="editProjectPermissionsUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<script type="text/javascript">
window.location='${editProjectPermissionsUrl}';
</script>