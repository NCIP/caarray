<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/collaborators/editTable.action" var="sortUrl">
	<c:param name="targetGroup" value="${targetGroup.id}"/>
</c:url>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
	<display:table class="searchresults" cellspacing="0" defaultsort="1" list="${targetGroup.group.users}"
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
		<display:column titleKey="button.remove">
			<c:url value="/protected/collaborators/removeUsers.action" var="removeUrl">
				<c:param name="users" value="${row.userId}"/>
				<c:param name="targetGroup" value="${targetGroup.id}"/>
			</c:url>
			<a href="${removeUrl}">
				<img src="<c:url value="/images/ico_remove.gif"/>" 
					 alt="<fmt:message key="button.remove"/>"
					 title="<fmt:message key="button.remove"/>"/>
			</a>
		</display:column>
	</display:table>
</ajax:displayTag>
