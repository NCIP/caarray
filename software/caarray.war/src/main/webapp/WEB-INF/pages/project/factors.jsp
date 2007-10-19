<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3><fmt:message key="experiment.experimentalFactors" /></h3>
        <div class="addlink">
            <c:url value="/protected/ajax_Project_loadGenericTab_factorEdit.action" var="addFactorUrl">
                <c:param name="proposal.id" value="${proposal.id}" />
                <c:param name="ajax" value="true" />
            </c:url>
            <ajax:anchors target="tabboxlevel2wrapper">
                <a href="${addFactorUrl}" class="add"><fmt:message key="experiment.factors.add" /></a>
            </ajax:anchors>
        </div>
    </div>

    <c:url value="ajax_Project_loadGenericTab_factors.action" var="sortUrl">
        <c:param name="proposal.id" value="${proposal.id}" />
        <c:param name="ajax" value="true" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${proposal.project.experiment.factors}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="proposal.id">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.experimentalFactors.name" sortable="true"/>
            <display:column property="type.category.name" titleKey="experiment.experimentalFactors.category" sortable="true" />
            <display:column titleKey="button.edit">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax_Project_loadGenericTab_factorEdit.action" var="editFactorUrl">
                        <c:param name="proposal.id" value="${proposal.id}" />
                        <c:param name="currentFactor.id" value="${row.id}" />
                        <c:param name="ajax" value="true" />
                    </c:url>
                    <a href="${editFactorUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.copy">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax_Project_copy_factor.action" var="copyFactorUrl">
                        <c:param name="proposal.id" value="${proposal.id}" />
                        <c:param name="currentFactor.id" value="${row.id}" />
                        <c:param name="ajax" value="true" />
                    </c:url>
                    <a href="${copyFactorUrl}"><img src="<c:url value="/images/ico_copy.gif"/>" alt="<fmt:message key="button.copy"/>" /></a>
                </ajax:anchors>
            </display:column>
            <display:column titleKey="button.delete">
                <ajax:anchors target="tabboxlevel2wrapper">
                    <c:url value="/protected/ajax_Project_remove_factor.action" var="removeFactorUrl">
                        <c:param name="proposal.id" value="${proposal.id}" />
                        <c:param name="currentFactor.id" value="${row.id}" />
                        <c:param name="ajax" value="true" />
                    </c:url>
                    <a href="${removeFactorUrl}"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
                </ajax:anchors>
            </display:column>
        </display:table>
    </ajax:displayTag>

    <s:form action="ajax_Project_saveGenericTab_sources" cssClass="form" id="projectForm" method="get">
        <s:hidden name="proposal.id" />
        <s:hidden name="ajax" value="%{'true'}"/>
    </s:form>
    </div>
</caarray:tabPane>
