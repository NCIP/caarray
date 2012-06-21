// graceful  degradation of firebug's console object
if (! ("console" in window) || !("firebug" in console)) {
    var names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml", "group"
                 , "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];
    window.console = {};
    for (var i = 0; i <names.length; ++i) window.console[names[i]] = function() {};
}

/* Function to hide form elements that show through
   the search form when it is visible */
function toggleForm(frmObj, iState) // 1 visible, 0 hidden
{
  for(var i = 0; i < frmObj.length; i++) {
    if (frmObj.elements[i].type.indexOf("select") == 0 || frmObj.elements[i].type.indexOf("checkbox") == 0) {
            frmObj.elements[i].style.visibility = iState ? "visible" : "hidden";
    }
  }
}

// This function is used by the login screen to validate user/pass
// are entered.
function validateRequired(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    oRequired = new required();

    for (x in oRequired) {
        if ((form[oRequired[x][0]].type == 'text' || form[oRequired[x][0]].type == 'textarea' || form[oRequired[x][0]].type == 'select-one' || form[oRequired[x][0]].type == 'radio' || form[oRequired[x][0]].type == 'password') && form[oRequired[x][0]].value == '') {
           if (i == 0)
              focusField = form[oRequired[x][0]];

           fields[i++] = oRequired[x][1];

           bValid = false;
        }
    }

    if (fields.length > 0) {
       focusField.focus();
       alert(fields.join('\n'));
    }

    return bValid;
}

function selectAll(selectAllBox, theform) {
       var state = selectAllBox.checked;
       numElements = theform.elements.length;
       for (i = 0; i < numElements; i++) {
            var element = theform.elements[i];
            if ("checkbox" == element.type) {
                element.checked = state;
            }
        }
}


function highlightFormElements() {
    // add input box highlighting
    addFocusHandlers(document.getElementsByTagName("input"));
    addFocusHandlers(document.getElementsByTagName("textarea"));
}

function addFocusHandlers(elements) {
    for (i=0; i < elements.length; i++) {
        if (elements[i].type != "button" && elements[i].type != "submit" &&
            elements[i].type != "reset" && elements[i].type != "checkbox" && elements[i].type != "radio") {
            if (!elements[i].getAttribute('readonly') && !elements[i].getAttribute('disabled')) {
                elements[i].onfocus=function() {this.style.backgroundColor='#ffd';this.select()};
                elements[i].onmouseover=function() {this.style.backgroundColor='#ffd'};
                elements[i].onblur=function() {this.style.backgroundColor='';}
                elements[i].onmouseout=function() {this.style.backgroundColor='';}
            }
        }
    }
}

window.onload = function() {
    highlightFormElements();
    if ($('successMessages')) {
        new Effect.Highlight('successMessages');
        // causes webtest exception on OS X : http://lists.canoo.com/pipermail/webtest/2006q1/005214.html
        // window.setTimeout("Effect.DropOut('successMessages')", 3000);
    }
    if ($('errorMessages')) {
        new Effect.Highlight('errorMessages');
    }

    /* Initialize menus for IE */
    if ($("primary-nav")) {
        var navItems = $("primary-nav").getElementsByTagName("li");

        for (var i=0; i<navItems.length; i++) {
            if (navItems[i].className == "menubar") {
                navItems[i].onmouseover=function() { this.className += " over"; }
                navItems[i].onmouseout=function() { this.className = "menubar"; }
            }
        }
    }
}

// Show the document's title on the status bar
window.defaultStatus=document.title;

var Caarray = {
    truePredicate: function(o) { return true; },
    submitAjaxForm: function(formId, divId, options) {
        var formData = Form.serialize(formId);
        options = options || {};
        if (options.extraArgs) {
            formData = formData + '&' + Hash.toQueryString(options.extraArgs);
        }
        var url = options.url || $(formId).action;
        new Ajax.Updater(divId, url, {parameters: formData, evalScripts: true, insertion: options.insertion, onComplete: options.onComplete} );
    },

    focusFirstElement: function(form) {
      var form = $(form);
      if (form) {
        var elt = form.findFirstElement();
        if (elt) {
          elt.activate();
        }
      }
      return form;
  }
}

