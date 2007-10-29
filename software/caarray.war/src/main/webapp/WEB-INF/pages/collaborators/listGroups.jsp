<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content">
        <h1>Manage Collaboration Groups
        </h1>
        <div class="padme">
			<div class="addlink_notabs">
				<a href="edit.action" class="btn" onclick="this.blur();"><span class="btn_img"><span class="add">Add a New Collaboration Group</span></span></a>
			</div>		
		    <c:url value="/protected/ajax/collaborators/listGroups.action" var="sortUrl"/>
		    
	        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
	            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${groups}"
	                requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
	                <caarray:displayTagProperties/>
	                <display:column sortProperty="group.groupName" titleKey="collaboration.group.name" sortable="true">
	                	<a href="#">${row.group.groupName}</a>
	                </display:column>
	                <display:column titleKey="collaboration.group.members">
	                	<c:if test="${fn:length(row.group.users) > 20}">
	                		<a href="#">(View All ${fn:length(row.group.users)})</a>
	                	</c:if>
	                	<c:if test="${fn:length(row.group.users) <= 20}">
		                	<c:forEach items="${row.group.users}" var="curUser" varStatus="status">
		                		<c:url value="/protected/collaborators/userDetail.action" var="viewUserUrl">
		                			<c:param name="targetUserId" value="${curUser.userId}"/>
		                		</c:url>
		                		<a href="${viewUserUrl}">${curUser.firstName} ${curUser.lastName}</a><c:if test="${!status.last}">,</c:if>
		                	</c:forEach>
	                	</c:if>
	                	<c:if test="${fn:length(row.group.users) == 0}">
	                		(Empty group)
	                	</c:if>
	                </display:column>
	                <display:column titleKey="button.edit">
	                	<c:if test="${row.owner.loginName eq pageContext.request.remoteUser}">
		                    <a href="#"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
		                </c:if>
	                </display:column>
	                <display:column titleKey="button.delete">
	                	<c:if test="${row.owner.loginName eq pageContext.request.remoteUser}">
		                	<c:url value="/protected/collaborators/delete.action" var="deleteUrl">
		                		<c:param name="targetGroup" value="${row.id}"/>
		                	</c:url>
		               		<a href="${deleteUrl}"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
		               	</c:if>
	                </display:column>
	            </display:table>
	        </ajax:displayTag>
		</div>
		<s:actionerror/>
		<s:actionmessage/>
    </div>
 </body>