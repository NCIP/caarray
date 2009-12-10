Contents
--------

   1. Introduction
   2. Layout
   3. Prerequisites
   4. Getting Started
   5. Build Management
   6. Source Control
   7. Issue Tracking
   8. Developing with Eclipse
   9. Static Analysis
   10. Continuous Integration

Introduction
---------------------------

caArray2 is a microarray repository, allowing researchers to share microarray experiment annotations and data. It is a J2EE web application, built 
with EJB3, Hibernate, Struts2 and several other open source technologies. It includes a "Java API" (a set of remote EJBs), and a caGrid API.

Layout
------------------------

This section describes how the project source is laid out on disk, and the major components.

/ <root directory>

	software/			Contains source code and related artifacts for the caArray webapp and services	

		caarray-common.jar   	Set of classes used by both the business (EJB) tier, the Web tier, and the Grid service implementation. 
 								Contains the internal domain model classes, the DAO classes, the external (service) data classes, and various
 								utility classes. This also has the MAGE-TAB parsing framework.
 						
		caarray-ejb.jar			The business tier. Contains local EJBs implementing the business functions used
 								by the web tier, as well as remote EJBs implementing the "Java API" that can be used by remote clients.
 						
 		caarray.war				The web tier. Contains Struts2 action classes, JSP pages, and various configuration files implementing the 
			 					web UI.
 								
		caarray.ear				Contains various configuration files needed needed to assemble the caarray2 EAR out of the other components 
 						
 		caarraydb				Contains various database population scripts, as well as database migration scripts for upgrading from
 								previous versions of the application.																	
 								
		caarray-client.jar		Supporting classes and configuration files for the caArray client JAR, which contains everything needed to
								accessing caArray API (both grid and java).
								
		test					Contains selenium and API (both grid and java) tests.
		
		load-test				Contains JMeter load tests. These are currently out of date.
		
		grid/legacy				Contains the implementation of the "legacy" grid service. This directory is generated and managed by Introduce.
							 								
		grid/v1_0				Contains the implementation of version 1.0 of the "external" grid service. 
								This directory is generated and managed by Introduce.

	docs/				Contains all the documentation for caArray. The general layout follows RUP practice. Selected important directories
						are highlighted below.
							
		analysis_and_design		Contains the architecture documentation. The models directory holds the EA UML models.
		    					
		requirements/use_cases	Contains the use cases and user stories. These are generally not maintained after initial implementation.
		
		end_user_documentation	Contains the end-user manuals. Both the sources (in Word), and finished copies (PDFs) are checked in here.
		
	test-data			Contains a number of sample data sets. These are used in some of the unit and integration tests, and can also be used
						for populating an application.
						
	api_client_examples Contains a set of example programs illustrating the use of the caArray API.
													
 	qa					Contains documentation of QA processes, QA test cases, and results of QA execution runs.
 	
Prerequisites
------------------------

The following tools are needed for working on caArray2 code:

- JDK 1.5.0_14 (or the latest 1.5 release). The Java binaries should be on your PATH and JAVA_HOME variable should be set to the location
where the JDK is installed.
- Ant 1.7.0 or later. ant should be on your PATH and ANT_HOME environment variable should be set to the location where Ant is installed.
- MySQL 5.0.45 (or the latest 5.0.x release). Should be downloaded and installed from the MySQL download site.
	- when installing, ensure the root user has the password defined in software/build/default.properties under the key "database.system.password" 
  	- copy $CAARRAY_HOME/software/build/resources/my.cnf to the location from where MySQL reads its option files. This varies depending on
      OS, refer to http://dev.mysql.com/doc/refman/5.0/en/option-files.html.
      Alternatively, if you already have a MySQL my.cnf file, add the lines in the file above to it.
- caGrid 1.2. This is needed if you intend to work on grid services. This can be obtained from http://cagrid.org/display/downloads/caGrid+1.2.
  You can use either the installer or the source code distribution and build it.

Getting Started
------------------------

- Make sure you have installed and configured all of the prerequisites as described above.
- Check out caArray. Most likely, you will check out the trunk - see the Source Control section for URL locations.
  Below, we use $CAARRAY_HOME to refer to the location of the caArray checkout. 
