<%--
    The purpose of this tag is to prevent URLs that have ticks ("'") in them
    from causing javascript errors or other cross-site scripting vulnerabilities.
    This should be fixed by ajaxtags and/or prototype eventually.  When they
    fix, we should remove this class and replace all calls to <caarray:tab with
    <ajax:tab
    
    This taglib does not fix problems with URLEncoded parameters not being properly
    decoded during round trips.  That is a separate bug.
--%>

<%@ tag display-name="tab" 
    description="wrapper for the ajax taglib tab tag" 
    body-content="empty"%>

<%@ attribute name="baseUrl" required="true"%>
<%@ attribute name="caption" required="true"%>
<%@ attribute name="defaultTab" required="false"%>

<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ajax:tab caption="${caption}" 
          baseUrl="${fn:replace(baseUrl, '\\\'','\\\\\\\'')}" 
          defaultTab="${defaultTab}"/>