var ExtTreeUtils = {
    /**
     * Returns whether the given tree has any checked nodes.
     * @param tree the Ext.tree.TreePanel tree to search
     * @return true if the tree has any checked nodes, false otherwise
     */
    hasCheckedNodes: function(tree) {
        return (ExtTreeUtils.findDescendent(tree.root, "checked", true) != null);
    },

    /**
     * Find a descendent of the given node whose value for the given attribute is equal to the given value.
     * @param parent the root node of the subtree to search
     * @param attribute the name of the attribute of each node to check
     * @param value the value that the given attribute of the node should have
     * @return if any matches are found, one of the matches (which match will be returned is unspecified).
     * If no matches found, then null
     */
    findDescendent: function(parent, attribute, value) {
        var match = null;
        parent.cascade(function(node) {
            if (node.attributes[attribute] == value) {
                match = node;
                return false;
            }
        });
        return match;
    },

    /**
     * Set the enabled status of all nodes matching the given predicate in the subtree rooted at the given node
     * to the given status.
     * @param parent the root node of the subtree all of whose nodes will be enabled.
     * @param status the enabled status (true or false) to set
     * @param predicate (optional) a function that should return a boolean value for each node indicating
     * whether to set the status for that node. if omitted, each node's status will be set
     */
    setEnabledStatus: function(parent, status, predicate) {
        parent.cascade(function(node) {
            if ((predicate || Caarray.truePredicate)(node)) {
                if (status) {
                    node.enable();
                } else {
                    node.disable();
                }
            }
        });
    },

    /**
     * Returns an array of the nodes underneath (and including) the given node in Ext.tree.TreePanel that are checked
     * @param rootNode the root node of the subtree to search (pass in the root node of the tree to search the whole
     * tree)
     * @param nodeProperty if this is specified, then the array returned will contain this attribute of each node
     * that is checked; if it is not specified, the array will contain the nodes themselves
     * @return the array of either the checked nodes or the given attribute of the checked nodes
     */
    getCheckedNodes: function(rootNode, nodeProperty) {
        var checked = [], i;
        if (rootNode.attributes.checked && !rootNode.disabled) {
            checked.push(nodeProperty ? rootNode.attributes[nodeProperty] : rootNode);
        }
        if (!rootNode.isLeaf()) {
                for (i = 0; i < rootNode.childNodes.length; i++) {
                    checked = checked.concat(this.getCheckedNodes(rootNode.childNodes[i], nodeProperty));
                }
            }
        return checked;
    }
}

