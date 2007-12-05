- Check out the project root (https://gforge.nci.nih.gov/svnroot/caarray2/trunk)
- install mysql (archive available in the tools directory)
  -- unzip and run the setup command make the root user have the password defined in:    https://gforge.nci.nih.gov/svnroot/caarray2/trunk/software/build/default.properties
     At the time of this files creation, the password is passw0rd.
  -- add the following line to the my.ini file in your mySql server installation directory:
     max_allowed_packet = 64M
  -- restart mySql
- Install JBoss 4.0.5 GA from http://labs.jboss.com/jbossas/downloads/
  Download installer that installs JBoss 4.0.5 EJB 3.0 RC9 from here:
    http://labs.jboss.com/jemsinstaller/
- Install Ant 1.7.0 from http://ant.apache.org/
- Start JBoss
- Open up a command prompt and go to $CAARRAY_DEV_ROOT$\software\build
- execute:  ant database:reinitialize
- run ant -projecthelp for the list of useful targets for building and deploying the application.