<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<html>
<head>
    <title>Experiment Permissions</title>
</head>

<body>
    <h1>Experiment Permissions</h1>

    <script type="text/javascript">
    function loadProfile(url) {
        $('access_profile_loading').show();
        new Ajax.Updater('access_profile_details', url,
              {evalScripts: true, onComplete: function() { $('access_profile_loading').hide();}});
        return false;
    }
    </script>

    <div class="padme">
        <c:url var="detailsUrl" value="/protected/project/details.action">
            <c:param name="project.id" value="${project.id}" />
        </c:url>
        <h2><span class="dark">Experiment:</span> ${project.experiment.title} (<a href="${detailsUrl}">Details</a>)</h2>

        <div id="tabboxwrapper_notabs">
            <div class="boxpad" style="padding-bottom: 10px">
                <p class="instructions" style="margin-bottom: 10px">
                    Assigning an Access Profile to this experiment allows you to control who gets access.
                    Assign specific access rules down to the sample level in the box on the right.
                </p>

                <div id="browsability_container" style="margin-left: auto; margin-right: auto">
                     This project is currently <em>${!project.browsable ? 'not' : ''}</em> browsable
                    <s:form action="project/permissions/toggleBrowsability" theme="simple" id="browsability_form" cssStyle="display: inline">
                        <s:hidden name="project.id"/>
                        <caarray:linkButton actionClass="save" text="Toggle" onclick="$('browsability_form').submit(); return false;"/>
                    </s:form>
                </div>

                <div class="clear" style="margin-top: 20px"></div>
                <div class="leftrightwrapper" style="padding-bottom: 20px; min-height: 500px;">
                    <div class="leftsidetable" style="width: 305px; float: left">
                        <div class="datatable" id="access_profile_owners">
                            <table class="searchresults" cellspacing="0" style="height: 30.6em">
                                <tr>
                                    <th style="height: 2.5em;">Access Profiles</th>
                                </tr>
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
                                <c:if test="${!empty collaboratorGroupsWithoutProfiles}">
                                    <tr>
                                        <td style="border-left: 0; padding-left: 0">
                                            <div class="bigbold">Add New Collaboration Group Access Profile</div>
                                            <s:form action="project/permissions/addGroupProfile" cssClass="form" theme="simple" cssStyle="display: inline" id="add_group_profile_form">
                                                <s:select name="collaboratorGroup" label="Collaboration Group" list="collaboratorGroupsWithoutProfiles"
                                                          listKey="id" listValue="group.groupName" headerKey="" headerValue="-- Select a Collaboration Group"/>
                                                <s:hidden name="project.id"/>
                                                <caarray:linkButton actionClass="add" text="Add" onclick="$('add_group_profile_form').submit(); return false;"/>
                                            </s:form>
                                        </td>
                                    </tr>
                                </c:if>
                            </table>
                        </div>
                    </div>

                    <div class="rightsidetable" style="width: 305px; float: left">
                        <div class="datatable">
                            <div id="access_profile_loading" style="display: none">
                                <img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" /> Loading access profile
                            </div>
                            <div id="access_profile_details">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>