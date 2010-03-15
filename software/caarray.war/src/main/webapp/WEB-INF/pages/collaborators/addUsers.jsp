<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Collaboration Group</title>
    <script type="text/javascript">
    function clearFilter() {
      document.getElementById('targetUserLastName').value = '';
      document.getElementById('targetUserFirstName').value = '';
      document.getElementById('targetUserOrganization').value = '';
      document.getElementById('filterForm').submit();
    }
    </script>
</head>
<body>
    <h1>Manage Collaboration Group</h1>
    <caarray:helpPrint/>
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
    <caarray:successMessages />
        <p class="instructions" style="margin-bottom: 10px;">Search for users by choosing filter criteria.  Click the corresponding
        <strong>Add icon</strong> in the column on the right to add the user as a group member.
        </p>
    </div>
    <div id="searchboxwrapper">
      <s:form action="/protected/collaborators/addUsers.action" cssClass="form" id="filterForm">
        <s:textfield name="targetUser.lastName" key="label.lastName" size="30" tabindex="1" value="%{targetUser.lastName}" id="targetUserLastName"/>
        <s:textfield name="targetUser.firstName" key="label.firstName" size="30" tabindex="2" value="%{targetUser.firstName}" id="targetUserFirstName"/>
        <s:textfield name="targetUser.organization" key="label.institution" size="30" tabindex="3" value="%{targetUser.organization}" id="targetUserOrganization"/>
        <s:hidden name="targetGroup" value="%{targetGroup.id}"/>
        <input type="submit" class="enableEnterSubmit"/>
      </s:form>
      <caarray:focusFirstElement formId="filterForm"/>
       <caarray:actions>
            <caarray:action onclick="clearFilter();" actionClass="cancel" text="Clear" />
            <caarray:action onclick="document.getElementById('filterForm').submit();" actionClass="filter" text="Filter" />
        </caarray:actions>
    </div>
    <%@ include file="/WEB-INF/pages/collaborators/addTable.jsp" %>
  </div>
  </div>
</body>
</html>