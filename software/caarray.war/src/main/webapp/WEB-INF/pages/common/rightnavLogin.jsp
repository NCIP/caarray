<%@ include file="/WEB-INF/pages/common/taglibs.jsp" %>

<div id="sidebar" class="homepage">
    <h1>caArray Login</h1>
    <form method="post" id="login" action="<c:url value='/j_security_check'/>" >
        <table class="login">
            <c:if test="${param.error != null}">
            <tr>
                <td colspan="2" class="centered">
                    <br />
                    <img align="absmiddle" src="<c:url value="/images/iconWarning.gif"/>" alt="<fmt:message key='icon.warning'/>" class="icon"/>
                    <fmt:message key="errors.password.mismatch"/>
                </td>
            </tr>
            </c:if>
            <tr>
                <td colspan="2" class="space">&nbsp;</td>
            </tr>
            <tr>
                <td scope="row" class="label"><label for="username">Username:</label></td>
                <td class="value"><input type="text" id="j_username" name="j_username" maxlength="100" size="15" value="" style="width:90px" tabindex="1"/></td>
            </tr>
            <tr>
                <td scope="row" class="label"><label for="password">Password:</label></td>
                <td class="value"><input type="password" id="j_password" name="j_password" maxlength="100" size="15" value="" style="width:90px" tabindex="2"/></td>
            </tr>
            <tr>
                <td colspan="2" class="centered">
                    <del class="btnwrapper">
                        <ul id="btnrow">
                            <li><caarray:linkButton actionClass="register" text="Login" tabindex="3" onclick="document.getElementById('login').submit();"/></li>
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
