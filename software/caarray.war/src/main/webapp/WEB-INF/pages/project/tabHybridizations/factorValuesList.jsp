<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/ajax/project/listTab/Hybridizations/factorValuesList.action" var="sortUrl"/>
        <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
<caarray:tabPane paneTitleKey="project.tabs.values" subtab="true">
    <div class="tableboxpad" style="overflow:auto; max-height:500px">
        <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${currentHybridization.factorValues}"
            requestURI="${sortUrl}" sort="list" id="row">
            <display:column titleKey="experiment.hybridizations.factorName" sortable="true" sortProperty="factor.name">
              <caarray:projectListTabActionLink linkContent="${row.factor.name}" entityName="Factor" action="view" itemId="${row.factor.id}" isSubtab="true"/>
            </display:column>
            <display:column property="value" titleKey="experiment.hybridizations.factorValue" sortable="true"/>
        </display:table>
    </div>
</caarray:tabPane>
</ajax:displayTag>