var TabUtils = {

    savedFormData : null,

    preFunction : function(confirmMessage) {
        if (!TabUtils.confirmNavigateFromForm(confirmMessage)) {
            return false;
        }
        TabUtils.showLoadingText();
    },

    confirmNavigateFromForm : function(confirmMessage) {
        confirmMessage = confirmMessage || 'There are unsaved changes in your form, are you sure you want to continue?';
        if (TabUtils.hasFormChanges()) {
            if (!confirm(confirmMessage)) {
                return false;
            }
        }
        return true;
    },

    hasFormChanges : function() {
        return TabUtils.savedFormData != null && $('projectForm') && TabUtils.savedFormData != Form.serialize('projectForm');
    },

    updateSavedFormData : function() {
        if ($('projectForm')) {
            TabUtils.savedFormData = Form.serialize('projectForm');
        } else {
            TabUtils.savedFormData = null;
        }
    },
    setSelectedTab : function() {
        tabMenuItems = $('tabs').getElementsByTagName('li');
        for(var i = 0; i < tabMenuItems.length; i++) {
            tabLink = tabMenuItems[i].getElementsByTagName('a')[0];
            tabLink.blur();
        }
        // Set summary=layout attribute for form tables for Section 508 compliance.
        var tableArr = document.getElementsByTagName("table");
        for (var i = 0; i < tableArr.length; i++) {
	    if (tableArr[i].className == "form") {
                tableArr[i].summary = "layout";
	    }
        }
    },

    setSelectedLevel2Tab : function() {
        tabMenuItems = $('tablevel2').getElementsByTagName('li');
        for(var i = 0; i < tabMenuItems.length; i++) {
            tabLink = tabMenuItems[i].getElementsByTagName('a')[0];
            tabLink.blur();
        }
    },

    showSubmittingText : function() {
        if ($('submittingText')) {
            $('submittingText').show();
            $('theForm').hide();
        }
    },

    hideSubmittingText : function() {
        if ($('submittingText')) {
            $('submittingText').hide();
            $('theForm').show();
        }
    },

    showLoadingText : function(keepMainContent) {
        var elts = document.getElementsByClassName('loadingText');
        var loadingElt = elts.length > 0 ? elts[0] : null;
        if (loadingElt) {
            $(loadingElt).show();
            if (!keepMainContent) {
                $('theForm').hide();
            }
        } else {
            tabwrapperdiv = $('tabboxlevel2wrapper');
            if (!tabwrapperdiv) {
                tabwrapperdiv = $('tabboxwrapper');
            }
            if (tabwrapperdiv) {
                // write out the loading text
                tabwrapperdiv.innerHTML = '<div><img alt="Indicator" align="absmiddle" src="' + contextPath + '/images/indicator.gif"/>&nbsp;Loading...</div>';
            }
        }
        if ($('tabHeader') && !keepMainContent) {
            $('tabHeader').hide();
        }
    },

    showLoadingTextKeepMainContent: function() {
        TabUtils.showLoadingText(true);
    },

    disableFormCheckboxes: function(checkboxIds) {
        if (checkboxIds.length) {
            for (var i = 0; i < checkboxIds.length; i++) {
              checkboxIds[i].disabled = true;
            }
        } else {
            checkboxIds.disabled = true;
        }
    },

    hideLoadingText : function() {
        var tmp = document.getElementsByClassName('loadingText');
        for(var i = 0; i < tmp.length; i++) {
            tmp[i].hide();
        }
        tmp = document.getElementsByClassName('message');
        for(var i = 0; i < tmp.length; i++) {
            tmp[i].hide();
        }
    },

    submitTabForm : function(formId, tabDivId) {
        TabUtils.showSubmittingText();
      Caarray.submitAjaxForm(formId, tabDivId);
    },

    submitTabFormToUrl : function(formId, url, divId) {
        TabUtils.showSubmittingText();
        Caarray.submitAjaxForm(formId, divId, { url: url});
    },

    loadLinkInTab: function(tabCaption, url) {
        executeAjaxTab_tabs(this.findTabByCaption('tabs', tabCaption), 'active', url, '');
    },

    loadLinkInSubTab: function(tabCaption, url) {
        executeAjaxTab_tablevel2(this.findTabByCaption('tablevel2', tabCaption), 'selected', url, '');
    },

    findTabByCaption: function(containerId, caption, isRegexp) {
        tabMenuItems = $(containerId).getElementsByTagName('li');
        for(var i = 0; i < tabMenuItems.length; i++) {
            tabLink = tabMenuItems[i].getElementsByTagName('a')[0];
            var match = isRegexp ? caption.test(tablink.innerHTML) : caption == tabLink.innerHTML;
            if (match) {
                return tabLink;
            }
        }
        return tabMenuItems[0].getElementsByTagName('a')[0];
    },
    addScrollTabs: function(tabPanelId, selectedTab) {
        var tabs = $(tabPanelId).getElementsByTagName('li')
        var tabList = tabs[0].parentNode;
        tabList.numTabs = tabs.length;
        tabList.index = selectedTab;
        tabList.offsets = new Array(tabList.numTabs);
        totalOffset = 0;
        for (i=0; i<tabList.numTabs; i++) {
          tabList.offsets[i] = totalOffset;
          totalOffset += tabs[i].offsetWidth;
        }
        if (tabList.parentNode.offsetWidth-totalOffset > 0) {
          return;
        } else {
          tabList.style.width = totalOffset+"px";
        }
        new Effect.Move(tabList,{ x: -tabList.offsets[selectedTab], mode: 'absolute'});

        innerPrev = document.createElement('div');
        innerPrev.innerHTML = '&lt;';
        innerPrev.className = 'scrollButton';
        prevButton = document.createElement('div');
        prevButton.className = 'leftScrollBox';
        prevButton.appendChild(innerPrev);
        prevButton.onclick = function() {
          if (tabList.index > 0) {
            new Effect.Move(tabList,{ x: -tabList.offsets[--tabList.index], mode: 'absolute', duration: .3});
          }
        };
        tabList.parentNode.appendChild(prevButton);

        innerNext = document.createElement('div');
        innerNext.innerHTML = '&gt;';
        innerNext.className = 'scrollButton';
        nextButton = document.createElement('div');
        nextButton.className = 'rightScrollBox';
        nextButton.appendChild(innerNext);
        nextButton.onclick = function() {
          var curOffset = tabList.offsets[tabList.index];
          var maxOffset = tabList.offsetWidth - tabList.parentNode.offsetWidth;
          if (curOffset < maxOffset && tabList.index < tabList.numTabs-1) {
            var newOffset = Math.min(maxOffset, tabList.offsets[++tabList.index]);
            new Effect.Move(tabList,{ x: -newOffset, mode: 'absolute', duration: .3});
          }
        };
        tabList.parentNode.appendChild(nextButton);
    }
}

