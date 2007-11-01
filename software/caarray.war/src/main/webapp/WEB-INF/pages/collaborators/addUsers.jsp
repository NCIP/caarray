<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Collaboration Group</title>
</head>
<body>
    <h1>Manage Collaboration Group</h1>
    <div class="padme">
    <div id="tabboxwrapper_notabs">
      <div class="boxpad2">
        <h3>
          <a href="listGroups.action">Collaboration Groups</a> &gt;
          <span class="dark">
          	${targetGroup.group.groupName}
          </span>
        </h3>
      </div>
      <div class="boxpad" style="padding-bottom: 10px;">
      	<p class="instructions" style="margin-bottom: 10px;">Search for users to add in the list below.  Click the corresponding
      	<strong>Add icon</strong> in the column on the right to add the user as a group member.
      	</p>
	  </div>
		<c:url value="/protected/ajax/collaborators/addUsers.action" var="sortUrl">
			<c:param name="targetGroup" value="${targetGroup.id}"/>
		</c:url>
		<ajax:displayTag id="dataTable" ajaxFlag="true" tableClass="searchresults">
			<display:table class="searchresults" cellspacing="0" defaultsort="1" list="${allUsers}"
				requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
				<caarray:displayTagProperties/>
				<display:column titleKey="member.name" sortable="true">
					${row.lastName}, ${row.firstName}
				</display:column>
				<display:column titleKey="label.institution" sortable="true" property="organization"/>
				<display:column titleKey="label.email" sortable="true" sortProperty="email">
					<a href="mailto:${row.emailId}">${row.emailId}</a>
				</display:column>
				<display:column titleKey="button.add">
					<c:url value="/protected/collaborators/addUsers.action" var="addUrl">
						<c:param name="users" value="${row.userId}"/>
						<c:param name="targetGroup" value="${targetGroup.id}"/>
					</c:url>
					<a href="${addUrl}"><img src="<c:url value="/images/ico_add.gif"/>" alt="<fmt:message key="button.add"/>"/></a>
				</display:column>
			</display:table>
		</ajax:displayTag>
	</div>
	</div>
</body>
</html>