<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane>
    <c:url value="/protected/ajax/project/workspace/publicProjects.action" var="sortUrl" />
    <%@ include file="/WEB-INF/pages/project/list.jsp" %>
</caarray:tabPane>