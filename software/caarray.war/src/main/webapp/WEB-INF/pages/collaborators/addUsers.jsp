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
          	<a href="edit.action?targetGroup=${targetGroup.id}">${targetGroup.group.groupName}</a>
          </span>
        </h3>
      </div>
      <div class="boxpad" style="padding-bottom: 10px;">
      	<p class="instructions" style="margin-bottom: 10px;">Search for users to add in the list below.  Click the corresponding
      	<strong>Add icon</strong> in the column on the right to add the user as a group member.
      	</p>
	  </div>
		<%@ include file="/WEB-INF/pages/collaborators/addTable.jsp" %>
	</div>
	</div>
</body>
</html>