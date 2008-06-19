<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="editProjectUrl" value="/protected/project/${editMode ? 'edit' : 'details'}.action">
    <c:forEach items="${paramValues}" var="parameter">
       <c:forEach var="value" items="${parameter.value}">
         <c:param name="${parameter.key}" value="${value}"/>
       </c:forEach>
    </c:forEach>    
</c:url>
<script type="text/javascript">
window.location='${editProjectUrl}';
</script>