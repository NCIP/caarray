<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/project/files/listTable.action" var="sortUrl">
    <c:param name="project.id" value="${project.id}" />
</c:url>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.files}"
        requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
        <caarray:displayTagProperties/>
        <display:column>
            <s:checkbox name="selectedFiles" fieldValue="${row.id}" value="false" theme="simple" />
        </display:column>
        <display:column sortProperty="name" titleKey="experiment.files.name" sortable="true">
             <ajax:anchors target="tabboxlevel2wrapper">
                <c:url value="/protected/ajax/project/files/validationMessages.action" var="viewMessagesUrl">
                    <c:param name="project.id" value="${project.id}" />
                    <c:param name="selectedFiles" value="${row.id}" />
                </c:url>
                <a href="${viewMessagesUrl}">${row.name}</a>
            </ajax:anchors>
        </display:column>
        <display:column property="type.name" titleKey="experiment.files.type" sortable="true" />
        <display:column property="status" titleKey="experiment.files.status" sortable="true" />
    </display:table>
</ajax:displayTag>