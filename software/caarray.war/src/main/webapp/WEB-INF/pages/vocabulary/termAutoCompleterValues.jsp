<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="selectList">
    <c:if test="${empty terms}"><li style="background: none; cursor: auto;">-- No items found --</li></c:if>
    <c:forEach var="item" items="${terms}"><li><input type="hidden" value="${item.id}"/>${item.valueAndSource}</li></c:forEach>
</ul>