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
    submitAjaxForm: function(formId, divId, options) {
        var formData = Form.serialize(formId);
        options = options || {};
        if (options.extraArgs) {
            formData = formData + '&' + Hash.toQueryString(options.extraArgs);
        }
        var url = options.url || $(formId).action;
        new Ajax.Updater(divId, url, {parameters: formData, evalScripts: true, insertion: options.insertion} );
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
        tabMenuItems = $('tabbed').getElementsByTagName('li');
        for(var i = 0; i < tabMenuItems.length; i++) {
            tabLink = tabMenuItems[i].getElementsByTagName('a')[0];
            if(tabLink.className == 'current') {
                tabMenuItems[i].className = 'active';
            } else {
                tabMenuItems[i].className = '';
            }
            tabLink.blur();
        }
    },

    setSelectedLevel2Tab : function() {
        tabMenuItems = $('tablevel2').getElementsByTagName('li');
        for(var i = 0; i < tabMenuItems.length; i++) {
            tabLink = tabMenuItems[i].getElementsByTagName('a')[0];
            if(tabLink.className == 'current') {
                tabMenuItems[i].className = 'selected';
            } else {
                tabMenuItems[i].className = '';
            }
            tabLink.blur();
        }
    },

    showSubmittingText : function() {
        if ($('submittingText')) {
            $('submittingText').show();
            $('theForm').hide();
        }
    },

    showLoadingText : function(keepMainContent) {
        var elts = document.getElementsByClassName('loadingText');
        var loadingElt = elts.length > 0 ? elts[0] : null;
        if (loadingElt) {
      //DOES NOT WORK IN IE -- only works in Safari, Firebug, Opera (commented out)
            //console.log("showing existing loading text");
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
        //DOES NOT WORK IN IE -- only works in Safari, Firebug, Opera (commented out)
                //console.log("creating new loading text");
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

    hideLoadingText : function() {
        document.getElementsByClassName('loadingText').each(function(elt) { $(elt).hide(); });
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
        $(PermissionUtils.PROFILE_DETAILS_ID).update(this.oldDetails);
    },

    saveProfile: function() {
        $(PermissionUtils.PROFILE_SAVING_ID).show();
        Caarray.submitAjaxForm(PermissionUtils.PROFILE_FORM_ID, PermissionUtils.PROFILE_DETAILS_ID);
    }
}

//
// Download stuff here
//

function DownloadMgr(dUrl, removeImageUrl) {
  this.downloadUrl = dUrl;
  this.removeImageUrl = removeImageUrl;
  this.files = new Object();
  this.downloadFiles = new Object();
  this.totalDownloadSize = 0;
}

DownloadMgr.prototype.addFile = function(name, id, size) {
    this.files[id] = { name: name, id: id, size: size };
  }

DownloadMgr.prototype.addDownloadRow = function(id) {
    if (this.downloadFiles[id]) {
        if (doAlert == null) {
          alert('File ' + name + ' already in queue.');
        }
        return;
    }
    
  this.deleteTotalRow();
  this.doAddDownloadRow(id, true);
  this.addTotalRow();
}

DownloadMgr.prototype.doAddDownloadRow = function(id, highlightRow) {
    
    var file = this.files[id];
    this.downloadFiles[id] = file;
    this.totalDownloadSize += file.size;

  var tbl = $('downloadTbl');
  var row = tbl.insertRow(-1);
  row.id = 'downloadRow' + id;

  var cell = row.insertCell(0);
  cell.innerHTML = '<img src="'+ this.removeImageUrl + '" alt="remove" onclick="downloadMgr.removeRow(' + id + '); return false;"/>&nbsp;&nbsp;' + file.name;

  if (highlightRow) {      
      new Effect.Highlight($('fileRow' + file.id).up('tr'));
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
}

DownloadMgr.prototype.resetDownloadInfo = function() {
  for (id in this.downloadFiles) {
      delete this.downloadFiles[id];
  }
  this.totalDownloadSize = 0;
  var tbl = $('downloadTbl');
  while (tbl.rows.length > 1) {
    tbl.deleteRow(1);
  }
  this.deleteTotalRow();
  this.addTotalRow();
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
  var textNode = document.createTextNode(" Job Size: " + (this.totalDownloadSize / 1024 | 0) + " KB");
  cell.appendChild(textNode);
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
    for (id in this.files) {
        this.doAddDownloadRow(id, false);
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
        if (id == null || id == '') {
            return;
        }
        if (multiple != 'true') {
            if ($(baseId + 'SelectedItemDiv').getElementsBySelector('input').length  > 0) {
                alert('Only one ' + listLabel + ' may be selected.');
                return;
            }
        }
        if ($(baseId + 'SelectedItemDiv').getElementsBySelector('input[value="' + id + '"]').length  > 0) {
            alert(listLabel + ' already selected.');
            return;
        }

        var newItem = document.createElement("li");
        newItem.id = baseId + '_' + id;
        var newInput = selectedItem.childNodes[0].cloneNode(false);
        newInput.name = listFieldName;
        newItem.appendChild(newInput);
        var newText = selectedItem.childNodes[1].cloneNode(false);
        newItem.appendChild(newText);
        newItem.onclick = function() { ListPickerUtils.removeSelection(this, autoUpdater); };
        $(baseId + 'SelectedItemDiv').appendChild(newItem);

        if (allowReordering) {
            Sortable.create(baseId + 'SelectedItemDiv', {onUpdate: function() { autoUpdater.sortableReordered = true; } });
        }
    },

    removeSelection : function(selectedItem, autoUpdater) {
        if (!autoUpdater.sortableReordered) {
            Element.remove(selectedItem);
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

// Help Window functions
// Pre Requisite
// 1. The variable "contextPath" is set to an appropriate value in the JSP page including this javascript file.

// Constant values
var helpURL = "/help/index.html";
var helpContext = "caArray_Online_Help";
var context = "context="+helpContext;


// Opens a new window with Left Navigation.
function openHelpWindowWithNavigation(contextName) {
    // Get the URL
    var pageURL = makeHelpURL(contextName, true);
    openWin(pageURL);
}

// Opens a new window with Left Navigation.
function openHelpWindow(contextName) {
    openHelpWindowWithNavigation (contextName);
}

// Opens a new window without Left Navigation.
function openHelpWindowWithoutNavigation(contextName) {
    // Get the URL
    var pageURL = makeHelpURL(contextName, false);
    openWin(pageURL);
}

// Actually opens the help window
function openWin(pageURL) {
    window.open (pageURL,"Help", "status,scrollbars,resizable,width=800,height=500");
}


// Create the full URL.
// Params:
//          1. contextName - If contextName is not blank or null, the topic will be appended.
//          2. includeNav - If true, the left navigation pane appears. Else, the navigation pane is not displayed.

function makeHelpURL(contextName, includeNav) {
    // Do not display the navigation pane by default.
    var nav = "single=true";
    if (includeNav) {
        // if includeNav = true, then display the navigation pane.
        nav = "single=false";
    }

    var fullURL = contextPath + helpURL + "?" + nav + "&" + context;
    if ((contextName != null) && (contextName != "")) {
        fullURL += "&topic=" + contextName;
    }

    return fullURL;
}
