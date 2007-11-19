<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="editProjectUrl" value="/project/edit.action">
    <c:param name="project.id" value="${project.id}"/>
    <c:param name="editMode" value="${editMode}" />
</c:url>
<script type="text/javascript">
window.location='${editProjectUrl}';
</script>