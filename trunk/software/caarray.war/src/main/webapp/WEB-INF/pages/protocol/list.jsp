<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane>
    <div class="boxpad2">
        <fmt:message key="protocols.tabs.protocol" var="tabTitle"/>
        <h3>Manage ${tabTitle}</h3>
        <div class="addlink">
            <c:url value="/protected/ajax/protocol/edit.action" var="addProtocolUrl" />
            <caarray:linkButton actionClass="add" text="Add Protocol" onclick="TabUtils.loadLinkInTab('${tabTitle}', '${addProtocolUrl}'); return false;"/>
        </div>
    </div>

    <div class="tableboxpad">
        <c:url value="/protected/ajax/protocol/list.action" var="sortUrl" />

        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
            <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${protocols}"
                requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
                <caarray:displayTagProperties/>
                <display:column titleKey="protocol.name" sortProperty="name" sortable="true">
                    <c:url value="/protected/ajax/protocol/details.action" var="viewProtocolUrl">
                        <c:param name="protocol.id" value="${row.id}" />
                    </c:url>
                    <ajax:anchors target="tabboxwrapper">
                        <a href="${viewProtocolUrl}"><caarray:abbreviate value="${row.name}" maxWidth="25"/></a>
                    </ajax:anchors>
                </display:column>>
                <display:column titleKey="protocol.type" property="type.value" sortable="true" maxLength="30"/>
                <display:column title="Source" sortProperty="source.name" sortable="true">
                    <c:choose>
                        <c:when test="${not empty row.source.url}"><a href="${row.source.url}" target="_blank"><caarray:abbreviate value="${row.source.nameAndVersion}" maxWidth="11"/></a></c:when>
                        <c:otherwise><caarray:abbreviate value="${row.source.nameAndVersion}" maxWidth="11"/></c:otherwise>
                    </c:choose>
                </display:column>
                <display:column titleKey="protocol.description" property="description" sortable="true" maxLength="30" />
                <display:column titleKey="protocol.contact" property="contact" sortable="true" maxLength="25" />
                <display:column titleKey="protocol.url" sortable="true" >
                    <a href="${row.url}" target="_blank"><caarray:abbreviate value="${row.url}" maxWidth="30"/></a>
                </display:column>
                <display:column titleKey="button.edit" class="centered" headerClass="centered">
                    <c:if test="${caarrayfn:canWrite(row, caarrayfn:currentUser())}">
                        <c:url value="/protected/ajax/protocol/edit.action" var="editProtocolUrl">
                            <c:param name="protocol.id" value="${row.id}" />
                        </c:url>
                        <ajax:anchors target="tabboxwrapper">
                            <a href="${editProtocolUrl}">
                            	<img src="<c:url value="/images/ico_edit.gif"/>" 
                            		 alt="<fmt:message key="button.edit"/>" 
                            		 title="<fmt:message key="button.edit"/>"/>
                          	</a>
                        </ajax:anchors>
                    </c:if>
                </display:column>
            </display:table>
        </ajax:displayTag>
    </div>
</caarray:tabPane>