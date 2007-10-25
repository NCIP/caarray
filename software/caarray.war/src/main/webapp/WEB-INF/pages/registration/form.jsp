<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type=text/javascript>
<!--
function showMe(id) { // This gets executed when the user clicks on the checkbox
    var obj = document.getElementById(id);
if (obj.style.visibility=="visible") { // if it is checked, make it visible, if not, hide it
    obj.style.visibility = "hidden";
} else {
    obj.style.visibility = "visible";
}
}

function authenticate()
{
    box = eval(document.forms[0].elements["ldapAuthenticate"]);
    if (box.checked == false) {
        document.getElementById('regForm').action = "save.action";
    } else {
       document.getElementById('regForm').action = "saveAuthenticate.action";
    }
}

function dbAuthenticate()
{
    document.getElementById('regForm').action = "saveAuthenticate.action";
}

function cancel()
{
    document.getElementById('regForm').action = "cancel.action";
}
//-->
</script>

<head>
</head>
<body>
    <div id="content">
        <h1>Registration</h1>
        <caarray:successMessages />
        <s:if test="hasFieldErrors()">
            <div class="error" id="errorMessages">
                <s:iterator value="fieldErrors">
                    <s:iterator value="value">
                        <img src="<c:url value="/images/iconWarning.gif"/>"
                        alt="<fmt:message key="icon.warning"/>" class="icon" />
                        <s:property escape="false"/><br />
                    </s:iterator>
                </s:iterator>
            </div>
        </s:if>
        <s:form action="save" method="post" id="regForm" theme="simple">
            <c:if test="${ldapInstall == 'TRUE'}">
                <div style="text-align: left">
                    <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>LDAP Account ?:</div>
                    <div id="fieldsarea"><s:checkbox  name="ldapAuthenticate" key="registrationRequest.ldap" value="aBoolean" fieldValue="true" onclick="showMe('div1')"/></div>
                </div>
            </c:if>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>First Name:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.firstName" key="registrationRequest.firstName" size="40" tabindex="1" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Middle Initial:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.middleInitial" key="registrationRequest.middleInitial" size="40" tabindex="2"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Last Name:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.lastName" key="registrationRequest.lastName" size="40" tabindex="3" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Phone:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.phone" key="registrationRequest.phone" size="40" tabindex="4" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Fax:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.fax" key="registrationRequest.fax" size="40" tabindex="5"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Organization:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.organization" key="registrationRequest.organization" size="40" tabindex="6" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Address 1:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.address1" key="registrationRequest.address1" size="40" tabindex="7" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Address 2:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.address2" key="registrationRequest.address2" size="40" tabindex="8"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>City:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.city" key="registrationRequest.city" size="40" tabindex="9" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>State:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.state" key="registrationRequest.state" size="40" tabindex="10"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Province:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.province" key="registrationRequest.province" size="40" tabindex="11"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Country:</div>
                <div id="fieldsarea">
                    <s:select key="registrationRequest.country"
                              name="selectedCountry"
                              list="countryList"
                              listKey="code"
                              listValue="printableName"
                              tabindex="12"
                              required="true"
                    />
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Zip:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.zip" key="registrationRequest.zip" size="40" tabindex="13" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Email:</div>
                <div id="fieldsarea"><s:textfield name="registrationRequest.email" key="registrationRequest.email" size="40" tabindex="14" required="true"/></div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Role:</div>
                    <div id="fieldsarea">
                        <s:select key="registrationRequest.role"
                                  name="selectedRole"
                                  list="#{'System Administrator':'System Administrator','Principal Investigator':'Principal Investigator',
                                  'Lab Administrator':'Lab Administrator','Lab Scientist':'Lab Scientist','Biostatistician':'Biostatistician'}"
                                  tabindex="15"
                                  required="true"
                        />
                    </div>
                </div>
                <c:choose>
                    <c:when test="${ldapInstall == 'TRUE'}">
                        <c:set var="div1Style" value="visibility:hidden;" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="div1Style" value="visibility:visible;" />
                    </c:otherwise>
                </c:choose>
                <div id="div1" style="<c:out value='${div1Style}' />">
                    <div style="text-align: left">
                        <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Username:</div>
                        <div id="fieldsarea"><s:textfield name="registrationRequest.loginName" key="registrationRequest.loginName" size="40" tabindex="16" required="true"/></div>
                    </div>
                    <div style="text-align: left">
                        <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Password:</div>
                        <div id="fieldsarea"><s:textfield name="password" key="registrationRequest.password" size="40" tabindex="17" required="true"/></div>
                    </div>
                </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"></div>
                <div id="fieldsarea">
                    <c:choose>
                        <c:when test="${ldapInstall == 'TRUE'}">
                            <input type="submit" value="Save" onclick="authenticate()" />
                        </c:when>
                        <c:otherwise>
                            <input type="submit" value="Save" onclick="dbAuthenticate()" />
                        </c:otherwise>
                    </c:choose>
                    <input type="submit" value="Cancel" onclick="cancel()" />
                </div>
            </div>
        </s:form>
    </div>
</body>
