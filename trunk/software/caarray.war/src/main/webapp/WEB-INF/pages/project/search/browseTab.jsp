<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane>
    <c:url value="/ajax/browse/list.action" var="sortUrl" />
    <%@ include file="/WEB-INF/pages/project/search/list.jsp" %>
</caarray:tabPane>