var PermissionUtils = {
    SAMPLE_LIST_ID: 'access_profile_samples',
    SAMPLE_DROPDOWN_SELECTOR: 'select.sample_security_level',
    PROFILE_LOADING_ID: 'access_profile_loading',
    PROFILE_SAVING_ID: 'access_profile_saving',
    PROFILE_DETAILS_ID: 'access_profile_details',
    PROFILE_FORM_ID: 'profileForm',
    ACTION_BUTTON_ID: 'actionButton',

    changeExperimentAccess: function(theselect) {
        var selectVal = $F(theselect);
        var sampleSecLevels = SecurityLevel.sampleSecurityLevels[selectVal];
        if (sampleSecLevels.length == 0) {
            $(PermissionUtils.SAMPLE_LIST_ID).hide();
        } else {
            $(PermissionUtils.SAMPLE_LIST_ID).show();
            var sampleSelects = $(PermissionUtils.SAMPLE_LIST_ID).getElementsBySelector(PermissionUtils.SAMPLE_DROPDOWN_SELECTOR);
            sampleSelects.each(function(sampleSelect) {
                var currentValue = $F(sampleSelect);
                sampleSelect.options.length = 0;
                sampleSecLevels.each(function(sampleSecLevel) {
                    var opt = new Option(sampleSecLevel.label, sampleSecLevel.value, false, sampleSecLevel.value == currentValue);
                    sampleSelect.options[sampleSelect.options.length] = opt;
                });
            });
        }
    },

    loadProfile: function(url) {
        $(PermissionUtils.PROFILE_LOADING_ID).show();
        this.oldDetails = $(PermissionUtils.PROFILE_DETAILS_ID).innerHTML;
        new Ajax.Updater(PermissionUtils.PROFILE_DETAILS_ID, url,
              {evalScripts: true, onComplete: function() { $(PermissionUtils.PROFILE_LOADING_ID).hide();}});
        return false;
    },

    loadProfileFromForm: function(formId) {
        $(PermissionUtils.PROFILE_LOADING_ID).show();
        this.oldDetails = $(PermissionUtils.PROFILE_DETAILS_ID).innerHTML;

        var formData = Form.serialize(formId);
        var url = $(formId).action;

        new Ajax.Updater(PermissionUtils.PROFILE_DETAILS_ID, url,
              {parameters: formData, evalScripts: true, onComplete: function() { $(PermissionUtils.PROFILE_LOADING_ID).hide();}});
        return false;
    },

    cancelEditProfile: function() {
      $(PermissionUtils.ACTION_BUTTON_ID).value = 'cancel';
        $(PermissionUtils.PROFILE_DETAILS_ID).update(this.oldDetails);
    },

    saveProfile: function() {
      $(PermissionUtils.ACTION_BUTTON_ID).value = 'save';
        $(PermissionUtils.PROFILE_SAVING_ID).show();
        Caarray.submitAjaxForm(PermissionUtils.PROFILE_FORM_ID, PermissionUtils.PROFILE_DETAILS_ID);
    },

    listSampleProfile: function() {
        $(PermissionUtils.ACTION_BUTTON_ID).value = 'search';
        Caarray.submitAjaxForm(PermissionUtils.PROFILE_FORM_ID, PermissionUtils.PROFILE_DETAILS_ID);
    }
}

