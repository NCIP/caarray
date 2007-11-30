<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Hybridization" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Hybridizations/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${project.experiment.hybridizations}"
            requestURI="${sortUrl}" sort="list" id="row" pagesize="20" excludedParams="project.id">
            <caarray:displayTagProperties/>
            <display:column titleKey="experiment.hybridizations.name" sortable="true" sortProperty="name">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Hybridization" action="view" itemId="${row.id}" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.hybridizations.relatedLabeledExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true"/>
            </display:column>
            <display:column titleKey="experiment.files.uncompressedSize" sortable="true" sortProperty="arrayData.dataFile.uncompressedSize">
                <fmt:formatNumber value="${row.arrayData.dataFile.uncompressedSize / 1024}" maxFractionDigits="0"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Hybridization" item="${row}" actions="!edit,!delete" isSubtab="true"/>
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
