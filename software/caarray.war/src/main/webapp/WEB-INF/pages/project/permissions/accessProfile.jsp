<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:form action="ajax/project/permissions/saveAccessProfile" theme="simple" id="profileForm" onsubmit="PermissionUtils.saveProfile(); return false;">
    <s:hidden name="project.id"/>
    <s:hidden name="accessProfile.id"/>
    <c:if test="${!empty collaboratorGroup}">
        <s:hidden name="collaboratorGroup.id"/>    
    </c:if>

    <caarray:accessProfileDetails accessProfile="${accessProfile}" readOnly="false"/>    
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
