<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>

<body>
    <div id="content" class="homepage">
        <h1>Propose Experiment</h1>
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <s:form action="Project_save" method="post" validate="true">
            <table>
                <tr>
                    <th>Title for new project:</th>
                    <td>
                        <s:textfield id="title" name="proposal.project.experiment.title" required="true"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <s:submit id="submit" key="button.save"/>
                    </td>
                </tr>
            </table>
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
                                <li><a href="Project_create.action" class="selected">Create/Propose Experiment</a></li>
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
        <h1>Propose Experiment</h1>
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <s:form action="Project_save" method="post" validate="true">
            <table>
                <tr>
                    <th>Title for new project:</th>
                    <td>
                        <s:textfield id="title" name="proposal.project.experiment.title" required="true"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <s:submit id="submit" key="button.save"/>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
 </body>
  --%>