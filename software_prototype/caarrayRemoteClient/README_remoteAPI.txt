Java client to test the CaArray Remote Java API:
-------------------------------------------------

1. The client com.fiveamsolutions.caarrayclient.CaArrayRemoteClient
   connects to the JNDI server, looks up the remote EJB called
   <CaArraySearchService.JNDI_NAME>, and makes calls to the exposed
   methods:
   (a) Search By Example: Tested by looking for a Category with the
       given name.
   (b) Search By HQL: Tested by looking for Protocols of the given type.
   (c) Search By CQL: Tested by looking for Categories with a name
       that matches the given pattern.
2. The Ant file build.xml provides targets to compile and run the test client.
3. caArray's build.xml has a target called "build:caarray-client.jar"
   which creates caarray-client.jar. This jar must be put into the
   lib directory of this client application.
4. The following jars must also be present in the lib directory of
   the client application:
   activation.jar
   commons-logging-1.1.jar
   javassist.jar
   jbossall-client.jar
   jboss-aop-jdk50-client.jar
   jboss-ejb3-client.jar
   jbossws-client.jar
   jnp-client.jar
   log4j-1.2.13.jar
   mail.jar
5. There must be a jndi.properties file in the src directory, which
   points to the URL where the JNDI service is running.
6. Early on, we had discussed the possibility of providing transparent
   remote lazy loading of associations through the remote Java API.
   (This allows a remote Java client to traverse associations even
   if they happen to be lazy-loaded by Hibernate.)
   There is a 3rd party product called H3T which I had investigated
   a few months ago. Look at the thread "IllegalArgumentException on client"
   at http://sourceforge.net/forum/forum.php?forum_id=520703.
   A decision has not been made yet re: the approach we will take here.

