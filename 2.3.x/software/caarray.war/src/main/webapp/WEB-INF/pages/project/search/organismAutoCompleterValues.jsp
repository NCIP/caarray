<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="selectList">
    <c:if test="${empty filterOrganisms}"><li style="background: none; cursor: auto;">-- No items found --</li></c:if>
    <c:forEach var="item" items="${filterOrganisms}"><li><input type="hidden" value="${item.id}"/>${item.scientificName}/${item.commonName}</li></c:forEach>
</ul>