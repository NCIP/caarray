<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<h1>Register</h1>
    <caarray:helpPrint/>
<div class="padme">
<div id="tabboxwrapper_notabs">
<div class="boxpad2"><h3>Become a caArray User</h3></div>
<div class="boxpad">
    <p class="instructions">Welcome to caArray. Submit the form to below to request access to caArray. Required fields are highlighted and have <span class="required"><span class="asterisk">*</span>asterisks<span class="asterisk">*</span></span>.</p>

    <caarray:successMessages />
    <s:actionerror/>
    <s:form action="save" method="post" id="regForm">
        <table class="form" summary="layout">
            <tr><th colspan="2">Security Information</th></tr>
            <c:if test="${ldapInstall == 'true'}">
                <s:radio
                    name="ldapAuthenticate" key="registrationRequest.ldap" list="#{true: 'Yes', false:'No'}" tabindex="1"
                    onclick="$('acct_details')[$('regForm_ldapAuthenticatetrue').checked ? 'show' : 'hide']();"
                />
            </c:if>

            <tbody id="acct_details" <s:if test="!ldapAuthenticate">style="display: none"</s:if> >
            <s:textfield name="registrationRequest.loginName" key="registrationRequest.loginName" id="loginName" tabindex="2" required="true"/>
            <s:password name="password" key="registrationRequest.password" id="password" tabindex="3" required="true"/>
            <c:if test="${ldapInstall == 'false'}">
                <s:password name="passwordConfirm" key="registrationRequest.passwordConfirm" id="passwordConfirm" tabindex="4" required="true"/>
            </c:if>
            </tbody>
            <s:checkboxlist name="registrationRequest.role" key="registrationRequest.role"
                list="@gov.nih.nci.caarray.web.action.registration.UserRole@values()" listKey="name" listValue="name" tabindex="5" required="true"/>
            <tr><th colspan="2">Account Details</th></tr>
            <s:textfield name="registrationRequest.firstName" key="registrationRequest.firstName" size="50" tabindex="6" required="true" />
            <s:textfield name="registrationRequest.middleInitial" key="registrationRequest.middleInitial" size="50" tabindex="7" />
            <s:textfield name="registrationRequest.lastName" key="registrationRequest.lastName" size="50" tabindex="8" required="true" />
            <s:textfield name="registrationRequest.email" key="registrationRequest.email" size="50" tabindex="9" required="true" />
            <s:textfield name="registrationRequest.organization" key="registrationRequest.organization" size="50" tabindex="10" required="true" />
            <s:textfield name="registrationRequest.address1" key="registrationRequest.address1" size="50" tabindex="11" required="true" />
            <s:textfield name="registrationRequest.address2" key="registrationRequest.address2" size="50" tabindex="12" />
            <s:textfield name="registrationRequest.city" key="registrationRequest.city" size="50" tabindex="13" required="true" />
            <s:select key="registrationRequest.country"
                  name="registrationRequest.country"
                  list="countryList"
                  listKey="id"
                  listValue="printableName"
                  headerKey=""
                  headerValue="--Select a Country--"
                  value="registrationRequest.country.id"
                  tabindex="14"
                  required="true"
                  onchange="$('reg_state')[$('regForm_registrationRequest_country').value == '226' ? 'show' : 'hide']();"
            />
            <tbody id="reg_state">
            <s:select key="registrationRequest.state" name="registrationRequest.state" list="stateList"
                  listKey="id" listValue="code" headerKey="" headerValue="--Select a State--"
                  id="state" value="registrationRequest.state.id" tabindex="15" required="true"
            />
            </tbody>
            <s:textfield name="registrationRequest.zip" key="registrationRequest.zip" size="20" tabindex="15" required="true" />
            <s:textfield name="registrationRequest.phone" key="registrationRequest.phone" size="20" tabindex="16" required="true"/>
            <s:textfield name="registrationRequest.fax" key="registrationRequest.fax" size="20" tabindex="17"/>
        </table>

        <caarray:actions>
            <c:url value="/home.action" var="homeUrl"/>
            <caarray:action actionClass="cancel" text="Cancel" url="${homeUrl}"/>
            <caarray:action actionClass="register" text="Submit Registration Request" onclick="$('regForm').submit()"/>
        </caarray:actions>
        <input type="submit" class="enableEnterSubmit"/>
    </s:form>
    <caarray:focusFirstElement formId="regForm"/>
</div>
</div>
</div>
