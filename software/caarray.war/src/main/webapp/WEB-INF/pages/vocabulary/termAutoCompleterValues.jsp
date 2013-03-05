<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="selectList">
    <c:if test="${empty terms}"><li style="background: none; cursor: auto;">-- No items found --</li></c:if>
    <c:forEach var="item" items="${terms}"><li><a href="#" onclick="return false;"><input type="hidden" value="${item.id}"/>${item.valueAndSource}</a></li></c:forEach>
</ul>