Steps for remote Build and Deployment for caArray 2

Prereqs: It is assumed you have Ant 1.7 and a JDK installed and in your System's PATH. It is also assumed that you are operating in the NCI LAN. Remember, you are emulating what will be a centralized build environment, so these steps will only need to be performed when setting up a new build machine. Right now, it assumes you are running on a Windows OS.

   1. Perform a clean checkout from a different directory than your current caArray 2 development directory (if this exists)
         a) https://gforge.nci.nih.gov/svnroot/caarray2/trunk/ 
   2. Extract the attached remote-deploy.zip file to your newly checked out [caarray2]/software/build directory. Files contained in this zip file are: DEV.properties, local.properties, QA.properties and id_dsa
   3. Modify the ssh.key.file attribute in your [caarray2]/software/build/[DEV/QA].properties file to point to the location of your id_dsa file (SSH private key). The path must be fully qualified.
   4. For the DEV environment and from the command line in your [caarray2]/software/build directory, type: 

   ant -f remote-build.xml -Dbuild.env=DEV

   5. Once deployment is complete, verify a successful remote deployment by going to http://cbvapp-d1002.nci.nih.gov:19280/caarray/ 


For QA, use the same procedures with the following exceptions: 

   4. For the QA environment and from the command line in your [caarray2]/software/build directory, type: 

   ant -f remote-build.xml -Dbuild.env=QA -Dlabel.ear=CAARRAY_R2_0_0_QA2
   5. Once deployment is complete, verify a successful remote deployment by going to    http://cbvapp-q1001.nci.nih.gov:19280/caarray/




