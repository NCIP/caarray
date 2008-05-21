<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:set var="uploadMessages">
  <caarray:successMessages />
  <s:actionerror/>
</c:set>

<script type="text/javascript">
  window.parent.uploadFinished('<c:out value="${caarrayfn:escapeJavaScript(uploadMessages)}" escapeXml="false"/>');
</script>
