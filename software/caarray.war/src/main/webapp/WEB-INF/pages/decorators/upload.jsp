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

        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.min.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/vendor/jquery.ui.widget.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/tmpl.min.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/load-image.min.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/canvas-to-blob.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.iframe-transport.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.fileupload.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.fileupload-ip.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.fileupload-ui.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/jquery.fileupload-fallback.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/application.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/upload/cors/jquery.xdr-transport.js'/>"></script>
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/upload/bootstrap.min.css'/>" />
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/upload/jquery.fileupload-ui.css'/>" />
        <script type="text/javascript" language="javascript">
            // for help script - upload.jsp
            var contextPath = "<%=request.getContextPath()%>";
         </script>
         <script>
            var fileUploadErrors = {
                maxFileSize: 'File is too big',
                minFileSize: 'File is too small',
                acceptFileTypes: 'Filetype not allowed',
                maxNumberOfFiles: 'Max number of files exceeded',
                uploadedBytes: 'Uploaded bytes exceed file size',
                emptyResult: 'Empty file upload result'
            };
         </script>

         <script id="template-upload" type="text/html">
            {% for (var i=0, files=o.files, l=files.length, file=files[0]; i<l; file=files[++i]) { %}
                <tr class="template-upload fade">
                    <td>check</td>
                    <td class="name">{%=file.name%}</td>
                    <td class="size">{%=o.formatFileSize(file.size)%}</td>
                    {% if (file.error) { %}
                        <td class="error" colspan="2"><span class="label important">Error</span> {%=fileUploadErrors[file.error] || file.error%}</td>
                    {% } else if (o.files.valid && !i) { %}
                        <td class="progress"><div class="progressbar"><div style="width:0%;"></div></div></td>
                        <td class="start" style="width: 2%">{% if (!o.options.autoUpload) { %}<button class="btn primary">Start</button>{% } %}</td>
                    {% } else { %}
                        <td colspan="2"></td>
                    {% } %}
                    <td class="cancel" style="width: 7em;">{% if (!i) { %}<button class="btn info">Cancel</button>{% } %}</td>
                    <td style="width: 1%">&nbsp;</td>
                </tr>
            {% } %}
       </script>

        <script id="template-download" type="text/html">
            {% for (var i=0, files=o.files, l=files.length, file=files[0]; i<l; file=files[++i]) { %}
                <tr class="template-download fade">
                    <td class="name">{%=file.name%}</td>
                    <td class="size">{%=o.formatFileSize(file.size)%}</td>
                    {% if (file.error) { %}
                        <td class="error" colspan="2"><span class="label important">Error</span> {%=fileUploadErrors[file.error] || file.error%}</td>
                    {% } else { %}
                        <td colspan="2"></td>
                    {% } %}
                    <td class="delete" style="width: 7em;">
                        <div style="width: 24px; height: 24px;"><img src="/caarray/images/ok.png" width="100%" height="100%"/></div>
                    </td>
                    <td style="width: 1%">&nbsp;</td>
                </tr>
            {% } %}
        </script>
        <decorator:head/>
    </head>

    <body>
      <div id="leftnavandcontent" style="background: none">
         <decorator:body/>
      </div>
    </body>
</html>
