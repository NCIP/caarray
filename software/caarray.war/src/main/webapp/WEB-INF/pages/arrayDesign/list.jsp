<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%@page import="gov.nih.nci.caarray.domain.file.FileStatus"%>

<c:url value="/protected/ajax/arrayDesign/create.action" var="importInBackgroundUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>

<script type="text/javascript">
    openNewImportWindow = function() {
       uploadWindow = window.open('${importInBackgroundUrl}', '_blank', "width=685,height=480,left=0,top=0,toolbar,scrollbars,resizable,status=yes");
    }

    openEditFileWindow = function(url) {
       uploadWindow = window.open(url, '_blank', "width=685,height=480,left=0,top=0,toolbar,scrollbars,resizable,status=yes");
    }

    confirmDeleteArrayDesign = function(id) {
        var buttonDiv = 'delete_button' + id;
        if (confirm('You are about to delete this array design.   Please note that large array designs will take longer to delete.\nPress OK to continue.')) {
            $('delete_progress').show();
            $(buttonDiv).hide();
            return true;
        }
        return false;
    }

</script>

<html>
<head>
    <title>Manage Array Designs</title>
</head>
<body>
    <h1>Manage Array Designs</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="delete_progress" class="confirm_msg" style="display: none; margin: 3px 3px">
            Array Design deletion is in progress.
        </div>
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>Array Designs</h3>
                <div class="addlink">
                    <caarray:linkButton actionClass="add" text="Import a New Array Design" onclick="openNewImportWindow()" />
                </div>
            </div>
            <caarray:successMessages />
            <div class="tableboxpad">
                <c:url value="/protected/ajax/arrayDesign/list.action" var="sortUrl"/>
                <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
                    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${arrayDesigns}"
                                   requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
                        <display:column property="name" titleKey="arrayDesign.name" sortable="true" url="/protected/arrayDesign/view.action" paramId="arrayDesign.id" paramProperty="id" maxLength="30"/>
                        <display:column property="provider.name" titleKey="arrayDesign.provider" sortable="true"/>
                        <display:column titleKey="arrayDesign.assayTypes" >
                            <c:choose>
                                <c:when test="${row.assayTypes != null}">
                                    <c:forEach items="${row.assayTypes}" var="currType" varStatus="status">
                                        <c:if test="${!status.first}">, </c:if>${currType.name}
                                    </c:forEach>                                
                                </c:when>
                                <c:otherwise>&nbsp;</c:otherwise>
                            </c:choose>
                        </display:column>
                        <display:column property="version" titleKey="arrayDesign.version" sortable="true"/>
                        <display:column property="technologyType.value" titleKey="arrayDesign.technologyType" sortable="true"/>
                        <display:column property="organism.scientificName" titleKey="arrayDesign.organism" sortable="true"/>
                        <display:column titleKey="button.edit" class="centered" headerClass="centered">
                            <c:set var="importingStatus" value="<%= FileStatus.IMPORTING.name() %>"/>
                            <c:if test="${row.designFileSet.status != importingStatus}">
                                <c:url value="/protected/arrayDesign/edit.action" var="editDesignUrl">
                                     <c:param name="arrayDesign.id" value="${row.id}" />
                                </c:url>
                                <a href="${editDesignUrl}">
                                	<img src="<c:url value="/images/ico_edit.gif"/>" 
                                		 alt="<fmt:message key="button.edit"/>" 
                                		 title="<fmt:message key="button.edit"/>"/>
                               	</a>
                          </c:if>
                        </display:column>
                        <display:column titleKey="button.editFile" class="centered" headerClass="centered">
                            <c:set var="importingStatus" value="<%= FileStatus.IMPORTING.name() %>"/>
                            <c:set var="importedStatus" value="<%= FileStatus.IMPORTED.name() %>"/>
                            <c:if test="${row.designFileSet.status != importingStatus && row.designFileSet.status != importedStatus}">
                                <c:url value="/protected/ajax/arrayDesign/editFile.action" var="editFileDesignUrl">
                                     <c:param name="arrayDesign.id" value="${row.id}" />
                                </c:url>
                                <a href="#" onclick="openEditFileWindow('${editFileDesignUrl}')">
                                	<img src="<c:url value="/images/ico_edit.gif"/>" 
                                		 alt="<fmt:message key="button.editFile"/>" 
                                		 title="<fmt:message key="button.editFile"/>"/>
                               	</a>
                            </c:if>
                        </display:column>
                        <display:column titleKey="button.delete" class="centered" headerClass="centered">
                            <c:set var="importingStatus" value="<%= FileStatus.IMPORTING.name() %>"/>
                            <c:if test="${row.designFileSet.status != importingStatus}">
                                <c:url value="/protected/arrayDesign/delete.action" var="deleteDesignUrl">
                                     <c:param name="arrayDesign.id" value="${row.id}" />
                                </c:url>
                                <div id="delete_button${row.id}">
                                    <a href="${deleteDesignUrl}" onclick="return confirmDeleteArrayDesign(${row.id});">
                                    	<img src="<c:url value="/images/ico_delete.gif"/>" 
                                    		 alt="<fmt:message key="button.delete"/>"
                                    		 title="<fmt:message key="button.delete"/>"/>
                                   	</a>
                                </div>
                          </c:if>
                        </display:column>
                        <display:column titleKey="button.reimport" class="centered" headerClass="centered">
                            <c:if test="${row.unparsedAndReimportable}">
                                <c:url value="/protected/arrayDesign/reimport.action" var="reimportDesignUrl">
                                     <c:param name="arrayDesign.id" value="${row.id}" />
                                </c:url>
                                <a href="${reimportDesignUrl}">
                                	<img src="<c:url value="/images/ico_import.gif"/>" 
                                		 alt="<fmt:message key="button.reimport"/>"
                                		 title="<fmt:message key="button.reimport"/>"/>
                           	    </a>
                          </c:if>
                        </display:column>
                        <display:column sortProperty="designFileSet.status" titleKey="experiment.files.status" sortable="true" url="/protected/arrayDesign/view.action" paramId="arrayDesign.id" paramProperty="id" >
                            <c:if test="${not empty row.designFileSet.status}">
                                <ajax:anchors target="tabboxlevel2wrapper">
                                    <fmt:message key="experiment.files.filestatus.${row.designFileSet.status}">
                                        <fmt:param><c:url value="/" /></fmt:param>
                                    </fmt:message>
                            
                                </ajax:anchors>
                            </c:if>
                        </display:column>
                      <caarray:arrayDesignListDownloadColumn itemId="${row.id}"/>
                    </display:table>
                </ajax:displayTag>
            </div>
        </div>
    </div>
</body>
</html>