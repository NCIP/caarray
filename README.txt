Contents

--------

   1. Introduction
   2. Layout
   3. Prerequisites
   4. Getting Started
   5. Build Management
   6. Database
   7. Source Control
   8. Issue Tracking
   9. Developing with Eclipse
   10. Static Analysis
   11. Testing

Introduction
---------------------------

caArray2 is a microarray repository, allowing researchers to share microarray experiment annotations and data. It is a
J2EE web application, built with EJB3, Hibernate, Struts2 and several other open source technologies. It includes a
"Java API" (a set of remote EJBs), and a caGrid API.

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

- JDK 1.6.0_20 (or the latest 1.6 release).
  - Set JAVA_HOME to your jdk location
  - Add JAVA_HOME/bin to your PATH variable
- Ant 1.7.0 or later.
  - Set ANT_OPTS to "-Xmx256m"
    Otherwise, you may get an OutOfMemoryException when running unit tests during report generation
  - Set ANT_HOME to your ant install directory
  - Add ANT_HOME/bin to your PATH variable
- MySQL 5.0.45 is officially supported, but MySQL 5.1.x (up to 5.1.48) is known to work.
  Should be downloaded and installed from the MySQL download site.
  - Set a root password (if not set during install). This will be referenced under the key database.system.password in
    /software/local.properties and your copy of /software/master_build/install.properties.  Steps to configure these
    files described in the next section. To set the password, you can execute:
    $ mysqladmin -u root password NEWPASSWORD
- caGrid 1.2. This is optional, as it is needed only if you are working on grid services. This can be obtained from
  http://cagrid.org/display/downloads/caGrid+1.2. You can use either the installer or the source code distribution
  and build it.

Getting Started
------------------------

- Make sure you have installed and configured all of the prerequisites as described above.
- Check out caArray. Most likely, you will check out the trunk - see the Source Control section for URL locations.
  Below, we use $CAARRAY_HOME to refer to the location of the caArray checkout.
- Copy $CAARRAY_HOME/software/build/resources/my.cnf to the location from where MySQL reads its option files.
  This varies depending on OS, refer to http://dev.mysql.com/doc/refman/5.0/en/option-files.html.
  Alternatively, if you already have a MySQL my.cnf file, add the lines in the file above to it.
- Copy the "$CAARRAY_HOME/software/master_build/install.properties" file, rename it to whatever you wish, and configure
  it with values appropriate for your desired local deployment. You should at least edit:
    - application.base.path
    - database.system.user
    - database.system.password
    - database.name
    - database.user
    - database.password
    - dataStorage.fileSystem.baseDir (if any of the other dataStorage properties are set to file-system)
- Create and configure a "$CAARRAY_HOME/software/build/local.properties" file. The purpose of this file is to allow
  developers to override property values from $CAARRAY_HOME/software/build/default.properties file and thus prevent
  accidental check-in of $CAARRAY_HOME/software/build/default.properties, so localize your environment in
  $CAARRAY_HOME/software/build/local.properties rather than $CAARRAY_HOME/software/build/default.properties.
  - Database setup (values should be same as in install.properties copy above):
    - database.system.user
    - database.system.password
    - database.server
    - database.port
    - database.name
    - database.user
    - database.password
    - dataStorage.fileSystem.baseDir
  - JBoss setup (values should be same as in install.properties copy above if they exist): 
    - jboss.home set its value to be what is set for application.base.path in the install.properties file copy above,
      with jboss-5.1.0.GA-nci appended (e.g., application.base.path set to /usr/local/caarray, so jboss.home is set to
      /usr/local/caarray/jboss-5.1.0.GA-nci).
    - jboss.server.jndi.port
    - jboss.server.port
  - Ivy Resolution. This will speed up ivy resolution.  Unset if dependencies change (or clean out your cache):
    - ivy.resolve.pessimistic=false  
    - ivy.noclean=true
    - local.repo.dir (optional if you checked out the ivy repository. See Build Management below)
  - Grid Services
    - globoss.home
    - application.base.path
