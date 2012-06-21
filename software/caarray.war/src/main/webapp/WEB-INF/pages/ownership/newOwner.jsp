<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Ownership</title>
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
    <h1>Manage Ownership</h1>
    <caarray:helpPrint/>
    <div class="padme">
    <div id="tabboxwrapper_notabs">
      <div class="boxpad2">
        <h3>New Owner</h3>
      </div>
      <div class="boxpad" style="padding-bottom: 10px;">
    <caarray:successMessages />
    <s:actionerror/>
        <p class="instructions" style="margin-bottom: 10px;">Search for users by choosing filter criteria.  Select a user and press 
        <strong>Assign</strong> to change the owner of the previously selected assets to the new user.
        </p>
    </div>
    <div id="searchboxwrapper">
      <s:form action="/protected/ownership/newOwner.action" cssClass="form" id="filterForm">
        <s:token/>
        <s:textfield name="user.lastName" key="label.lastName" size="30" tabindex="1" value="%{user.lastName}" id="targetUserLastName"/>
        <s:textfield name="user.firstName" key="label.firstName" size="30" tabindex="2" value="%{user.firstName}" id="targetUserFirstName"/>
        <s:textfield name="user.organization" key="label.institution" size="30" tabindex="3" value="%{user.organization}" id="targetUserOrganization"/>
        <input type="submit" class="enableEnterSubmit"/>
        <s:hidden name="targetUserId" value="%{targetUserId}"/>
        <s:iterator value="projectIds" id="id">
            <s:hidden name="projectIds" value="%{id}"/>
        </s:iterator>
        <s:iterator value="groupIds" id="id">
            <s:hidden name="groupIds" value="%{id}"/>
        </s:iterator>
      </s:form>
      <caarray:focusFirstElement formId="filterForm"/>
      <caarray:actions>
            <caarray:action onclick="clearFilter();" actionClass="cancel" text="Clear" />
            <caarray:action onclick="document.getElementById('filterForm').submit();" actionClass="filter" text="Filter" />
      </caarray:actions>
    </div>
     <s:form action="/protected/ownership/reassign.action" theme="simple">
        <s:token/>
        <%@ include file="/WEB-INF/pages/ownership/usersTable.jsp" %>
        <s:iterator value="projectIds" id="id">
            <s:hidden name="projectIds" value="%{id}"/>
        </s:iterator>
        <s:iterator value="groupIds" id="id">
            <s:hidden name="groupIds" value="%{id}"/>
        </s:iterator>
        <s:hidden name="targetUserId" value="%{targetUserId}"/>
        <s:submit value="Assign" />
    </s:form>
  </div>
  </div>
</body>
</html>

