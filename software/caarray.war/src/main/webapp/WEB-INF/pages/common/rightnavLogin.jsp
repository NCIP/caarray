<%@ include file="/WEB-INF/pages/common/taglibs.jsp" %>

<%-- 
   DEVELOPER NOTE 
   The servlet spec makes it hard to have a login widget on a public page that you just
   fill out, without first attempting to access a protected page. It will basically ignore
   the attempted login unless the user tries to access a protected page first. Therefore we
   trick the container by firing off an ajax request in the background to a protected page,
   then once that returns, submitting the login form as if redirected there by the container.
--%>

<script type="text/javascript">
function startLogin() {
$('login_progress').show();
new Ajax.Request('<c:url value="/protected/project/workspace.action"/>', { onSuccess: completeLogin });
}

function completeLogin() {
$('login').submit();
}

</script>
<div id="sidebar" class="homepage">
    <h1>caArray Login</h1>
    <div id="login_progress" style="display: none; margin: 3px 3px">
       <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Logging in
    </div>
    <form id="login" action="<c:url value='/j_security_check'/>" >
        <table class="login">
            <c:if test="${param.error != null}">
            <tr>
                <td colspan="2" class="centered">
                    <br />
                    <img align="top" src="<c:url value="/images/iconWarning.gif"/>" alt="<fmt:message key='icon.warning'/>" class="icon"/>
                    <fmt:message key="errors.password.mismatch"/>
                </td>
            </tr>
            </c:if>
            <tr>
                <td colspan="2" class="space">&nbsp;</td>
            </tr>
            <tr>
                <td scope="row" class="label"><label for="j_username">Username:</label></td>
                <td class="value"><input type="text" id="j_username" name="j_username" maxlength="100" size="15" value="" style="width:90px" tabindex="1"/></td>
            </tr>
            <tr>
                <td scope="row" class="label"><label for="j_password">Password:</label></td>
                <td class="value"><input type="password" id="j_password" name="j_password" maxlength="100" size="15" value="" style="width:90px" tabindex="2"/></td>
            </tr>
            <tr>
                <td colspan="2" class="centered">
                    <del class="btnwrapper">
                        <ul id="btnrow">
                            <li><caarray:linkButton actionClass="register" text="Login" tabindex="3" onclick="startLogin()"/></li>
                        </ul>
                    </del>
                </td>
            </tr>
            <tr>
                <td colspan="2" class="centered">
                    <br />
                    <a href="<c:url value="/registration/input.action"/>">Register</a>
                    <span class="bar">|</span>
                    <a href="<c:url value="/notYetImplemented.jsp" />">Forgot Password?</a>
              </td>
          </tr>
      </table>
    </form>
</div>
