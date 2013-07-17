<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/audit/list.action" var="sortUrl" />
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <c:set var="auditLogDatePattern" value="MM/dd/yyyy h:mm a" />
    <display:table class="searchresults" cellspacing="0" list="${results}" requestURI="${sortUrl}" id="row" style="clear: none;" sort="external">
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="results.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="results.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="results.pageNumber" />
        <display:column title="Date" sortable="true" sortProperty="DATE">
            <fmt:formatDate value="${row.createdDate}" pattern="${auditLogDatePattern}"/>
        </display:column>
        <display:column property="username" title="Username" sortable="true" sortProperty="USERNAME"/>
        <display:column title="Activities" sortable="false">
            <ul>
            <c:forEach items="${row.details}" var="detail">
                <li><c:out value="${detail.message}"/></li>
            </c:forEach>
            </ul>
        </display:column>
    </display:table>
</ajax:displayTag>