- Create the caArray DB schema (prerequisite for installation),
    $ mysql -u root -p
    mysql> create schema caarraydb;
    mysql> create user 'caarrayop'
    mysql> grant all on caarraydb.* to 'db-user'@'%' identified by 'db-password' with grant option;
    mysql> grant all on caarraydb.* to 'db-user'@'localhost' identified by 'db-password' with grant option;
    mysql> flush privileges;
  where db-user and db-password are the values you set in local.properties and your copy of install.properties.
- To initialize the database, open a command prompt and execute
    $ cd $CAARRAY_HOME/software/build
    $ ant database:reinitialize
- To install the caArray application,
    $ cd $CAARRAY_HOME/software/master_build
    $ ant -Dproperties.file=<absolute path to install.properties file copy> deploy:local:install
  (replace "<absolute path to install.properties file copy>" with actual path)
  caArray will be installed locally and both caArray JBoss and grid service JBoss will be started automatically.

You can now access the application at http://${jboss.server.hostname}:${jboss.server.port}/caarray.
Default logins are
  caarrayuser/caArray2!
  caarrayadmin/caArray2!
*NOTE: Please change these passwords immediately for any non-development environment

The Grid services will be available at http://${grid.server.hostname}:${globoss.server.http.port}/wsrf/services/cagrid/CaArraySvc (Legacy)
and http://${grid.server.hostname}:${globoss.server.http.port}/wsrf/services/cagrid/CaArraySvc_v1_0 (External v. 1.0)