- Copy the file "$CAARRAY_HOME/software/build/default.properties" to "$CAARRAY_HOME/software/build/local.properties". Configure the "$CAARRAY_HOME/software/build/local.properties" file for your desired deployment.
- Configure the "$CAARRAY_HOME/software/master_build/install.properties" file to fit your desired deployment (set idenntical values for properties with same same name in "$CAARRAY_HOME/software/build/local.properties" file.
- Open a command prompt and from $CAARRAY_HOME/software/build, execute
  ant database:recreate-database
- cd to $CAARRAY_HOME/software/master_build, execute
  ant deploy:local:install
- caArray will be installed locally and both caArray JBoss and grid service JBoss will be started automatically.

You can now access the application at http://${jboss.server.hostname}:${jboss.server.port}/caarray. The Grid services
will be available at http://${grid.server.hostname}:${18080}/wsrf/services/cagrid/CaArraySvc (Legacy) and 
http://${grid.server.hostname}:${18080}/wsrf/services/cagrid/CaArraySvc_v1_0 (External v. 1.0)

Build Management
-----------------------

caArray uses Ant for builds. There are two separate build scripts:

software/build/build.xml is used for local (development) builds.
software/master_build/build.xml is used for creating the GUI and command-line installers, and deploying to the NCIA tiers as well as remote and local installs and upgrades.

The local build script is documented here. The installer build script is documented in a separate "Installers" section.

You can execute "ant -p" for a full list of ant targets to run. A few of the key ones are:

* clean - cleans up all generated artifacts (jar, war, ear files, etc).
* deploy:caarray.ear - builds the EAR file for the caArray webapp, and deploys it into JBoss.
* deploy:caarray-grid-svc - builds and deploys all the caArray grid services (Legacy and External). depending on value of
  the "grid.useJBoss" build property, the grid services are deployed to either JBoss or Globus.
* deploy:copy-jsp - copies JSPs and other web assets (CSS, Javascript, etc) to the caArray deployment inside JBoss, without
  redeploying the overall webapp.
* database:reinitialize - drops all the tables in the caArray database, reruns the schema generation, recreates the tables,
  and populates them with the initial set of data.  
* database:recreate-database - drops the caArray database and user and then creates them again. Should be run when changes
  to the caArray schema are made, because otherwise the database:reinitialize command may fail.
* test - runs the JUnit test suite.
* check - runs static analysis tools on the code base (currently, Checkstyle and FindBugs).
* continuous-integration - target invoked by the CI build. Cleans and builds the application, 
  runs the static analysis checks and the JUnit test suite.
* nightly-build - target invoked by the nightly build. Cleans and builds the application and runs the selenium and API test suite.

There are a number of build properties used by the build script. Some are defined in software/build/build.xml, and others in 
software/build/default.properties. As a rule, those build properties which may need to be overridden are put in the latter.

The build script will also attempt to read properties from software/build/local.properties. This is the place where you should
put properties specific to your local checkout. They will take precedence over the defaults. This file should never be checked into
subversion, and is set to be svn:ignored.

Another important aspect is that caArray uses Ivy for dependency management. The ivy settings file is at software/ivy-caarray-settings.xml,
and the ivy project file is at software/ivy-caarray.xml. All dependencies should be configured in this latter file. caArray references the
NCICB ivy repository, at http://gforge.nci.nih.gov/svnroot/commonlibrary/trunk/ivy-repo.

It is recommended, for quicker builds, to check out this ivy repository to a local directory (this requires about 0.75 GB of hard drive space).
Then, set the "local.repo.dir" build property to point to this directory. This will make the build fetch dependencies from your local copy first,
but will fall back upon the canonical repository if it cannot find it there. Doing this (and ensuring your local copy is up to date) is required
in order to be able to build caArray without Internet connectivity.

Source Control
------------------------

The base Subversion URL is https://gforge.nci.nih.gov/svnroot/caarray2. The mainline of development is under the "trunk" subdirectory,
thus typically you would want to check out https://gforge.nci.nih.gov/svnroot/caarray2/trunk.

Branches are created as needed under https://gforge.nci.nih.gov/svnroot/caarray2/branches for work on previous or future versions of
caArray in parallel with mainline development. Also, developers should create their own private branches here for work that spans 
more than 1-2 days.

Tags are created under https://gforge.nci.nih.gov/svnroot/caarray2/tags for each milestone, release candidate and GA release.

Issue Tracking
------------------------

caArray uses GForge for tracking bugs and feature requests. We use the tracker portion of GForge for this:

https://gforge.nci.nih.gov/tracker/?group_id=305

More specifically, we use two trackers. The Implementation Items tracker (https://gforge.nci.nih.gov/tracker/?atid=1344&group_id=305)
is used by the DEV team for all features, feature enhancements, bugs, code improvements, and other changes to the source code.

The Community Change Requests tracker (https://gforge.nci.nih.gov/tracker/?atid=1339&group_id=305) is used for managing requests from
the caArray user community. Eventually, as the requests are accepted and slated for releases, corresponding items are created in the
Implementation Items tracker.

Developing with Eclipse
------------------------

caArray includes configuration files making it easy to use Eclipse for development. The configuration files are for the Eclipse 3.5 
series, but should work with earlier versions as well.

To import the caArray into Eclipse, simply invoke File->Import, then select General->Existing Projects into Workspace. In the resulting dialog
box, select the "choose root directory" option and point it at the base directory of the caArray checkout.

We recommend using the JEE distribution of Eclipse with the following plugins:
- Subversive or Subclipse (for Subversion integration)
- Checkstyle
- PMD
- IvyDE (for Ivy integration)

The Checkstyle and PMD plugins should then be configured to use the caArray rulesets (see static analysis section).

Static Analysis
------------------------

caArray uses static analysis tools to ensure code quality and conformance to standards. Currently we use Checkstyle and PMD. 

The rulesets for these are in software/build/resources/caarray_checks.xml and software/build/resources/pmd-ruleset.xml respectively.

Continuous Integration
------------------------

caArray uses the Hudson CI server. Two types of builds exist: the continuous-integration build is run after every commit and the nightly build
is run every night just after midnight. Builds are set up for the trunk and each active branch.

The continuous integration build does a clean build, and runs the static analysis and junit tests. The nightly build also runs the selenium
and API tests.

The Hudson CI server url is http://cbvapp-c1003.nci.nih.gov:48080/hudson. It can only be accessed from within the NCI network.

