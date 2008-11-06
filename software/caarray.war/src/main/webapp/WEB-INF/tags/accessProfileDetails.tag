<%@ tag display-name="accessProfileDetails"
    description="renders an entry for an owner of an access profile (ie public or a collab group)"
    body-content="empty"%>

<%@ attribute name="accessProfile" required="true" type="java.lang.Object"%>
<%@ attribute name="readOnly" required="false"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>
<%@ taglib uri="/WEB-INF/caarray-functions.tld" prefix="caarrayfn" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

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

    <table class="searchresults" cellspacing="0">
        <tr>
            <th>Control Access to Specific Content for ${profileOwnerName}</th>
        </tr>
        <tr>
            <td class="filterrow" style="border-bottom: 1px solid #999">
                <s:label for="profileForm_accessProfile_securityLevel" value="Experiment Access" theme="simple"/>
                <c:choose>
                    <c:when test="${readOnly}">
                        <span id="profileForm_accessProfile_securityLevel"><fmt:message key="${accessProfile.securityLevel.resourceKey}"/></span>
                    </c:when>
                    <c:otherwise>
                        <c:set var="secLevelSubset" value="${project.draft ? 'draftLevels' : (accessProfile.publicProfile ? 'publicLevels' : 'collaboratorGroupLevels')}"/>
                        <s:select required="true" name="accessProfile.securityLevel" tabindex="1"
                            list="@gov.nih.nci.caarray.domain.permissions.SecurityLevel@${secLevelSubset}()" listValue="%{getText(resourceKey)}"
                            onchange="PermissionUtils.changeExperimentAccess(this)"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>

    </table>

    <div class="datatable" style="padding-bottom: 0px">
        <div class="scrolltable" style="height: auto; max-height: 500px; overflow-x: hidden">
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
        <tr class="odd">
            <td class="left">&nbsp;</td>
            <td colspan="2" class="right">
                    <caarray:action actionClass="search" text="Search" onclick="PermissionUtils.listSampleProfile()" />
            </td>
        </tr>
        <c:if test="${sampleResultsCount > 0}">
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
            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${sampleResults}" pagesize="10" id="row">
            <caarray:displayTagProperties/>
            <display:column title="${checkboxAll}">
                <s:checkbox id="chk${row.id}" name="sampleSecurityLevels" fieldValue="${row.id}" value="false" theme="simple" />
            </display:column>
            <display:column titleKey="experiment.samples.name" >
                         <c:url var="sampleUrl" value="/ajax/project/listTab/Samples/view.action">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="currentSample.id" value="${row.id}" />
                        </c:url>
                        <c:url var="projectUrl" value="/project/details.action">
                            <c:param name="project.id" value="${project.id}"/>
                            <c:param name="initialTab" value="annotations"/>
                            <c:param name="initialTab2" value="samples"/>
                            <c:param name="initialTab2Url" value="${sampleUrl}"/>
                        </c:url>
                        <a href="${projectUrl}"><caarray:abbreviate value="${row.name}" maxWidth="30"/></a>
            </display:column>
            <display:column titleKey="experiment.samples.description">
                        ${row.description}
            </display:column>
            <display:column titleKey="project.permissions.selectSecLevel">
                    <c:choose>
                        <c:when test="${!empty accessProfile.sampleSecurityLevels &&
                            accessProfile.sampleSecurityLevels[row] != null}">
                            <c:set var="sampleSecLevel" value="${accessProfile.sampleSecurityLevels[row]}"/>
                            <fmt:message key="${sampleSecLevel.resourceKey}"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="SecurityLevel.none"/>
                        </c:otherwise>
                    </c:choose>
            </display:column>
            </display:table>
            </td>
        </tr>
        </c:if>

        </tbody>
        </table>
        </div>
    </div>
