<%@ tag display-name="writeVersionedUrl" description="create versioned filename" body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<%@ attribute name="value" required="true" %>

<c:url value='${fn:substringBefore(value,".")}.${initParam["buildTime"]}.${fn:substringAfter(value,".")}'/>

