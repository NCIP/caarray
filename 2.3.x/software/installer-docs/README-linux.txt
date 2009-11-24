Caarray2 Installer README for Linux

Before you Begin
-----------
The current installation assumes that Sun JDK 1.5.0_xx and Apache Ant 1.7.0 are 
installed and configured on the host machine and that a MySQL 5.0.27 server 
(local or remote) is installed and properly configured (see below for installation 
and configuration instructions). In addition, there are several changes to the OS
files that need to be made before starting the installation.


Operating System
--------------------
1. Become root - su root --login
2. Type ./hostname
3. Type /sbin/ifconfig
3. Add the result of the above commands to /etc/hosts in the 
IP_ADDRESS		HOSTNAME format e.g. (165.112.132.173   lsd-dev.nih.nci.gov) - 
this is needed for the grid service to start properly


Sun JDK 1.5.0_xx
--------------------
1. Navigate to http://java.sun.com/javase/downloads/index_jdk5.jsp
2. Select the appropriate JDK version and select a binary distribution package, e.g. Linux RPM in self-extracting file
3. Accept all Sun licenses and download the package
4. Make the bin file executable chmod +x jdk-1_5_0_14-linux-i586-rpm.bin
5. Run ./jdk-1_5_0_14-linux-i586-rpm.bin
6. Accept all Sun licenses, type yes. This will extract a java RPM file jdk-1_5_0_14-linux-i586.rpm
7. Run rpm -i jdk-1_5_0_14-linux-i586.rpm
8. As root add the following to /etc/profile

	export JAVA_HOME=/usr/java/jdk1.5.0_14
	export PATH=$JAVA_HOME/bin:$PATH

9. Add the same to /root/.bash_profile
10. Verify the installation by typing java -version. You should see the correct JDK version information displayed 


Apache Ant 1.7.0
--------------------


MySQL 5.0.27
--------------------


CAARRAY2
--------------------
1. Copy the https://gforge.nci.nih.gov/svnroot/lsd/trunk/dist/caarray2-installer.zip to the target environment you are installing to
2. Copy the https://gforge.nci.nih.gov/svnroot/lsd/trunk/caarray/caarray-install.sh file to the user's home directory in the target environment
3. Run the ./install.sh script from the target environment
4. Navigate to the caarray2 directory. If you need to modify the default properties, 
open caarray2.properties and enter the proper values for your environment
5. Type ant from the command prompt
 - The installer will start a JBoss instance with the application. 
6. Point a web browser to http://<jboss.server.hostname>:<jboss.server.port>/caarray
7. Provide the appropriate credentials to login