- Check out the project root (https://gforge.nci.nih.gov/svnroot/caarray2/trunk)
- install mysql (archive available in the tools directory)
  -- unzip and run the stup command make the root user have the password defined in:
     https://gforge.nci.nih.gov/svnroot/caarray2/trunk/software/build/default.properties
     At the time of this files creation, the password is passw0rd.
  -- add the following line to the my.ini file in your mySql server installation directory:
     max_allowed_packet = 64M
  -- restart mySql
- Install JBoss 4.0.5 GA  (Download installer that installs JBoss 4.0.5, EJB 3.0 RC9 from her:
    http://labs.jboss.com/jemsinstaller/
- Install ANT
- Start JBoss
- Open up a command prompt and go to $CAARRAY_DEV_ROOT$\software\build
- execute:  ant database:reinitialize
- If you are not using globus, create a local.properties file and add noglobus=true to the file.
- run ant -porjecthelp for the list of useful targets for building and deploying the application.
  

