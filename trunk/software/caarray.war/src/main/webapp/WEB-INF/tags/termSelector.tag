<%@ tag display-name="Term Selector"
        description="Renders the Input for term selection"
        body-content="empty"%>

<%@ attribute name="baseId" required="true" type="java.lang.String"%>
<%@ attribute name="category" required="true" type="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory" %>
<%@ attribute name="termLabel" required="false" type="java.lang.String" %>
<%@ attribute name="termField" required="true" type="java.lang.Object" %>
<%@ attribute name="termFieldName" required="true" type="java.lang.String" %>
<%@ attribute name="tabIndex" required="true" type="java.lang.String" %>
<%@ attribute name="required" required="false" type="java.lang.String" %>
<%@ attribute name="multiple" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab1" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2Url" required="false" type="java.lang.String" %>
<%@ attribute name="hideAddButton" required="false" type="java.lang.String" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<c:if test="${empty termLabel}">
    <fmt:message key="${termFieldName}" var="termLabel"/>
</c:if>

<c:set var="filterFieldName" value="currentTerm.value"/>
<c:url var="addButtonUrl" value="/protected/vocabulary/manage.action">
    <c:param name="initialTab" value="${category}" />
    <c:param name="startWithEdit" value="true" />
    <c:param name="returnProjectId" value="${project.id}" />
    <c:param name="returnInitialTab1" value="${returnInitialTab1}" />
    <c:param name="returnInitialTab2" value="${returnInitialTab2}" />
    <c:param name="returnInitialTab2Url" value="${returnInitialTab2Url}" />
</c:url>
<c:set var="objectLabel" value="valueAndSource"/>
<c:url var="autocompleteUrl" value="/protected/ajax/vocabulary/searchForTerms.action" />

<caarray:listSelector baseId="${baseId}" listLabel="${termLabel}" listField="${termField}"
    listFieldName="${termFieldName}" tabIndex="${tabIndex}" required="${required}" multiple="${multiple}"
    returnInitialTab1="${returnInitialTab1}" returnInitialTab2="${returnInitialTab2}"
    returnInitialTab2Url="${returnInitialTab2Url}" hideAddButton="${hideAddButton}" addButtonUrl="${addButtonUrl}"
    filterFieldName="${filterFieldName}" objectLabel="${objectLabel}" objectValue="${objectValue}"
    autocompleteUrl="${autocompleteUrl}" autocompleteParamNames="category" autocompleteParamValues="${category}" />
