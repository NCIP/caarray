<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>

<body>
    <div id="content" class="homepage">
        <h1>Experiment Workspace</h1>
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <s:set name="projects" value="projects" scope="request"/>
        <display:table name="projects" class="table" requestURI="" id="projectList">
            <display:column property="experiment.title" title="Experiment Title" escapeXml="true" href="Project_details.action" paramId="project.id" paramProperty="id" titleKey="project.id"/>
        </display:table>
    </div>
    <script type="text/javascript">
        highlightTableRows("projectList");
    </script>
</body>




<%--
<body>
                <div id="leftnav">

                            <!--caArray Menu-->

                            <ul class="caarraymenu">

                                <li class="liheader">caArray</li>
                                <li><a href="#">Public Home</a></li>

                                <li class="liheader">Experiments</li>
                                <li><a href="Project_list.action" class="selected">My Experiment Workspace</a></li>
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
        <%@ include file="/WEB-INF/pages/common/messages.jsp" %>
        <h1>My Experiment Workspace</h1>
        <div class="pagehelp">
            <a href="#" class="help">Help</a>
            <a href="#" class="print">Print</a>
        </div>
        <div class="padme">
            <ul id="tabs" class="tabs">
                <li><a href="#" class="active">Work Queue (4)</a></li>
                <li><a href="#">Completed Experiments (1)</a></li>
                <li><a href="#">Public Experiments (0)</a></li>
            </ul>
            <div class="addlink" style="width:15em">
                <a href="Project_create.action" class="add">Add a New Experiment</a>
            </div>
            <div id="tabboxwrapper">

                <div id="workqueue">
                    <p class="small">Displaying 1-4 of 4 Total.</p>
                    <div class="pagingtop">
                        &nbsp;&nbsp;
                        Page 1
                        &nbsp;&nbsp;
                        &lt; Back <span class="bar">|</span>
                        Next &gt;
                    </div>
                    <table class="searchresults" cellspacing="0">
                        <tr>
                            <th><a href="#">Experiment Title</a></th>
                            <th><a href="#">Array Type</a></th>
                            <th><a href="#">Organism</a></th>
                            <th><a href="#">Disease State</a></th>
                            <th><a href="#">Status</a></th>
                            <th><a href="#">Date Created</a></th>
                            <th><a href="#"># Samples</a></th>
                            <th class="actions">Properties</th>
                            <th class="actions">Edit</th>
                        </tr>
                        <c:choose>
                            <c:when test="${not empty projects}" >
                                <c:forEach var="project" items="${projects}" varStatus="loop">
                                    <tr class="${((loop.index % 2) == 0) ? 'odd' : 'even'}">
                                        <td class="title">
                                            <c:url var="url" value="/protected/Project_details.action" ><c:param name="project.id" value="${project.id}" /></c:url>
                                            <a href="${url}"><c:out value="${project.experiment.title}" /></a></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td class="alignright"><a href="#"></a></td>
                                        <td class="icon">
                                            <a href="#"><img src="${ctx}/images/ico_properties.gif" alt="Properties" /></a>
                                        </td>
                                        <td class="icon">
                                            <a href="#"><img src="${ctx}/images/ico_edit.gif" alt="Edit" /></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr class="odd">
                                    <td colspan="9">There are currently no items in this list.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </table>
                    <div class="paging">
                        Page 1
                        &nbsp;&nbsp;
                        &lt; Back <span class="bar">|</span>
                        Next &gt;
                    </div>
                </div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</body>
--%>