<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:choose>
    <c:when test="${accessProfile.publicProfile}">
        <fmt:message var="profileOwnerName" key="project.permissions.publicProfile"/>
    </c:when>
    <c:when test="${accessProfile.groupProfile}">
        <fmt:message var="profileOwnerName" key="project.permissions.groupProfile">
            <fmt:param value="${accessProfile.group.group.groupName}"/>
        </fmt:message>
    </c:when>
</c:choose>
<fmt:message key="experiment.files.selectAllCheckBox" var="checkboxAll">
    <fmt:param value="selectAllCheckbox" />
    <fmt:param value="'profileForm'" />
</fmt:message>
    <table class="searchresults" cellspacing="0" style="height: auto; width: 400px; overflow-x: hidden">
        <tr>
            <th>Control Access to Specific Content for ${profileOwnerName}</th>
        </tr>
        <tr>
            <td class="filterrow" style="border-bottom: 1px solid #999">
                <s:label for="profileForm_accessProfile_securityLevel" value="Experiment Access" theme="simple"/>
                <c:choose>
                    <c:when test="false">
                        <span id="profileForm_accessProfile_securityLevel"><fmt:message key="${accessProfile.securityLevel.resourceKey}"/></span>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${accessProfile.publicProfile}">
                                <s:set var="secLevels" value="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@publicLevels()"/>
                            </c:when>
                            <c:otherwise>
                                <s:set var="secLevels" value="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@collaboratorGroupLevels()"/>                            
                            </c:otherwise>
                        </c:choose>
                        <s:select required="true" name="accessProfile.securityLevel" tabindex="1"
                            list="%{#secLevels}" listValue="%{getText(resourceKey)}"
                            onchange="PermissionUtils.changeExperimentAccess(this)"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>

    <div class="datatable" style="padding-bottom: 0px">
        <div class="scrolltable" style="height: auto; max-height: 500px; width: 400px; overflow-x: hidden">
        <table class="searchresults permissiontable" cellspacing="0">
            <tbody id="access_profile_samples"
                <c:if test="${!accessProfile.securityLevel.sampleLevelPermissionsAllowed}">style="display:none"</c:if>
            >
        <tr>
             <td class="left">
                <s:label><b><fmt:message key="search.keyword"/>:</b></s:label>
             </td>
             <td colspan="2" class="right">
                <s:textfield name="permSampleKeyword"/>
             </td>
        </tr>
        <tr>
            <td class="left">
                    <s:label><b><fmt:message key="search.category"/>:</b></s:label>
            </td>
            <td colspan="2" class="right">
                    <s:select name="permSampleSearch" label="Search Samples"
                            list="@gov.nih.nci.caarray.web.action.project.ProjectPermissionsAction@getSearchSampleCategories()"
                            listValue="%{getText(label)}" listKey="value"/>
            </td>
        </tr>
        <tr id="characteristic_category_dropdown_id" 
		        <c:if test="${!selectedArbitraryCharacteristicCategory}">style="display:none"</c:if> >
            <td class="left">
	            <s:label><b><fmt:message key="search.category.arbitraryCharacteristic.label"/>:</b></s:label>
            </td>
            <td colspan="2" class="right">
	            <s:select name="arbitraryCharacteristicCategoryId" label="Search Samples"
		                list="arbitraryCharacteristicCategories"
                        listKey="id" listValue="name" />
            </td>
        </tr>
        <tr class="odd">
            <td class="left">&nbsp;</td>
            <td colspan="2" class="right">
                    <caarray:action actionClass="search" text="Search" onclick="PermissionUtils.listSampleProfile()" />
            </td>
        </tr>
        <c:choose>
        <c:when test="${sampleResultsCount > 0}">
        <tr>
            <td class="left">
                <s:label><b><fmt:message key="project.permissions.selectSecLevel"/>:</b>&nbsp;&nbsp;</s:label>
            </td>
            <td colspan="2" class="right">
                <s:select required="true" name="securityChoices" tabindex="1" cssClass="sample_security_level"
                    list="accessProfile.securityLevel.sampleSecurityLevels" listValue="%{getText(resourceKey)}"/>
            </td>
        </tr>
        <tr>
            <td colspan="3">
              <%@ include file="/WEB-INF/pages/project/permissions/list.jsp" %>
            </td>
        </tr>
        </c:when>
        <c:when test="${sampleResultsCount == 0}">
         <tr>
            <td colspan="3">
              <fmt:message key="project.permissions.search.noResults"/>
            </td>
         </tr>
        </c:when>
        </c:choose>

        </tbody>
        </table>

        </div>
    </div>
