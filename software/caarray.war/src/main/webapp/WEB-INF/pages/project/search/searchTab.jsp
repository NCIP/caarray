<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane>
    <c:url value="/ajax/search/${currentTab}.action" var="sortUrl" />
    <c:choose>
        <c:when test="${searchType == 'SEARCH_BY_EXPERIMENT'}">
            <%@ include file="/WEB-INF/pages/project/search/list.jsp" %>
        </c:when>
        <c:when test="${currentTab == 'samples'}">
            <%@ include file="/WEB-INF/pages/project/search/sampleList.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/pages/project/search/sourceList.jsp"%>
        </c:otherwise>
    </c:choose>

</caarray:tabPane>