//
// Download stuff here
//

function DownloadMgr(dUrl, dgUrl, removeImageUrl, addImageUrl) {
  this.downloadUrl = dUrl;
  this.downloadGroupsUrl = dgUrl;
  this.removeImageUrl = removeImageUrl;
  this.files = new Object();
  this.downloadFiles = new Object();
  this.totalDownloadSize = 0;
  this.addImageUrl = addImageUrl;
  this.hideQueue = true;
}

DownloadMgr.prototype.addFile = function(name, id, size) {
    this.files[id] = { name: name, id: id, size: size };
  }

DownloadMgr.prototype.inQueue = function(id) {
    if (this.downloadFiles[id]) {
        return true;
    }
    return false;
}

DownloadMgr.prototype.addDownloadRow = function(id) {
    if (this.inQueue(id)) {
        alert('File ' + name + ' already in queue.');
        return;
    }
  var tbl = $('downloadTbl');
  this.deleteTotalRow();
  this.doAddDownloadRow(id, true, tbl , true);
  this.addTotalRow();
}

DownloadMgr.prototype.doAddDownloadRow = function(id, highlightRow, tbl, showRemoveIcon) {

  var file = this.files[id];
  this.downloadFiles[id] = file;
  this.totalDownloadSize += file.size;

  if (this.hideQueue == false) {
      var row = tbl.insertRow(-1);
      row.id = 'downloadRow' + id;

      var cell = row.insertCell(0);
      cell.innerHTML = '<img id="rIcon'+ id +'" src="'+ this.removeImageUrl + '" alt="remove" onclick="downloadMgr.removeRow(' + id + '); return false;"/>&nbsp;&nbsp;' + file.name;
  }

  if (highlightRow) {
      new Effect.Highlight($('fileRow' + file.id).up('tr'));
  }

  if(showRemoveIcon) {
    var fileCell = $('fileRow' + file.id)
    if (fileCell) {
      fileCell.innerHTML = '<img id="rIcon'+ id +'" src="'+ this.removeImageUrl + '" alt="remove" onclick="downloadMgr.removeRow(' + id + '); return false;"/>';
    }
  }
}

DownloadMgr.prototype.removeRow = function(id) {
  var tbl = $('downloadTbl');
  var row = $('downloadRow' + id);
  var file = this.downloadFiles[id];
  delete this.downloadFiles[id];
  this.totalDownloadSize -= file.size;
  for (i = 0; i < tbl.rows.length; ++i) {
    if (tbl.rows[i] == row) {
      tbl.deleteRow(i);
    }
  }
  this.deleteTotalRow();
  this.addTotalRow();

  var fileCell = $('fileRow' + file.id);
  if (fileCell) {
    fileCell.innerHTML = '<img id="rIcon'+ id +'" src="'+ this.addImageUrl +'" alt="Add '+ file.name + '" onclick="downloadMgr.addDownloadRow(' + id + '); return false;"/>';
  }
}

DownloadMgr.prototype.resetDownloadInfo = function() {
  for (id in this.downloadFiles) {
      this.removeRow(id);
  }
  this.totalDownloadSize = 0;
  this.cleanupDownloadQueue();
}

DownloadMgr.prototype.cleanupDownloadQueue = function() {
  var tbl = $('downloadTbl');
  while (tbl.rows.length > 1) {
    tbl.deleteRow(1);
  }
  this.deleteTotalRow();
  this.addTotalRow();
  this.hideQueue=true;
}

DownloadMgr.prototype.resetAllFiles = function() {
  this.files = new Object();
}

