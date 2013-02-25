<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>caArray - <decorator:title default="Array Data Management System" /></title>
        <%@ include file="/WEB-INF/pages/common/meta.jsp" %>
        <link rel="address bar icon" href="<c:url value="/images/favicon.ico"/>" />
        <link rel="icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
        <link rel="stylesheet" type="text/css" media="all" href="<caarray:writeVersionedUrl value='/styles/ext-all.css'/>" />
        <link rel="stylesheet" type="text/css" media="all" href="<caarray:writeVersionedUrl value='/styles/xtheme-gray.css'/>" />
        <link rel="stylesheet" type="text/css" media="all" href="<caarray:writeVersionedUrl value='/styles/caarray.css'/>" />
        <link rel="stylesheet" type="text/css" media="all" href="<caarray:writeVersionedUrl value='/styles/overwrites.css'/>" />
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/prototype.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scriptslib/scriptaculous.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/control_progress_bar101.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/global.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value="/scripts/ajaxtags.js"/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value="/scripts/ajaxtags_controls.js"/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value="/scripts/ajaxtags_parser.js"/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/ext-prototype-adapter.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/ext-all-debug.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/sessionTimeout.js'/>"></script>
        <script type="text/javascript">
            sessionTimeout.init(function() {
                window.close();
            });
        </script>
        <script type="text/javascript" language="javascript">
    // for help script
    var contextPath = "<%=request.getContextPath()%>";
    </script>
        <decorator:head/>
    </head>

    <body>
      <div id="leftnavandcontent" style="background: none">
         <decorator:body/>
      </div>
    </body>
</html>
