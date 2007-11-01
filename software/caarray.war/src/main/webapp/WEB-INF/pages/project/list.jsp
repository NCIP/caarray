<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
        <div class="tableboxpad">
            <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
                <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${projects}" requestURI="${sortUrl}"
                    sort="list" id="row" pagesize="20" excludedParams="project.id" style="clear: none;">
                    <caarray:displayTagProperties/>
                    <display:column property="experiment.publicIdentifier" title="Experiment ID" escapeXml="true" sortable="true"
                        url="/protected/project/edit.action" paramId="project.id" paramProperty="id" />
                    <display:column property="experiment.title" title="Experiment Title" escapeXml="true" sortable="true"/>
                    <display:column sortProperty="experiment.assayType" title="Assay Type" sortable="true" >
                        <c:if test="${row.experiment.assayType != null}">
                            <fmt:message key="${row.experiment.assayType.resourceKey}" />
                        </c:if>
                    </display:column>
                    <display:column property="experiment.organism.commonName" title="Organism" sortable="true" />
                    <display:column sortProperty="status" title="Status" sortable="true">
                        <fmt:message key="${row.status.resourceKey}" />
                    </display:column>
                    <display:column title="Permissions">
                        <c:url value="/protected/project/permissions/editPermissions.action" var="editProjectPermissionsUrl">
                            <c:param name="project.id" value="${row.id}" />
                        </c:url>
                            <a href="${editProjectPermissionsUrl}"><img src="<c:url value="/images/ico_permissions.gif"/>" alt="Permissions" /></a>
                    </display:column>
                    <display:column titleKey="button.edit">
                        <c:if test="${row.status == 'DRAFT' || row.status == 'RETURNED_FOR_REVISION'}">
                            <c:url value="/protected/project/edit.action" var="editProjectUrl">
                                <c:param name="project.id" value="${row.id}" />
                            </c:url>
                            <a href="${editProjectUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                        </c:if>
                    </display:column>
                </display:table>
            </ajax:displayTag>
        </div>
