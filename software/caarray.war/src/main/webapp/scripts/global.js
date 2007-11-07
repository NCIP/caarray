/* This function is used to change the style class of an element */
function swapClass(obj, newStyle) {
    obj.className = newStyle;
}

function isUndefined(value) {
    var undef;
    return value == undef;
}

function checkAll(theForm) { // check all the checkboxes in the list
  for (var i=0;i<theForm.elements.length;i++) {
    var e = theForm.elements[i];
    var eName = e.name;
      if (eName != 'allbox' &&
            (e.type.indexOf("checkbox") == 0)) {
          e.checked = theForm.allbox.checked;
    }
  }
}

/* Function to clear a form of all it's values */
function clearForm(frmObj) {
  for (var i = 0; i < frmObj.length; i++) {
        var element = frmObj.elements[i];
    if(element.type.indexOf("text") == 0 ||
        element.type.indexOf("password") == 0) {
          element.value="";
    } else if (element.type.indexOf("radio") == 0) {
      element.checked=false;
    } else if (element.type.indexOf("checkbox") == 0) {
      element.checked = false;
    } else if (element.type.indexOf("select") == 0) {
      for(var j = 0; j < element.length ; j++) {
        element.options[j].selected=false;
      }
            element.options[0].selected=true;
    }
  }
}

