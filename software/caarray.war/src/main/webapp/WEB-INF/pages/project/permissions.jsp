<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<html>
<head>
    <title>Experiment Permissions</title>
</head>
<body>
    <h1>Experiment Permissions</h1>
    <caarray:successMessages />

    This project is currently
    <c:if test="${!project.browsable}">
       <em>not</em>
    </c:if>
    browsable.

    <p>To modify the browsability status, click 'Save Permissions' below.

    <div class="actions">
       <c:url value="/protected/project/list.action" var="cancelUrl"/>
       <c:url value="/protected/project/toggle.action" var="toggleUrl">
            <c:param name="project.id" value="${project.id}" />
       </c:url>

       <a href="${cancelUrl}" class="cancel"><img src="<c:url value="/images/btn_cancel.gif"/>" alt="<fmt:message key="button.cancel" />"></a>
       <a href="${toggleUrl}"  class="submit_experiment"><img src="<c:url value="/images/btn_savepermissions.gif"/>" alt="Toggle Browsable Status"></a>
    </div>
</body>
</html>