<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane>
    <c:url value="/ajax/search/${currentTab}.action" var="sortUrl" >
     <c:param name="browseItems" value="${browseItems}"/>
     <c:param name="browseLink" value="${browseLink}"/>
    </c:url>

    <c:choose>
        <c:when test="${currentTab == 'sources'}">
            <%@ include file="/WEB-INF/pages/project/search/sourceList.jsp" %>
        </c:when>
        <c:when test="${currentTab == 'samples'}">
            <%@ include file="/WEB-INF/pages/project/search/sampleList.jsp" %>
        </c:when>
        <c:when test="${currentTab == 'browse'}">
            <%@ include file="/WEB-INF/pages/project/search/expFieldsTab.jsp" %>
        </c:when>
        <c:when test="${currentTab == 'biomaterial_search'}">
            <%@ include file="/WEB-INF/pages/project/search/sampleFieldsTab.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/pages/project/search/list.jsp" %>
        </c:otherwise>
    </c:choose>

</caarray:tabPane>