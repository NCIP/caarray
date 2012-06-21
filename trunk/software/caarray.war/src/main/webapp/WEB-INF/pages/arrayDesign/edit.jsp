<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.file.FileStatus"%>

<c:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</c:if>

<c:choose>
    <c:when test="${createMode}">
        <c:set var="actionUrl" value="/protected/ajax/arrayDesign/saveMeta.action" scope="request"/>
    </c:when>
    <c:otherwise>
         <c:set var="actionUrl" value="/protected/arrayDesign/saveMeta.action" scope="request"/>
    </c:otherwise> 
</c:choose>

<c:choose>
    <c:when test="${locked || !editMode}">
        <c:set var="lockedTheme" value="readonly"/>
    </c:when>
    <c:otherwise>
        <c:set var="lockedTheme" value="xhtml"/>
    </c:otherwise>
</c:choose>

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
                        <c:choose>
                            <c:when test="${empty arrayDesign.id}">
                                New Array Design (Step 1)
                            </c:when>
                            <c:otherwise>
                                ${arrayDesign.name}
                            </c:otherwise>
                        </c:choose>
                    </span>
                </h3>
                <caarray:successMessages />
            </div>
            <div class="boxpad">
                <div id="submittingText" style="display: none;">
                    <div><img alt="Indicator" align="absmiddle" src="<c:url value="/images/indicator.gif"/>" />
                    <fmt:message key="experiment.files.processing" /></div>
                </div>
                <c:if test="${locked}">
                    <div class="instructions">
                       Some details of this array design may not be modified because it is already associated with an existing experiment.
                    </div>
                </c:if>
                <div id="theForm">
                    <s:form action="%{#attr.actionUrl}" cssClass="form" enctype="multipart/form-data" method="post" id="arrayDesignForm">
                        <s:token/>
                        <tbody>
                            <tr><th colspan="2">Array Design Details</th></tr>
                            <c:if test="${!empty arrayDesign.id}">
                                <s:textfield theme="readonly" key="arrayDesign.name" size="50" tabindex="1"/>
                                <s:textfield theme="readonly" key="arrayDesign.lsid" size="50" tabindex="2"/>
                            </c:if>
                            <s:textarea key="arrayDesign.description" cols="80" rows="5" tabindex="3"/>
                            <c:url var="autocompleteUrl" value="/protected/ajax/arrayDesign/generateAssayList.action" />
                            <c:choose>
                                <c:when test="${!locked && editMode}">
                                    <caarray:listSelector baseId="assayTypes" listField="${arrayDesign.assayTypes}" listFieldName="arrayDesign.assayTypes"
                                          tabIndex="1" objectValue="id" required="true" multiple="true" hideAddButton="true"
                                          showFilter="false" autocompleteUrl="${autocompleteUrl}"/>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td class="tdLabel"><label class="label">Assay Type<span class="required">*</span>:</label></td>
                                        <td>
                                            <c:forEach items="${arrayDesign.assayTypes}" var="currType" varStatus="status">
                                                <c:if test="${!status.first}">, </c:if>${currType.name}
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            <s:select theme="%{#attr.lockedTheme}" required="true" key="arrayDesign.provider" tabindex="5"
                                      list="providers" listKey="id" listValue="name"
                                      headerKey="" headerValue="--Please select a Provider--" value="arrayDesign.provider.id"/>
                            <s:textfield required="true" key="arrayDesign.version" size="50" tabindex="6"/>
                            <s:select required="true" key="arrayDesign.technologyType" tabindex="7"
                                      list="featureTypes" listKey="id" listValue="valueAndSource" value="arrayDesign.technologyType.id"
                                      headerKey="" headerValue="--Please select a Feature Type--"/>
                            <s:select required="true" key="arrayDesign.organism" tabindex="8"
                                      list="organisms" listKey="id" listValue="nameAndSource" value="arrayDesign.organism.id"
                                      headerKey="" headerValue="--Please select an Organism--"/>
                            <s:textfield key="arrayDesign.geoAccession" size="50" tabindex="9"/>
                            <s:hidden name="arrayDesign.id"/>
                            <s:hidden name="createMode"/>
                            <s:hidden name="editMode"/>
                        </tbody>
                        <input type="submit" class="enableEnterSubmit"/>
                    </s:form>
                    <caarray:focusFirstElement formId="arrayDesignForm"/>
                    <caarray:actions>
                         <c:choose>
                             <c:when test="${createMode}">
                                <caarray:linkButton actionClass="cancel" text="Cancel" onclick="window.close()"/>
                                <caarray:action onclick="document.getElementById('arrayDesignForm').submit();" actionClass="import" text="Next" tabindex="12"/>
                             </c:when>
                             <c:otherwise>
                                <c:set var="importingStatus" value="<%= FileStatus.IMPORTING.name() %>"/>
                                <c:url value="/protected/arrayDesign/list.action" var="listUrl"/>
                                <caarray:action url="${listUrl}" actionClass="cancel" text="Cancel" tabindex="11"/>
                                <c:if test="${editMode}">
                                    <caarray:action onclick="document.getElementById('arrayDesignForm').submit();" actionClass="save" text="Save" tabindex="12"/>
                                </c:if>
                                <c:if test="${!editMode && arrayDesign.designFileSet.status != importingStatus}">
                                    <c:url value="/protected/arrayDesign/edit.action" var="editUrl">
                                        <c:param name="arrayDesign.id" value="${arrayDesign.id}"/>
                                    </c:url>
                                    <caarray:action url="${editUrl}" actionClass="edit" text="Edit" tabindex="12"/>
                                </c:if>
                             </c:otherwise>
                         </c:choose>.
                    </caarray:actions>
                </div>
                <a name="files"/>
                <c:if test="${!empty arrayDesign.designFiles}">
                    <div class="boxpad">
                        <table class="form" >
                            <thead>
                                <tr><th colspan="3">Array Design Files and Status</th></tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${arrayDesign.designFiles}" var="file">
                                <tr><td class="tdLabel" style="text-align: left"><c:out value="${file.name}" escapeXml="true"/></td>
                                    <td style="width: 17em">${file.type}</td>
                                    <td style="width: 17em">${file.status}</td></tr>
                                <c:forEach items="${file.validationResult.messages}" var="message">
                                    <tr><td colspan="3" class="instructions" style="padding-left:5em"><c:out value="${message.message}" escapeXml="true"/></td></tr>
                                </c:forEach>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>

            </div>
        </div>
    </div>
  </body>
</html>
</page:applyDecorator>
