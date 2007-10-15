<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:if test="${experiment.id != null}">
    <c:set var="formAction" value="ajax/experiment/management/save/overview"/>
</s:if>
<s:else>
    <c:set var="formAction" value="experiment/management/save"/>
</s:else>

<caarray:tabPane subtab="true">
    <div class="boxpad2">
        <h3>Sources</h3>
        <div class="addlink">
            <c:url value="Project_addSource.action" var="addSourceUrl">
                <c:param name="cancelResult" value="dashboard"/>
            </c:url>
            <a href="${addSampleUrl}" class="add">Add a new source</a>
        </div>
    </div>

    <c:url var="sortUrl" value="/ajax/experiment/search/doSearch.action" />
    <c:url var="loadUrlBase" value="/experiment/management/load.action" />
    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" list="${sources}" requestURI="${sortUrl}" id="row" pagesize="20">
            <caarray:displayTagProperties/>
            <display:column property="name" titleKey="experiment.sources.name" sortable="true"/>
            <display:column property="description" titleKey="experiment.sources.description" sortable="true" />
            <display:column property="organism.commonName" titleKey="experiment.sources.organism" sortable="true" />
            <display:column titleKey="experiment.sources.relatedSamples">
                <a href="#">view</a>
            </display:column>
            <display:column titleKey="button.edit">
                <a href="#"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
            </display:column>
            <display:column titleKey="button.copy">
                <a href="#"><img src="<c:url value="/images/ico_copy.gif"/>" alt="<fmt:message key="button.copy"/>" /></a>
            </display:column>
            <display:column titleKey="button.delete">
                <a href="#"><img src="<c:url value="/images/ico_delete.gif"/>" alt="<fmt:message key="button.delete"/>" /></a>
            </display:column>
        </display:table>
    </ajax:displayTag>    
    </div>
</caarray:tabPane>
