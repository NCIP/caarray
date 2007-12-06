<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane>
    <div class="boxpad2">
        <fmt:message key="protocols.tabs.protocol" var="tabTitle"/>
        <h3>Manage ${tabTitle}</h3>
        <div class="addlink">
            <c:url value="/protected/ajax/protocol/edit.action" var="addProtocolUrl" />
            <caarray:linkButton actionClass="add" text="Add ${tabTitle}" onclick="TabUtils.loadLinkInTab('${tabTitle}', '${addProtocolUrl}'); return false;"/>
        </div>
    </div>

    <div class="tableboxpad">
        <c:url value="/protected/ajax/protocol/list.action" var="sortUrl" />

        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${protocols}"
                requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
                <caarray:displayTagProperties/>
                <display:column titleKey="protocol.name" sortProperty="name" sortable="true">
                    <c:url value="/protected/ajax/protocol/details.action" var="viewProtocolUrl">
                        <c:param name="protocol.id" value="${row.id}" />
                    </c:url>
                    <ajax:anchors target="tabboxwrapper">
                        <a href="${viewProtocolUrl}">${row.name}</a>
                    </ajax:anchors>
                </display:column>>
                <display:column titleKey="protocol.type" property="type.value" sortable="true" />
                <display:column titleKey="protocol.description" property="description" sortable="true" />
                <display:column titleKey="protocol.contact" property="contact" sortable="true" />
                <display:column titleKey="protocol.url" property="url" sortable="true" />
                <display:column titleKey="button.edit" class="centered" headerClass="centered">
                    <c:if test="${caarrayfn:canWrite(row, caarrayfn:currentUser())}">
                        <c:url value="/protected/ajax/protocol/edit.action" var="editProtocolUrl">
                            <c:param name="protocol.id" value="${row.id}" />
                        </c:url>
                        <ajax:anchors target="tabboxwrapper">
                            <a href="${editProtocolUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                        </ajax:anchors>
                    </c:if>
                </display:column>
            </display:table>
        </ajax:displayTag>
    </div>
</caarray:tabPane>