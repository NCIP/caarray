<%@ tag display-name="helpPrint" description="Renders the help and print buttons at the top of each page" body-content="empty"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ attribute name="extraContent" required="false" fragment="true"%>

<div class="pagehelp">
    <a href="javascript:window.print()" class="print">Print</a>
    <a href="javascript:openHelpWindow()" class="help">User Guide</a>
    <c:if test="${!empty extraContent}">
        <jsp:invoke fragment="extraContent"/>
    </c:if>
</div>
