Steps for remote Build and Deployment for caArray 2

Prereqs: It is assumed you have Ant 1.7.0 and a JDK installed and in your System's PATH. It is also assumed that you are operating in the NCI LAN and you have already created an id_dsa file (SSH private key) which allows you to SSH to the deployment target without specifying a password. Remember, you are emulating what will be a centralized build environment, so these steps will only need to be performed when setting up a new build machine/build track in Anthill Pro.

   1. Perform a clean checkout from a different directory than your current caArray 2 development directory (if this exists)
         a) https://gforge.nci.nih.gov/svnroot/caarray2/trunk/ 
   2. Checkout the deployment properties files
         a) https://gforge.nci.nih.gov/svnroot/scm-private/trunk/caarray2/properties/
   3. Modify the ssh.key.file attribute in your [DEV/QA].properties file to point to the location of your id_dsa file (SSH private key). The path must be fully qualified.
   4. For the DEV environment and from the command line in your [caarray2]/software/master_build directory, type: 

   (Without Tag)
   ant -Dclean.libs.and.ivy.cache=true -Dnocheck=true -Dnotest=true -Dnodbintegration=true -Dproperties.file.name=<path to deployment properties folder>/DEV_prodcopy.properties -Dssh.server.username=<caArray JBoss user> -Dssh.grid.username=<grid JBoss user> -Dssh.server.hostname=<deployment target> -Dssh.port=<ssh port> -Dssh.key.file=<path to private key folder>/id_dsa -Dssh.dir.temp=<path on deployment target to stage install files> deploy:remote:upgrade:tier
    
   (With Tag)
   ant -Dclean.libs.and.ivy.cache=true -Dnocheck=true -Dnotest=true -Dnodbintegration=true -Dproperties.file.name=<path to deployment properties folder>/DEV_prodcopy.properties -Dssh.server.username=<caArray JBoss user> -Dssh.grid.username=<grid JBoss user> -Dssh.server.hostname=<deployment target> -Dssh.port=<ssh port> -Dssh.key.file=<path to private key folder>/id_dsa -Dssh.dir.temp=<path on deployment target to stage install files> -Dcreate.tag=true -Dsvn.tag=CAARRAY_R2_3_1_ALPHA_3 deploy:remote:upgrade:tier 

   5. Once deployment is complete, verify a successful remote deployment by going to http://${jboss.server.hostname}:${jboss.server.port}/caarray/ 


For QA deployments, use the same procedures as above.

For additonal detail on this process, see https://gforge.nci.nih.gov/svnroot/caarray2/trunk/docs/deployment/auto-remote-deploy/
