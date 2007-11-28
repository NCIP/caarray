<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Users</title>
</head>
<body>
    <h1>View User Information</h1>
	<div class="pagehelp">
		<a href="javascript:openHelpWindow('')" class="help">Help</a>
		<a href="javascript:printpage()" class="print">Print</a>
	</div>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
            <h3>
                <a href="listGroups.action">Collaboration Groups</a> &gt;
                <span class="dark">${targetUser.firstName} ${targetUser.lastName}</span>
            </h3>
            </div>
            <div class="boxpad">
                <s:form theme="readonly" cssClass="form">
                    <s:textfield name="targetUser.loginName" key="label.username"/>
                    <s:textfield name="targetUser.firstName" key="label.firstName"/>
                    <s:textfield name="targetUser.lastName" key="label.lastName"/>
                    <s:textfield name="targetUser.emailId" key="label.email"/> <%-- TODO: is it possible to make this a mailto link? --%>
                    <s:textfield name="targetUser.organization" key="label.institution"/>
                    <s:textfield name="targetUser.phoneNumber" key="label.phoneNumber"/>
                </s:form>
            </div>
        </div>
    </div>
</body>
</html>