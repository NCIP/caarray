<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<page:applyDecorator name="default">
<html>
    <head>
    </head>
    <body>
        <h1>Login</h1>
        <caarray:helpPrint/>
        <div class="padme">
            <div id="tabboxwrapper_notabs">
                <div class="boxpad2">
                    <h3>Login to caArray</h3>
                </div>

                <div class="boxpad">
                    <p class="instructions">
                        caArray registered users may login below. First time here? Please <a href="<c:url value="/registration/input.action"/>">register</a>
to become a caArray user.
                    </p>

                    <form method="post" id="login" action="<c:url value='/j_security_check'/>">
                        <table class="form">
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
                                <td scope="row" class="label">
                                    <label for="j_username">Username:</label>
                                </td>
                                <td class="value">
                                    <input type="text" id="j_username" name="j_username" maxlength="100" size="15"/>                                    
                                </td>
                            </tr>
                            <tr>
                                <td scope="row" class="label">
                                    <label for="j_password">Password:</label>
                                </td>
                                <td class="value">
                                    <input type="password" id="j_password" name="j_password" maxlength="100" size="15"/>
                                    <br />
                                    <a href="<c:url value="/notYetImplemented.jsp" />">Forgot your password?</a>
                                </td>
                            </tr>
                        </table>

                        <caarray:actions>
                            <caarray:action actionClass="cancel" text="Cancel">
                                <jsp:attribute name="url"><c:url value="/home.action"/></jsp:attribute>
                            </caarray:action>
                            <caarray:action actionClass="register" text="Login" onclick="document.getElementById('login').submit();"/>
                        </caarray:actions>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
</page:applyDecorator>