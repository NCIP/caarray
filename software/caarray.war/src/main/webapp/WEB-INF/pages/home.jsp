<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <meta name="showLoginSidebar" content="true"/>
</head>
<body>
    <div class="homebanner"><img src="<c:url value="/images/banner_caarray.jpg"/>" width="598" height="140" alt="" /></div>
    <h1>Welcome to the caArray Data Portal</h1>
    <caarray:helpPrint/>
    <p><strong>caArray</strong> is an open-source, web and programmatically accessible array data management system. caArray guides the annotation and exchange of array data using a federated model of local installations whose results are shareable across the cancer Biomedical Informatics Grid (caBIG™). caArray furthers translational cancer research through acquisition, dissemination and aggregation of semantically interoperable array data to support subsequent analysis by tools and services on and off the Grid. As array technology advances and matures, caArray will extend its logical library of assay management.</p>
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
                              list="@gov.nih.nci.caarray.web.action.SearchAction@getSearchCategories()" listValue="%{getText(label)}"
                              listKey="value" value="EXPERIMENT_ID"/>
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
                    <input type="submit" class="enableEnterSubmit"/>
                </s:form>
                <caarray:focusFirstElement formId="searchform"/>
           </div>
        </div>
    </div>
    <div class="clear"></div>
</body>
</html>
