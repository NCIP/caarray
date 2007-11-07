<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:form action="ajax/project/permissions/saveAccessProfile" theme="simple" id="profileForm">
    <s:hidden name="project.id"/>
    <s:hidden name="accessProfile.id"/>
    
    <table class="searchresults" cellspacing="0">
        <tr>
            <th style="height: 2.5em;">Control Access to Experiment for ${profileOwnerName}</th>
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
                                
        <table class="searchresults" cellspacing="0" style="width: 305px">
            <tbody id="access_profile_samples" 
                <c:if test="${!accessProfile.securityLevel.sampleLevelPermissionsAllowed}">style="display:none"</c:if>
            >
            <c:forEach items="${project.experiment.samples}" var="sample">
                <c:set var="sampleSecLevel" value="${accessProfile.sampleSecurityLevels[sample]}"/>
                <tr class="odd">
                    <td>
                        <c:url var="sampleUrl" value="/protected/ajax/project/listTab/Samples/view.action">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="currentSample.id" value="${sample.id}" />
                        </c:url>
                        <c:url var="projectUrl" value="/protected/project/edit.action">
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
            <tfoot>
                <tr>
                    <td colspan="3">
        <caarray:actions divclass="actionsthin">
            <caarray:action actionClass="cancel" text="Cancel" onclick="$('access_profile_details').update(''); return false; " />
            <caarray:action actionClass="save" text="Save" onclick="Caarray.submitAjaxForm('profileForm', 'access_profile_details'); return false;" />
        </caarray:actions>
                    
                    </td>                
                </tr>            
            </tfoot>
        </table>
    
</s:form>
