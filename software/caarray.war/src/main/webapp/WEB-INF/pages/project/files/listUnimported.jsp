<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/ajax/project/files/listUnimportedForm.action" var="listUnimportedFormUrl" />
<c:url value="/protected/ajax/project/files/upload/uploadInBackground.action" var="uploadInBackgroundUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>

<c:url value="/ajax/project/listTab/Samples/jsonList.action" var="samplesJsonListUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>
<c:url value="/ajax/project/listTab/Hybridizations/jsonList.action" var="hybsJsonListUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>

<c:url value="/ajax/project/files/importTreeNodesJson.action" var="nodesJsonUrl"/>
<c:url value="/protected/ajax/project/files/validateSelectedImportFiles.action" var="validateImportFilesUrl"/>

<script type="text/javascript">
    fileTypeLookup = new Object();
    fileNameLookup = new Object();
    fileSizeLookup = new Object();
    fileStatusLookup = new Object();
    jobSize = 0;
    jobNumFiles = 0;
    SDRF_FILE_TYPE = '<s:property value="@gov.nih.nci.caarray.domain.file.FileTypeRegistry@MAGE_TAB_SDRF"/>';
    IDF_FILE_TYPE = '<s:property value="@gov.nih.nci.caarray.domain.file.FileTypeRegistry@MAGE_TAB_IDF"/>';
    GPR_FILE_TYPE = '<s:property value="@gov.nih.nci.caarray.plugins.genepix.GprHandler@GPR_FILE_TYPE"/>';
    UPLOADING_FILE_STATUS = '<s:property value="@gov.nih.nci.caarray.domain.file.FileStatus@UPLOADING"/>';
    <c:forEach items="${files}" var="file">
    fileTypeLookup['${file.id}'] = '${file.fileType}';
    fileNameLookup['${file.id}'] = '${caarrayfn:escapeJavaScript(file.name)}';
    fileSizeLookup['${file.id}'] = ${file.uncompressedSize};
    fileStatusLookup['${file.id}'] = '${file.fileStatus}';
    </c:forEach>
    <c:forEach items="${selectedFileIds}" var="sid">
      <c:if test="${sid.class.name == 'java.lang.Long'}">
      if(${sid}) {
        toggleFileInJob(true, ${sid});
      }
      </c:if>
    </c:forEach>

    toggleAllFiles = function(checked, theform) {
        numElements = theform.elements.length;
        for (i = 0; i < numElements; i++) {
             var element = theform.elements[i];
             if ("checkbox" == element.type && element.checked != checked && element.id.indexOf('chk') >= 0) {
                 element.checked = checked;
                 toggleFileInJob(checked, element.value);
             }
        }
    }

    toggleFileInJob = function(checked, id) {
        var size = fileSizeLookup[id];
    if (checked) {
      jobNumFiles++;
      jobSize += size;
    } else {
      jobNumFiles--;
      jobSize -= size;
    }
    updateJobSummary();
    }

    updateJobSummary = function() {
        var jobSizeContent = $('jobSizeContent');
        if(jobSizeContent) {
            jobSizeContent.innerHTML= jobNumFiles + " Files, " +formatFileSize(jobSize);
        }
    }

  checkRefFileSelection = function(findRefUrl) {
    var count = 0;
    numElements = $('selectFilesForm').elements.length;
    for (i = 0; i < numElements; i++) {
         var element = $('selectFilesForm').elements[i];
         if ("checkbox" == element.type && element.checked && element.id.indexOf('chk') >= 0) {
                count++;
                var type = fileTypeLookup[element.value];
                if (type != IDF_FILE_TYPE) {
                  alert('Only an IDF file may be selected.');
                  return false;
                }
                if (fileStatusLookup[element.value] == UPLOADING_FILE_STATUS) {
                  alert('A selected file is still uploading.');
                  return false;
                }
         }
    }

    if (count > 1) {
      alert('Only one IDF file may be selected.');
      return false;
     }

    return TabUtils.submitTabFormToUrl('selectFilesForm', findRefUrl, 'tabboxlevel2wrapper');
  }

  deleteFiles = function(deleteUrl) {
      var formElts = document.getElementById('datatable').getElementsByTagName('input');
      var hasUploadingFile = $A(formElts).any(function(elt) {
          return elt.checked && (elt.id.lastIndexOf('chk') > -1) && fileStatusLookup[elt.value] == UPLOADING_FILE_STATUS;
      });
      if (hasUploadingFile && !confirm("A file you have selected is still in the Uploading status."
              + " Attempting to delete a file in the middle of an upload could result in a corrupted file."
              + " Are you sure you want to delete the selected file(s)?")) {
          return false;
      }

      return TabUtils.submitTabFormToUrl('selectFilesForm', deleteUrl, 'tabboxlevel2wrapper');
  }
  
  passOnSelectedFiles = function(findRefUrl) {
    var checkboxes = $('selectFilesForm').selectedFileIds || {};
    var params = new Object();
    params['selectedFileIds'] = new Array();
    for (i = 0; i < checkboxes.length; ++i) {
      if (checkboxes[i].checked) {
        params['selectedFileIds'].push(checkboxes[i].value);
      }
    }
    TabUtils.showSubmittingText();
    Caarray.submitAjaxForm('selectFilesForm', 'tabboxlevel2wrapper', { url: findRefUrl, extraArgs: params});
  }

    unimportedFilterCallBack = function() {
        TabUtils.hideLoadingText();
    }

    doFilter = function() {
    	jobNumFiles = 0;
        jobSize = 0;
        var checkboxIds = $('selectFilesForm').__checkbox_selectedFileIds || {};
        TabUtils.disableFormCheckboxes(checkboxIds);
        TabUtils.showLoadingTextKeepMainContent();
        Caarray.submitAjaxForm('selectFilesForm', 'unimportedForm', {url: '${listUnimportedFormUrl}', onComplete: unimportedFilterCallBack});
    }

    openUploadWindow = function() {
        uploadWindow = window.open('${uploadInBackgroundUrl}', '_blank', "width=685,height=350,left=0,top=0,toolbar,scrollbars,resizable,status=yes");
    }

    isMageTabImport = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).any(function(elt) {
            return elt.checked && (elt.id.lastIndexOf('chk') > -1) && (fileTypeLookup[elt.value] == SDRF_FILE_TYPE || fileTypeLookup[elt.value] == IDF_FILE_TYPE);
        });
    }

    isGprImport = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).any(function(elt) {
            return elt.checked && (elt.id.lastIndexOf('chk') > -1) && (fileTypeLookup[elt.value] == GPR_FILE_TYPE);
        });
    }
    
    checkAnyFilesSelected = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).any(function(elt) {
            if(elt.id.lastIndexOf('chk') < 0) {
             return false;
            }
            return elt.checked;
        });
    }

    getSelectedFileNames = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).select(function(elt) {
          if(elt.id.lastIndexOf('chk') < 0) {
             return false;
            }
            return elt.checked;
        }).map(function(elt) {
            return fileNameLookup[elt.value];
        });
    }

    importFiles = function(importUrl) {
        if (!checkAnyFilesSelected()) {
            alert("At least one file must be selected");
        } else if (isGprImport() && !isMageTabImport()) {
            alert("Genepix GPR files can only be imported/validated as part of a MAGE-TAB dataset with at least one IDF and SDRF file.");
        } else if (isMageTabImport()) {
            openImportDescDialog(importUrl);
        } else {
            openImportDialog(importUrl);
        }
    }

    validateFiles = function(validateUrl) {
        if (!checkAnyFilesSelected()) {
            alert("At least one file must be selected");
        } else if (isGprImport() && !isMageTabImport()) {
            alert("Genepix GPR files can only be imported/validated as part of a MAGE-TAB dataset with at least one IDF and SDRF file.");
        } else {
            TabUtils.submitTabFormToUrl('selectFilesForm', validateUrl, 'tabboxlevel2wrapper');
        }
    }

    doImportFiles = function(importUrl, importDescription, createChoice, newAnnotationName, selectedNodes, selectedNodesType) {
        var formData = Form.serialize('selectFilesForm');
        var extraArgs = new Object();
        if (importDescription) {
            extraArgs['importDescription'] = importDescription;
        }
        if (createChoice) {
            extraArgs['targetAnnotationOption'] = createChoice;
        }
        if (newAnnotationName && newAnnotationName.length > 0) {
            extraArgs['newAnnotationName'] = newAnnotationName;
        }
        if (selectedNodes && selectedNodes.length > 0) {
           extraArgs['targetNodeIds'] = new Array();
           for (var i = 0; i < selectedNodes.length; i++) {
               extraArgs['targetNodeIds'].push(selectedNodes[i]);
           }
           extraArgs["nodeType"] = selectedNodesType;
        }
        formData = formData + '&' + Hash.toQueryString(extraArgs);
        TabUtils.showSubmittingText();
        new Ajax.Request('${validateImportFilesUrl}', {
            onSuccess: function(result) {
                var json = eval('(' + result.responseText + ')');
                if (json.validated || confirm(json.confirmMessage)) {
                    Caarray.submitAjaxForm('selectFilesForm', 'tabboxlevel2wrapper', { url: importUrl, extraArgs : extraArgs});
                } else {
                    TabUtils.hideSubmittingText();
                }
            },
            parameters: formData
        });
    }

    disableNodesWithOtherNodeType = function(parent, nodeType) {
        parent.cascadeBy(function(node) {
            if (node.raw.nodeType != nodeType && node.raw.checked != undefined) {
//                node.disable(); WC: fix
            }
        });
    }

    openImportDescDialog = function(importUrl) {
        Ext.MessageBox.show({
            title: 'Enter Description of Import',
            width: 300,
            buttonText: {
                yes: 'Import',
                cancel: 'Cancel',
            },
            fn: function(btn, importDescription) {
                if (btn == "yes") {
                    doImportFiles(importUrl, importDescription);
                }
            },
            multiline: true
        });
    }

    openImportDialog = function(importUrl) {
        var store = Ext.create('Ext.data.TreeStore', {
            proxy: {
                type: 'ajax',
                url: '${nodesJsonUrl}'
            },
            listeners: {
                beforeload: function(store, operation) {
                    operation.params["project.id"] = '${project.id}';
                    operation.params.nodeType = operation.node.raw.nodeType;
                },
                load: function(store, node, records) {
                    // when new nodes are loaded, disable them if a different node type is selected
                    // also add a handler to each to handle disabling different node types if checked
                    var rootNode = node.getOwnerTree().getRootNode();
                    var checkedNode = rootNode.findChild("checked", true, true);
                    if (checkedNode != null) {
                        disableNodesWithOtherNodeType(node, checkedNode.raw.nodeType);
                    }
                }
            },
            sorters: [{
                property: 'sort',
                direction: 'ASC'
            }],
            root: {
                text: 'Experiment',
                nodeType: 'ROOT',
                sort: 'Experiment',
                draggable:false, // disable root node dragging
                id:'ROOT'
            }
        });
        var tree = Ext.create('Ext.tree.TreePanel', {
            animate: true,
            autoScroll: true,
            hidden: true,
            hideMode: 'visibility',
            border: false,
            bodyStyle:'padding-top: 5px; padding-left: 15px',
            rootVisible: false,
            store: store,
            xtype: 'check-tree',
            listeners: {
                beforeselect: function() {
                    return false;
                },
                checkchange: function(nd, checked) {
                    var rootNode = nd.getOwnerTree().getRootNode();
                    if (checked) {
                        disableNodesWithOtherNodeType(rootNode, nd.raw.nodeType);
                    } else if (nd.getOwnerTree().findChild("checked", true, true) != null) {
                        nd.getOwnerTree().getRootNode().cascadeBy(function(node) {
//                            node.enable(); WC: fix
                        });
                    }
                }
            }
        });

       var formPanel = new Ext.FormPanel({
            bodyStyle:'padding:5px 5px 0',
            height: 500,
            autoScroll: true,
            border: false,
            bodyCls: 'annotation_dialog_form',
            items: [
                    {
                        html: '<p>For the ' + getSelectedFileNames().length + ' selected file(s), please identify how biomaterial and '
                            + ' hybridization annotations should be created or mapped',
                        border: false
                    },
                    {
                        xtype: 'radio',
                        boxLabel: 'Autocreate annotation sets (Source -> Sample -> Extract -> Labeled'
                            + ' Extract -> Hybridization) for each selected file',
                        id: 'create_choice_autocreate_per_file',
                        name: 'create_choice',
                        inputValue: '<s:property value="@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@AUTOCREATE_PER_FILE"/>',
                        componentCls: 'create_choice_form_item',
                        hideLabel: true
                    },
                    {
                        html: 'Note:  For GenePix and Illumina files, in which >1 biomaterial may be'
                        + ' represented in a given file, biomaterial annotations will be generated'
                        + ' based on the number of samples in the file.<p>',
                        border: false
                    },
                    {
                        xtype: 'radio',
                        boxLabel: 'Autocreate a single annotation set (Source -> Sample -> Extract -> Labeled'
                            + ' Extract -> Hybridization) for all selected files',
                        id: 'create_choice_autocreate_single',
                        name: 'create_choice',
                        inputValue: '<s:property value="@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@AUTOCREATE_SINGLE"/>',
                        componentCls: 'create_choice_form_item',
                        hideLabel: true
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Name for created annotations',
                        id: 'autocreate_single_annotation_name',
                        name: 'autocreate_single_annotation_name',
                        labelAlign: 'right',
                        componentCls: 'create_choice_indented_item'
                    },
                    {
                        xtype: 'radio',
                        boxLabel: 'Associate selected file(s) to existing biomaterial or hybridization',
                        listeners: {
                            'change' : {
                                fn: function(theradio,ischecked) {
                                    Ext.getCmp('experiment_design_tree_instructions').setVisible(ischecked);
                                    tree.setVisible(ischecked);
                                }
                           }
                        },
                        id: 'create_choice_associate_to_biomaterials',
                        name: 'create_choice',
                        inputValue: '<s:property value="@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@ASSOCIATE_TO_NODES"/>',
                        componentCls: 'create_choice_form_item',
                        hideLabel: true
                    },
                    {
                        html: 'Note that biomaterials and hybridizations will be autocreated downstream'
                            + ' of the selected node in the annotation set.  Be sure to select the most'
                            + ' specific node in the annotation set.',
                        border: false,
                        hidden: true,
                        id: 'experiment_design_tree_instructions'
                    },
                    tree,
                    {
                        xtype: 'textarea',
                        fieldLabel: 'Import Description',
                        id: 'importDescription',
                        name: 'importDescription',
                        width: 380,
                        height: 125,
                        autoScroll: true,
                        labelAlign: 'right'
                    }
            ]
        });

        var annotationDialog = new Ext.Window({
            title: 'Import Options',
            width: 650,
            modal: true,
            items: [formPanel],
            autoScroll: true,
            buttons: [{
                text: 'Import',
                listeners: {
                    click: {
                        fn: function() {
                            var selectedRadio, newAnnotationName, checkedNodes = [], checkedNodesType;
                            if (Ext.getCmp('create_choice_autocreate_per_file').checked) {
                                selectedRadio = Ext.getCmp('create_choice_autocreate_per_file').inputValue;
                            } else if (Ext.getCmp('create_choice_autocreate_single').getValue()) {
                                newAnnotationName = Ext.getCmp('autocreate_single_annotation_name').getValue();
                                if (!newAnnotationName || newAnnotationName.length == 0) {
                                    alert("You must enter a value for the new annotation name");
                                    return;
                                }
                                selectedRadio = Ext.getCmp('create_choice_autocreate_single').inputValue;
                            } else if (Ext.getCmp('create_choice_associate_to_biomaterials').getValue()) {
                                var rootNode = tree.getRootNode()
                                var nd = rootNode.findChild("checked", true, true);
                                if (nd == null) {
                                    alert("You must select at least one biomaterial or hybridization.");
                                    return
                                }
                                checkedNodesType = nd.raw.nodeType;
                                rootNode.cascadeBy(function(node) {
                                    if (node.get('checked')) {
                                        checkedNodes.push(node.raw.entityId);
                                    }
                                    return true;
                                });
                                selectedRadio = Ext.getCmp('create_choice_associate_to_biomaterials').inputValue;
                            } else {
                                alert('You must select an import option');
                                return;
                            }
                            doImportFiles(importUrl, $('importDescription').value, selectedRadio, newAnnotationName, checkedNodes, checkedNodesType);
                            annotationDialog.close();
                        }
                    }
                }
            }, {
                text: 'Cancel',
                listeners: {
                    click: { fn: function() { annotationDialog.close()}}
                }
            }]
        });
        annotationDialog.show();
    }