DownloadMgr.prototype.deleteTotalRow = function() {
    var tbl = $('downloadTbl');
    var lastRow = tbl.rows.length;
    if (lastRow > 1) {
      tbl.deleteRow(lastRow - 1);
    }
  }

DownloadMgr.prototype.addTotalRow = function() {
  var tbl = $('downloadTbl');
  var row = tbl.insertRow(-1);
  var cell = row.insertCell(0);
  var numFiles = 0;
  if (Object.values(this.downloadFiles).length != undefined) {
    numFiles = Object.values(this.downloadFiles).length;
  }
  var textNode = document.createTextNode(numFiles + " Files, Job Size: "+formatFileSize(this.totalDownloadSize));
  cell.appendChild(textNode);
}

DownloadMgr.prototype.toggleQueue = function() {
    var newStatus = !this.hideQueue;
    this.cleanupDownloadQueue();
    this.hideQueue = newStatus;

    if (newStatus) {
        var href = $('toggleQueue');
        href.innerHTML='Show Files';
    } else {
        this.totalDownloadSize = 0;
        this.reAddFromQueue();
        var href = $('toggleQueue');
        href.innerHTML='Hide Files';
    }
}

DownloadMgr.prototype.doDownloadFiles = function() {
  var files = Object.values(this.downloadFiles);
  if (files.length == 0) {
    alert("Select file(s) first.");
    return;
  }

  var form = document.createElement("form");
  form.method="post";
  form.style.display="none";
  form.action=this.downloadUrl;
  for (i = 0; i < files.length; ++i) {
      var elt = document.createElement("input");
      elt.type="hidden";
      elt.name="selectedFileIds";
      elt.value=files[i].id;
      form.appendChild(elt);
  }
  document.body.appendChild(form);
  form.submit();
  $(form).remove();
  this.resetDownloadInfo();

}

DownloadMgr.prototype.addAll = function() {
    this.deleteTotalRow();
    var tbl = $('downloadTbl');
    for (id in this.files) {
        if (!this.downloadFiles[id]) {
            this.doAddDownloadRow(id, false, tbl, false);
        }
    }
    this.displayAllRemoveIcons();
    this.addTotalRow();
}

DownloadMgr.prototype.displayAllRemoveIcons = function() {
  var icons = document.getElementById('datatable_downloadFilesListTable').getElementsByTagName('img');
  for(i=0;i<icons.length;i++) {
    if(icons[i].id.lastIndexOf("rIcon") != -1) {
      icons[i].src = this.removeImageUrl;
      icons[i].alt = "remove";
      var dlMgr = this;
      icons[i].onclick= function() {
        var fid = this.id.substring(5,this.id.length);
        dlMgr.removeRow(fid);return false;
      };
    }
     }
}

DownloadMgr.prototype.reAddFromQueue = function() {
    this.deleteTotalRow();
    var tbl = $('downloadTbl');
    for (id in this.files) {
        if (this.downloadFiles[id]) {
            this.doAddDownloadRow(id, false, tbl, true);
        }
    }
    this.addTotalRow();
}

DownloadMgr.prototype.reApplySelection = function(id, highlightRow) {
    this.deleteTotalRow();
        for (id in this.files) {
            if (this.downloadFiles[id]) {
                 var file = this.files[id];
           this.downloadFiles[id] = file;

          if (false) {
              new Effect.Highlight($('fileRow' + file.id).up('tr'));
          }

          var fileCell = $('fileRow' + file.id)
          if (fileCell) {
            fileCell.innerHTML = '<img id="rIcon'+ id +'" src="'+ this.removeImageUrl + '" alt="remove" onclick="downloadMgr.removeRow(' + id + '); return false;"/>';
             }
            }
        }
    this.addTotalRow();
}

function AssociationPicker(baseId, associatedEntityName, entityName, projectId, itemId, url) {
  this.baseId = baseId;
  this.associatedEntityName = associatedEntityName;
  this.entityName = entityName;
  this.projectId = projectId;
  this.itemId = itemId;
  this.url = url;
}


