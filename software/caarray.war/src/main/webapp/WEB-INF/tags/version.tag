<%@ tag display-name="version" description="Display version information for carray" body-content="empty"%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

Version: 
<span title='Subsersion URL: ${initParam.svnUrl}, revision: ${initParam.svnRevision}'>
caArray ${initParam.caarrayVersion}
</span>