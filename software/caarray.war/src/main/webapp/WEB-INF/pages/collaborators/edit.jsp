<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Collaboration Group</title>
</head>
<body>
    <h1>Manage Collaboration Group</h1>
    <div class="padme">
      <div class="boxpad2">
        <h3>
          <a href="listGroups.action">Collaboration Groups</a> &gt;
          <span class="dark">
            <c:if test="${empty targetGroup}">
                Add a New Collaboration Group
              </c:if>
            <c:if test="${!empty targetGroup}">
                ${targetGroup.group.groupName}
              </c:if>
          </span>
        </h3>
      </div>
      <div class="boxpad" style="padding-bottom: 10px;">
        <p class="instructions" style="margin-bottom: 10px;">Choose a name for the group.</p>
        <s:form action="/protected/collaborators/name.action" cssClass="form" id="newGroupForm">
          <s:textfield required="true" name="groupName" key="collaboration.group.name" size="50" tabindex="1" value="${targetGroup.group.groupName}"/>
          <s:hidden name="targetGroup" value="${targetGroup.id}"/>
        </s:form>
        <caarray:actions>
            <caarray:action onclick="document.getElementById('newGroupForm').submit();" actionClass="save" text="Save" />
        </caarray:actions>
      </div>
  </div>
</body>
</html>