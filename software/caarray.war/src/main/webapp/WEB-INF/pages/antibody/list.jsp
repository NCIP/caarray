<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Antibodies</title>
</head>
<body>
    <h1>Manage Antibodies</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>Antibodies</h3>
                <!--
                <div class="addlink">
                    <caarray:linkButton url="edit.action" actionClass="add" text="Import a New Array Design" />
                </div>
                -->
            </div>
            <div class="tableboxpad">
                <c:url value="/protected/ajax/antibody/list.action" var="sortUrl"/>
                <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
                    <display:table class="searchresults" cellspacing="0" defaultsort="2" list="${arrayDesigns}"
                                   requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
                        <caarray:displayTagProperties/>
                        <display:column property="name" titleKey="antibody.name" sortable="true" url="/protected/antibody/view.action" paramId="antibody.id" paramProperty="id" maxLength="30"/>
                       <display:column property="geneNames" titleKey="antibody.genenames" sortable="true"/>
                        <display:column property="epitope" titleKey="antibody.epitope" sortable="true"/>
                        <display:column property="immunogen" titleKey="antibody.immunogen" sortable="true"/>
                        <display:column property="MW(kDa)" titleKey="antibody.molecularWeight" sortable="true"/>
                        <display:column property="specificity" titleKey="antibody.specificity" sortable="true"/>
                        <display:column property="provider" titleKey="antibody.provider" sortable="true"/>
                        <display:column property="catalogID" titleKey="antibody.catalogId" sortable="true"/>
                        <display:column property="lotID" titleKey="antibody.lotId" sortable="true"/>
                         <display:column property="numberValidations" titleKey="antibody.numberValidations"  sortable="true"/>
                    </display:table>
                </ajax:displayTag>
            </div>
        </div>
    </div>
</body>
</html>