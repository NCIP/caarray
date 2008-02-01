<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:set name="msgText" value="%{getText('experiment.hybridizations.confirmDelete')}"/>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Hybridization" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Hybridizations/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <c:set var="canWriteProject" value="${caarrayfn:canWrite(project, caarrayfn:currentUser())}"/>
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.hybridizations.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Hybridization" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.hybridizations.relatedLabeledExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.files.uncompressedSize">
                <fmt:formatNumber value="${row.uncompressedSizeOfDataFiles / 1024}" maxFractionDigits="0"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Hybridization" item="${row}" actions="!edit,!delete" isSubtab="true" 
                confirmText="${msgText}" canWriteProject="${canWriteProject}"/>
            <display:column titleKey="button.download">
				<c:url value="/ajax/project/listTab/Hybridizations/download.action" var="actionUrl">
				    <c:param name="project.id" value="${project.id}" />
				    <c:param name="currentHybridization.id" value="${row.id}" />
                    <c:param name="editMode" value="${editMode}" />
				</c:url>
		        <a href="${actionUrl}">
	            	<img src="<c:url value="/images/ico_download.gif"/>" alt="<fmt:message key="button.download"/>">
		        </a>
            </display:column>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
