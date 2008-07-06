<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%-- 
Set the downloadUrl var and render the contents of the Download File Groups page. 
--%>
<c:choose>
    <c:when test="${param.tab == 'Sources'}">
        <c:set value="/ajax/project/listTab/${param.tab}/download.action?project.id=${project.id}&currentSource.id=${currentSource.id}&editMode=${editMode}"
               var="downloadUrl"/>
    </c:when>
    <c:when test="${param.tab == 'Samples'}">
        <c:set value="/ajax/project/listTab/${param.tab}/download.action?project.id=${project.id}&currentSample.id=${currentSample.id}&editMode=${editMode}"
               var="downloadUrl"/>
    </c:when>
    <c:when test="${param.tab == 'Extracts'}">
        <c:set value="/ajax/project/listTab/${param.tab}/download.action?project.id=${project.id}&currentExtract.id=${currentExtract.id}&editMode=${editMode}"
               var="downloadUrl"/>
    </c:when>
    <c:when test="${param.tab == 'LabeledExtracts'}">
        <c:set value="/ajax/project/listTab/${param.tab}/download.action?project.id=${project.id}&currentLabeledExtract.id=${currentLabeledExtract.id}&editMode=${editMode}"
               var="downloadUrl"/>
    </c:when>
    <c:when test="${param.tab == 'Hybridizations'}">
        <c:set value="/ajax/project/listTab/${param.tab}/download.action?project.id=${project.id}&currentHybridization.id=${currentHybridization.id}&editMode=${editMode}"
               var="downloadUrl"/>
    </c:when>
</c:choose>
<%@ include file="/WEB-INF/pages/project/files/downloadFileGroups.jsp" %>
