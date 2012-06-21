<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="editProjectUrl" value="/protected/project/${editMode ? 'edit' : 'details'}.action">
    <c:param name="project.id" value="${project.id}"/>
</c:url>
<script type="text/javascript">
window.location='${editProjectUrl}';
</script>