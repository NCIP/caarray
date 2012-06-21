<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<c:url value="/protected/ajax/admin/import/designs.action" var="sortUrl"/>
<ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${arrayDesigns}" requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
        <caarray:displayTagProperties/>
        <display:column property="name" titleKey="arrayDesign.name" url="/protected/arrayDesign/view.action" paramId="arrayDesign.id" paramProperty="id" maxLength="30"/>
        <display:column titleKey="button.reimport" class="centered" headerClass="centered">
            <c:url value="/protected/arrayDesign/reimport.action" var="reimportDesignUrl">
                 <c:param name="arrayDesign.id" value="${row.id}" />
            </c:url>
            <a href="${reimportDesignUrl}">
            	<img src="<c:url value="/images/ico_import.gif"/>" 
            		 alt="<fmt:message key="button.reimport"/>" 
            		 title="<fmt:message key="button.reimport"/>"/>
            </a>
        </display:column>
    </display:table>
</ajax:displayTag>