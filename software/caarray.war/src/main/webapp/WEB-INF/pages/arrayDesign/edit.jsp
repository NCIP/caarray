<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.file.FileStatus"%>
<s:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</s:if>

<s:if test="${createMode}">
    <c:set var="actionUrl" value="/protected/ajax/arrayDesign/saveMeta.action" scope="request"/>
</s:if>
<s:else>
     <c:set var="actionUrl" value="/protected/arrayDesign/saveMeta.action" scope="request"/>
</s:else>



<s:if test="${locked || !editMode}">
    <c:set var="lockedTheme" value="readonly"/>
</s:if>
<s:else>
    <c:set var="lockedTheme" value="xhtml"/>
</s:else>

<page:applyDecorator name="popup">
<html>
<head>
    <title>Manage Array Designs</title>
</head>
<body>
    <h1>Manage Array Designs</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <span class="dark">
                        <s:if test="${empty arrayDesign.id}">
                            New Array Design
                        </s:if>
                        <s:else>
                            ${arrayDesign.name}
                        </s:else>
                    </span>
                </h3>
                <caarray:successMessages />
            </div>
            <div class="boxpad">
                <div id="submittingText" style="display: none;">
                    <div><img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" />
                    <fmt:message key="experiment.files.processing" /></div>
                </div>
                <s:if test="${locked}">
                    <div class="instructions">
                       Some details of this array design may not be modified because it is already associated with an existing experiment.
                    </div>
                </s:if>
                <div id="theForm">
                    <s:form action="${actionUrl}" onsubmit="TabUtils.showSubmittingText(); return true;" cssClass="form" enctype="multipart/form-data" method="post" id="arrayDesignForm">
                        <tbody>
                            <tr><th colspan="2">Array Design Details</th></tr>
                            <s:if test="${!empty arrayDesign.id}">
                                <s:textfield theme="readonly" key="arrayDesign.name" size="50" tabindex="1"/>
                                <s:textfield theme="readonly" key="arrayDesign.lsid" size="50" tabindex="2"/>
                            </s:if>
                            <s:textarea key="arrayDesign.description" cols="80" rows="5" tabindex="3"/>
                            <s:select theme="${lockedTheme}" required="true" key="arrayDesign.assayType" tabindex="4"
                                      list="@gov.nih.nci.caarray.domain.project.AssayType@values()" listValue="%{getText(resourceKey)}"
                                      listKey="getValue()" headerKey="" headerValue="--Please select an Assay Type--"/>
                            <s:select theme="${lockedTheme}" required="true" key="arrayDesign.provider" tabindex="5"
                                      list="providers" listKey="id" listValue="name"
                                      headerKey="" headerValue="--Please select a Provider--" value="arrayDesign.provider.id"/>
                            <s:textfield required="true" key="arrayDesign.version" size="50" tabindex="6"/>
                            <s:select required="true" key="arrayDesign.technologyType" tabindex="7"
                                      list="featureTypes" listKey="id" listValue="valueAndSource" value="arrayDesign.technologyType.id"
                                      headerKey="" headerValue="--Please select a Feature Type--"/>
                            <s:select required="true" key="arrayDesign.organism" tabindex="8"
                                      list="organisms" listKey="id" listValue="nameAndSource" value="arrayDesign.organism.id"
                                      headerKey="" headerValue="--Please select an Organism--"/>
                            <s:hidden name="arrayDesign.id"/>
                            <s:hidden name="createMode"/>
                            <s:hidden name="editMode"/>
                        </tbody>
                        <input type="submit" class="enableEnterSubmit"/>
                    </s:form>
                    <caarray:actions>
                         <s:if test="${createMode}">
                            <caarray:linkButton actionClass="cancel" text="Cancel" onclick="window.close()"/>
                            <caarray:action onclick="document.getElementById('arrayDesignForm').submit();" actionClass="import" text="Next" tabindex="12"/>
                         </s:if>
                         <s:else>
                            <c:set var="importingStatus" value="<%= FileStatus.IMPORTING.name() %>"/>
                            <c:url value="/protected/arrayDesign/list.action" var="listUrl"/>
                            <caarray:action url="${listUrl}" actionClass="cancel" text="Cancel" tabindex="11"/>
                            <s:if test="${editMode}">
                                <caarray:action onclick="document.getElementById('arrayDesignForm').submit();" actionClass="save" text="Save" tabindex="12"/>
                            </s:if>
                            <s:elseif test="${arrayDesign.designFile.status != importingStatus}">
                                <c:url value="/protected/arrayDesign/edit.action" var="editUrl">
                                    <c:param name="arrayDesign.id" value="${arrayDesign.id}"/>
                                </c:url>
                                <caarray:action url="${editUrl}" actionClass="edit" text="Edit" tabindex="12"/>
                            </s:elseif>
                         </s:else>
                    </caarray:actions>
                </div>
            </div>
        </div>
    </div>
  </body>
</html>
</page:applyDecorator>