<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type=text/javascript>
<!--
function authenticate() {
    if(document.forms[0].ldapAuthenticate[0].checked == true) {
        document.getElementById('regForm').action = "saveAuthenticate.action";
    } else {
        document.getElementById('regForm').action = "save.action";
    }
    document.forms[0].submit();
}
//-->
</script>

<head>
</head>
<body>
    <h1>Register</h1>
    <div class="padme">
    <div id="tabboxwrapper_notabs">
    <div class="boxpad2"><h3>Become a caArray User</h3></div>
    <div class="boxpad">
        <p class="instructions">Welcome to caArray. Submit the form to below to request access to caArray. Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.</p>

        <caarray:successMessages />
        <s:actionerror/>
        <s:form action="save" method="post" id="regForm">
            <table class="form">
                <tr><th colspan="2">Security Information</th></tr>
                <c:if test="${ldapInstall == 'true'}">
                    <s:radio
                        name="ldapAuthenticate"
                        key="registrationRequest.ldap"
                        list="#{true: 'Yes', false:'No'}"
                        tabindex="1"
                        onchange="javascript:new Effect.toggle($('loginName'),'blind'); new Effect.toggle($('password'),'blind'); new Effect.toggle($('passwordConfirm'),'blind')"
                    />
                </c:if>

                <s:textfield name="registrationRequest.loginName"
                     key="registrationRequest.loginName"
                     cssStyle="width:30%;"
                     id="loginName"
                     tabindex="2"
                     required="true"
                     template="text_display"
                     templateDir="WEB-INF/classes/template"
                     theme="freemarker"
                />
                <s:password name="password"
                    key="registrationRequest.password"
                    cssStyle="width:30%;"
                    id="password"
                    tabindex="3"
                    required="true"
                    template="password_display"
                    templateDir="WEB-INF/classes/template"
                    theme="freemarker"
                />
                <c:if test="${ldapInstall == 'false'}">
                    <s:password name="passwordConfirm"
                        key="registrationRequest.passwordConfirm"
                        cssStyle="width:30%;"
                        id="passwordConfirm"
                        tabindex="4"
                        required="true"
                        template="password_display"
                        templateDir="WEB-INF/classes/template"
                        theme="freemarker"
                    />
                </c:if>
                <s:checkboxlist name="registrationRequest.role"
                    key="registrationRequest.role"
                    list="roleList"
                    listKey="name"
                    listValue="name"
                    template="registration_checkboxlist"
                    templateDir="WEB-INF/classes/template"
                    theme="freemarker"
                    tabindex="5"
                    required="true"
                />
                <tr><th colspan="2">Account Details</th></tr>
                <s:textfield name="registrationRequest.firstName" key="registrationRequest.firstName" cssStyle="width:50%;" tabindex="6" required="true" />
                <s:textfield name="registrationRequest.middleInitial" key="registrationRequest.middleInitial" cssStyle="width:5%;" tabindex="7" />
                <s:textfield name="registrationRequest.lastName" key="registrationRequest.lastName" cssStyle="width:50%;" tabindex="8" required="true" />
                <s:textfield name="registrationRequest.email" key="registrationRequest.email" cssStyle="width:50%;" tabindex="9" required="true" />
                <s:textfield name="registrationRequest.organization" key="registrationRequest.organization" cssStyle="width:50%;" tabindex="10" required="true" />
                <s:textfield name="registrationRequest.address1" key="registrationRequest.address1" cssStyle="width:50%;" tabindex="11" required="true" />
                <s:textfield name="registrationRequest.address2" key="registrationRequest.address2" cssStyle="width:50%;" tabindex="12" />
                <s:textfield name="registrationRequest.city" key="registrationRequest.city" cssStyle="width:50%;" tabindex="13" required="true" />
                <s:select key="registrationRequest.state"
                      name="registrationRequest.state"
                      list="stateList"
                      listKey="id"
                      listValue="code"
                      headerKey=""
                      headerValue="--Select a State--"
                      template="select_display"
                      templateDir="WEB-INF/classes/template"
                      theme="freemarker"
                      id="state"
                      value="registrationRequest.state.id"
                      tabindex="14"
                      required="true"
                />
                <s:textfield name="registrationRequest.zip" key="registrationRequest.zip" size="20" cssStyle="width:30%;" tabindex="15" required="true" />
                <s:select key="registrationRequest.country"
                      name="registrationRequest.country"
                      list="countryList"
                      listKey="id"
                      listValue="printableName"
                      headerKey=""
                      headerValue="--Select a Country--"
                      value="registrationRequest.country.id"
                      tabindex="15"
                      required="true"
                      onchange="javascript:if(this.options[selectedIndex].text == 'United States'){new Effect.BlindDown('state')}else{new Effect.BlindUp('state')}"
                />
                <s:textfield name="registrationRequest.phone" key="registrationRequest.phone" size="20" cssStyle="width:30%;" tabindex="16" required="true"/>
                <s:textfield name="registrationRequest.fax" key="registrationRequest.fax" size="20" cssStyle="width:30%;" tabindex="17"/>
            </table>

            <div class="actions">
                <del class="btnwrapper">
                    <ul id="btnrow">
                        <c:url value="/registration/cancel.action" var="cancelUrl"/>
                        <c:url value="/registration/saveAuthenticate.action" var="saveUrl"/>
                        <li><a href="${cancelUrl}" class="btn" onclick="this.blur();"><span class="btn_img"><span class="cancel">Cancel</span></span></a></li>
                        <c:choose>
                            <c:when test="${ldapInstall == 'true'}">
                                <li><a href="javascript:authenticate()" class="btn" onclick="this.blur();"><span class="btn_img"><span class="register">Submit Registration Request</span></span></a></li>
                            </c:when>
                            <c:otherwise>
                                <li><a href="${saveUrl}" class="btn" onclick="this.blur();"><span class="btn_img"><span class="register">Submit Registration Request</span></span></a></li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </del>
            </div>
        </s:form>
    </div>
</body>
