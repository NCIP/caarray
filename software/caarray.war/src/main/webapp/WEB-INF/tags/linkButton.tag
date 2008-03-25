<%@ tag display-name="linkButton"
        description="Renders a button that is actually a link"
        body-content="empty"%>
<%@ attribute name="onclick" required="false"%>
<%@ attribute name="url" required="false"%>
<%@ attribute name="actionClass" required="true"%>
<%@ attribute name="text" required="true"%>
<%@ attribute name="tabindex" required="false"%>
<%@ attribute name="style" required="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:if test="${empty url}">
    <c:set var="url" value="#"/>
</c:if>
<a <c:if test="${!empty onclick}">onclick="${onclick}"</c:if> class="btn" href="${url}" <c:if test="${!empty tabindex}">tabindex="${tabindex}"</c:if> <c:if test="${!empty style}">style="${style}"</c:if> >
    <span class="btn_img"><span class="${actionClass}">${text}</span></span>
</a>