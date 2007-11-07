<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <caarray:successMessages />

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.importedFiles" /></h3>
    </div>

    <div class="tableboxpad">
        <s:form action="protected/ajax/project/files/process" id="selectFilesForm" method="post" theme="simple">
            <s:hidden name="project.id" value="${project.id}" />
            <%@ include file="/WEB-INF/pages/project/files/listTable.jsp" %>
        </s:form>
    </div>
    <c:if test="${project.saveAllowed}">
        <caarray:actions divclass="actionsthin">
            <c:url value="/ajax/notYetImplemented.jsp" var="associationsUrl" />
            <caarray:linkButton actionClass="manage_associations" text="Manage Associations" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${associationsUrl}', 'tabboxlevel2wrapper');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>