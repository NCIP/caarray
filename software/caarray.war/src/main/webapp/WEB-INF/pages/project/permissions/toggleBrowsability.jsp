<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    $('browsability_status').update("${!project.browsable ? 'not' : ''}");
    new Effect.Highlight('browsability_container');
</script>