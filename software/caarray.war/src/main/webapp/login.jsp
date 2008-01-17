<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
  window.location='<c:url value="/login.action?fromAjax=${header['X-Requested-With'] == 'XMLHttpRequest' ? 'true' : 'false'}"/>';
</script>