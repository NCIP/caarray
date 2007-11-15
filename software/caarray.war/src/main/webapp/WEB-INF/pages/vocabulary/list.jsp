<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane>
    <div class="boxpad2">
        <fmt:message key="vocabulary.tabs.${category}" var="tabTitle"/>
        <h3>Manage ${tabTitle}</h3>
        <div class="addlink">
            <c:url value="/protected/ajax/vocabulary/edit.action" var="addTermUrl">
                <c:param name="category" value="${category}" />
            </c:url>
            <caarray:linkButton actionClass="add" text="Add ${tabTitle}" onclick="TabUtils.loadLinkInTab('${tabTitle}', '${addTermUrl}'); return false;"/>
        </div>
    </div>

    <div class="tableboxpad">
        <c:url value="/protected/ajax/vocabulary/list.action" var="sortUrl">
            <c:param name="category" value="${category}" />
        </c:url>

        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${terms}"
                requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="category">
                <caarray:displayTagProperties/>
                <display:column title="Value" property="value" sortable="true" />
                <display:column title="Description" property="description" sortable="true" />
                <display:column title="Source" property="source.name" sortable="true" />
                 <display:column titleKey="button.edit" class="centered" headerClass="centered">
                    <c:url value="/protected/ajax/vocabulary/edit.action" var="editTermUrl">
                        <c:param name="category" value="${category}" />
                        <c:param name="currentTerm.id" value="${row.id}" />
                    </c:url>
                    <ajax:anchors target="tabboxwrapper">
                        <a href="${editTermUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                    </ajax:anchors>
                </display:column>
            </display:table>
        </ajax:displayTag>
    </div>
</caarray:tabPane>