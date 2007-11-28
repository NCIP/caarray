<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <meta name="showLoginSidebar" content="true"/>
</head>
<body>
    <div class="homebanner"><img src="<c:url value="/images/banner_caarray.jpg"/>" width="600" height="140" alt="" /></div>
    <h1>Welcome to the caArray Data Portal</h1>
    <div class="pagehelp" style="margin-top:-1.8em">
        <a href="javascript:openHelpWindow('')" class="help">Help</a>
        <a href="javascript:printpage()" class="print">Print</a>
    </div>
    <p><strong>caArray</strong> is an open-source, role-based, Web and programmatically accessible data management system that guides the annotation and exchange of array data through a federated model of local and centralized installations. It provides browser-based and programmatic access to the data stored locally; enables mechanisms for accessing all local installation data over <a href="http://cabig.nci.nih.gov/workspaces/Architecture/caGrid/" class="external" target"#">caGrid</a>; supports silver compatibility with <a href="http://cabig.cancer.gov/index.asp" class="external" target"#">caBIG</a> guidelines, promotes compatibility with the MIAME 1.1 guidelines and the import of MAGE-TAB and provides a data service for caBIG analytical services.</p>
    <div id="browsesearchwrapper">
        <div id="browseboxhome" style="margin-bottom: 20px">
            <h2 class="tanbar">Browse caArray</h2>
            <div class="boxpad">
                <table class="alttable" cellspacing="0">                
                    <tr>
                        <th colspan="2">
                    <label for="location"><fmt:message key="search.location"/></label>
                    <s:select name="location" theme="simple"
                              list="#{'NCICB':'NCICB'}"
                              headerKey="" headerValue="(All Locations)"/>                                            
                        </th>
                    </tr>
                    <c:forEach items="${browseItems}" var="row" varStatus="rowStatus">
                        <tr class="${rowStatus.count % 2 == 0 ? 'even' : 'odd'}">
                            <td>
                                <s:if test="${!empty row.category}">
                                    <c:url value="/browse.action" var="browseLink">
                                        <c:param name="category" value="${row.category}"/>
                                    </c:url>
                                    <a href="${browseLink}"><s:text name="${row.category.resourceKey}"/></a>
                                </s:if><s:else>
                                    <s:text name="${row.resourceKey}"/>
                                </s:else>                            
                            </td>
                            <td>
                                <s:if test="${!empty row.category}">
                                    <c:url value="/browse.action" var="browseLink">
                                        <c:param name="category" value="${row.category}"/>
                                    </c:url>
                                    <a href="${browseLink}">${row.count}</a>
                                </s:if><s:else>${row.count}</s:else>                            
                            </td>                        
                        </tr>                    
                    </c:forEach>
                </table>
            </div>
        </div>
        <div id="searchboxhome">
            <h2 class="tanbar">Search caArray</h2>
            <div class="boxpad">
                <s:form id="searchform" action="/search/basicSearch.action" cssClass="alttable">
                    <s:textfield name="keyword" key="search.keyword"/>
                    <s:select name="category" key="search.category"
                              list="@gov.nih.nci.caarray.domain.search.SearchCategory@values()" listValue="%{getText(resourceKey)}"
                              headerKey="" headerValue="(All Categories)"/>
                    <s:select name="location" key="search.location"
                              list="#{'NCICB':'NCICB'}"
                              headerKey="" headerValue="(All Locations)"/>
                    <tr>
                        <td colspan="2" class="centered">
                    <del class="btnwrapper">
                        <ul id="btnrow">
                            <caarray:action onclick="$('searchform').submit(); return false;" actionClass="search" text="Search"/>                        
                         </ul>
                     </del>
                        </td>
                    </tr>
                </s:form>
            </div>
        </div>
    </div>
    <div class="clear"></div>
</body>
</html>
