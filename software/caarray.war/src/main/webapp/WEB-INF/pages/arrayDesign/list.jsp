<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Import Array Designs</title>
</head>
<body>
    <h1>Import Array Designs</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>Array Designs</h3>
                <div class="addlink">
                    <caarray:linkButton url="edit.action" actionClass="add" text="Import a New Array Design" />
                </div>
            </div>
            <div class="tableboxpad">
                <c:url value="/protected/ajax/arrayDesign/list.action" var="sortUrl"/>
                <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults">
                    <display:table class="searchresults" cellspacing="0" defaultsort="2" list="${arrayDesigns}"
                                   requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
                        <caarray:displayTagProperties/>
                        <display:column property="name" titleKey="arrayDesign.name" sortable="true" url="/protected/arrayDesign/view.action" paramId="arrayDesign.id" paramProperty="id"/>
                        <display:column property="provider.name" titleKey="arrayDesign.provider" sortable="true"/>
                        <display:column property="assayType" titleKey="arrayDesign.assayType" sortable="true"/>
                        <display:column property="version" titleKey="arrayDesign.version" sortable="true"/>
                        <display:column property="technologyType.value" titleKey="arrayDesign.technologyType" sortable="true"/>
                        <display:column property="organism.commonName" titleKey="arrayDesign.organism" sortable="true"/>
                        <display:column titleKey="button.edit" class="centered" headerClass="centered">
                            <c:url value="/protected/arrayDesign/edit.action" var="editDesignUrl">
                                <c:param name="arrayDesign.id" value="${row.id}" />
                            </c:url>
                            <a href="${editDesignUrl}"><img src="<c:url value="/images/ico_edit.gif"/>" alt="<fmt:message key="button.edit"/>" /></a>
                        </display:column>
                    </display:table>
                </ajax:displayTag>
            </div>
        </div>
    </div>
</body>
</html>