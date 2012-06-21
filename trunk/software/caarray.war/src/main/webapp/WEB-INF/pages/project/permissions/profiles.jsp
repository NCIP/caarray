<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<html>
<head>
    <title>Experiment Permissions</title>
    <style>
        table.searchresults table.form td {
          border: none;
        }
    </style>
</head>

<body>
    <h1>Experiment Permissions</h1>
    <caarray:helpPrint/>
    <script type="text/javascript">

    var SecurityLevel = new Object();
    SecurityLevel.sampleSecurityLevels = new Object();
    <s:iterator value="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@values()" id="secLevel">
    SecurityLevel.sampleSecurityLevels['<s:property value="#secLevel.name()"/>'] = new Array();
        <s:iterator value="#secLevel.sampleSecurityLevels" id="sampleSecLevel">
    SecurityLevel.sampleSecurityLevels['<s:property value="#secLevel.name()"/>'].push({ value: '<s:property value="#sampleSecLevel.name()"/>', label: '<s:property value="getText(#sampleSecLevel.resourceKey)"/>' });
        </s:iterator>
    </s:iterator>
    </script>

    <div class="padme">
        <c:url var="detailsUrl" value="/project/details.action">
            <c:param name="project.id" value="${project.id}" />
        </c:url>
        <h2><span class="dark">Experiment:</span> ${project.experiment.title} (<a href="${detailsUrl}">Details</a>)</h2>

        <div id="tabboxwrapper_notabs" style="height:650px;">
            <div class="boxpad">
                <p class="instructions">
                    Assigning an Access Profile to this experiment allows you to control who gets access.
                    Assign specific access rules down to the sample level in the box on the right.
                </p>

                <caarray:successMessages />
                <div class="message" id="ajax_success_messages" style="display: none">
                </div>

                <div style="padding-bottom: 20px; min-height: 500px; margin-top: 10px">
                    <div class="leftsidetable" style="width: 350px; float: left">
                        <table class="searchresults permissiontable" cellspacing="0">
                            <tr>
                                <th>Who May Access this Experiment</th>
                            </tr>
                        </table>
                        <div class="datatable" id="access_profile_owners" style="padding-bottom: 0px">
                            <div class="scrolltable" style="height: auto">
                            <table class="searchresults permissiontable" cellspacing="0">
                                <tr>
                                    <td>
                                        <div class="bigbold">The Public</div>
                                        <c:url var="loadPublicProfileUrl" value="/protected/ajax/project/permissions/loadPublicProfile.action">
                                            <c:param name="project.id" value="${project.id}"/>
                                        </c:url>
                                        <caarray:linkButton actionClass="edit" text="Edit Access Control" style="margin-left: 0px" onclick="PermissionUtils.loadProfile('${loadPublicProfileUrl}');"/>
                                        <p class="nopad" style="clear: both"><br>Edit access to the experiment by anonymous users</p>
                                        <p>
                                        The following access profiles can be managed to grant visibility into the summary
                                        and/or contents of an experiment prior to its publication. The profiles are:
                                        </p>
                                        <ul id="profile_legend">
                                            <s:iterator value="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@publicLevels()" id="publicLevel">
                                            <li>
                                                <span class="bigbold"><fmt:message key="${publicLevel.resourceKey}"/></span>
                                                - <fmt:message key="${publicLevel.resourceKey}.description"/>
                                            </li>                                            
                                            </s:iterator>
                                        </ul>
                                        <br/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <div class="bigbold">Collaboration Groups</div>
                                        <c:choose>
                                            <c:when test="${!empty collaboratorGroups}">
                                                <p class="nopad">Edit access to the experiment by members of selected group</p>
                                                <s:form action="ajax/project/permissions/loadGroupProfile" cssClass="form" theme="simple" id="collaborators_form" onsubmit="PermissionUtils.loadProfileFromForm('collaborators_form'); return false;">
                                                    <s:hidden name="project.id"/>
                                                    <s:label for="collaborators_form_collaboratorGroup_id" value="Collaboration Group"/>
                                                    <s:select required="true" name="collaboratorGroup.id" tabindex="1" label="Collaboration Group"
                                                              list="collaboratorGroups" listKey="id" listValue="group.groupName"/>
                                                    <caarray:linkButton actionClass="edit" text="Edit Access Control" style="margin-left: 0px" onclick="PermissionUtils.loadProfileFromForm('collaborators_form');"/>
                                                    <input type="submit" class="enableEnterSubmit"/>
                                                </s:form>
                                            </c:when>
                                            <c:otherwise>
                                                There are no collaboration groups defined.
                                            </c:otherwise>
                                        </c:choose>
                                        <caarray:linkButton actionClass="add" text="Add New Group" style="margin-left: 0px">
                                            <jsp:attribute name="url"><c:url value="/protected/collaborators/edit.action"/></jsp:attribute>
                                        </caarray:linkButton>
                                    </td>
                                </tr>
                            </table>
                            </div>
                        </div>
                    </div>

                    <div class="rightsidetable" style="width: 305px; float: left">
                        <div class="datatable">
                            <div id="access_profile_loading" style="display: none">
                                <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Loading access profile
                            </div>
                            <div id="access_profile_saving" style="display: none">
                                <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Saving access profile
                            </div>
                            <div id="access_profile_details">
                                <%@ include file="/WEB-INF/pages/project/permissions/summary.jsp" %>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</body>
</html>