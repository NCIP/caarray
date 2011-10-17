<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="createProjectUrl" value="/protected/project/create.action"></c:url>
<script type="text/javascript">
window.location='${createProjectUrl}';
</script>