var AssociationPickerUtils = {
    processSelection : function(selectedItem, baseId, associatedEntityName) {
        var id = selectedItem.firstChild.value;
        if (id == null || id == '') {
            return;
        }
        if ($(baseId + 'SelectedItemDiv').getElementsBySelector('input[value="' + id + '"]').length  > 0) {
            alert(associatedEntityName + ' already selected.');
            return;
        }

        var itemsToRemove = $(baseId + 'ItemsToRemove').getElementsBySelector('input[value="' + id + '"]');
        var found = false;
        if (itemsToRemove.length  > 0) {
            Element.remove(itemsToRemove[0]);
            found = true;
        }

        var newItem = document.createElement("li");
        var newInput = selectedItem.childNodes[0].cloneNode(false);
        newInput.name = (found) ? '' : 'itemsToAssociate';
        newItem.appendChild(newInput);
        var newText = selectedItem.childNodes[1].cloneNode(false);
        newItem.appendChild(newText);
        newItem.onclick = function() {AssociationPickerUtils.removeSelection(this, baseId);}
        $(baseId + 'SelectedItemDiv').appendChild(newItem);
    },

    removeSelection : function(selectedItem, baseId) {
        var inputName = selectedItem.firstChild.name;
        var id = selectedItem.firstChild.value;
        if (inputName != 'itemsToAssociate') {
            var newItem = selectedItem.firstChild.cloneNode(true);
            newItem.name = 'itemsToRemove';
            $(baseId + 'ItemsToRemove').appendChild(newItem);
        }
        Element.remove(selectedItem);
    },

    createAutoUpdater : function(baseId, url, projectId, entityName, itemId, associatedEntityName) {
        autoUpdater =  new Ajax.Autocompleter(baseId + "AssociatedValueName", baseId +"AutocompleteDiv", url,
            {paramName: "associatedValueName", minChars: '0', indicator: baseId + 'ProgressMsg', frequency: 0.75,
             updateElement: function(selectedItem) {AssociationPickerUtils.processSelection(selectedItem, baseId, associatedEntityName);},
             onHide: function() {}, onShow: function() {},
             parameters: 'project.id=' + projectId + '&current' + entityName + '.id=' + itemId });
        Element.show(autoUpdater.update);
        autoUpdater.activate();
        return autoUpdater;
    }
}

