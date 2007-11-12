<%@ tag display-name="annotationAssociationPicker"
        description="Renders the Input for an annotations association selection"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="associatedEntityName" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="baseId" required="true"%>
<%@ include file="projectListTabCommon.tagf"%>
<tr>
    <td class="tdLabel"><label class="label">${associatedEntityName}s:</label></td>
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
                            <c:forEach items="${currentAssociationsCollection}" var="currentItem">
                                <li onclick="removeSelection(this); "><input type="hidden" value="${currentItem.id}"/>${currentItem.name}</li>
                            </c:forEach>
                        </ul>
                    </div>
                    <span id="${baseId}ItemsToRemove"></span>
                </div>
            </div>
            <c:url value="/protected/ajax/project/listTab/${plural}/searchForAssociationValues.action" var="autocompleteUrl" />
            <script type="text/javascript">
                processSelection = function(selectedItem) {
                    var id = selectedItem.firstChild.value;
                    if (id == null || id == '') {
                        return;
                    }
                    var selectedItems = document.getElementById('${baseId}SelectedItemDiv').getElementsByTagName('input');
                    for (i = 0; i < selectedItems.length; ++i) {
                        if (selectedItems[i].value == id) {
                            alert('${associatedEntityName} already selected.');
                            return;
                        }
                    }

                    var itemsToRemove = document.getElementById('${baseId}ItemsToRemove').getElementsByTagName('input');
                    var found = false;
                    for (i = 0; i < itemsToRemove.length && !found; ++i) {
                        if (itemsToRemove[i].value == id) {
                            Element.remove(itemsToRemove[i]);
                            found = true;
                        }
                    }

                    var newItem = selectedItem.cloneNode(true);
                    newItem.firstChild.name = (found) ? '' : 'itemsToAssociate';
                    newItem.onclick = function() {removeSelection(this);}
                    $('${baseId}SelectedItemDiv').appendChild(newItem);
                }

                removeSelection = function(selectedItem) {
                    var inputName = selectedItem.firstChild.name;
                    var id = selectedItem.firstChild.value;
                    if (inputName != 'itemsToAssociate') {
                        var newItem = selectedItem.firstChild.cloneNode(true);
                        newItem.name = 'itemsToRemove';
                        $('${baseId}ItemsToRemove').appendChild(newItem);
                    }
                    Element.remove(selectedItem);
                }

                doNothing = function() { }

                var ${baseId}Autoupdater = new Ajax.Autocompleter("${baseId}AssociatedValueName", "${baseId}AutocompleteDiv", "${autocompleteUrl}",
                        {paramName: "associatedValueName", minChars: '0', indicator: '${baseId}ProgressMsg', frequency: 0.75,
                         updateElement: processSelection, onHide: doNothing, onShow: doNothing,
                         parameters: 'project.id=${project.id}&current${entityName}.id=${itemId}' });
                Element.show(${baseId}Autoupdater.update);
                ${baseId}Autoupdater.activate();
            </script>
        </s:if>
        <s:else>
            <div class="selectionside">
                <h4>Associated ${associatedEntityName}s</h4>
                <div class="scrolltable2">
                    <ul id="${baseId}SelectedItemDiv">
                        <c:forEach items="${currentAssociationsCollection}" var="currentItem">
                            <li>${currentItem.name}</li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </s:else>
    </td>
</tr>
