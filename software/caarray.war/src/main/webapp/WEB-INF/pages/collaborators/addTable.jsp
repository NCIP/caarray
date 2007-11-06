<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/collaborators/addUsers.action" var="sortUrl">
	<c:param name="targetGroup" value="${targetGroup.id}"/>
</c:url>
<ajax:displayTag id="dataTable" ajaxFlag="true" tableClass="searchresults">
	<display:table class="searchresults" cellspacing="0" defaultsort="1" list="${allUsers}"
		requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
		<caarray:displayTagProperties/>
		<display:column titleKey="member.name" sortable="true" comparator="gov.nih.nci.caarray.web.util.UserComparator">
          <c:url value="/protected/collaborators/userDetail.action" var="viewUserUrl">
            <c:param name="targetUserId" value="${row.userId}"/>
          </c:url>
          <a href="${viewUserUrl}">${row.lastName}, ${row.firstName}</a>
		</display:column>
		<display:column titleKey="label.institution" sortable="true" property="organization"/>
		<display:column titleKey="label.email" sortable="true" property="emailId" autolink="true"/>
		<display:column titleKey="button.add">
			<c:url value="/protected/collaborators/addUsers.action" var="addUrl">
				<c:param name="users" value="${row.userId}"/>
				<c:param name="targetGroup" value="${targetGroup.id}"/>
			</c:url>
			<a href="${addUrl}"><img src="<c:url value="/images/ico_add.gif"/>" alt="<fmt:message key="button.add"/>"/></a>
		</display:column>
	</display:table>
</ajax:displayTag>