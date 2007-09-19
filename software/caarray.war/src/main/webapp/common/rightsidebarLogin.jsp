<%@ include file="/common/taglibs.jsp" %>

<div id="sidebar" class="homepage">
        <h1>caArray Login</h1>
        <form method="post" id="loginForm" action="<c:url value='/j_security_check'/>" onsubmit="saveUsername(this);return validateForm(this)">
        <fieldset style="padding-bottom: 0">
            <ul>
                <c:if test="${param.error != null}">
                    <li class="error">
                        <img src="${ctx}/images/iconWarning.gif" alt="<fmt:message key='icon.warning'/>" class="icon"/>
                        <fmt:message key="errors.password.mismatch"/>
                    </li>
                </c:if>
                <li>
                    <label for="j_username">
                        <fmt:message key="label.username"/> <span>*</span>
                    </label>
                    <input type="text" id="j_username" name="j_username" maxlength="100" size="15" value="" style="width:90px" tabindex="1"/>
                </li>
                <li>
                    <label for="j_password">
                        <fmt:message key="label.password"/> <span>*</span>
                    </label>
                    <input type="password" id="j_password" name="j_password" maxlength="100" size="15" value="" style="width:90px" tabindex="2"/>
                </li>
                <li>
                    <input type="submit" class="button" name="login" value="<fmt:message key='button.login'/>" tabindex="3" />
                    <p>
                        <fmt:message key="login.signup">
                            <fmt:param><c:url value="/signup.html"/></fmt:param>
                        </fmt:message>
                    </p>
                </li>
            </ul>
        </fieldset>
        </form>
        <%@ include file="/scripts/login.js"%>
        <p><fmt:message key="login.passwordHint"/></p>
        <br />
        <h1 style="border-top:1px solid #fff;">What's New</h1>
        <p class="small">caArray 2.0 software is available for download now. This installation features a new interface and increased functionality.<br />
            <a href="#">Download caArray 2.0 &gt;&gt;</a><br />
            <a href="#">Release Notes &gt;&gt;</a>
        </p>
    </div>
