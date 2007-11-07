<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<html>
<head>
    <title>Experiment Permissions</title>
</head>

<body>
    <h1>Experiment Permissions</h1>

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
        <c:url var="detailsUrl" value="/protected/project/details.action">
            <c:param name="project.id" value="${project.id}" />
        </c:url>
        <h2><span class="dark">Experiment:</span> ${project.experiment.title} (<a href="${detailsUrl}">Details</a>)</h2>

        <div id="tabboxwrapper_notabs">
            <div class="boxpad">
                <p class="instructions">
                    Assigning an Access Profile to this experiment allows you to control who gets access.
                    Assign specific access rules down to the sample level in the box on the right.
                </p>

                <div class="datatable" id="access_profile_owners" style="margin-top: 10px">
                    <table class="searchresults" cellspacing="0">
                        <tr>
                            <th>Browsability</th>
                        </tr>
                        <tr>
                            <td>
                                This project is currently <em>${!project.browsable ? 'not' : ''}</em> browsable
                            </td>                        
                        </tr>
                        <tr>
                            <td>
                                <s:form action="project/permissions/toggleBrowsability" theme="simple" id="browsability_form">
                                    <s:hidden name="project.id"/>
                                    <caarray:actions divclass="actionsthin">
                                        <caarray:action actionClass="save" text="Toggle" onclick="$('browsability_form').submit(); return false;"/>
                                    </caarray:actions>
                                </s:form>                                                        
                            </td>                        
                        </tr>
                    </table>
                </div>                    

                <c:if test="${project.browsable}">
                    <div class="line" style="margin-bottom: 10px"></div>
                    <div style="padding-bottom: 20px; min-height: 500px;">
                            <div class="leftsidetable" style="width: 305px; float: left">
                            <table class="searchresults permissiontable" cellspacing="0">
                                <tr>
                                    <th>Access Profiles</th>
                                </tr>
                            </table>
                            <div class="datatable" id="access_profile_owners" style="padding-bottom: 0px">
                                <div class="scrolltable" style="height: auto; max-height: 200px; margin: 0px; overflow-x: hidden">
                                <table class="searchresults permissiontable" cellspacing="0">
                                    <c:url var="loadPublicProfileUrl" value="/protected/ajax/project/permissions/loadPublicProfile.action">
                                        <c:param name="project.id" value="${project.id}"/>
                                    </c:url>
                                    <caarray:projectAccessProfileOwner name="The Public" loadProfileUrl="${loadPublicProfileUrl}"
                                        description="Control access to the experiment by anonymous users"/>
                                    <c:forEach items="${project.groupProfiles}" var="groupMapping">
                                        <c:url var="loadGroupProfileUrl" value="/protected/ajax/project/permissions/loadGroupProfile.action">
                                            <c:param name="project.id" value="${project.id}"/>
                                            <c:param name="collaboratorGroup.id" value="${groupMapping.key.id}"/>
                                        </c:url>
                                        <caarray:projectAccessProfileOwner loadProfileUrl="${loadGroupProfileUrl}"
                                            name="Collaboration Group: ${groupMapping.key.group.groupName}"
                                            description="Control access to the experiment by members of this group"/>
                                    </c:forEach>
                                </table>
                                </div>
                            </div>
                            <table class="searchresults" cellspacing="0">
                                <c:if test="${!empty collaboratorGroupsWithoutProfiles}">
                                    <tr>
                                        <th>
                                            Add New Collaboration Group Access Profile                                        
                                        </th>
                                    </tr>
                                    <tr>
                                        <td>
                                            <s:form action="project/permissions/addGroupProfile" cssClass="form" theme="simple" cssStyle="display: inline" id="add_group_profile_form">
                                                <s:select name="collaboratorGroup" label="Collaboration Group" list="collaboratorGroupsWithoutProfiles"
                                                    listKey="id" listValue="group.groupName" headerKey="" headerValue="-- Select a Collaboration Group"/>
                                                <s:hidden name="project.id"/>
                                                <caarray:linkButton actionClass="add" text="Add" onclick="$('add_group_profile_form').submit(); return false;" style="margin-top: 3px"/>
                                            </s:form>
                                        </td>
                                    </tr>
                                </c:if>
                            </table>
                        </div>

                        <div class="rightsidetable" style="width: 305px; float: left">
                            <div class="datatable">
                                <div id="access_profile_loading" style="display: none">
                                    <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Loading access profile
                                </div>
                                <div id="access_profile_saving" style="display: none">
                                    <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Saving access profile
                                </div>
                                <div id="access_profile_instructions">
                                    Select an Access Profile to edit its specific access rules
                                </div>
                                <div id="access_profile_details"></div>
                            </div>
                        </div>
                    </div>                
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>