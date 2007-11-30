<%@ tag display-name="annotationAssociationPicker"
        description="Renders the Input for an annotations association selection"
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<c:if test="${empty termLabel}">
    <fmt:message key="${termFieldName}" var="termLabel"/>
</c:if>

<s:if test="fieldErrors['${termFieldName}'] != null">
<tr errorfor="${termFieldName}">
    <td valign="top" align="center" colspan="2">
        <s:fielderror><s:param>${termFieldName}</s:param></s:fielderror>
    </td>
</tr>
</s:if>
<tr>
    <td class="tdLabel"><label class="label">${termLabel}<c:if test="${multiple == 'true'}">s</c:if><c:if test="${required == 'true'}"><span class="required">*</span></c:if>:</label></td>
    <td>
        <s:if test="${editMode}">
            <div class="selectListWrapper">
                <div class="selectListSide">
                    <div class="selectListHeader">
                        <span class="selectListFilterLabel">Filter:</span> <s:textfield id="${baseId}SearchInput" name="currentTerm.value" theme="simple" size="10" tabindex="${tabIndex}" cssStyle="align:left;" />
                        <span id="${baseId}ProgressMsg" style="display: none"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /></span>
                        <c:url value="/protected/vocabulary/manage.action" var="addTermUrl">
                            <c:param name="initialTab" value="${category}" />
                            <c:param name="startWithEdit" value="true" />
                            <c:param name="returnProjectId" value="${project.id}" />
                            <c:param name="returnInitialTab1" value="${returnInitialTab1}" />
                            <c:param name="returnInitialTab2" value="${returnInitialTab2}" />
                            <c:param name="returnInitialTab2Url" value="${returnInitialTab2Url}" />
                        </c:url>
                        <span style="position: relative; left: 15px; margin-top: -24px; float: right;">
                            <caarray:linkButton actionClass="add" text="Add" url="${addTermUrl}" onclick="return TabUtils.confirmNavigateFromForm()"/>
                        </span>
                    </div>
                    <div id="${baseId}AutocompleteDiv"></div>
                </div>
                <div class="selectionside">
                    <h4>Selected ${termLabel}s</h4>
                    <div>
                        <input name="${termFieldName}" type="hidden" value=""/>
                        <ul id="${baseId}SelectedItemDiv" class="selectedItemList">
                            <c:choose>
                                <c:when test="${multiple != 'true' && !empty termField}">
                                    <li onclick="TermPickerUtils.removeSelection(this); "><input name="${termFieldName}" type="hidden" value="${termField.id}"/>${termField.value}</li>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${termField}" var="currentItem">
                                        <li onclick="TermPickerUtils.removeSelection(this); "><input name="${termFieldName}" type="hidden" value="${currentItem.id}"/>${currentItem.value}</li>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                    </div>
                </div>
            </div>
            <c:url value="/protected/ajax/vocabulary/searchForTerms.action" var="autocompleteUrl" />
            <script type="text/javascript">
                var ${baseId}Picker = TermPickerUtils.createAutoUpdater('${baseId}', '${autocompleteUrl}', '${termLabel}', '${category}', '${termFieldName}', '${multiple}');
            </script>
        </s:if>
        <s:else>
            <c:choose>
                <c:when test="${multiple != 'true'}">
                    ${termField.value}
                </c:when>
                <c:otherwise>
                    <c:forEach items="${termField}" var="currentItem" varStatus="status">
                        <c:if test="${!status.first}">, </c:if>${currentItem.value}
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </s:else>
    </td>
</tr>
