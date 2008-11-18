<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/ajax/project/files/listUnimportedForm.action" var="listUnimportedFormUrl" />
<c:url value="/protected/ajax/project/files/uploadInBackground.action" var="uploadInBackgroundUrl">
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
    jobSize = 0;
    jobNumFiles = 0;
    MAX_JOB_SIZE = <s:property value="@gov.nih.nci.caarray.web.action.project.ProjectFilesAction@MAX_IMPORT_TOTAL_SIZE"/>;
    SDRF_FILE_TYPE = '<s:property value="@gov.nih.nci.caarray.domain.file.FileType@MAGE_TAB_SDRF"/>';
    IDF_FILE_TYPE = '<s:property value="@gov.nih.nci.caarray.domain.file.FileType@MAGE_TAB_IDF"/>';
    <c:forEach items="${files}" var="file">
    fileTypeLookup['${file.id}'] = '${file.fileType}';
    fileNameLookup['${file.id}'] = '${caarrayfn:escapeJavaScript(file.name)}';
    fileSizeLookup['${file.id}'] = ${file.uncompressedSize};
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
		$('jobSizeContent').innerHTML= jobNumFiles + " Files, " +formatFileSize(jobSize);
    }

    unimportedFilterCallBack = function() {
        TabUtils.hideLoadingText();
    }

    doFilter = function() {
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
        } else if (jobSize >= MAX_JOB_SIZE) {
            alert("<fmt:message key='project.fileImport.error.jobTooLarge'/>");
        } else if (isMageTabImport()) {
            doImportFiles(importUrl);
        } else {
            openImportDialog(importUrl);
        }
    }

    doImportFiles = function(importUrl, createChoice, newAnnotationName, selectedNodes, selectedNodesType) {
        var formData = Form.serialize('selectFilesForm');
        var extraArgs = new Object();
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

    getCheckedNodeType = function(root) {
        var nd = ExtTreeUtils.findDescendent(root, "checked", true);
        return (nd == null ? null : nd.attributes.nodeType);
    }

    disableNodesWithOtherNodeType = function(parent, nodeType) {
        ExtTreeUtils.setEnabledStatus(parent, false, function(node) {
            var shouldDisable =  (node.attributes.nodeType != nodeType && node.attributes.checked != undefined);
            return shouldDisable;
        });
    }

    openImportDialog = function(importUrl) {
        var treeLoader = new Ext.tree.TreeLoader({
            dataUrl: '${nodesJsonUrl}'
        });
        treeLoader.on("beforeload", function(tl, node) {
            this.baseParams["project.id"] = '${project.id}';
            this.baseParams["nodeType"] = node.attributes.nodeType;
        }, treeLoader);
        // when new nodes are loaded, disable them if a different node type is selected
        // also add a handler to each to handle disabling different node types if checked
        treeLoader.on("load", function(tl, node) {
            if (ExtTreeUtils.hasCheckedNodes(node.getOwnerTree())) {
                disableNodesWithOtherNodeType(node, getCheckedNodeType(node.getOwnerTree().getRootNode()));
            }
            node.childNodes.each(function(childNode) {
                childNode.on("checkchange", function(nd, checked) {
                    if (checked) {
                        disableNodesWithOtherNodeType(nd.getOwnerTree().getRootNode(), nd.attributes.nodeType);
                    } else if (!ExtTreeUtils.hasCheckedNodes(nd.getOwnerTree())) {
                        ExtTreeUtils.setEnabledStatus(nd.getOwnerTree().getRootNode(), true);
                    }
                }, childNode);
           });
        }, treeLoader);

        // prevent selecting of nodes - instead we use the checkboxes and checked status for each node to select them
        var treeSelModel = new Ext.tree.DefaultSelectionModel();
        treeSelModel.on("beforeselect", function(selModel, node, oldNode) {
            return false;
        });

        var tree = new Ext.tree.TreePanel({
            animate:true,
            autoScroll:true,
            enableDD:false,
            containerScroll: true,
            hidden: true,
            hideMode: 'visibility',
            border: false,
            bodyStyle:'padding-top: 5px; padding-left: 15px',
            loader: treeLoader,
            rootVisible: false,
            selModel: treeSelModel
        });
        new Ext.tree.TreeSorter(tree, { property : "sort"});

        // set the root node and expand one level to get the categories (root node itself is invisible)
        var root = new Ext.tree.AsyncTreeNode({
            text: 'Experiment',
            nodeType: 'ROOT',
            sort: 'Experiment',
            draggable:false, // disable root node dragging
            id:'ROOT'
        });
        tree.setRootNode(root);
        root.expand();

        // set up the form panel to handle autocreation selection and target node selection
        var formPanel = new Ext.FormPanel({
            autoWidth: true,
            height: 500,
            url:'save-form.php',
            bodyStyle:'padding:5px 5px 0',
            labelAlign: 'right',
            autoScroll: true,
            border: false,
            items: [{
                                layout: 'form',
                                border: false,
                                width: 'auto',
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
                                                itemCls: 'create_choice_form_item',
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
                                                itemCls: 'create_choice_form_item',
                                                hideLabel: true
                                            },
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Name for created annotations',
                                                id: 'autocreate_single_annotation_name',
                                                name: 'autocreate_single_annotation_name',
                                                itemCls: 'create_choice_indented_item'
                                            },
                                            {
                                                xtype: 'radio',
                                                boxLabel: 'Associate selected file(s) to existing biomaterial or hybridization',
                                                listeners: {
                                                    'check' : {
                                                        fn: function(theradio,ischecked) {
                                                            formPanel.findById('experiment_design_tree_instructions').setVisible(ischecked);
                                                            tree.setVisible(ischecked);
                                                        }
                                                   }
                                                },
                                                id: 'create_choice_associate_to_biomaterials',
                                                name: 'create_choice',
                                                inputValue: '<s:property value="@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@ASSOCIATE_TO_NODES"/>',
                                                itemCls: 'create_choice_form_item',
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
                                            tree
                                       ]
                            }
            ]
        });

        var annotationDialog = new Ext.Window({
            title: 'Import Options',
            closable:true,
            width:650,
            height:570,
            items: [formPanel],
            buttons: [{
                text: 'Import',
                listeners: {
                    'click' : {
                        fn: function() {
                            var createChoiceRadios = [$('create_choice_autocreate_per_file'), $('create_choice_autocreate_single'), $('create_choice_associate_to_biomaterials')];
                            var selectedCreateChoiceRadio = $A(createChoiceRadios).find(function(elt) { return elt.checked });
                            if (!selectedCreateChoiceRadio) {
                                alert('You must select an import option');
                                return;
                            }
                            var newAnnotationName = $('autocreate_single_annotation_name').value;
                            if ($('create_choice_autocreate_single').checked && (!newAnnotationName || newAnnotationName.length == 0)) {
                                alert("You must enter a value for the new annotation name");
                                return;
                            }
                            var checkedNodes = ExtTreeUtils.getCheckedNodes(tree.getRootNode(), 'entityId');
                            var checkedNodesType = getCheckedNodeType(tree.getRootNode());
                            doImportFiles(importUrl, selectedCreateChoiceRadio.value, newAnnotationName, checkedNodes, checkedNodesType);
                            annotationDialog.close();
                        }
                    }
                }
            },{
                text: 'Cancel',
                listeners: {
                'click' : {
                    fn: function() { annotationDialog.close(); }
                }
            }
            }]
        });
        annotationDialog.show();
    }
