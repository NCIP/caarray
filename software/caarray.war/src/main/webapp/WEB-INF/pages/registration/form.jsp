<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type=text/javascript>
<!--
function showMe(id) {
    var obj = document.getElementById(id);
    if (obj.style.visibility=="visible") {
        obj.style.visibility = "hidden";
    } else {
        obj.style.visibility = "visible";
    }
}
function authenticate() {
    box = eval(document.forms[0].elements["ldapAuthenticate"]);
    if (box.checked == false) {
        document.getElementById('regForm').action = "save.action";
    } else {
       document.getElementById('regForm').action = "saveAuthenticate.action";
    }
}
//-->
</script>

<head>
</head>
<body>
        <h1>Registration</h1>
        <caarray:successMessages />
        <s:actionerror/>
        <s:form action="save" method="post" id="regForm" theme="simple">
            <c:if test="${ldapInstall == 'true'}">
                <div style="text-align: left">
                    <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>LDAP Account ?:</div>
                    <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                        <c:choose>
                            <c:when test="${ldapAuthenticate == 'false'}">
                                <s:checkbox  name="ldapAuthenticate" key="registrationRequest.ldap" value="false" onclick="showMe('div1')"/>
                            </c:when>
                            <c:otherwise>
                                <s:checkbox  name="ldapAuthenticate" key="registrationRequest.ldap" value="true" onclick="showMe('div1')"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>First Name:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.firstName</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.firstName" key="registrationRequest.firstName" size="40" tabindex="1" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Middle Initial:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.middleInitial</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.middleInitial" key="registrationRequest.middleInitial" size="40" tabindex="2"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Last Name:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.lastName</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.lastName" key="registrationRequest.lastName" size="40" tabindex="3" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Phone:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.phone</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.phone" key="registrationRequest.phone" size="40" tabindex="4" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Fax:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.fax</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.fax" key="registrationRequest.fax" size="40" tabindex="5"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Organization:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.organization</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.organization" key="registrationRequest.organization" size="40" tabindex="6" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Address 1:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.address1</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.address1" key="registrationRequest.address1" size="40" tabindex="7" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Address 2:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.address2</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.address2" key="registrationRequest.address2" size="40" tabindex="8"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>City:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.city</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.city" key="registrationRequest.city" size="40" tabindex="9" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>State:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.state</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.state" key="registrationRequest.state" size="40" tabindex="10"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span></span>Province:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.province</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.province" key="registrationRequest.province" size="40" tabindex="11"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Country:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:select key="registrationRequest.country"
                              name="registrationRequest.country"
                              list="countryList"
                              listKey="id"
                              listValue="printableName"
                              tabindex="12"
                              required="true"
                    />
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Zip:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.zip</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.zip" key="registrationRequest.zip" size="40" tabindex="13" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Email:</div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <s:fielderror><s:param>registrationRequest.email</s:param></s:fielderror>
                    <s:textfield name="registrationRequest.email" key="registrationRequest.email" size="40" tabindex="14" required="true"/>
                </div>
            </div>
            <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Role:</div>
                    <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                        <s:select key="registrationRequest.role"
                                  name="registrationRequest.role"
                                  list="#{'System Administrator':'System Administrator','Principal Investigator':'Principal Investigator',
                                  'Lab Administrator':'Lab Administrator','Lab Scientist':'Lab Scientist','Biostatistician':'Biostatistician'}"
                                  tabindex="15"
                                  required="true"
                        />
                    </div>
                </div>
                <c:choose>
                    <c:when test="${ldapInstall == 'true'&& ldapAuthenticate == 'false'}">
                        <c:set var="div1Style" value="visibility:hidden;" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="div1Style" value="visibility:visible;" />
                    </c:otherwise>
                </c:choose>
                <div id="div1" style="<c:out value='${div1Style}' />">
                    <div style="text-align: left">
                        <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Username:</div>
                        <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                            <s:fielderror><s:param>registrationRequest.loginName</s:param></s:fielderror>
                            <s:textfield name="registrationRequest.loginName" key="registrationRequest.loginName" size="40" tabindex="16" required="true"/>
                        </div>
                    </div>
                    <div style="text-align: left">
                        <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Password:</div>
                        <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                            <s:fielderror><s:param>password</s:param></s:fielderror>
                            <s:password name="password" key="registrationRequest.password" size="40" tabindex="17" required="true"/>
                        </div>
                    </div>
                     <div style="text-align: left">
                        <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"><span>*</span>Confirm Password:</div>
                        <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                            <s:fielderror><s:param>passwordConfirm</s:param></s:fielderror>
                            <s:password name="passwordConfirm" key="registrationRequest.passwordConfirm" size="40" tabindex="18" required="true"/>
                        </div>
                    </div>
                </div>
                <div style="text-align: left">
                <div style="float:left; width:100px; margin-left:11px; margin-top:5px; text-align:left;"></div>
                <div style="margin-top:5px; margin-bottom:5px; padding:0px;">
                    <c:choose>
                        <c:when test="${ldapInstall == 'true'}">
                            <s:submit type="submit" value="Save" onclick="authenticate()"/>
                        </c:when>
                        <c:otherwise>
                            <s:submit type="submit" value="Save" method="saveAuthenticate"/>
                        </c:otherwise>
                    </c:choose>
                    <s:submit type="submit" value="Cancel" method="cancel"/>
                </div>
            </div>
        </s:form>

</body>
