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
			<jsp:useBean id="sampleComparator" class="gov.nih.nci.caarray.domain.sample.Sample$ByNameComparator"/>
			<s:sort comparator="#attr.sampleComparator" source="project.experiment.samples" id="sortedSamples"/>
            <c:forEach items="${sortedSamples}" var="sample">
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
                        <a href="${projectUrl}"><caarray:abbreviate value="${sample.name}" maxWidth="30"/></a>
                    </td>
                    <td>${sample.description}</td>
                    <td>
                        <c:choose>
                            <c:when test="${readOnly}">
                                <fmt:message key="${sampleSecLevel.resourceKey}"/>
                            </c:when>
                            <c:otherwise>
                                <s:select required="true" name="sampleSecurityLevels[${sample.id}]" tabindex="1" cssClass="sample_security_level"
                                    list="accessProfile.securityLevel.sampleSecurityLevels" listValue="%{getText(resourceKey)}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
    </div>
