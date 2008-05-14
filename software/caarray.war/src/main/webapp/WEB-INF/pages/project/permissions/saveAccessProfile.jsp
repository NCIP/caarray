<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<%@ include file="/WEB-INF/pages/project/permissions/summary.jsp" %>

<script type="text/javascript">
    $(PermissionUtils.PROFILE_SAVING_ID).hide();
    $('ajax_success_messages').hide().innerHTML='';
    <c:forEach var="msg" items="${messages}" varStatus="msgStatus">
        new Insertion.Bottom('ajax_success_messages', '${caarrayfn:escapeJavaScript(msg)}');
        <c:if test="${msgStatus.last}">
            $('ajax_success_messages').show();
            window.setTimeout(function() { Effect.Fade('ajax_success_messages') }, 5000);
        </c:if>
    </c:forEach>
</script>
<c:remove var="messages" scope="session"/>