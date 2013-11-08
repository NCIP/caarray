<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    confirmSaveProfile = function() {
        var newLevel = $(profileForm_accessProfile_securityLevel).value;
        if (PermissionUtils.lastPublicLevel == 'NO_VISIBILITY' && newLevel != 'NO_VISIBILITY') {
            Ext.MessageBox.confirm('Confirm Experiment Access',
                'This Experiment will be made available to the public. '
                + 'Submitters are responsible for ensuring that all data and annotations are free of '
                + 'Personally Identifiable Information and Protected Health Information (PII/PHI). '
                + 'By sharing this data through NCI\'s caArray, you are certifying that you are the author of this '
                + 'data, and are authorized to release the data. You also certify that you will post only data '
                + 'generated and/or produced by you or your laboratory, and that you will consult with your '
                + 'institution\'s technology development office before posting or disclosing confidential information '
                + 'which may be patentable.',
                function(btn) {
                    if (btn == "yes") {
                        PermissionUtils.saveProfile();
                    }
                }
            );
        } else {
            PermissionUtils.saveProfile();
        }
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
