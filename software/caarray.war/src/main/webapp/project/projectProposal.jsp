<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

  <head>
    <title>Welcome to caArray&trade; - Array Data Management System</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <link rel="address bar icon" href="favicon.ico" />
    <link rel="icon" href="favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
    <!--<link rel="stylesheet" type="text/css" href="common/style.css" />-->
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/common/3coltemplate.css" />
  </head>

  <body>
  <f:view>
    <a href="#content" id="navskip">Skip to Page Content</a>

    <!--NCI/NIH Header-->

    <div id="nciheader">

      <div id="ncilogo"><a href="http://www.cancer.gov"><img src="<%= request.getContextPath() %>/images/logotype.gif" width="283" height="37" alt="Logo: National Cancer Institute" /></a></div>
      <div id="nihtag"><a href="http://www.cancer.gov"><img src="<%= request.getContextPath() %>/images/tagline.gif" width="295" height="37" alt="Logo: U.S. National Institutes of Health | www.cancer.gov" /></a></div>

    </div>

    <!--/NCI/NIH Header-->

    <!--caArray Header-->

    <div id="caarrayheader">

      <div id="caarraylogo"><a href="./"><img src="<%= request.getContextPath() %>/images/logo_caarray.gif" width="172" height="46" alt="Logo: caArray - Array Data Management System" /></a></div>

    </div>

    <!--/caArray Header-->

    <!--caArray Infobar-->

    <div id="infobar">

      <div id="rightinfo"> <caarray:version/>  |  Node: <span>NCICB</span></div>

    </div>

    <!--/caArray Infobar-->

    <!--Work Area-->

    <div id="workarea">

      <div id="mainwrapper">

        <div id="main">

          <div id="leftnavandcontent">

            <div id="leftnav">

              <!--caArray Menu-->

              <ul class="caarraymenu">
                <li class="liheader">Public Pages</li>
                <li><a href="./" class="selected">Home</a></li>
                <li><a href="register.htm">Register</a></li>
                <li><a href="login.htm">Login</a></li>
                <li class="liheader">caArray Software</li>
                <li><a href="about.htm">What is caArray?</a></li>
                <li><a href="install.htm">Install caArray</a></li>
                <li><a href="userguide.htm">User Guide</a></li>
                <li><a href="releasenotes.htm">Release Notes</a></li>
                <li><a href="techdocs.htm">Technical Documentation</a></li>
              </ul>

              <!--/caArray Menu-->


              <!--Quicklinks Menu-->

              <ul class="quicklinks">
                <li class="liheader">Global Quick Links</li>
                <li><a href="http://www.cancer.gov/" class="external">National Cancer Institute (NCI)</a></li>
                <li><a href="http://ncicb.nci.nih.gov/" class="external">NCI Center for Bioinformatics (NCICB)</a></li>
                <li><a href="https://cabig.nci.nih.gov/" class="external">caBIG&trade; - Cancer Biomedical Informatics Grid&trade;</a></li>
              </ul>

              <!--/Quicklinks Menu-->

            </div>

            <!--Content-->

            <div id="content" class="homepage">

              <!--ADD CONTENT HERE-->


              <h1>Propose Experiment</h1>

              <%@ include file="projectProposal.jspf" %>


              <!--/ADD CONTENT HERE-->

            </div>

            <!--/Content-->

          </div>

        </div>

        <div id="sidebar" class="homepage">

          <h1>Actions</h1>

          <table>
            <tr>
              <td>
              <h:form id="mainMenu">
              <h:commandLink id="proposeProject" action="#{projectManagementBean.openWorkspace}" value="Return to Workspace" />
              </h:form>
              </td>
            </tr>
          </table>

          <h1 style="border-top:1px solid #fff;">What's New</h1>

          <p class="small">
            caArray 2.0 software is available for download now. This installation features a new interface and increased functionality.<br />
            <a href="#">Download caArray 2.0 &gt;&gt;</a><br />
            <a href="#">Release Notes &gt;&gt;</a>
          </p>

        </div>

        <div class="clear"></div>

      </div>

    </div>

    <!--Footer-->

    <div id="footerwrapper">

      <div id="footernavwrapper">
        <div id="footernav">
          <a href="contact.htm">Contact Us</a>
          <a href="http://www.nih.gov/about/privacy.htm">Privacy Notice</a>
          <a href="http://www.nih.gov/about/disclaim.htm">Disclaimer</a>
          <a href="http://www3.cancer.gov/accessibility/nci508.htm">Accessibility</a>
          <a href="help.htm" class="last">caArray Support</a>
        </div>
      </div>

      <div id="partnerlogos">
        <a href="http://www.cancer.gov"><img src="<%= request.getContextPath() %>/images/footer_nci.gif" width="63" height="31" alt="Logo: National Cancer Institute" /></a>
        <a href="http://www.dhhs.gov"><img src="<%= request.getContextPath() %>/images/footer_hhs.gif" width="39" height="31" alt="Logo: Department of Health and Human Services" /></a>
        <a href="http://www.nih.gov"><img src="<%= request.getContextPath() %>/images/footer_nih.gif" width="46" height="31" alt="Logo: National Institutes of Health" /></a>
        <a href="http://www.usa.gov"><img src="<%= request.getContextPath() %>/images/footer_usagov.gif" width="91" height="31" alt="Logo: USA.gov" /></a>
      </div>

    </div>

    <!--/Footer-->
  </f:view>
  </body>

</html>