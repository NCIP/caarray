<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE struts PUBLIC
    "-//Apache Software Foundation//DTD Struts Configuration 2.0//EN"
    "http://struts.apache.org/dtds/struts-2.0.dtd">

<struts>
    <!-- Configuration for the default package. -->
    <package name="default" extends="struts-default">
        <interceptors>
            <interceptor name="caarrayParams"
                class="com.fiveamsolutions.nci.commons.web.struts2.interceptor.DisplayTagParametersInterceptor" />

            <interceptor name="nullCollectionElements"
                class="com.fiveamsolutions.nci.commons.web.struts2.interceptor.NullCollectionElementInterceptor" />

            <interceptor name="parameterTrim"
                class="com.fiveamsolutions.nci.commons.web.struts2.interceptor.ParameterTrimInterceptor" />

            <interceptor name="maliciousInput"
                class="com.fiveamsolutions.nci.commons.web.struts2.interceptor.XSSFilterInterceptor" />

            <interceptor-stack name="caarrayParamsPrepareParamsStack">
                <interceptor-ref name="exception" />
                <interceptor-ref name="alias" />
                <interceptor-ref name="caarrayParams" />
                <interceptor-ref name="servletConfig" />
                <interceptor-ref name="prepare" />
                <interceptor-ref name="i18n" />
                <interceptor-ref name="chain" />
                <interceptor-ref name="modelDriven" />
                <interceptor-ref name="fileUpload" />
                <interceptor-ref name="checkbox" />
                <interceptor-ref name="staticParams" />
                <interceptor-ref name="caarrayParams" />
                <interceptor-ref name="conversionError" />
                <interceptor-ref name="nullCollectionElements" />
                <interceptor-ref name="maliciousInput" />
                <interceptor-ref name="parameterTrim">
                  <param name="trimToNull">true</param>
                </interceptor-ref>
                <interceptor-ref name="validation">
                    <param name="excludeMethods">input,back,cancel</param>
                </interceptor-ref>
                <interceptor-ref name="workflow">
                    <param name="excludeMethods">input,back,cancel</param>
                </interceptor-ref>
            </interceptor-stack>

            <interceptor-stack name="caarrayDefaultStack">
                <interceptor-ref name="exception" />
                <interceptor-ref name="alias" />
                <interceptor-ref name="servletConfig" />
                <interceptor-ref name="prepare" />
                <interceptor-ref name="i18n" />
                <interceptor-ref name="chain" />
                <interceptor-ref name="debugging" />
                <interceptor-ref name="profiling" />
                <interceptor-ref name="scopedModelDriven" />
                <interceptor-ref name="modelDriven" />
                <interceptor-ref name="fileUpload" />
                <interceptor-ref name="checkbox" />
                <interceptor-ref name="staticParams" />
                <interceptor-ref name="caarrayParams">
                    <param name="excludeParams">dojo\..*</param>
                </interceptor-ref>
                <interceptor-ref name="conversionError" />
                <interceptor-ref name="nullCollectionElements" />
                <interceptor-ref name="maliciousInput" />
                <interceptor-ref name="parameterTrim">
                  <param name="trimToNull">true</param>
                </interceptor-ref>
                <interceptor-ref name="validation">
                    <param name="excludeMethods">input,back,cancel,browse</param>
                </interceptor-ref>
                <interceptor-ref name="workflow">
                    <param name="excludeMethods">input,back,cancel,browse</param>
                </interceptor-ref>
            </interceptor-stack>
        </interceptors>

        <default-interceptor-ref name="caarrayParamsPrepareParamsStack" />

        <global-results>
            <result name="permissionDenied">/permissionDenied.jsp</result>
            <result name="error">/error.jsp</result>
            <result name="securityError" type="httpheader">
                <param name="status">403</param>
                <param name="errorMessage">tcp error</param>
            </result>
            <result name="notYetImplemented">/notYetImplemented.jsp</result>
        </global-results>

        <global-exception-mappings>
            <exception-mapping result="permissionDenied" exception="gov.nih.nci.caarray.security.PermissionDeniedException"/>
            <exception-mapping result="error" exception="java.lang.Throwable"/>
            <exception-mapping result="securityError" exception="java.lang.NoSuchMethodException"/>
        </global-exception-mappings>

        <action name="ajax/uploadProgress" class="gov.nih.nci.caarray.web.fileupload.UploadProgress">
        </action>

        <action name="home" class="gov.nih.nci.caarray.web.action.HomeAction">
            <result name="input">/WEB-INF/pages/home.jsp</result>
        </action>
        <action name="logout" class="com.fiveamsolutions.nci.commons.web.struts2.action.LogoutAction" method="logout">
            <result name="success" type="redirect-action">
                <param name="namespace">/</param>
                <param name="actionName">home</param>
            </result>
        </action>
        <action name="login">
            <result>/WEB-INF/pages/login.jsp</result>
        </action>
        <action name="search/*" class="gov.nih.nci.caarray.web.action.SearchAction" method="{1}">
            <result name="success">/WEB-INF/pages/project/search/searchResults.jsp</result>
            <result name="input" type="chain">home</result>
        </action>
        <action name="ajax/search/*" class="gov.nih.nci.caarray.web.action.SearchAction" method="{1}">
            <result name="tab">/WEB-INF/pages/project/search/searchTab.jsp</result>
            <result name="success">/WEB-INF/pages/project/search/searchResults.jsp</result>
            <result name="input" type="chain">home</result>
        </action>
        <action name="browse" class="gov.nih.nci.caarray.web.action.BrowseAction">
            <result name="success">/WEB-INF/pages/project/search/browseResults.jsp</result>
        </action>
        <action name="ajax/browse/*" class="gov.nih.nci.caarray.web.action.BrowseAction" method="{1}">
            <result name="tab">/WEB-INF/pages/project/search/browseTab.jsp</result>
        </action>

        <action name="project/details" class="gov.nih.nci.caarray.web.action.project.ProjectAction" method="details">
            <result name="login-details-id" type="redirect-action">protected/project/details${requestParameters}</result>
            <result name="login-details-publicid" type="redirect-action">protected/project/details${requestParameters}</result>
            <result name="workspace" type="redirect-action">protected/project/workspace</result>
            <result name="input">/WEB-INF/pages/project/form.jsp</result>
        </action>

        <action name="project/browse" class="gov.nih.nci.caarray.web.action.project.ProjectAction" method="browse">
            <result name="workspace" type="redirect-action">/protected/project/workspace</result>
            <result name="input">/WEB-INF/pages/project/form.jsp</result>
            <result name="browse">/WEB-INF/pages/project/browse.jsp</result>
        </action>

        <action name="ajax/project/tab/*/*"
            class="gov.nih.nci.caarray.web.action.project.Project{1}Action" method="{2}">
            <result name="input">/WEB-INF/pages/project/tab{1}.jsp</result>
            <result name="success">/WEB-INF/pages/project/tab{1}.jsp</result>
            <result name="workspace">/WEB-INF/pages/project/backToWorkspace.jsp</result>
            <result name="reload-project">/WEB-INF/pages/project/reloadProject.jsp</result>
            <result name="xmlArrayDesigns" type="stream">
                <param name="contentType">text/xml;charset=UTF-8</param>
                <param name="inputName">arrayDesignsAsXml</param>
                <param name="bufferSize">4096</param>
            </result>
            <result name="protocolAutoCompleterValues">/WEB-INF/pages/protocol/protocolAutoCompleterValues.jsp</result>
        </action>

        <action name="ajax/project/listTab/*/*"
            class="gov.nih.nci.caarray.web.action.project.Project{1}Action" method="{2}">
            <result name="input">/WEB-INF/pages/project/tab{1}/edit.jsp</result>
            <result name="success">/WEB-INF/pages/project/tab{1}/list.jsp</result>
            <result name="list">/WEB-INF/pages/project/tab{1}/list.jsp</result>
            <result name="workspace">/WEB-INF/pages/project/backToWorkspace.jsp</result>
            <result name="reload-project">/WEB-INF/pages/project/reloadProject.jsp</result>
            <result name="associationValues">/WEB-INF/pages/project/annotationAssociatedValues.jsp</result>
            <result name="noSourceData" type="redirect-action">${editMode ? '/protected/' : ''}project/${editMode ? 'edit' : 'details'}?project.id=${project.id}&amp;initialTab=annotations&amp;initialTab2=sources</result>
            <result name="noSampleData" type="redirect-action">${editMode ? '/protected/' : ''}project/${editMode ? 'edit' : 'details'}?project.id=${project.id}&amp;initialTab=annotations&amp;initialTab2=samples</result>
            <result name="noExtractData" type="redirect-action">${editMode ? '/protected/' : ''}project/${editMode ? 'edit' : 'details'}?project.id=${project.id}&amp;initialTab=annotations&amp;initialTab2=extracts</result>
            <result name="noLabeledExtractData" type="redirect-action">${editMode ? '/protected/' : ''}project/${editMode ? 'edit' : 'details'}?project.id=${project.id}&amp;initialTab=annotations&amp;initialTab2=labeledExtracts</result>
            <result name="noHybData" type="redirect-action">${editMode ? '/protected/' : ''}project/${editMode ? 'edit' : 'details'}?project.id=${project.id}&amp;initialTab=annotations&amp;initialTab2=hybridizations</result>
            <result name="downloadGroups">/WEB-INF/pages/project/annotationAssociatedDownload.jsp?tab={1}</result>
            <result name="downloadFiles">/WEB-INF/pages/project/tabCommon/downloadFiles.jsp</result>
            <result name="downloadFilesList">/WEB-INF/pages/project/tabCommon/downloadFilesList.jsp</result>
            <result name="downloadFilesListTable">/WEB-INF/pages/project/tabCommon/downloadFilesListTable.jsp</result>
            <result name="factorValuesList">/WEB-INF/pages/project/tab{1}/factorValuesList.jsp</result>
        </action>

        <action name="project/files/*" class="gov.nih.nci.caarray.web.action.project.ProjectFilesAction" method="{1}">
            <result name="upload">/WEB-INF/pages/project/files/uploadComplete.jsp</result>
            <result name="input">/error.jsp</result>
            <result name="denied" type="chain">project/details</result>
            <result name="downloadGroups">/WEB-INF/pages/project/files/downloadFileGroups.jsp</result>
        </action>

        <action name="ajax/project/files/deleteSupplementalFiles" class="gov.nih.nci.caarray.web.action.project.ProjectFilesAction" method="deleteSupplementalFiles">
            <result name="input">/WEB-INF/pages/project/files/listSupplemental.jsp</result>
            <result name="listSupplemental">/WEB-INF/pages/project/files/listSupplemental.jsp</result>
        </action>

        <action name="ajax/project/files/deleteImportedFiles" class="gov.nih.nci.caarray.web.action.project.ProjectFilesAction" method="deleteImportedFiles">
            <result name="input">/WEB-INF/pages/project/files/listImported.jsp</result>
            <result name="listImported">/WEB-INF/pages/project/files/listImported.jsp</result>
        </action>

        <action name="ajax/project/files/*" class="gov.nih.nci.caarray.web.action.project.ProjectFilesAction" method="{1}">
            <result name="input">/WEB-INF/pages/project/files/listUnimported.jsp</result>
            <result name="listImported">/WEB-INF/pages/project/files/listImported.jsp</result>
            <result name="listUnimported">/WEB-INF/pages/project/files/listUnimported.jsp</result>
            <result name="listSupplemental">/WEB-INF/pages/project/files/listSupplemental.jsp</result>
            <result name="listUnimportedForm">/WEB-INF/pages/project/files/listUnimportedForm.jsp</result>
            <result name="listImportedForm">/WEB-INF/pages/project/files/listImportedForm.jsp</result>
            <result name="uploadInBackground">/WEB-INF/pages/project/files/uploadBackground.jsp</result>
            <result name="table">/WEB-INF/pages/project/files/listTable.jsp</result>
            <result name="downloadGroups">/WEB-INF/pages/project/files/downloadFileGroups.jsp</result>
            <result name="success">/WEB-INF/pages/project/files/{1}.jsp</result>
        </action>

        <action name="upgradeStatus" class="gov.nih.nci.caarray.web.action.UpgradeStatusAction">
            <result name="success">/upgradeStatus.jsp</result>
        </action>

    </package>

    <package name="protected" namespace="/protected" extends="default">
        <action name="project/details" class="gov.nih.nci.caarray.web.action.project.ProjectAction" method="details">
            <result name="workspace" type="redirect-action">project/workspace</result>
            <result name="input">/WEB-INF/pages/project/form.jsp</result>
        </action>

        <action name="project/workspace" class="gov.nih.nci.caarray.web.action.project.ProjectWorkspaceAction" method="workspace">
            <result name="success">/WEB-INF/pages/project/workspace/tabs.jsp</result>
        </action>

        <action name="ajax/project/workspace/*" class="gov.nih.nci.caarray.web.action.project.ProjectWorkspaceAction" method="{1}">
            <result name="success">/WEB-INF/pages/project/workspace/{1}.jsp</result>
        </action>

        <action name="project/create" class="gov.nih.nci.caarray.web.action.project.ProjectAction" method="create">
            <result name="input">/WEB-INF/pages/project/form.jsp</result>
        </action>

        <action name="project/edit" class="gov.nih.nci.caarray.web.action.project.ProjectAction" method="edit">
            <result name="workspace" type="redirect-action">project/workspace</result>
            <result name="input">/WEB-INF/pages/project/form.jsp</result>
        </action>

        <action name="project/delete" class="gov.nih.nci.caarray.web.action.project.ProjectAction" method="delete">
            <result name="workspace" type="redirect-action">project/workspace</result>
        </action>

        <action name="project/changeWorkflowStatus" class="gov.nih.nci.caarray.web.action.project.ProjectAction" method="changeWorkflowStatus">
            <result name="workspace" type="redirect-action">project/workspace</result>
            <result name="input">/WEB-INF/pages/project/form.jsp</result>
        </action>

        <action name="project/permissions/*" class="gov.nih.nci.caarray.web.action.project.ProjectPermissionsAction" method="{1}">
            <result name="input">/WEB-INF/pages/project/permissions/profiles.jsp</result>
            <result name="success">/WEB-INF/pages/project/permissions/profiles.jsp</result>
        </action>

        <action name="ajax/project/permissions/*" class="gov.nih.nci.caarray.web.action.project.ProjectPermissionsAction" method="{1}">
            <result name="input">/WEB-INF/pages/project/permissions/{1}.jsp</result>
            <result name="success">/WEB-INF/pages/project/permissions/{1}.jsp</result>
            <result name="accessProfile">/WEB-INF/pages/project/permissions/accessProfile.jsp</result>
            <result name="list">/WEB-INF/pages/project/permissions/list.jsp</result>
        </action>

        <action name="collaborators/*" class="gov.nih.nci.caarray.web.action.CollaboratorsAction" method="{1}">
            <result name="input">/WEB-INF/pages/collaborators/edit.jsp</result>
            <result name="success">/WEB-INF/pages/collaborators/{1}.jsp</result>
            <result name="list">/WEB-INF/pages/collaborators/listGroups.jsp</result>
            <result name="addUsers">/WEB-INF/pages/collaborators/addUsers.jsp</result>
        </action>

        <action name="ajax/collaborators/*" class="gov.nih.nci.caarray.web.action.CollaboratorsAction" method="{1}">
            <result name="list">/WEB-INF/pages/collaborators/listGroups.jsp</result>
            <result name="editTable">/WEB-INF/pages/collaborators/editTable.jsp</result>
            <result name="success">/WEB-INF/pages/collaborators/addTable.jsp</result>
        </action>

        <action name="arrayDesign/*" class="gov.nih.nci.caarray.web.action.ArrayDesignAction" method="{1}">
            <result name="list">/WEB-INF/pages/arrayDesign/list.jsp</result>
            <result name="input">/WEB-INF/pages/arrayDesign/edit.jsp</result>
            <result name="metaValid">/WEB-INF/pages/arrayDesign/fileEdit.jsp</result>
            <result name="importComplete">/WEB-INF/pages/arrayDesign/importComplete.jsp</result>
            <result name="success" type="redirect-action">arrayDesign/list</result>
        </action>

        <action name="ajax/arrayDesign/*" class="gov.nih.nci.caarray.web.action.ArrayDesignAction" method="{1}">
            <result name="list">/WEB-INF/pages/arrayDesign/list.jsp</result>
            <result name="input">/WEB-INF/pages/arrayDesign/edit.jsp</result>
            <result name="metaValid">/WEB-INF/pages/arrayDesign/fileEdit.jsp</result>
            <result name="importComplete">/WEB-INF/pages/arrayDesign/importComplete.jsp</result>
        </action>

        <action name="vocabulary/manage">
            <result>/WEB-INF/pages/vocabulary/manage.jsp</result>
        </action>

        <action name="ajax/vocabulary/*" class="gov.nih.nci.caarray.web.action.vocabulary.VocabularyAction" method="{1}">
            <result name="input">/WEB-INF/pages/vocabulary/edit.jsp</result>
            <result name="success">/WEB-INF/pages/vocabulary/list.jsp</result>
            <result name="termAutoCompleterValues">/WEB-INF/pages/vocabulary/termAutoCompleterValues.jsp</result>
            <result name="projectEdit">/WEB-INF/pages/vocabulary/reloadProject.jsp</result>
        </action>

        <action name="protocol/manage" class="gov.nih.nci.caarray.web.action.protocol.ProtocolManagementAction" method="manage">
            <result name="success">/WEB-INF/pages/protocol/manage.jsp</result>
        </action>

        <action name="ajax/protocol/*" class="gov.nih.nci.caarray.web.action.protocol.ProtocolManagementAction" method="{1}">
            <result name="input">/WEB-INF/pages/protocol/edit.jsp</result>
            <result name="success">/WEB-INF/pages/protocol/list.jsp</result>
            <result name="projectEdit">/WEB-INF/pages/vocabulary/reloadProject.jsp</result>
        </action>

    </package>

    <package name="registration" namespace="/registration" extends="default">
        <action name="*" class="gov.nih.nci.caarray.web.action.registration.RegistrationAction" method="{1}">
            <result name="input">/WEB-INF/pages/registration/form.jsp</result>
            <result name="success">/WEB-INF/pages/registration/success.jsp</result>
        </action>
     </package>
</struts>
