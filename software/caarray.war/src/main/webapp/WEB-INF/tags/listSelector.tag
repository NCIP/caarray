<%@ tag display-name="listSelectionPicker"
        description="Renders the double-select Input for selecting one or more items from a list"
        body-content="empty"%>

<%@ attribute name="baseId" required="true" type="java.lang.String"%>
<%@ attribute name="listLabel" required="false" type="java.lang.String" %>
<%@ attribute name="listField" required="true" type="java.lang.Object" %>
<%@ attribute name="listFieldName" required="true" type="java.lang.String" %>
<%@ attribute name="tabIndex" required="true" type="java.lang.String" %>
<%@ attribute name="required" required="false" type="java.lang.String" %>
<%@ attribute name="multiple" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab1" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2Url" required="false" type="java.lang.String" %>
<%@ attribute name="hideAddButton" required="false" type="java.lang.String" %>
<%@ attribute name="addButtonUrl" required="false" type="java.lang.String" description="Required if hideAddButton != true" %>
<%@ attribute name="showFilter" required="false" type="java.lang.String" description="true (default) to allow filtering, false otherwise" %>
<%@ attribute name="filterFieldName" required="false" type="java.lang.String" description="Required if using filtering" %>
<%@ attribute name="objectLabel" required="false" type="java.lang.String" description="The property that supplies the option label visible to the end user; defaults to name." %>
<%@ attribute name="objectValue" required="false" type="java.lang.String" description="The property that supplies the value returned to the server; defaults to id" %>
<%@ attribute name="autocompleteUrl" required="true" type="java.lang.String" description="URL for filter autocompletion" %>
<%@ attribute name="autocompleteParamNames" required="false" type="java.lang.String" description="Additional parameter names to pass to the autocomplete filter (comma-separated list)" %>
<%@ attribute name="autocompleteParamValues" required="false" type="java.lang.String" description="Additional parameter values to pass to the autocomplete filter (comma-separated list)" %>
<%@ attribute name="allowReordering" required="false" type="java.lang.String" description="true to allow reordering of the selected items, false otherwise.  only relevant if multipe = true" %>
<%@ attribute name="divstyle" required="false" type="java.lang.String" description="style for the outer most div"%>
<%@ attribute name="displayResourceValue" required="false" type="java.lang.String" description="true to call getText() on the list field in order to display the resource value"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<c:if test="${empty listLabel}">
    <fmt:message key="${listFieldName}" var="listLabel"/>
</c:if>
<c:if test="${empty showFilter}">
    <c:set var="showFilter" value="true"/>
</c:if>
<c:if test="${empty objectLabel || objectLabel == null}">
    <c:set var="objectLabel" value="name"/>
</c:if>
<c:if test="${empty objectValue || objectValue == null}">
    <c:set var="objectValue" value="id"/>
</c:if>

<s:if test="fieldErrors['${listFieldName}'] != null">
<tr errorfor="${listFieldName}">
    <td valign="top" align="center" colspan="2">
        <s:fielderror><s:param>${listFieldName}</s:param></s:fielderror>
    </td>
</tr>
</s:if>