During your iterative development process for working on an issue, you can just deploy modified code to the caArray
installation by opening a command prompt at $CAARRAY_HOME/software/build and executing ant deploy, which will build the
caarray.ear file and copy it to your caArray JBoss server (defined by jboss.home property in
$CAARRAY_HOME/software/build/local.properties" file).

After deploying your code, you can restart jboss using:
  $ ant deploy:stop-jboss-servers
  $ ant deploy:start-jboss-servers


Build Management
-----------------------

caArray uses Ant for builds. There are two separate build scripts:

software/build/build.xml is used for local (development) builds, and deploying code while doing iterative development on an issue.
software/master_build/build.xml is used for creating the GUI and command-line installers, and deploying to the NCIA tiers, as well as remote and local installs and upgrades.

The local build script is documented here.

You can execute "ant -p" for a full list of ant targets to run. A few of the key ones are:

* clean - cleans up all generated artifacts (jar, war, ear files, etc).
* deploy:caarray.ear - builds the EAR file for the caArray webapp, and deploys it into JBoss.
* deploy:caarray-grid-svc - builds and deploys all the caArray grid services (Legacy and External). depending on value of
  the "grid.useJBoss" build property, the grid services are deployed to either JBoss or Globus.
* deploy:copy-jsp - copies JSPs and other web assets (CSS, Javascript, etc) to the caArray deployment inside JBoss, without
  redeploying the overall webapp.
* database:dropAll - cleans out the database by dropping all tables and data
* database:update - brings the database up to date by running any necessary schema, population and update scripts. Can be
run on a database in any state, including a blank one
* database:reinitialize - resets the database to the initial state (up-to-date schema and initial dataset) by calling database:dropAll and database:update
* database:recreate-database - drops the caArray database and user and then creates them again. Should be run when changes
  to the caArray schema are made, because otherwise the database:reinitialize command may fail.
* test - runs the JUnit test suite.
* check - runs static analysis tools on the code base (currently, Checkstyle and FindBugs).
* continuous-integration - the defult target, it is invoked by jsut running ant without specifying a target from the software/build directory. Execution of this target cleans and builds the application, 
  runs the static analysis checks, and runs the JUnit test suite.
* nightly-build - this target cleans and builds the application and runs the selenium and API test suite.

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

Database 
------------------------
We use MySQL as the database for caArray, Hibernate as the ORM tool to persist our domain model in the database, and 
Liquibase to manage database upgrades.

The Liquibase paradigm is that database population always proceeds by starting from a baseline schema, and then applying
a set of upgrade scripts corresponding to changes made to the domain model and/or initial population scripts. The ant target
"database:update" will bring a database in any state up-to-date. Liquibase maintains a set of which upgrade scripts have been
run, so this target can be run multiple times and upgrade scripts will not be run twice.

The database upgrade scripts are under software/caarraydb/liquibase, organized by version. The db-upgrade.xml is the master
changelog file, which then references db-*.xml files for each version, which in turn reference individual changesets. 
A changeset should correspond to a single GForge issue and should be named for that issue.

So, whenever making changes to the data model that will result in changes to the database schema, the process to do so is:
- make appropriate changes to the hibernate-annotated domain classes
- optionally, run "ant:generate-schema-sql". This will use hibernate to generate a schema creation script corresponding to the 
  current data model. This is not necessary but will probly make it easier to write the upgrade script
- create an upgrade script, named for the gforge issue, in the appropriate directory, and add a changeset to the appropriate
db-*.xml file referencing the script. You can use the schema file from the previous step to help with writing the script.
- run ant database:update to apply the script.
- deploy and run your code / unit tests as appropriate.

Source Control
------------------------

The base Subversion URL is https://ncisvn.nci.nih.gov/svn/caarray2. The mainline of development is under the "trunk" subdirectory,
thus typically you would want to check out https://ncisvn.nci.nih.gov/svn/caarray2/trunk.

Branches are created as needed under https://ncisvn.nci.nih.gov/svn/caarray2/branches for work on previous or future versions of
caArray in parallel with mainline development. Also, developers should create their own private branches here for work that spans 
more than 1-2 days.

Tags are created under https://ncisvn.nci.nih.gov/svn/caarray2/tags for each milestone, release candidate and GA release.

Issue Tracking
------------------------

caArray uses JIRA for tracking bugs, feature requests, and project management.

https://tracker.nci.nih.gov/browse/ARRAY

Developing with Eclipse
------------------------

caArray includes configuration files making it easy to use Eclipse for development. The configuration files are for the Eclipse 3.5 
series, but should work with earlier versions as well.

To import the caArray into Eclipse, simply invoke File->Import, then select General->Existing Projects into Workspace. In the resulting dialog
box, select the "choose root directory" option and point it at the base directory of the caArray checkout.

You must install the IvyDE plugin, which is required for Ivy integration.

We also recommend using the JEE distribution of Eclipse with the following plugins:
- Subversive or Subclipse (for Subversion integration)
- Checkstyle (v4.4.3. Current caarray_checks.xml is incompatible with v5+)
- PMD

The Checkstyle and PMD plugins should then be configured to use the caArray rulesets (see static analysis section).

To debug caArray from Eclipse, do the following:
- Edit [jboss.home]/bin/run.conf (run.conf.bat for Windows) and uncomment the line for remote socket debugging.  It will look something like
JAVA_OPTS="$JAVA_OPTS -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"
- Start jboss
- From Eclipse
  - Run > Debug Configurations > Remote Java Application
  - Click "New" button and set the following
    - Project = caArray2
    - Host = localhost
    - Port = 8787 (or whatever you specified in run.conf)
  - Click Debug

Static Analysis
------------------------

caArray uses static analysis tools to ensure code quality and conformance to standards. Currently we use Checkstyle and PMD. 

The rulesets for these are in software/build/resources/caarray_checks.xml and software/build/resources/pmd-ruleset.xml respectively.

Testing
------------------------

caArray uses JUnit for unit testing. To run the unit test, execute "ant test" from software/build.
If you want to run a specific module, you can use one of the following ant targets: 
- test:junit-caarray-common.jar
- test:junit-caarray-ejb.jar,
- test:junit-caarray.war,
- test:junit-caarray-plugins,
- test:junit-cn2magetab,
- test:junit-report,

If you want to be even more specific and run only selected test files, you may edit local.properties and add:
- test.source.include (e.g. test.source.include=**/IlluminaFileImportIntegrationTest.java)
- test.source.exclude
