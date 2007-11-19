<%@ tag display-name="annotationAssociationPicker"
        description="Renders the Input for an annotations association selection"
        body-content="empty"%>

<%@ attribute name="baseId" required="true" type="java.lang.String"%>
<%@ attribute name="category" required="true" type="gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory" %>
<%@ attribute name="termLabel" required="true" type="java.lang.String" %>
<%@ attribute name="initialTerms" required="true" type="java.util.Collection" %>
<%@ attribute name="termFieldName" required="true" type="java.lang.String" %>
<%@ attribute name="tabIndex" required="true" type="java.lang.String" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<s:if test="fieldErrors['${termFieldName}'] != null">
<tr errorfor="${termFieldName}">
    <td valign="top" align="center" colspan="2">
        <s:fielderror><s:param>${termFieldName}</s:param></s:fielderror>
    </td>
</tr>
</s:if>
<tr>
    <td class="tdLabel"><label class="label">${termLabel}s:</label></td>
    <td>
        <s:if test="${editMode}">
            <div class="selectListWrapper">
                <div class="selectListSide">
                    <div class="selectListHeader">
                        <span class="selectListFilterLabel">Filter:</span> <s:textfield id="${baseId}SearchInput" name="currentTerm.value" theme="simple" size="20" tabindex="${tabIndex}" cssStyle="align:left;" />
                        <span id="${baseId}ProgressMsg" style="display:none;"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /></span>
                        <c:url value="/protected/vocabulary/manage.action" var="addTermUrl">
                            <c:param name="initialTab" value="${category}" />
                            <c:param name="startWithCreate" value="true" />
                            <c:param name="returnProjectId" value="${project.id}" />
                        </c:url>
                        <span style="position: relative; left: 15px; margin-top: -24px; float: right;"><caarray:linkButton actionClass="add" text="Add" url="${addTermUrl}" /></span>
                    </div>
                    <div id="${baseId}AutocompleteDiv"></div>
                </div>
                <div class="selectionside">
                    <h4>Selected ${termLabel}s</h4>
                    <div class="scrolltable2">
                        <input name="${termFieldName}" type="hidden" value=""/>
                        <ul id="${baseId}SelectedItemDiv" class="selectedItemList">
                            <c:forEach items="${initialTerms}" var="currentItem">
                                <li onclick="TermPickerUtils.removeSelection(this); "><input name="${termFieldName}" type="hidden" value="${currentItem.id}"/>${currentItem.value}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
            <c:url value="/protected/ajax/vocabulary/searchForTerms.action" var="autocompleteUrl" />
            <script type="text/javascript">
                var ${baseId}Picker = TermPickerUtils.createAutoUpdater('${baseId}', '${autocompleteUrl}', '${termLabel}', '${category}', '${termFieldName}');
            </script>
        </s:if>
        <s:else>
            <c:forEach items="${initialTerms}" var="currentItem" varStatus="status">
                <c:if test="${!status.first}">, </c:if>${currentItem.value}
            </c:forEach>
        </s:else>
    </td>
</tr>
