<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults"
    preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
	<display:table class="searchresults" cellspacing="0" defaultsort="1" list="${users}"
		requestURI="/protected/ajax/ownership/usersTable.action" sort="list" id="row" pagesize="20">
		<caarray:displayTagProperties/>
        <display:column>
            <input type="radio" name="ownerId" value="${row.userId}" id="u${row.userId}"/>
		</display:column>
        <display:column sortable="true" title="Username" sortProperty="loginName">
            <label for="u${row.userId}">${row.loginName}</label>
		</display:column>
		<display:column titleKey="member.name" sortable="true" comparator="gov.nih.nci.caarray.web.util.UserComparator">
          <c:url value="/protected/collaborators/userDetail.action" var="viewUserUrl">
            <c:param name="targetUserId" value="${row.userId}"/>
          </c:url>
          <a href="${viewUserUrl}">${row.lastName}, ${row.firstName}</a>
		</display:column>
		<display:column titleKey="label.institution" sortable="true" property="organization"/>
		<display:column titleKey="label.email" sortable="true" property="emailId" autolink="true"/>
	</display:table>
</ajax:displayTag>