</script>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.unimportedFiles" /></h3>
        <c:if test="${!project.locked && caarrayfn:canFullWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
            <div class="addlink">
                <fmt:message key="experiment.data.upload" var="uploadLabel" />
                <caarray:linkButton actionClass="add" text="${uploadLabel}" onclick="openUploadWindow()"/>
            </div>
        </c:if>
    </div>

    <div id="uploadInProgressDiv" style="display: none;">
        <fmt:message key="data.file.upload.inProgress"/>
    </div>

    <div id="tree" style="float:left; margin:20px; border:1px solid #c3daf9; width:250px; height:300px; display: none"></div>

  <div class="tableboxpad" id="unimportedForm">
      <%@ include file="/WEB-INF/pages/project/files/listUnimportedForm.jsp" %>
    </div>

    <c:if test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
        <caarray:actions divclass="actionsthin">
            <c:if test="${(!project.importingData)}">
                <c:url value="/protected/ajax/project/files/deleteFiles.action" var="deleteUrl" />
                <caarray:linkButton actionClass="delete" text="Delete" onclick="deleteFiles('${deleteUrl}');" />
                <c:url value="/protected/ajax/project/files/unpackFiles.action" var="unpackUrl" />
                <caarray:linkButton actionClass="import" text="Unpack Archive" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${unpackUrl}', 'tabboxlevel2wrapper');" />
                <c:url value="/protected/ajax/project/files/editFiles.action" var="editUrl" />
                <caarray:linkButton actionClass="edit" text="Change File Type" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${editUrl}', 'tabboxlevel2wrapper');" />
                <c:url value="/protected/ajax/project/files/findRefFiles.action" var="findRefUrl" />
                <caarray:linkButton actionClass="validate" text="Select Referenced Files" onclick="checkRefFileSelection('${findRefUrl}');" />
                <c:url value="/protected/ajax/project/files/validateFiles.action" var="validateUrl" />
                <caarray:linkButton actionClass="validate" text="Validate" onclick="validateFiles('${validateUrl}');" />
                <c:url value="/protected/ajax/project/files/importFiles.action" var="importUrl"/>
                <caarray:linkButton actionClass="import" text="Import" onclick="importFiles('${importUrl}');" />
                <c:url value="/protected/ajax/project/files/addSupplementalFiles.action" var="supplementalUrl"/>
                <caarray:linkButton actionClass="import" text="Add Supplemental Files" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${supplementalUrl}', 'tabboxlevel2wrapper');" />
            </c:if>
            <c:url value="/protected/ajax/project/files/listUnimportedWithoutClearingCheckboxes.action" var="unimportedUrl"/>
            <caarray:linkButton actionClass="import" text="Refresh Status" onclick="passOnSelectedFiles('${unimportedUrl}');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>
