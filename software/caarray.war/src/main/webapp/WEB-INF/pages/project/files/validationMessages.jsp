<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <c:set var="file" value="${selectedFiles[0]}" />
    <div class="boxpad2"><h3>Validation Messages for ${file.name}</h3></div>
    <c:url value="/protected/ajax/project/files/validationMessages.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
        <c:param name="selectedFiles" value="${file.id}" />
    </c:url>
    <div class="tableboxpad">
        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${file.validationResult.messages}"
                requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
                <caarray:displayTagProperties/>
                <display:column property="type" title="Type" sortable="true" />
                <display:column property="line" title="Line" sortable="true" />
                <display:column property="column" title="Column" sortable="true" />
                <display:column property="message" title="Message" sortable="true" />
            </display:table>
        </ajax:displayTag>
    </div>
     <caarray:actions divclass="actionsthin">
        <c:url value="/protected/ajax/project/files/deleteFiles.action" var="deleteUrl" />
        <caarray:linkButton actionClass="delete" text="Delete" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '${deleteUrl}', 'tabboxlevel2wrapper');" />
        <c:url value="/protected/ajax/project/files/validateFiles.action" var="validateUrl" />
        <caarray:linkButton actionClass="validate" text="Validate" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '${validateUrl}', 'tabboxlevel2wrapper');" />
        <c:url value="/protected/ajax/project/files/importFiles.action" var="importUrl"/>
        <caarray:linkButton actionClass="import" text="Import" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '${importUrl}', 'tabboxlevel2wrapper');" />
        <c:url value="/ajax/notYetImplemented.jsp" var="associationsUrl" />
        <caarray:linkButton actionClass="manage_associations" text="Manage Associations" onclick="TabUtils.submitSubTabFormToUrl('projectForm', '${associationsUrl}', 'tabboxlevel2wrapper');" />
    </caarray:actions>
    <div class="actionsthin">
        <c:url value="/protected/ajax/project/files/list.action" var="manageDataUrl">
            <c:param name="project.id" value="${project.id}" />
        </c:url>
        <caarray:linkButton actionClass="cancel" text="Cancel" onclick="executeAjaxTab_tablevel2(null,'selected', '${manageDataUrl}', '');" />
    </div>
</caarray:tabPane>