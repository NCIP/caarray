<%@ tag display-name="actions" description="Renders the actions at the bottom of a tab." body-content="scriptless"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="divclass" required="false"%>
<c:if test="${empty divclass}">
    <c:set value="actions" var="divclass" />
</c:if>
<div class="${divclass}">
    <del class="btnwrapper">
        <ul id="btnrow">
            <jsp:doBody />
        </ul>
    </del>
</div>
