<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    confirmDelete = function() {
        if ( confirm('<fmt:message key="project.confirmDelete"/>') ) {
        	$('delete_progress').show();
        	return true;
        }

        return false;
    }
</script>
<style>
span.level-NONE:after
{ content: 'No access to project or any samples.'; }
span.level-VISIBLE:after
{ content: 'Experiment summary information without access to annotation and array data.'; }
span.level-READ:after
{ content: 'Read access to the experiment as a whole, providing a preview into its content.'; }
span.level-READ_SELECTIVE:after
{ content: 'Selective access to specific sample annotation and data.'; }
span.level-READ_WRITE_SELECTIVE:after
{ content: 'Write access to project. Read access and/or write access to specificed samples.'; }
span.level-NO_VISIBILITY:after
{ content: 'No access to project or any samples - public profile version.'; }
</style>
<div id="delete_progress" class="confirm_msg" style="display: none; margin: 3px 3px">
	Experiment deletion is in progress.
</div>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" list="${projects}" requestURI="${sortUrl}"
        id="row" excludedParams="project.id" style="clear: none;">
        <caarray:displayTagProperties/>
        <display:setProperty name="pagination.sort.param" value="projects.sortCriterion" />
        <display:setProperty name="pagination.sortdirection.param" value="projects.sortDirection" />
        <display:setProperty name="pagination.pagenumber.param" value="projects.pageNumber" />
        <display:column sortProperty="PUBLIC_ID" title="Experiment ID" sortable="true" >
            <c:set var="canReadRow" value="${caarrayfn:canRead(row, caarrayfn:currentUser())}"/>
            <c:choose>
                <c:when test="${canReadRow}">
                    <c:url var="viewUrl" value="/project/details.action">
                        <c:param name="project.id" value="${row.id}"/>
                    </c:url>
                    <a href="${viewUrl}" title="View experiment ${row.experiment.publicIdentifier} in read only mode">${row.experiment.publicIdentifier}</a>
                </c:when>
                <c:otherwise>
                    ${row.experiment.publicIdentifier}
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column sortProperty="TITLE" title="Experiment Title" sortable="true" maxLength="30"><c:out value="${row.experiment.title}" escapeXml="true"/></display:column>
        <display:column titleKey="project.experiment.assayTypes" >
            <s:if test="${row.experiment.assayTypes != null}">
                <c:forEach items="${row.experiment.assayTypes}" var="currType" varStatus="status">
                    <c:if test="${!status.first}">, </c:if>${currType.name}
                </c:forEach>
           </s:if>
            <s:else>&nbsp;
            </s:else>
        </display:column>
        <display:column title="Samples">
            <c:if test="${canReadRow}">
                <c:url value="/project/details.action" var="viewSamplesUrl">
                    <c:param name="project.id" value="${row.id}" />
                    <c:param name="initialTab" value="annotations" />
                    <c:param name="initialTab2" value="samples" />
                </c:url>
                <a href="${viewSamplesUrl}">${row.experiment.sampleCount}</a>
            </c:if>
        </display:column>
        <display:column sortProperty="STATUS" title="Status" sortable="true">
            <c:choose>
                <c:when test="${row.locked}">Locked</c:when>
                <c:otherwise>In Progress</c:otherwise>
            </c:choose>
        </display:column>
        <display:column title="Public Access" class="centered" headerClass="centered" sortable="true" sortProperty="PUBLIC_ACCESS">
            <div class="tooltip">
                <fmt:message key="${row.publicProfile.securityLevel.resourceKey}"/>
                <span class="level-${row.publicProfile.securityLevel}"/></span>
            </div>
        </display:column>
        <display:column title="Permissions" class="centered" headerClass="centered">
            <c:if test="${caarrayfn:canModifyPermissions(row, caarrayfn:currentUser())}">
                <c:url value="/protected/project/permissions/editPermissions.action" var="editProjectPermissionsUrl">
                    <c:param name="project.id" value="${row.id}" />
                </c:url>
                <a href="${editProjectPermissionsUrl}"><img src="<c:url value="/images/ico_permissions.gif"/>" alt="Permissions" /></a>
            </c:if>
        </display:column>
        <display:column titleKey="button.edit" class="centered" headerClass="centered">
            <c:if test="${caarrayfn:canWrite(row, caarrayfn:currentUser()) && !row.locked}">
                <c:url value="/protected/project/edit.action" var="editProjectUrl">
                    <c:param name="project.id" value="${row.id}" />
                </c:url>
                <a href="${editProjectUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
            </c:if>
        </display:column>
        <display:column titleKey="button.delete" class="centered" headerClass="centered">
            <c:if test="${caarrayfn:canWrite(row, caarrayfn:currentUser()) && !row.locked}">
                <c:url value="/protected/project/delete.action" var="deleteProjectUrl">
                    <c:param name="project.id" value="${row.id}" />
                </c:url>
                <a href="${deleteProjectUrl}" onclick="return confirmDelete();"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
            </c:if>
        </display:column>
    </display:table>
</ajax:displayTag>
