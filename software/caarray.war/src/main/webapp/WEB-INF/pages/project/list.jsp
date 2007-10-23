<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">

        <h1>Experiment Workspace</h1>

        <div class="boxpad">
            <caarray:tabPane>
                <c:url value="/protected/project/list.action" var="sortUrl" />
                <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
                    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${projects}" requestURI="${sortUrl}"
                        sort="list" id="row" pagesize="20" excludedParams="project.id" style="clear: none;">
                        <caarray:displayTagProperties/>
                        <display:column property="experiment.title" title="Experiment Title" escapeXml="true" sortable="true"
                            url="/protected/project/edit.action" paramId="project.id" paramProperty="id" titleKey="project.id"/>
                        <display:column sortProperty="experiment.assayType" title="Assay Type" sortable="true" >
                            <c:if test="${row.experiment.assayType != null}">
                                <fmt:message key="${row.experiment.assayType.resourceKey}" />
                            </c:if>
                        </display:column>
                        <display:column property="experiment.organism.commonName" title="Organism" sortable="true" />
                        <display:column sortProperty="status" title="Status" sortable="true">
                            <fmt:message key="${row.status.resourceKey}" />
                        </display:column>
                        <display:column title="Properties">
                            <c:url value="/protected/project/editPermissions.action" var="editProjectPermissionsUrl">
                                <c:param name="project.id" value="${row.id}" />
                            </c:url>
                            <a href="${editProjectPermissionsUrl}"><img src="<c:url value="/images/ico_properties.gif"/>" alt="Properties" /></a>
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
            </caarray:tabPane>
        </div>
    </div>

    <script type="text/javascript">
        highlightTableRows("projectList");
    </script>
</body>
