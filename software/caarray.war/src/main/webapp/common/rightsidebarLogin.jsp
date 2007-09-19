<%@ include file="/common/taglibs.jsp" %>

<div id="sidebar" class="homepage">
        <h1>caArray Login</h1>
        <form method="post" id="loginForm" action="<c:url value='/j_security_check'/>" onsubmit="saveUsername(this);return validateForm(this)">
        	<table class="login">
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
            		<td colspan="2" class="centered"><input type="submit" class="button" name="login" value="<fmt:message key='button.login'/>" tabindex="3" /></td>
        		</tr>
        		<tr>
            		<td colspan="2" class="centeredsmall">
                		<a href="register.htm">Register</a> |
                		<a href="password_reminder.htm">Forgot Password?</a>
            		</td>
        		</tr>
    		</table>
        </form>
        <%@ include file="/scripts/login.js"%>
        <br />
        <h1 style="border-top:1px solid #fff;">What's New</h1>
        <p class="small">caArray 2.0 software is available for download now. This installation features a new interface and increased functionality.<br />
            <a href="#">Download caArray 2.0 &gt;&gt;</a><br />
            <a href="#">Release Notes &gt;&gt;</a>
        </p>
    </div>
