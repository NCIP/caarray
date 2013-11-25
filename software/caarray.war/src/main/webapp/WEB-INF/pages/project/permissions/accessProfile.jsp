<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    confirmSaveProfile = function() {
        <c:choose><c:when test='${initParam["expAccess.warning.show"]}'>
            var newLevel = $(profileForm_accessProfile_securityLevel).value;
            var oldLevel = PermissionUtils.lastPublicLevel; 
            if ((oldLevel == 'NO_VISIBILITY' || oldLevel == 'VISIBLE') &&
                !(newLevel == 'NO_VISIBILITY' || newLevel == 'VISIBLE')) {
                Ext.MessageBox.confirm('Confirm Experiment Access',
                    '${initParam["expAccess.warning"]}',
                    function(btn) {
                        if (btn == "yes") {
                            PermissionUtils.saveProfile();
                        }
                    }
                );
            } else {
                PermissionUtils.saveProfile();
            }
        </c:when><c:otherwise>
            PermissionUtils.saveProfile();
        </c:otherwise></c:choose>
    }
</script>

<s:form action="ajax/project/permissions/saveAccessProfile" theme="simple" id="profileForm" onsubmit="return false;">
    <s:token/>
    <s:hidden name="project.id"/>
    <s:hidden name="accessProfile.id"/>
    <s:hidden id="actionButton" name="actionButton" />
    <s:hidden name="sampleSecurityLevels" value="-1"/>
    <c:if test="${!empty collaboratorGroup}">
        <s:hidden name="collaboratorGroup.id"/>
    </c:if>

    <%@ include file="/WEB-INF/pages/project/permissions/accessProfileDetails.jsp" %>
    <table class="searchresults permissiontable" cellspacing="0" style="height: auto; width: 400px; overflow-x: hidden">
        <tr>
            <td>
                <caarray:actions divclass="actionsthin">
                    <caarray:action actionClass="cancel" text="Cancel" onclick="PermissionUtils.cancelEditProfile(); " />
                    <caarray:action actionClass="save" text="Save" onclick="confirmSaveProfile();" />
                </caarray:actions>
            </td>
        </tr>
    </table>

</s:form>
