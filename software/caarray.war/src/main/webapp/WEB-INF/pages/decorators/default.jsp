<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>caArray - <decorator:title default="Array Data Management System" /></title>
        <%@ include file="/WEB-INF/pages/common/meta.jsp" %>
        <link rel="address bar icon" href="<c:url value="/images/favicon.ico"/>" />
        <link rel="icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/caarray.css'/>" />
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/overwrites.css'/>" />
        <script type="text/javascript" src="<c:url value='/scripts/prototype.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/scriptaculous.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/global.js'/>"></script>
        <script type="text/javascript" src="<c:url value="/scripts/ajaxtags.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/scripts/ajaxtags_controls.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/scripts/ajaxtags_parser.js"/>"></script>
        <script type="text/javascript" language="javascript">
    		// for help script
	       	var contextPath = "<%=request.getContextPath()%>";
		</script>
        <script type="text/javascript" src="<c:url value='/scripts/help.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/print.js'/>"></script>
        <decorator:head/>
    </head>

    <c:if test="${pageContext.request.remoteUser == null}">
        <c:set var="showLoginSidebar"><decorator:getProperty property="meta.showLoginSidebar" default="false"/></c:set>
    </c:if>
    <body id="${showLoginSidebar ? 'home' : 'twocol'}">
        <div id="wrapper">
            <jsp:include page="/WEB-INF/pages/common/header.jsp"/>
            <div id="workarea">
                <div id="mainwrapper">
                    <div id="main">
                        <div id="leftnavandcontent">
                            <jsp:include page="/WEB-INF/pages/common/leftnav.jsp"/>
                            <div id="content">
                                <decorator:body/>
                                <div class="clear"></div>
                            </div>
                        </div>
                    </div>
                    <c:if test="${showLoginSidebar}">
                        <jsp:include page="/WEB-INF/pages/common/rightnavLogin.jsp"/>
                        <div class="clear"></div>
                    </c:if>
                </div>
            </div>
            <!-- footer -->
            <jsp:include page="/WEB-INF/pages/common/footer.jsp"/>
        </div>
    </body>
</html>
