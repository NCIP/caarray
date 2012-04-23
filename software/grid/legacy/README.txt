Introduce Generated Service Skeleton:
======================================
This is an Introduce generated service.  

All that is needed for this service at this point is to populate the 
service side implementation in the <service package>.service.<service name>Impl.java

Prerequisites:
=======================================
Java 1.6 and JAVA_HOME env defined
Ant 1.7+ and ANT_HOME env defined
Globus 4.0.3 installed and GLOBUS_LOCATION env defined
(optional) Tomcat 5.0.28 installed and "CATALINA_HOME" env defined with globus deployed to it
(optional) JBoss 5.1 installed and "JBOSS_HOME" env defined

To Build:
=======================================
"ant all" will build 
"ant deployGlobus" will deploy to "GLOBUS_LOCATION"
"ant deployTomcat" will deploy to "CATALINA_HOME"
"ant deployJBoss" will deploy to "JBOSS_HOME"

To Run Smoke tests:
=======================================

Deploy caArray.
Create a public experiment with at least one hybridization and data file. The Test3 design + data set work well.
Modify the CaArraySvnClient static constants to the correct database values.
"ant runClient" exercises the cql query interface, grid transfer of files, and an analytic service.
Successful output shows a cql run, file contents, and hybridizaton / data values.