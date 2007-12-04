<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:form action="ajax/project/permissions/saveAccessProfile" theme="simple" id="profileForm" onsubmit="PermissionUtils.saveProfile(); return false;">
    <s:hidden name="project.id"/>
    <s:hidden name="accessProfile.id"/>

    <table class="searchresults" cellspacing="0">
        <tr>
            <th>Control Access to Experiment for ${profileOwnerName}</th>
        </tr>
        <tr>
            <td class="filterrow" style="border-bottom: 1px solid #999">
                <s:label for="profileForm_accessProfile_securityLevel" value="Experiment Access"/>
                <s:select required="true" name="accessProfile.securityLevel" tabindex="1"
                    list="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@${publicProfile ? 'publicLevels' : 'values'}()" listValue="%{getText(resourceKey)}"
                    onchange="PermissionUtils.changeExperimentAccess(this)"/>
            </td>
        </tr>
        <tr>
            <th class="title">Sample ID</th>
        </tr>
    </table>

    <div class="datatable" style="padding-bottom: 0px">
        <div class="scrolltable" style="height: auto; max-height: 170px; overflow-x: hidden">
        <table class="searchresults permissiontable" cellspacing="0">
            <tbody id="access_profile_samples"
                <c:if test="${!accessProfile.securityLevel.sampleLevelPermissionsAllowed}">style="display:none"</c:if>
            >
            <c:forEach items="${project.experiment.samples}" var="sample">
                <c:set var="sampleSecLevel" value="${accessProfile.sampleSecurityLevels[sample]}"/>
                <tr class="odd">
                    <td>
                        <c:url var="sampleUrl" value="/ajax/project/listTab/Samples/view.action">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="currentSample.id" value="${sample.id}" />
                        </c:url>
                        <c:url var="projectUrl" value="/project/details.action">
                            <c:param name="project.id" value="${project.id}"/>
                            <c:param name="initialTab" value="annotations"/>
                            <c:param name="initialTab2" value="samples"/>
                            <c:param name="initialTab2Url" value="${sampleUrl}"/>
                        </c:url>
                        <a href="${projectUrl}">${sample.name}</a>
                    </td>
                    <td>${sample.description}</td>
                    <td>
                        <s:select required="true" name="sampleSecurityLevels[${sample.id}]" tabindex="1" cssClass="sample_security_level"
                            list="accessProfile.securityLevel.sampleSecurityLevels" listValue="%{getText(resourceKey)}"/>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
    </div>
    <table class="searchresults permissiontable" cellspacing="0">
        <tr>
            <td>
                <caarray:actions divclass="actionsthin">
                    <caarray:action actionClass="cancel" text="Cancel" onclick="PermissionUtils.cancelEditProfile(); return false; " />
                    <caarray:action actionClass="save" text="Save" onclick="PermissionUtils.saveProfile(); return false;" />
                </caarray:actions>
            </td>
        </tr>
    </table>
    <input type="submit" class="enableEnterSubmit"/>
</s:form>
