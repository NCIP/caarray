<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${projects}" requestURI="${sortUrl}"
        sort="list" id="row" pagesize="20" excludedParams="project.id" style="clear: none;">
        <caarray:displayTagProperties/>
        <display:column sortProperty="experiment.publicIdentifier" title="Experiment ID" sortable="true" >
            <c:choose>
                <c:when test="${caarrayfn:canRead(row, caarrayfn:currentUser()) }">
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
        <display:column property="experiment.title" title="Experiment Title" escapeXml="true" sortable="true"/>
        <display:column sortProperty="experiment.assayType" title="Assay Type" sortable="true" >
            <s:if test="${row.experiment.assayType != null}">
                <fmt:message key="${row.experiment.assayType.resourceKey}" />
            </s:if>
            <s:else>&nbsp;
            </s:else>
        </display:column>
        <display:column sortProperty="experiment.sampleCount" title="Samples" sortable="true">
            <c:if test="${caarrayfn:canRead(row, caarrayfn:currentUser()) }">
                <c:url value="/project/details.action" var="viewSamplesUrl">
                    <c:param name="project.id" value="${row.id}" />
                    <c:param name="initialTab" value="annotations" />
                    <c:param name="initialTab2" value="samples" />
                </c:url>
                <a href="${viewSamplesUrl}">${row.experiment.sampleCount}</a>
            </c:if>
        </display:column>
        <display:column sortProperty="status" title="Status" sortable="true">
            <fmt:message key="${row.status.resourceKey}" />
        </display:column>
        <c:if test="${!row.public}">
            <display:column title="Permissions" class="centered" headerClass="centered">
                <c:if test="${caarrayfn:canModifyPermissions(row, caarrayfn:currentUser())}">
                    <c:url value="/protected/project/permissions/editPermissions.action" var="editProjectPermissionsUrl">
                        <c:param name="project.id" value="${row.id}" />
                    </c:url>
                    <a href="${editProjectPermissionsUrl}"><img src="<c:url value="/images/ico_permissions.gif"/>" alt="Permissions" /></a>
                </c:if>
            </display:column>
            <display:column titleKey="button.edit" class="centered" headerClass="centered">
                <c:if test="${caarrayfn:canWrite(row, caarrayfn:currentUser())}">
                    <c:url value="/protected/project/edit.action" var="editProjectUrl">
                        <c:param name="project.id" value="${row.id}" />
                    </c:url>
                    <a href="${editProjectUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                </c:if>
            </display:column>
        </c:if>
    </display:table>
</ajax:displayTag>