<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Collaboration Group</title>
</head>
<body>
    <h1>Manage Collaboration Groups
    </h1>
    <caarray:helpPrint/>
    <div class="padme">
    	<div id="tabboxwrapper_notabs">
    	<div class="boxpad2">
            <h3>Collaboration Groups</h3>
		    <div class="addlink_notabs">
		        <caarray:linkButton url="edit.action" actionClass="add" text="Add a New Collaboration Group" />
		    </div>
		</div>
		<caarray:successMessages />
      	<c:url value="/protected/ajax/collaborators/listGroups.action" var="sortUrl"/>

        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${groups}"
                requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
                <caarray:displayTagProperties/>
                <c:url value="/protected/collaborators/edit.action" var="editUrl">
                    <c:param name="targetGroup" value="${row.id}"/>
                </c:url>
                <display:column property="group.groupName" titleKey="collaboration.group.name" sortable="true"/>
                <display:column titleKey="collaboration.group.members">
                  <c:if test="${fn:length(row.group.users) > 20}">
                    <a href="${editUrl}">(View All ${fn:length(row.group.users)})</a>
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
                  <c:if test="${fn:toLowerCase(row.owner.loginName) eq caarrayfn:currentUsername()}">
                      <a href="${editUrl}">
                      	<img src="<c:url value="/images/ico_edit.gif"/>"
                      		 alt="<fmt:message key="button.edit"/>" 
                      		 title="<fmt:message key="button.edit"/>"/>
                    </a>
                  </c:if>
                </display:column>
                <display:column titleKey="button.delete">
                  <c:if test="${fn:toLowerCase(row.owner.loginName) eq caarrayfn:currentUsername()}">
                    <c:url value="/protected/collaborators/delete.action" var="deleteUrl">
                      <c:param name="targetGroup" value="${row.id}"/>
                    </c:url>
                     <a href="${deleteUrl}">
                     	<img src="<c:url value="/images/ico_delete.gif"/>"
                     		 alt="<fmt:message key="button.delete"/>" 
                     		 title="<fmt:message key="button.delete"/>"/>
                     </a>
                   </c:if>
                </display:column>
            </display:table>
        </ajax:displayTag>
  </div>
  </div>
</body>
</html>