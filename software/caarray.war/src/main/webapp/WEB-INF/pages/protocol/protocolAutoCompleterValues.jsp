<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<ul class="selectList">
    <c:if test="${empty protocols}"><li style="background: none; cursor: auto;">-- No items found --</li></c:if>
    <c:forEach var="item" items="${protocols}"><li><input type="hidden" value="${item.id}"/>${item.name}</li></c:forEach>
</ul>
