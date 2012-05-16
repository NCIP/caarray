<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Audit Log</title>
        <script type="text/javascript">
function clearFilter() {
  $("criteria.username").value = '';
  $("criteria.message").value = '';
  $("filterForm").submit();
}
        </script>
    </head>
    <body>
        <h1>Audit Log</h1>
        <caarray:helpPrint/>
        <div class="padme">
            <p class="instructions" style="margin-bottom: 10px;">The list of displayed audit log entries can be filtered
                by specifying the full <strong>Username</strong> of the actor, and/or a text fragment that would appear
                in the <strong>Activity</strong> description.
                Press <strong>Enter</strong> or click <strong>Filter</strong> to apply the filter.
            </p>
            <div id="searchboxwrapper">
            <s:form action="/protected/audit/logs.action" cssClass="form" id="filterForm">
                <s:textfield id="criteria.username" name="criteria.username" label="Username" size="30" tabindex="1" value="%{criteria.username}"/>
                <s:textfield id="criteria.message" name="criteria.message" label="Activity" size="30" tabindex="1" value="%{criteria.message}"/>
                <input type="submit" class="enableEnterSubmit"/>
            </s:form>
            <caarray:focusFirstElement formId="filterForm"/>
            <caarray:actions>
                <caarray:action onclick="clearFilter();" actionClass="cancel" text="Clear" />
                <caarray:action onclick="document.getElementById('filterForm').submit();" actionClass="filter" text="Filter" />
            </caarray:actions>
            </div>
            <%@ include file="/WEB-INF/pages/audit/list.jsp" %>
        
        </div>
    </body>
</html>
