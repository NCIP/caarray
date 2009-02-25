<%@ tag display-name="projectListTabRelatedItemsLinks"
        description="Renders a comma-separed list of links for viewing related entities to an item in a project list-type tab"
        body-content="empty"%>

<%@ attribute name="relatedItems" required="true" type="java.lang.Object"%>
<%@ attribute name="relatedEntityName" required="true"%>
<%@ attribute name="nameProperty" required="true"%>
<%@ attribute name="isSubtab" required="false"%>
<%@ attribute name="maxWidth" required="false" type="java.lang.Integer"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<c:if test="${empty isSubtab}">
    <c:set var="isSubtab" value="${false}"/>
</c:if>

<c:forEach items="${relatedItems}" var="item" varStatus="itemStatus">
    <caarray:projectListTabActionLink linkContent="${item[nameProperty]}" entityName="${relatedEntityName}" action="view" itemId="${item.id}" isSubtab="${isSubtab}" maxWidth="${maxWidth}"/>
    <c:if test="${!itemStatus.last}">, </c:if>
</c:forEach>