</script>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.unimportedFiles" /></h3>
        <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
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

    <c:if test="${project.saveAllowed && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
        <caarray:actions divclass="actionsthin">
            <c:if test="${(!project.importingData)}">
                <c:url value="/protected/ajax/project/files/deleteFiles.action" var="deleteUrl" />
                <caarray:linkButton actionClass="delete" text="Delete" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${deleteUrl}', 'tabboxlevel2wrapper');" />
                <c:url value="/protected/ajax/project/files/unpackFiles.action" var="unpackUrl" />
                <caarray:linkButton actionClass="import" text="Unpack Archive" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${unpackUrl}', 'tabboxlevel2wrapper');" />
                <c:url value="/protected/ajax/project/files/editFiles.action" var="editUrl" />
                <caarray:linkButton actionClass="edit" text="Change File Type" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${editUrl}', 'tabboxlevel2wrapper');" />
	            <c:url value="/protected/ajax/project/files/validateFiles.action" var="validateUrl" />
    	        <caarray:linkButton actionClass="validate" text="Validate" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${validateUrl}', 'tabboxlevel2wrapper');" />
                <c:url value="/protected/ajax/project/files/importFiles.action" var="importUrl"/>
                <caarray:linkButton actionClass="import" text="Import" onclick="importFiles('${importUrl}');" />
                <c:url value="/protected/ajax/project/files/addSupplementalFiles.action" var="supplementalUrl"/>
                <caarray:linkButton actionClass="import" text="Add Supplemental Files" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${supplementalUrl}', 'tabboxlevel2wrapper');" />
            </c:if>
            <c:url value="/protected/ajax/project/files/listUnimported.action" var="unimportedUrl"/>
            <caarray:linkButton actionClass="import" text="Refresh Status" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${unimportedUrl}', 'tabboxlevel2wrapper');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>
