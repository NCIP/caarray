<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>caArray - <decorator:title default="Array Data Management System" /></title>
        <%@ include file="/WEB-INF/pages/common/meta.jsp" %>
        <link rel="address bar icon" href="<c:url value="/images/favicon.ico"/>" />
        <link rel="icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
        <link rel="stylesheet" type="text/css" media="all" href="<caarray:writeVersionedUrl value='/styles/caarray.css'/>" />
        <link rel="stylesheet" type="text/css" media="all" href="<caarray:writeVersionedUrl value='/styles/overwrites.css'/>" />
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/prototype.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scriptslib/scriptaculous.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/control_progress_bar101.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value='/scripts/global.js'/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value="/scripts/ajaxtags.js"/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value="/scripts/ajaxtags_controls.js"/>"></script>
        <script type="text/javascript" src="<caarray:writeVersionedUrl value="/scripts/ajaxtags_parser.js"/>"></script>

        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.min.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/vendor/jquery.ui.widget.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/tmpl.min.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/load-image.min.js'/>"></script>

        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.iframe-transport.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.fileupload.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.fileupload-ui.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.fileupload-fallback.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/application.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/cors/jquery.xdr-transport.js'/>"></script>

        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/upload/bootstrap.min.css'/>" />
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/upload/jquery.fileupload-ui.css'/>" />

        <script type="text/javascript" language="javascript">
    // for help script - popup.jsp
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