<tr>
    <td class="tdLabel">
        <s:if test="${divstyle != null}">
            <div id="${baseId}OuterDivLabel" style="${divstyle}">
                <label class="label">${listLabel}<c:if test="${required == 'true'}"><span class="required">*</span></c:if>:</label>
            </div>
        </s:if>
        <s:else>
            <label class="label">${listLabel}<c:if test="${required == 'true'}"><span class="required">*</span></c:if>:</label>
        </s:else>
    </td>
    <td>
        <s:if test="${editMode}">
            <s:if test="${divstyle != null}">
               <div id="${baseId}OuterDivBody" class="selectListWrapper" style="${divstyle}">
            </s:if>
            <s:else>
               <div class="selectListWrapper">
            </s:else>
                <div class="selectListSide">
                    <div class="selectListHeader">
                        <c:if test="${showFilter == 'true'}">
                            <span class="selectListFilterLabel">Filter:</span> <s:textfield id="${baseId}SearchInput" name="${filterFieldName}" theme="simple" size="10" tabindex="${tabIndex}" cssStyle="align:left;" value=""/>
                        </c:if>
                        <c:if test="${showFilter == 'false'}">
                            <s:hidden id="${baseId}SearchInput" name="${filterFieldName}" value=""/>
                        </c:if>
                        <span id="${baseId}ProgressMsg" style="display: none"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /></span>
                        <c:if test="${hideAddButton != 'true'}">
                        <span style="position: relative; left: 15px; margin-top: -24px; float: right;">
                            <caarray:linkButton actionClass="add" text="Add" url="${addButtonUrl}" onclick="return TabUtils.confirmNavigateFromForm()"/>
                        </span>
                        </c:if>
                    </div>
                    <div id="${baseId}AutocompleteDiv"></div>
                </div>
                <div class="selectionside">
                    <h4>Selected ${listLabel}</h4>
                    <div>
                        <span style="display: none">
                            <c:choose>
                                <c:when test="${multiple != 'true' && !empty listField}">
                                    <select id="${baseId}SelectedItemValues" name="${listFieldName}">
                                        <option value=""></option>
                                        <option selected="selected" value="<s:property value='#attr.listField.${objectValue}'/>"><s:property value='#attr.listField.${objectValue}'/></option>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <select id="${baseId}SelectedItemValues" name="${listFieldName}" multiple="true">
                                        <option value=""></option>
                                        <c:forEach items="${listField}" var="currentItem">
                                            <option selected="selected" value="<s:property value='#attr.currentItem.${objectValue}'/>"><s:property value='#attr.currentItem.${objectValue}'/></option>
                                        </c:forEach>
                                    </select>
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <ul id="${baseId}SelectedItemDiv" class="selectedItemList">
                            <c:choose>
                                <c:when test="${multiple != 'true' && !empty listField}">
                                    <c:choose>
                                        <c:when test="${displayResourceValue != 'true'}">
                                            <li onclick="ListPickerUtils.removeSelection(this, ${baseId}Picker, '${baseId}');"><s:property value="#attr.listField.${objectLabel}"/></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li onclick="ListPickerUtils.removeSelection(this, ${baseId}Picker, '${baseId}');"><s:property value="getText(#attr.listField.${objectLabel})"/></li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${displayResourceValue != 'true'}">
                                            <c:forEach items="${listField}" var="currentItem">
                                                <li onclick="ListPickerUtils.removeSelection(this, ${baseId}Picker, '${baseId}');" id="${baseId}_<s:property value='#attr.currentItem.${objectValue}'/>"><s:property value="#attr.currentItem.${objectLabel}"/></li>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach items="${listField}" var="currentItem">
                                                <li onclick="ListPickerUtils.removeSelection(this, ${baseId}Picker, '${baseId}');" id="${baseId}_<s:property value='#attr.currentItem.${objectValue}'/>"><s:property value="getText(#attr.currentItem.${objectLabel})"/></li>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                        <s:if test="${allowReordering == 'true'}">
                            <fmt:message key="listSelector.reorderList"/>
                        </s:if>
                    </div>
                </div>
            </div>
            <script type="text/javascript">
                ${baseId}Picker = ListPickerUtils.createAutoUpdater('${baseId}', '${autocompleteUrl}', '${listLabel}', '${filterFieldName}', '${listFieldName}', '${multiple}', '${autocompleteParamNames}', '${autocompleteParamValues}', '${allowReordering}');
                <s:if test="${allowReordering == 'true'}">
                    ${baseId}Picker.sortableReordered = false;
                    Sortable.create('${baseId}SelectedItemDiv', { starteffect: function() { ${baseId}Picker.sortableReordered = true; } });
                </s:if>
            </script>
        </s:if>
        <s:else>
            <c:choose>
                <c:when test="${multiple != 'true'}">
                    <c:choose>
                        <c:when test="${displayResourceValue != 'true'}">
                            <s:property value="#attr.listField.${objectLabel}"/><br>
                        </c:when>
                        <c:otherwise>
                            <s:property value="getText(#attr.listField.${objectLabel})"/><br>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${displayResourceValue != 'true'}">
                            <c:forEach items="${listField}" var="currentItem" varStatus="status">
                                <c:if test="${!status.first}">, </c:if><s:property value="#attr.currentItem.${objectLabel}"/>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${listField}" var="currentItem" varStatus="status">
                                <c:if test="${!status.first}">, </c:if><s:property value="getText(#attr.currentItem.${objectLabel})"/>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>

                </c:otherwise>
            </c:choose>
        </s:else>
    </td>
</tr>

