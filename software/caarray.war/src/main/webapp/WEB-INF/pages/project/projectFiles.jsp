<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    var counter = 0;

    function init() {
        document.getElementById('moreUploads').onclick = moreUploads;
        moreUploads();
    }

    function moreUploads() {
        counter++;
        var newFields = document.getElementById('uploadDiv').cloneNode(true);
        newFields.id = '';
        newFields.style.display = 'block';
        var newField = newFields.childNodes;
        for (var i=0;i<newField.length;i++) {
            var theName = newField[i].name
            if (theName)
                newField[i].name = theName + counter;
        }
        var insertHere = document.getElementById('writeUploadDiv');
        insertHere.parentNode.insertBefore(newFields,insertHere);
    }
</script>

<head>
</head>
<body>

<div id="leftnav">

                            <!--caArray Menu-->

                            <ul class="caarraymenu">

                                <li class="liheader">caArray</li>
                                <li><a href="#">Public Home</a></li>

                                <li class="liheader">Experiments</li>
                                <li><a href="Project_list.action">My Experiment Workspace</a></li>
                                <li><a href="Project_create.action">Create/Propose Experiment</a></li>
                                <!--<li><a href="search_advanced.htm">Advanced Search</a></li>-->

                                <li class="liheader">Users &amp; Groups</li>
                                <li><a href="#">Manage Users</a></li>
                                <li><a href="#">Manage Institutional Groups</a></li>
                                <li><a href="#">Manage Collaborators</a></li>

                                <li class="liheader">Curation</li>
                                <li><a href="#">Import Array Design</a></li>

                            </ul>

                            <!--/caArray Menu-->

                            <!--About Menu-->

                            <ul class="aboutmenu">
                                <li class="liheader">caArray 2.0 Software</li>
                                <li><a href="#">What is caArray?</a></li>
                                <li><a href="#">Install caArray</a></li>
                                <li><a href="#">User Guide</a></li>
                                <li><a href="#">Release Notes</a></li>
                                <li><a href="#">Technical Documentation</a></li>
                            </ul>

                            <!--/About Menu-->

                            <!--Quicklinks Menu-->

                            <ul class="quicklinks">
                                <li class="liheader">Global Quick Links</li>
                                <li><a href="http://www.cancer.gov/" class="external">National Cancer Institute (NCI)</a></li>
                                <li><a href="http://ncicb.nci.nih.gov/" class="external">NCI Center for Bioinformatics (NCICB)</a></li>
                                <li><a href="https://cabig.nci.nih.gov/" class="external">caBIG&trade; - Cancer Biomedical Informatics Grid&trade;</a></li>
                            </ul>

                            <!--/Quicklinks Menu-->

                        </div>


    <div id="content">
        <h1>Experiment Files</h1>
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <p>You are managing files for <s:property value="project.experiment.title" />.</p>

        <s:if test='project.files != null && !project.files.isEmpty()'>
            <s:form action="File_import" method="post">
                <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
                <table>
                    <tr>
                        <td>Current Files</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr></tr>
                    <tr>
                        <td></td>
                        <td>Select Name</td>
                        <td>File Type</td>
                        <td>Status</td>
                    </tr>
                    <s:iterator value="project.files" status="status">
                    <tr>
                        <td><s:checkbox name="file:%{#status.index}:selected" /></td>
                        <td><s:property value="name"/></td>
                        <td><s:select name="file:%{#status.index}:fileType"
                                      listKey="label"
                                      listValue="value"
                                      list="fileTypes"
                                      value="type" />
                        </td>
                        <td>
                            <a name="file:<s:property value='%{#status.index}'/>:status" href="File_messages.action?project.id=<s:property value='project.id'/>&file.id=<s:property value='id'/>">
                                <s:property value="status"/>
                            </a>
                        </td>
                    </tr>
                    </s:iterator>
                    <tr>
                        <td>
                            <s:submit name="importFile" value="Import" method="importFile" id="importFile" />
                        </td>
                        <td>
                            <s:submit name="validateFile" value="Validate" method="validateFile" id="validateFile" />
                        </td>
                       <td>
                            <s:submit name="removeFile" value="Delete" method="removeFile" id="removeFile"/>
                        </td>
                        <td>
                            <s:submit name="saveExtension" value="Save Changes" method="saveExtension" id="saveExtension"/>
                        </td>
                    </tr>
                </table>
            </s:form>
        </s:if>

        <div id="uploadDiv" style="display: none">
            <input type="button" value="Remove" onclick="this.parentNode.parentNode.removeChild(this.parentNode);" /><br />
            <s:file id="upload" name="upload" label="File" />
        </div>

        <s:form action="File_upload" enctype="multipart/form-data" method="post">
            <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
            <s:file id="upload" name="upload" label="File" />
            <span id="writeUploadDiv"></span>
            <table>
                <tr>
                    <td>
                        <input type="button" id="moreUploads" value="More" onclick="init()"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <s:submit name="uploadFile" value="Upload" id="uploadFile"/>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
</body>

