<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<s:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</s:if>
<html>
<head>
    <title>Import Array Designs</title>
</head>
<body>
    <h1>Import Array Designs</h1>
    <div class="pagehelp">
        <a href="javascript:openHelpWindow('')" class="help">Help</a>
        <a href="javascript:printpage()" class="print">Print</a>
    </div>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <a href="list.action">Array Designs</a> &gt;
                    <span class="dark">
                        <s:if test="${empty arrayDesign}">
                            Import Array Design
                        </s:if><s:else>
                            ${arrayDesign.name}
                        </s:else>
                    </span>
                </h3>
                <caarray:successMessages />
            </div>
            <div class="boxpad">
                <div id="submittingText" style="display: none;">
                    <div><img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" />
                    <fmt:message key="experiment.files.processing" /><br><br>
                    <fmt:message key="arraydesign.file.upload.inProgress" /></div>
                </div>
                <div id="theForm">
                    <s:form action="/protected/arrayDesign/save.action" onsubmit="TabUtils.showSubmittingText(); return true;" cssClass="form" enctype="multipart/form-data" method="post" id="arrayDesignForm">
                        <tbody>
                            <tr><th colspan="2">Array Design Details</th></tr>
                            <s:textfield required="true" key="arrayDesign.name" size="50" tabindex="1"/>
                            <s:select required="true" key="arrayDesign.assayType" tabindex="2"
                                      list="@gov.nih.nci.caarray.domain.project.AssayType@values()" listValue="%{getText(resourceKey)}"
                                      headerKey="" headerValue="--Please select an Assay Type--"/>
                            <s:select required="true" key="arrayDesign.provider" tabindex="3"
                                      list="providers" listKey="id" listValue="name"
                                      headerKey="" headerValue="--Please select a Provider--" value="arrayDesign.provider.id"/>
                            <s:textfield required="true" key="arrayDesign.version" size="50" tabindex="4"/>
                            <s:select required="true" key="arrayDesign.technologyType" tabindex="5"
                                      list="featureTypes" listKey="id" listValue="value" value="arrayDesign.technologyType.id"
                                      headerKey="" headerValue="--Please select a Feature Type--"/>
                            <s:select required="true" key="arrayDesign.organism" tabindex="6"
                                      list="organisms" listKey="id" listValue="scientificName" value="arrayDesign.organism.id"
                                      headerKey="" headerValue="--Please select an Organism--"/>
                            <s:hidden name="arrayDesign.id"/>
                        </tbody>
                        <tbody>
                            <tr><th colspan="2">Upload Array Design File</th></tr>
                            <s:if test="${!empty arrayDesign.id}">
                                <s:textfield theme="readonly" key="arrayDesign.designFile.name" label="Current File"/>
                            </s:if>
                            <s:if test="${editMode}">
                                <s:file required="true" name="upload" label="Browse to File" tabindex="7"/>
                            </s:if>
                        </tbody>
                        <input type="submit" class="enableEnterSubmit"/>
                    </s:form>
                    <caarray:actions>
                        <c:url value="/protected/arrayDesign/list.action" var="listUrl"/>
                        <caarray:action url="${listUrl}" actionClass="cancel" text="Cancel" tabindex="8" />
                        <s:if test="${editMode}">
                            <caarray:action onclick="TabUtils.showSubmittingText(); document.getElementById('arrayDesignForm').submit();" actionClass="save" text="Save" tabindex="9"/>
                        </s:if>
                    </caarray:actions>
                </div>
            </div>
        </div>
    </div>
</body>
</html>