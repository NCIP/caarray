<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<table class="searchresults" id="row" cellspacing="0">
    <thead>
        <tr>
            <th>Control Access to Specific Content for</th>
            <th>Experiment Access</th>
        </tr>
    </thead>
    <tbody>
    <s:iterator value="accessProfiles" id="profile">
        <c:set var="hasSamples"
               value="${profile.securityLevel.sampleLevelPermissionsAllowed &&
                        !empty project.experiment.samples}"/>

        <tr class="odd">
            <td>
                <c:choose><c:when test="${profile.publicProfile}">
                    <fmt:message key="project.permissions.publicProfile"/>
                </c:when><c:when test="${profile.groupProfile}">
                    <fmt:message key="project.permissions.groupProfile">
                        <fmt:param value="${profile.group.group.groupName}"/>
                    </fmt:message>
                </c:when></c:choose>
            </td>
            <td>
                <fmt:message key="${profile.securityLevel.resourceKey}"/>
                <c:if test="${hasSamples}">
                    <a href="#" onclick="toggleList('samples_${profile.id}', this);" style="text-decoration: none;">[+]</a>
                </c:if>
            </td>
        </tr>
        <c:if test="${hasSamples}">
        <tr id="samples_${profile.id}" style="display:none"><td colspan="2">
          <div class="scrolltable" style="height: auto; max-height: 500px; width: 400px; overflow-x: hidden">
            <table class="searchresults permissiontable">
            <tbody>
      <jsp:useBean id="sampleComparator" class="gov.nih.nci.caarray.domain.sample.Sample$ByNameComparator"/>
      <s:sort comparator="#attr.sampleComparator" source="project.experiment.samples">
            <s:iterator id="sample">
                <c:set var="sampleSecLevel" value="${profile.sampleSecurityLevels[sample]}"/>
                <tr>
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
                        <a href="${projectUrl}"><caarray:abbreviate value="${sample.name}" title="${sample.description}" maxWidth="30"/></a>
                    </td>
                    <td><fmt:message key="${sampleSecLevel.resourceKey}"/></td>
                </tr>
            </s:iterator>
      </s:sort>
            </tbody>
            </table>
            </div>
        </td></tr>
        </c:if>
    </s:iterator>
    </tbody>
</table>


<script type="text/javascript">
    function toggleList(id, link){
        e = $(id);
        if (link != null) {
          link.innerHTML = e.style.display == 'none' ? '[-]' : '[+]';
        }
        e.style.display = e.style.display == 'none'? '' : 'none';
    }
</script>