<%@ tag display-name="helpPrint" description="Renders the help and print buttons at the top of each page" body-content="empty"%>


<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<%@ attribute name="extraContent" required="false" fragment="true"%>

<div class="pagehelp">
    <a href="javascript:printpage()" class="print">Print</a>
    <a href="javascript:openHelpWindow('')" class="help">Help</a>
    <c:if test="${!empty extraContent}">
        <jsp:invoke fragment="extraContent"/>
    </c:if>
</div>
