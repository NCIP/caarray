<%--
2: % This is the main decorator for all pages.
3: % It includes style sheet, header, footer and copyright notice.
4: --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <%@ include file="/common/meta.jsp" %>
        <title><decorator:title/> | <fmt:message key="webapp.name"/></title>

        <link rel="address bar icon" href="favicon.ico" />
        <link rel="icon" href="favicon.ico" type="image/x-icon" />
        <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/${appConfig["csstheme"]}/3coltemplate.css'/>" />

        <script type="text/javascript" src="<c:url value='/scripts/prototype.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/scriptaculous.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/global.js'/>"></script>
        <decorator:head/>
    </head>
    <body>
        <a href="#content" id="navskip">Skip to Page Content</a>
        <!-- header -->
        <jsp:include page="/common/header.jsp"/>

        <div id="workarea">
            <div id="mainwrapper">
                <div id="main">
                    <div id="leftnavandcontent">
                        <jsp:include page="/common/leftnav.jsp"/>
                        <decorator:body/>
                    </div>
                </div>
                <jsp:include page="/common/rightsidebarLogin.jsp"/>
                <div class="clear"></div>
            </div>
        </div>

        <!-- footer -->
        <jsp:include page="/common/footer.jsp"/>
    </body>
</html>
