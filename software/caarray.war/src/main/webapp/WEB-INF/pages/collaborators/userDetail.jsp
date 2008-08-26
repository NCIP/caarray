<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Users</title>
</head>
<body>
    <h1>View User Information</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
            <h3>
                <a href="listGroups.action">Collaboration Groups</a> &gt;
                <span class="dark">${targetUser.firstName} ${targetUser.lastName}</span>
            </h3>
            </div>
            <div class="boxpad">
                <s:form theme="readonly" cssClass="form" id="userInfoForm" >
                    <s:textfield name="targetUser.loginName" key="label.username" tabindex="1" />
                    <s:textfield name="targetUser.firstName" key="label.firstName" tabindex="2" />
                    <s:textfield name="targetUser.lastName" key="label.lastName" tabindex="3" />
                    <s:textfield name="targetUser.emailId" key="label.email"tabindex="4" /> <%-- TODO: is it possible to make this a mailto link? --%>
                    <s:textfield name="targetUser.organization" key="label.institution" tabindex="5" />
                    <s:textfield name="targetUser.phoneNumber" key="label.phoneNumber" tabindex="6" />
                </s:form>
                <caarray:focusFirstElement formId="userInfoForm"/>
            </div>
        </div>
    </div>
</body>
</html>