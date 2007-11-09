<%@ tag display-name="annotationAssociationPicker"
        description="Renders the Input for an annotations association selection"
        body-content="empty"%>

<%@ attribute name="entityName" required="true"%>
<%@ attribute name="itemId" required="true"%>
<%@ attribute name="baseId" required="true"%>
<%@ include file="projectListTabCommon.tagf"%>

<tr>
    <td class="tdLabel"><label class="label">${plural}:</label></td>
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
                    <h4>Selected ${plural}</h4>
                    <div class="scrolltable2">
                        <ul id="${baseId}SelectedItemDiv">
                            <c:forEach items="${currentAssociationsCollection}" var="currentItem">
                                <li>${currentItem.name}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
            <c:url value="/protected/ajax/project/listTab/${plural}/searchForAssociationValues.action" var="autocompleteUrl" />
            <script type="text/javascript">

                processSelection = function(selectedItem) {
                    var newItem = selectedItem.cloneNode(true);
                    newItem.firstChild.name = 'itemsToAssociate';
                    $('${baseId}SelectedItemDiv').appendChild(newItem);
                }

                doNothing = function() {
                }

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
                <h4>Associated ${plural}</h4>
                <div class="scrolltable2">
                    <ul id="${baseId}SelectedItemDiv">
                        <c:forEach items="${currentAssociationsCollection}" var="currentItm">
                            <li>${item.name}</li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </s:else>
    </td>
</tr>
