<%--
2: % This is the main decorator for all pages.
3: % It includes style sheet, header, footer and copyright notice.
4: --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <%@ include file="/WEB-INF/pages/common/meta.jsp" %>
        <title><decorator:title/> | <fmt:message key="webapp.name"/></title>
        <link rel="address bar icon" href="favicon.ico" />
        <link rel="icon" href="favicon.ico" type="image/x-icon" />
        <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/${appConfig["csstheme"]}/theme.css'/>" />
        <script type="text/javascript" src="<c:url value='/scripts/prototype.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/scriptaculous.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/global.js'/>"></script>
        <decorator:head/>
    </head>
    <body>
        <a href="#content" id="navskip">Skip to Page Content</a>
        <!-- header -->
        <jsp:include page="/WEB-INF/pages/common/header.jsp"/>

        <div id="workarea">
            <div id="mainwrapper">
                <div id="main">
                    <div id="leftnavandcontent">
                        <jsp:include page="/WEB-INF/pages/common/leftnav.jsp"/>
                        <decorator:body/>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${param.login != null}">
                        <jsp:include page="/WEB-INF/pages/common/rightsidebarLogin.jsp"/>
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="/WEB-INF/pages/common/rightsidebar.jsp"/>
                    </c:otherwise>
                </c:choose>
                <div class="clear"></div>
            </div>
        </div>

        <!-- footer -->
        <jsp:include page="/WEB-INF/pages/common/footer.jsp"/>
    </body>
</html>
