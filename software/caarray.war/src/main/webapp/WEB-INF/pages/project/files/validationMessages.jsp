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
        <s:form action="ajax/project/listTab/Sources/saveList" cssClass="form" id="projectForm" theme="simple">
            <s:hidden name="project.id" />
        </s:form>
    </div>
     <caarray:actions divclass="actionsthin">
        <c:url value="/protected/ajax/project/files/list.action" var="manageDataUrl">
            <c:param name="project.id" value="${project.id}" />
        </c:url>
        <caarray:linkButton actionClass="cancel" text="Cancel" onclick="executeAjaxTab_tablevel2(null,'selected', '${manageDataUrl}', '');" />
    </caarray:actions>
</caarray:tabPane>