var ListPickerUtils = {
    processSelection : function(selectedItem, baseId, listLabel, listFieldName, multiple, allowReordering, autoUpdater) {
        var id = selectedItem.firstChild.value;
        var selectedItemValues = $(baseId + 'SelectedItemValues');
        var currentCounter = 0;
        var currentId = null;
        if (id == null || id == '') {
            return;
        }

        if (multiple != 'true') {
            for(var i = 0; i < selectedItemValues.length; i++) {
                currentId = selectedItemValues.options[i].value;
                if(currentId != null && currentId != '') {
                    currentCounter++;
                }
            }

            if (currentCounter  >= 1) {
                alert('Only one ' + listLabel + ' may be selected.');
                return;
            }
        }
        for(var i = 0; i < selectedItemValues.length; i++) {
            if(id == selectedItemValues.options[i].value) {
                alert(listLabel + ' already selected.');
                return;
            }
        }

        var newItem = document.createElement("li");
        var option = new Option(selectedItem.childNodes[0].value, selectedItem.childNodes[0].value);
        var newText = selectedItem.childNodes[1].cloneNode(false);
        newItem.id = baseId + '_' + id;
        option.selected = true;
        selectedItemValues.options[selectedItemValues.length] = option;
        newItem.appendChild(newText);
        newItem.onclick = function() { ListPickerUtils.removeSelection(this, autoUpdater, baseId); };
        $(baseId + 'SelectedItemDiv').appendChild(newItem);

        fireEvent(selectedItemValues, "change", "onchange");

        if (allowReordering) {
            Sortable.create(baseId + 'SelectedItemDiv', { starteffect: function() { autoUpdater.sortableReordered = true; } });
        }
    },

    removeSelection : function(selectedItem, autoUpdater, baseId) {
        var selectedItemValues = $(baseId + 'SelectedItemValues');
        if (!autoUpdater.sortableReordered) {
            for(var i = 0; i < selectedItemValues.length; i++) {
                if(selectedItem.id == baseId + '_' + selectedItemValues.options[i].value) {
                    selectedItemValues.removeChild(selectedItemValues.options[i]);
                }
            }
            Element.remove(selectedItem);
            fireEvent(selectedItemValues, "change", "onchange");
        } else {
            autoUpdater.sortableReordered = false;
        }
    },

    forceUpdate : function(autoUpdater) {
        Element.show(autoUpdater.update);
        autoUpdater.activate();
    },

    parseParams : function(paramNames, paramValues) {
        var params = '';
        if (paramNames != null && paramNames != '' && paramValues != null && paramValues != '') {
            var paramNamesArray = paramNames.split(',');
            var paramValuesArray = paramValues.split(',');
            for (var i = 0; i < paramNamesArray.length; i++) {
                if (i > 0) {
                    params += "&";
                }
                params += paramNamesArray[i] + "=" + paramValuesArray[i];
            }
        }
        return params;
    },

    updateParams : function(autoUpdater, paramNames, paramValues) {
        autoUpdater.options.defaultParams = ListPickerUtils.parseParams(paramNames, paramValues);
    },

    // paramNames and paramValues are comma-delimited lists
    createAutoUpdater : function(baseId, url, listLabel, filterFieldName, listFieldName, multiple, paramNames, paramValues, allowReordering) {
        var params = ListPickerUtils.parseParams(paramNames, paramValues);
        autoUpdater =  new Ajax.Autocompleter(baseId + "SearchInput", baseId +"AutocompleteDiv", url,
            {paramName: filterFieldName, minChars: '0', indicator: baseId + 'ProgressMsg', frequency: 0.75,
             updateElement: function(selectedItem) {ListPickerUtils.processSelection(selectedItem, baseId, listLabel, listFieldName, multiple, allowReordering, this);},
             onHide: function() {}, onShow: function() {},
             parameters: params});
        Element.show(autoUpdater.update);
        autoUpdater.activate();
        return autoUpdater;
    }
}

// Opens a window to caArray user's guide.
function openHelpWindow() {
    // Get the URL
    var pageURL = "https://wiki.nci.nih.gov/x/LBo9Ag";
    window.open (pageURL,"caArray_User_Guide", "status,scrollbars,resizable");
}

function formatFileSize(value) {
    var returnVal="";

    if (value >= 1099511627776) {
        value *= 10;
        returnVal = (Math.round(value / 1099511627776)/10) + " TB";
    } else if (value >= 1073741824 ) {
        value *= 10;
        returnVal = (Math.round(value / 1073741824)/10) + " GB";
    } else if (value >= 1048576) {
        value *= 10;
        returnVal = (Math.round(value / 1048576)/10) + " MB";
    } else if (value >= 1024) {
        value *= 10;
        returnVal = (Math.round(value / 1024)/10) + " KB";
    } else {
        returnVal = value + " Bytes";
    }

    return returnVal;
}
// Fires an event for the element passed in.
// Params:
//          1. eventElement - If contextName is not blank or null, the topic will be appended.
//          2. includeNav - If true, the left navigation pane appears. Else, the navigation pane is not displayed.
function fireEvent(eventElement, firefoxEvent, ieEvent) {
    if(document.createEvent) {
        var e = document.createEvent('Events');
        e.initEvent(firefoxEvent, true, true);
    }
    else if(document.createEventObject) {
        var e = document.createEventObject();
    }

    if(eventElement.dispatchEvent) {
        eventElement.dispatchEvent(e);
    }
    else if(eventElement.fireEvent) {
        eventElement.fireEvent(ieEvent, e);
    }
}

(function($){
    $(function() {
        $('body').delegate('select[name="permSampleSearch"]', 'change', function() {
            if( $(this).val() == 'SAMPLE_ARBITRARY_CHARACTERISTIC' ) {
            	$('#characteristic_category_dropdown_id').show();
            } else {
            	$('#characteristic_category_dropdown_id').hide();
            }
        });
    });
})(jQuery);


