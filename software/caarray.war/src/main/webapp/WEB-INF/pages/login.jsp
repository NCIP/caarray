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

                    <script type="text/javascript">
                      function startLogin() {
                        $('login_progress').show();
                        <c:choose>
                            <c:when test="${param.fromAjax == 'true'}">
                        new Ajax.Request('<c:url value="/protected/project/workspace.action"/>', { onSuccess: completeLogin });
                            </c:when>
                            <c:otherwise>
                        completeLogin();                            
                            </c:otherwise>
                        </c:choose>                            
                      }

                      function completeLogin() {
                        $('login').submit();
                      }
                    </script>

                    <div id="login_progress" style="display: none; margin: 3px 3px">
                       <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Logging in
                    </div>

                    <s:form method="post" id="login" action="/j_security_check" onsubmit="startLogin(); return false;" cssClass="form">
                        <c:if test="${param.error != null}">
                            <tr>
                                <td colspan="2" class="centered">
                                    <br/>
                                    <img align="top" src="<c:url value="/images/iconWarning.gif"/>" alt="<fmt:message key='icon.warning'/>" class="icon"/>
                                    <fmt:message key="errors.password.mismatch"/>
                                </td>
                            </tr>
                        </c:if>
                        <tr>
                            <td colspan="2" class="space">&nbsp;</td>
                        </tr>
                        <s:textfield name="j_username" label="Username" maxlength="100" size="15"/>
                        <s:password name="j_password" label="Password" maxlength="100" size="15">
                            <s:param name="after">
                                <br />
                                <a href="<c:url value="/notYetImplemented.jsp" />">Forgot your password?</a>
                            </s:param>
                        </s:password>
                        <input type="submit" class="enableEnterSubmit"/>
                    </s:form>
                    <caarray:actions>
                        <caarray:action actionClass="cancel" text="Cancel">
                            <jsp:attribute name="url"><c:url value="/home.action"/></jsp:attribute>
                        </caarray:action>
                        <caarray:action actionClass="register" text="Login" onclick="startLogin();"/>
                    </caarray:actions>
                </div>
            </div>
        </div>
    </body>
</html>
</page:applyDecorator>