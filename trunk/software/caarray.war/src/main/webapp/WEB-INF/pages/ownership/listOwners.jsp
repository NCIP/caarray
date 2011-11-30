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
        <h3>Users</h3>
      </div>
      <div class="boxpad" style="padding-bottom: 10px;">
        <caarray:successMessages />
        <p class="instructions" style="margin-bottom: 10px;">Search for users by choosing filter criteria.
        Select the user who's assets you want to re-assign, then press the <strong>Find Assets ...</strong> button.
        </p>
    </div>
    <div id="searchboxwrapper">
      <s:form action="/protected/ownership/listOwners.action" cssClass="form" id="filterForm">
        <s:textfield name="user.lastName" key="label.lastName" size="30" tabindex="1" value="%{user.lastName}" id="targetUserLastName"/>
        <s:textfield name="user.firstName" key="label.firstName" size="30" tabindex="2" value="%{user.firstName}" id="targetUserFirstName"/>
        <s:textfield name="user.organization" key="label.institution" size="30" tabindex="3" value="%{user.organization}" id="targetUserOrganization"/>
        <input type="submit" class="enableEnterSubmit"/>
      </s:form>
      <caarray:focusFirstElement formId="filterForm"/>
       <caarray:actions>
            <caarray:action onclick="clearFilter();" actionClass="cancel" text="Clear" />
            <caarray:action onclick="document.getElementById('filterForm').submit();" actionClass="filter" text="Filter" />
        </caarray:actions>
    </div>
    <s:form action="/protected/ownership/assets.action" theme="simple" method="get">
        <%@ include file="/WEB-INF/pages/ownership/usersTable.jsp" %>
        <s:submit value="Find Assets ..." />
    </s:form>
  </div>
  </div>
</body>
</html>
