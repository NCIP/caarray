<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">

        <h1>Experiment Workspace</h1>

        <div class="boxpad">
            <caarray:tabPane>
                <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
                    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${projects}"
                        sort="list" id="row" pagesize="20" excludedParams="project.id" style="clear: none;">
                        <caarray:displayTagProperties/>
                        <display:column property="experiment.title" title="Experiment Title" escapeXml="true" sortable="true"
                            href="Project_edit.action" paramId="project.id" paramProperty="id" titleKey="project.id"/>
                        <display:column sortProperty="experiment.assayType" title="Assay Type" sortable="true">
                            <fmt:message key="${experiment.assayType.resourceKey}" />
                        </display:column>
                        <display:column title="Properties">
                            <c:url value="/protected/Project_editPermissions.action" var="editProjectPermissionsUrl">
                                <c:param name="project.id" value="${project.id}" />
                            </c:url>
                            <a href="${editProjectPermissionsUrl}"><img src="<c:url value="/images/ico_properties.gif"/>" alt="Properties" /></a>
                        </display:column>
                        <display:column titleKey="button.edit">
                            <c:url value="/protected/Project_edit.action" var="editProjectUrl">
                                <c:param name="project.id" value="${project.id}" />
                            </c:url>
                            <a href="${editProjectUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
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
