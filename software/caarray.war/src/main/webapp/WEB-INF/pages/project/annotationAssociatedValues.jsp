<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="selectList">
    <c:if test="${empty unassociatedValues}"><li style="background: none; cursor: auto;">-- No items found --</li></c:if>
    <c:forEach var="item" items="${unassociatedValues}">
        <li><a href="#" onclick="return false;"><input type="hidden" value="${item.id}"/>${item.name}</a></li>
    </c:forEach>
</ul>