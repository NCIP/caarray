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
            <s:textfield required="true" name="currentSample.name" key="experiment.samples.name" size="80" tabindex="1" />
        </c:if>
        <s:textarea name="currentSample.description" key="experiment.samples.description" rows="3" cols="75"
            tabindex="2" />
        <s:textfield key="currentSample.externalSampleId" size="80" tabindex="3" />
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
    
    <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>
    <script type="text/javascript">
	    downloadMgr.resetDownloadInfo();
	
	    showDownloadInProgress = function() {
	        if (Object.values(downloadMgr.downloadFiles).length > 0) {
	            $('downloadInProgressMsg').show();
	            setTimeout("hideMsg()",5000);
	        }
	    }
	
	    hideMsg = function() {
	        $('downloadInProgressMsg').hide();
	    }
    </script>
</caarray:tabPane>