/* Function to get a form's values in a string */
function getFormAsString(frmObj) {
    var query = "";
  for (var i = 0; i < frmObj.length; i++) {
        var element = frmObj.elements[i];
        if (element.type.indexOf("checkbox") == 0 ||
            element.type.indexOf("radio") == 0) {
            if (element.checked) {
                query += element.name + '=' + escape(element.value) + "&";
            }
    } else if (element.type.indexOf("select") == 0) {
      for (var j = 0; j < element.length ; j++) {
        if (element.options[j].selected) {
                    query += element.name + '=' + escape(element.value) + "&";
                }
      }
        } else {
            query += element.name + '='
                  + escape(element.value) + "&";
        }
    }
    return query;
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

/* Helper function for re-ordering options in a select */
function opt(txt,val,sel) {
    this.txt=txt;
    this.val=val;
    this.sel=sel;
}

/* Function for re-ordering <option>'s in a <select> */
function move(list,to) {
    var total=list.options.length;
    index = list.selectedIndex;
    if (index == -1) return false;
    if (to == +1 && index == total-1) return false;
    if (to == -1 && index == 0) return false;
    to = index+to;
    var opts = new Array();
    for (i=0; i<total; i++) {
        opts[i]=new opt(list.options[i].text,list.options[i].value,list.options[i].selected);
    }
    tempOpt = opts[to];
    opts[to] = opts[index];
    opts[index] = tempOpt
    list.options.length=0; // clear

    for (i=0;i<opts.length;i++) {
        list.options[i] = new Option(opts[i].txt,opts[i].val);
        list.options[i].selected = opts[i].sel;
    }

    list.focus();
}

/*  This function is to select all options in a multi-valued <select> */
function selectAll(elementId) {
    var element = document.getElementById(elementId);
  len = element.length;
  if (len != 0) {
    for (i = 0; i < len; i++) {
      element.options[i].selected = true;
    }
  }
}

/* This function is used to select a checkbox by passing
 * in the checkbox id
 */
function toggleChoice(elementId) {
    var element = document.getElementById(elementId);
    if (element.checked) {
        element.checked = false;
    } else {
        element.checked = true;
    }
}

/* This function is used to select a radio button by passing
 * in the radio button id and index you want to select
 */
function toggleRadio(elementId, index) {
    var element = document.getElementsByName(elementId)[index];
    element.checked = true;
}

/* This function is used to open a pop-up window */
function openWindow(url, winTitle, winParams) {
  winName = window.open(url, winTitle, winParams);
    winName.focus();
}


/* This function is to open search results in a pop-up window */
function openSearch(url, winTitle) {
    var screenWidth = parseInt(screen.availWidth);
    var screenHeight = parseInt(screen.availHeight);

    var winParams = "width=" + screenWidth + ",height=" + screenHeight;
        winParams += ",left=0,top=0,toolbar,scrollbars,resizable,status=yes";

    openWindow(url, winTitle, winParams);
}

/* This function is used to set cookies */
function setCookie(name,value,expires,path,domain,secure) {
  document.cookie = name + "=" + escape (value) +
    ((expires) ? "; expires=" + expires.toGMTString() : "") +
    ((path) ? "; path=" + path : "") +
    ((domain) ? "; domain=" + domain : "") + ((secure) ? "; secure" : "");
}

/* This function is used to get cookies */
function getCookie(name) {
  var prefix = name + "="
  var start = document.cookie.indexOf(prefix)

  if (start==-1) {
    return null;
  }

  var end = document.cookie.indexOf(";", start+prefix.length)
  if (end==-1) {
    end=document.cookie.length;
  }

  var value=document.cookie.substring(start+prefix.length, end)
  return unescape(value);
}

/* This function is used to delete cookies */
function deleteCookie(name,path,domain) {
  if (getCookie(name)) {
    document.cookie = name + "=" +
      ((path) ? "; path=" + path : "") +
      ((domain) ? "; domain=" + domain : "") +
      "; expires=Thu, 01-Jan-70 00:00:01 GMT";
  }
}

// This function is for stripping leading and trailing spaces
String.prototype.trim = function () {
    return this.replace(/^\s*(\S*(\s+\S+)*)\s*$/, "$1");
};

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

// This function is a generic function to create form elements
function createFormElement(element, type, name, id, value, parent) {
    var e = document.createElement(element);
    e.setAttribute("name", name);
    e.setAttribute("type", type);
    e.setAttribute("id", id);
    e.setAttribute("value", value);
    parent.appendChild(e);
}

function confirmDelete(obj) {
    var msg = "Are you sure you want to delete this " + obj + "?";
    ans = confirm(msg);
    if (ans) {
        return true;
    } else {
        return false;
    }
}

function highlightTableRows(tableId) {
    var previousClass = null;
    var table = document.getElementById(tableId);
    var tbody = table.getElementsByTagName("tbody")[0];
    var rows;
    if (tbody == null) {
        rows = table.getElementsByTagName("tr");
    } else {
        rows = tbody.getElementsByTagName("tr");
    }
    // add event handlers so rows light up and are clickable
    for (i=0; i < rows.length; i++) {
        rows[i].onmouseover = function() { previousClass=this.className;this.className+=' over' };
        rows[i].onmouseout = function() { this.className=previousClass };
        rows[i].onclick = function() {
            var cell = this.getElementsByTagName("td")[0];
            var link = cell.getElementsByTagName("a")[0];
            location.href = link.getAttribute("href");
            this.style.cursor="wait";
            return false;
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

function radio(clicked){
    var form = clicked.form;
    var checkboxes = form.elements[clicked.name];
    if (!clicked.checked || !checkboxes.length) {
        clicked.parentNode.parentNode.className="";
        return false;
    }

    for (i=0; i<checkboxes.length; i++) {
        if (checkboxes[i] != clicked) {
            checkboxes[i].checked=false;
            checkboxes[i].parentNode.parentNode.className="";
        }
    }

    // highlight the row
    clicked.parentNode.parentNode.className="over";
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

    preFunction : function() {
        if (TabUtils.savedFormData != null && $('projectForm') && TabUtils.savedFormData != Form.serialize('projectForm')) {
            if (!confirm('There are unsaved changed in your form, are you sure you want to continue?')) {
                return false;
            }
        }
        TabUtils.showLoadingText();
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

    showLoadingText : function() {
        if ($('loadingText')) {
            $('loadingText').show();
            $('theForm').hide();
        }
        if ($('tabHeader')) {
            $('tabHeader').hide();
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

    wrapTabLinks: function(projectId) {
        var oldTabLinkMethod = executeAjaxTab_tabs;
        executeAjaxTab_tabs = function(link, cls, url, arg4) {
            url = url.replace(/project\.id=/g, 'project.id=' + projectId);
            oldTabLinkMethod(link, cls, url, arg4);
        }
        if (window.executeAjaxTab_tablevel2) {
            var oldTabLevel2LinkMethod = executeAjaxTab_tablevel2;
            executeAjaxTab_tablevel2 = function(link, cls, url, arg4) {
                url = url.replace(/project\.id=/g, 'project.id=' + projectId);
                oldTabLevel2LinkMethod(link, cls, url, arg4);
            }
        }
    },

    loadLinkInTab: function(tabCaption, url) {
        executeAjaxTab_tabs(this.findTabByCaption('tabs', tabCaption), 'active', url, '');
    },

    loadLinkInSubTab: function(tabCaption, url) {
        executeAjaxTab_tablevel2(this.findTabByCaption('tablevel2', tabCaption), 'selected', url, '');
    },

    findTabByCaption: function(containerId, caption) {
        tabMenuItems = $(containerId).getElementsByTagName('li');
        for(var i = 0; i < tabMenuItems.length; i++) {
            tabLink = tabMenuItems[i].getElementsByTagName('a')[0];
            if (tabLink.innerHTML == caption) {
                return tabLink;
            }
        }
        return tabMenuItems[0].getElementsByTagName('a')[0];
    }
}

var PermissionUtils = {
    SAMPLE_LIST_ID: 'access_profile_samples',
    SAMPLE_DROPDOWN_SELECTOR: 'select.sample_security_level',
    PROFILE_LOADING_ID: 'access_profile_loading',
    PROFILE_SAVING_ID: 'access_profile_saving',
    PROFILE_INSTRUCTIONS_ID: 'access_profile_instructions',
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
        $(PermissionUtils.PROFILE_INSTRUCTIONS_ID).hide();
        $(PermissionUtils.PROFILE_LOADING_ID).show();
        new Ajax.Updater(PermissionUtils.PROFILE_DETAILS_ID, url,
              {evalScripts: true, onComplete: function() { $(PermissionUtils.PROFILE_LOADING_ID).hide();}});
        return false;
    },
        
    cancelEditProfile: function() {
        $(PermissionUtils.PROFILE_DETAILS_ID).update(''); 
        $(PermissionUtils.PROFILE_INSTRUCTIONS_ID).show(); 
    },
    
    saveProfile: function() {
        $(PermissionUtils.PROFILE_SAVING_ID).show(); 
        Caarray.submitAjaxForm(PermissionUtils.PROFILE_FORM_ID, PermissionUtils.PROFILE_DETAILS_ID);
    }            
}

//
// Download stuff here
//

function DownloadMgr(dUrl, iUrl) {
  this.downloadUrl = dUrl;
  this.imageUrl = iUrl;
  this.downloadIds = new Array();
  this.downloadSizeArray = new Array();
  this.count = 0;

  this.allIds = new Array();
  this.allNames = new Array();
  this.allSizes = new Array();
}

DownloadMgr.prototype.downloadUrl;
DownloadMgr.prototype.imageUrl;
DownloadMgr.prototype.downloadIds;
DownloadMgr.prototype.downloadSizeArray;
DownloadMgr.prototype.count;
// These three support the "Add All" functionality.
DownloadMgr.prototype.allIds;
DownloadMgr.prototype.allNames;
DownloadMgr.prototype.allSizes;

DownloadMgr.prototype.addDownloadRow = function(name, id, size) {
  this.addDownloadRow(name, id, size, null);
}

DownloadMgr.prototype.addDownloadRow = function(name, id, size, doAlert) {
    for (i = 0; i < this.downloadIds.length; ++i) {
      if (this.downloadIds[i] == id) {
        if (doAlert == null) {
          alert('File ' + name + ' already in queue.');
        }
        return;
      }
    }
    this.downloadIds.push(id);
    this.downloadSizeArray.push(size);
    ++this.count;

  var tbl = document.getElementById('downloadTbl');
  var lastRow = tbl.rows.length;
  var row = tbl.insertRow(lastRow - 1);
  row.id = 'downloadRow' + this.count;

  var cell = row.insertCell(0);
  cell.innerHTML = '<img src="'+ this.imageUrl + '" alt="remove" onclick="downloadMgr.removeRow(' + this.count + ')"/>&nbsp;&nbsp;' + name;

  this.addTotalRow();
}

DownloadMgr.prototype.removeRow = function(toRemove) {
  var tbl = document.getElementById('downloadTbl');
  var row = document.getElementById('downloadRow' + toRemove);
  for (i = 0; i < tbl.rows.length; ++i) {
    if (tbl.rows[i] == row) {
      tbl.deleteRow(i);
      this.downloadSizeArray.splice(i - 1, 1);
      this.downloadIds.splice(i - 1, 1);
    }
  }
  this.addTotalRow();
}

DownloadMgr.prototype.resetDownloadInfo = function() {
  this.downloadIds = new Array();
  this.downloadSizeArray = new Array();
  var tbl = document.getElementById('downloadTbl');
  while (tbl.rows.length > 1) {
    tbl.deleteRow(1);
  }
  this.addTotalRow();
}

DownloadMgr.prototype.addTotalRow = function() {
  var tbl = document.getElementById('downloadTbl');
  var lastRow = tbl.rows.length;
  if (lastRow > 1) {
    tbl.deleteRow(lastRow - 1);
  }
  lastRow = tbl.rows.length;
  var row = tbl.insertRow(lastRow);
  var cell = row.insertCell(0);
  var downloadSize = 0;
  for (i = 0; i < this.downloadSizeArray.length; ++i) {
    downloadSize += this.downloadSizeArray[i];
  }
  var textNode = document.createTextNode(" Job Size: " + (downloadSize / 1024 | 0) + " KB");
  cell.appendChild(textNode);
}

DownloadMgr.prototype.doDownloadFiles = function() {
  if (this.downloadIds.length == 0) {
    alert("Select file(s) first.");
    return;
  }
  var curLoc = window.location;
  var params = '';
  for (i = 0; i < this.downloadIds.length; ++i) {
    if (i != 0) {
      params = params + '&';
    }
    params = params + 'selectedFiles=' + this.downloadIds[i];
  }
  window.location= this.downloadUrl + "?" + params;
  this.resetDownloadInfo();
}

DownloadMgr.prototype.addAll = function() {
  for (j = 0; j < this.allNames.length; j++) {
    this.addDownloadRow(this.allNames[j], this.allIds[j], this.allSizes[j], false);
  }
}

DownloadMgr.prototype.populateAll = function(name, id, size) {
  this.allNames.push(name);
  this.allIds.push(id);
  this.allSizes.push(size);
}


