<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

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
                        function doLogin() {
                            <c:choose><c:when test='${empty initParam["login.warning"]}'>
                                startLogin();
                            </c:when><c:otherwise>
                                Ext.MessageBox.confirm(
                                    'Login warning',
                                    '${initParam["login.warning"]}',
                                    function(btn) {
                                        if (btn == "yes") {
                                            startLogin();
                                        }
                                    }
                                );
                            </c:otherwise></c:choose>
                        }
                        
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

                    <s:form method="post" id="login" action="/j_security_check" onsubmit="doLogin(); return false;" cssClass="form">
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
                        <s:textfield name="j_username" label="Username" maxlength="100" size="15" />
                        <s:password name="j_password" label="Password" maxlength="100" size="15" />
                        <input type="submit" class="enableEnterSubmit"/>
                    </s:form>
                    <caarray:focusFirstElement formId="login"/>
                    <caarray:actions>
                        <caarray:action actionClass="cancel" text="Cancel">
                            <jsp:attribute name="url"><c:url value="/home.action"/></jsp:attribute>
                        </caarray:action>
                        <caarray:action actionClass="register" text="Login" onclick="doLogin();"/>
                    </caarray:actions>
                </div>
            </div>
        </div>
    </body>
</html>
</page:applyDecorator>
