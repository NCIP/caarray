<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body onload="document.getElementById('regForm_user_loginName').focus();">
    <div id="content" class="homepage">
        <h1>Registration</h1>
        <caarray:successMessages />
        <s:form action="Registration_save" method="post" id="regForm">
            <s:textfield name="user.loginName" key="user.loginName" size="40" tabindex="1"/>
            <s:textfield name="user.firstName" key="user.firstName" size="40" tabindex="2"/>
            <s:textfield name="user.lastName" key="user.lastName" size="40" tabindex="3"/>
            <s:textfield name="user.emailId" key="user.emailId" size="40" tabindex="4"/>
            <s:password name="user.password" key="user.password" size="40" tabindex="5"/>
            <s:password name="passwordConfirmation" key="passwordConfirmation" size="40" tabindex="6"/>
            <s:select name="isLDAP"
                      key="ldap.label"
                      list="#{'No':'No','Yes':'Yes'}"
                      value="ldap"
                      tabindex="7"
            />
            <s:submit method="save" key="button.save" />
            <s:submit method="cancel" key="button.cancel" />
        </s:form>
    </div>
</body>
