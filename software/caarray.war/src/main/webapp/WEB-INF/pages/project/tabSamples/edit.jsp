<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory"%>
<c:url var="thisUrl" value="/ajax/project/listTab/Samples/edit.action">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="currentSample.id" value="${currentSample.id}" />
</c:url>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Sample" item="${currentSample}" itemName="${currentSample.name}"
        isSubtab="true">

        <c:if test="${editMode}">
            <s:textfield requiredLabel="true" name="currentSample.name" key="experiment.samples.name" size="80" tabindex="1" />
        </c:if>
        <s:textarea name="currentSample.description" key="experiment.samples.description" rows="3" cols="75"
            tabindex="2" />
        <s:if test="fieldErrors['currentSample'] != null">
            <tr errorfor="currentSample">
                <td valign="top" align="center" colspan="2"><s:fielderror>
                    <s:param>currentSample</s:param>
                </s:fielderror></td>
            </tr>
        </s:if>
        <s:textfield key="currentSample.externalId" size="80" tabindex="3" />
        <c:if test="${!empty currentSample.externalId}">
            <caarray:outputUrl var="permalinkUrl">
                <jsp:attribute name="url"><c:url value="/project/${project.experiment.publicIdentifier}/sample/${caarrayfn:encodeUrl(currentSample.externalId)}"/></jsp:attribute>
            </caarray:outputUrl>
            <s:textfield theme="readonly" label="Sample URL" value="%{#attr.permalinkUrl}">
                <s:param name="url">true</s:param>
            </s:textfield>
        </c:if>
        <caarray:annotationAssociationPicker baseId="sourcePicker" entityName="Sample" associatedEntityName="Source" itemId="${currentSample.id}" tabIndex="4" />
        <caarray:termSelector baseId="materialType" category="<%= ExperimentOntologyCategory.MATERIAL_TYPE %>" termField="${currentSample.materialType}"
            tabIndex="5" termFieldName="currentSample.materialType" returnInitialTab1="annotations" returnInitialTab2="samples" returnInitialTab2Url="${thisUrl}" />
        <caarray:protocolSelector returnInitialTab1="annotations" returnInitialTab2="samples" returnInitialTab2Url="${thisUrl}" tabIndex1="6" tabIndex2="7" multiple="true" />
        <caarray:annotationCharacteristics item="${currentSample}"/>
        <s:hidden name="currentSample.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>

    <c:if test="${!editMode}">
        <div id="tabHeader" class="boxpad2"><h3>Download Data</h3></div>
        <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>
    </c:if>
</caarray:tabPane>

