<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/ajax/project/listTab/Factors/factorValuesList.action" var="sortUrl"/>
        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
<caarray:tabPane paneTitleKey="project.tabs.values" subtab="true">
    <div class="tableboxpad" style="overflow:auto; max-height:500px">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${currentFactor.factorValues}"
            requestURI="${sortUrl}" sort="list" id="row">
            <display:column titleKey="experiment.hybridizations.name" sortable="true" sortProperty="hybridization.name">
              <caarray:projectListTabActionLink linkContent="${row.hybridization.name}" entityName="Hybridization" action="view" itemId="${row.hybridization.id}" isSubtab="true"/>
            </display:column>
            <display:column property="displayValue" titleKey="experiment.hybridizations.factorValue" sortable="true"/>
        </display:table>
    </div>
</caarray:tabPane>
</ajax:displayTag>