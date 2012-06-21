This instructions describe the procedure of updating/modifying CAS Server login screen with NCI compatible look and feel.

It assumes that CAS Server release that was originally obtained from http://downloads.jasig.org/cas/cas-server-3.4.11-release.zip 
is extracted and resided in ${cas-server-3.4.11} 

1. Download patch from here: https://ncisvn.nci.nih.gov/svn/caarray2/trunk/docs/analysis_and_design/prototypes/cas_customization/login-screen.patch

2. cd ${cas-server-3.4.11}

3. patch -p0 --binary -i ${download-directory}/login-screen.patch
   
		Verify the output being produced:
	patching file cas-server-webapp/src/main/webapp/images/footer_usagov.gif
	patching file cas-server-webapp/src/main/webapp/images/tagline.gif
	patching file cas-server-webapp/src/main/webapp/images/footer_nci.gif
	patching file cas-server-webapp/src/main/webapp/images/footer_nih.gif
	patching file cas-server-webapp/src/main/webapp/images/footer_hhs.gif
	patching file cas-server-webapp/src/main/webapp/images/logotype.gif
	patching file cas-server-webapp/src/main/webapp/WEB-INF/view/jsp/default/ui/includes/bottom.jsp
	patching file cas-server-webapp/src/main/webapp/WEB-INF/view/jsp/default/ui/includes/top.jsp
	patching file cas-server-webapp/src/main/webapp/WEB-INF/classes/cas-theme-default.properties
	patching file cas-server-webapp/src/main/webapp/css/caarray.css
    
4. Build the cas-server-webapp using your preferred build approach (command line, Ecliple-based, etc) with Maven build underneath.

5. Deploy the new .war file to the container.

6. Navigate to the CAS URL and verify the login screen new look according to
   https://ncisvn.nci.nih.gov/svn/caarray2/trunk/docs/analysis_and_design/prototypes/cas_customization/login-screen.png
   