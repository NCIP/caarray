<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>

<body>
    <div id="content" class="homepage">
        <h1>Experiment Workspace</h1>
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <p>You have selected <s:property value="project.experiment.title" />.
        <p>This project's browsability status: <s:property value="project.browsable"/>.
        <s:form action="Project_toggle" method="post">
          <input type=hidden name="project.id" value="<s:property value='%{project.id}'/>"/>
          <s:submit key="button.toggle"/>
        </s:form>
    </div>
</body>

<%--
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
        <h1>Experiment Details</h1>
        <div class="pagehelp">
            <a href="#" class="help">Help</a>
            <a href="#" class="print">Print</a>
        </div>
        <div class="padme">
            <h2><span class="dark">Experiment:</span>   Interlaboratory comparability study of cancer gene expression analysis using oligonucleotide microarrays</h2>
            <ul id="tabs" class="tabs2">
                <li><a href="#" class="active">Overview</a></li>
                <li><a href="#">Contacts</a></li>
                <li><a href="#">Annotations</a></li>
                <li>
                    <c:url var="url" value="/protected/File_manage.action" ><c:param name="project.id" value="${project.id}" /></c:url>
                    <a href="${url}">Data</a>
                </li>
                <li><a href="#">Supplemental</a></li>
                <li><a href="#">Publications</a></li>
            </ul>
            <div id="tabboxwrapper">
                <h3>Overall Experiment Characteristics</h3>
                    <div class="boxpad">
                        <p class="instructions">The Overall Experiment Characteristics represent the minimum set of attributes required to submit an experiment for review. </p>
                        <form name="editform" id="idform" action="#">
                            <table class="form">
                                <tr>
                                    <td class="label">Experiment Title:</td>
                                    <td class="value">Interlaboratory comparability study of cancer gene expression analysis using oligonucleotide microarrays</td>
                                </tr>
                                <tr>
                                    <td class="label">Status:</td>
                                    <td class="value">Draft </td>
                                </tr>
                                <tr>
                                    <td class="label">Experiment Identifier:</td>
                                    <td class="value">kletzl-89564</td>
                                </tr>
                                <tr>
                                    <td class="label">Service Type:</td>
                                    <td class="value">Analysis</td>
                                </tr>
                                <tr>
                                    <td class="label">Assay Type:</td>
                                    <td class="value">Gene Expression</td>
                                </tr>
                                <tr>
                                    <td class="label">Manufacturer:</td>
                                    <td class="value">Illumina</td>
                                </tr>
                                <tr>
                                    <td class="label">Array Design:</td>
                                    <td class="value">Sentrix</td>
                                </tr>
                                <tr>
                                    <td class="label">Pooled Samples:</td>
                                    <td class="value">Yes</td>
                                </tr>
                                <tr>
                                    <td class="label">Organism(s):</td>
                                    <td class="value">Mouse</td>
                                </tr>
                                <tr>
                                    <td class="label">Tissue Site:</td>
                                    <td class="value">Lung</td>
                                </tr>
                                <tr>
                                    <td class="label">Tissue Type(s):</td>
                                    <td class="value">total RNA</td>
                                </tr>
                                <tr>
                                    <td class="label">Cell Type(s):</td>
                                    <td class="value">Red Blood Cell</td>
                                </tr>
                                <tr>
                                    <td class="label">Disease/Conditions(s):</td>
                                    <td class="value">Actinic Keratosis</td>
                                </tr>
                            </table>
                            <div class="actions">
                                <a href="#" class="save"><img src="${ctx}/images/btn_edit.gif" alt="Save Draft" /></a>
                            </div>
                        </form>
                    </div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</body>
--%>