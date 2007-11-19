<%@ tag display-name="annotationAssociationPicker"
        description="Renders the Input for an annotations association selection"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="associatedEntityName" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="baseId" required="true"%>
<%@ include file="projectListTabCommon.tagf"%>
<s:if test="fieldErrors['associatedValueName'] != null">
<tr errorfor="associatedValueName">
    <td valign="top" align="center" colspan="2">
        <s:fielderror><s:param>associatedValueName</s:param></s:fielderror>
    </td>
</tr>
</s:if>
<tr>
    <td class="tdLabel"><label class="label">${associatedEntityName}s<span class="required">*</span>:</label></td>
    <td>
        <s:if test="${editMode}">
            <div class="selectListWrapper">
                <div class="selectListSide">
                    <div class="selectListHeader">
                        <span class="selectListFilterLabel">Filter:</span> <s:textfield id="${baseId}AssociatedValueName" name="associatedValueName" theme="simple" size="20" tabindex="3" cssStyle="align:left;" />
                        <span id="${baseId}ProgressMsg" style="display:none;"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /> Loading...</span>
                    </div>
                    <div id="${baseId}AutocompleteDiv"></div>
                </div>
                <div class="selectionside">
                    <h4>Selected ${associatedEntityName}s</h4>
                    <div class="scrolltable2">
                        <ul id="${baseId}SelectedItemDiv" class="selectedItemList">
                            <c:forEach items="${initialSavedAssociations}" var="currentItem">
                                <li onclick="AssociationPickerUtils.removeSelection(this, '${baseId}', '${associatedEntityName}'); "><input type="hidden" value="${currentItem.id}"/>${currentItem.name}</li>
                            </c:forEach>
                            <c:forEach items="${itemsToAssociate}" var="currentItem">
                                <li onclick="AssociationPickerUtils.removeSelection(this, '${baseId}', '${associatedEntityName}'); "><input type="hidden" name="itemsToAssociate" value="${currentItem.id}"/>${currentItem.name}</li>
                            </c:forEach>
                        </ul>
                    </div>
                    <span id="${baseId}ItemsToRemove">
                        <c:forEach items="${itemsToRemove}" var="currentItem">
                            <input type="hidden" name="itemsToRemove" value="${currentItem.id}"/>
                        </c:forEach>
                    </span>
                </div>
            </div>
            <c:url value="/ajax/project/listTab/${plural}/searchForAssociationValues.action" var="autocompleteUrl" />
            <script type="text/javascript">
                var ${baseId}Picker = AssociationPickerUtils.createAutoUpdater('${baseId}', '${autocompleteUrl}', '${project.id}', '${entityName}', '${itemId}',  '${associatedEntityName}');
            </script>
        </s:if>
        <s:else>
            <c:forEach items="${currentAssociationsCollection}" var="currentItem" varStatus="status">
                <c:if test="${!status.first}">, </c:if>${currentItem.name}
            </c:forEach>
        </s:else>
    </td>
</tr>
