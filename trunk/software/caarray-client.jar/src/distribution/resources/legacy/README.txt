Release Notes
=============

  #Product:#	caArray Legacy Service Client
  #Version:#	2.3.0
  #Date:#       October 2009

Contents
--------

   1. Introduction
   2. Distribution Contents
   3. Example Code
   4. Known Issues/Defects
   5. Bug Reports, Feature Requests, and Support
   6. Documentation and Files
   7. NCICB Web Pages

Introduction
---------------------------

This distribution contains the resources necessary to connect to either the
Remote Java Legacy API or caGrid Legacy API of an existing caArray 2.0 server. Also included
are the XSDs for the caArray grid service as a useful artifact for developers
using Introduce to generate grid applications that must connect to caArray.


Distribution Contents
------------------------

The caArray client distribution includes the following contents:

/ <root directory>
 caarray-client-legacy.jar	  Required for both the Java and grid APIs
 build.xml            Runs the provided example clients of the Java and grid
                      APIs via ant (required). This build file may also be
                      used as a reference for required classpaths for grid
                      and Java API client configuration.

/docs/api             Contains the Javadoc for all the caArray client classes

/docs/model           Contains Enterprise Architect UML model of the client API
                      (both Java and caGrid)

/grid/etc             Contains the grid client mapping configuration file
                      (client-config.wsdd). This directory must be on the
                      classpath for any grid clients using the caArray Java
                      classes. This directory also contains the domainModel.xml
                      file for reference.

/grid/lib             Contains all JARs required at runtime for any caArray grid
                      service clients written in Java. 

/grid/schema          Contains the XSDs that define the caArray grid service.
                      These may be imported into grid projects in Introduce in
                      order to include caArray data types and services.

/lib                  Contains all JARs required at runtime for any caArray 
                      Java API clients.


Example Code
------------------------

Example classes illustrating usage of various methods in the API can be downloaded from the GForge site at https://gforge.nci.nih.gov/frs/?group_id=305.
There are two relevant artifacts: java_api_client_examples_v1_0.zip has the Remote Java API examples, and grid_api_client_examples_v1_0.zip has the 
caGrid API examples. Please follow directions in the README files included in those ZIP files.

Known Issues/Defects
------------------------

See the GForge tracker for the latest use cases (implemented
and deferred), existing open defects, community requests,
resolutions and feature requests. The following issues are
highlighted.


https://gforge.nci.nih.gov/tracker/?group_id=305

* The caArray grid service only supports unauthenticated access. If
  authenticated access to private data is necessary, use the remote
  Java API.


Bug Reports, Feature Requests, And Support
------------------------------------------

Send email to ncicb@pop.nci.nih.gov to request support or
report a bug or request a new feature. Existing requests and
resolution may be viewed at the caArray GForge URL:

    * https://gforge.nci.nih.gov/tracker/?group_id=305


Documentation And Files
-----------------------

Links to all documentation and files can be found at:

http://caarray.nci.nih.gov/


NCICB Web Pages
---------------

    * The NCI Center for Bioinformatics, http://ncicb.nci.nih.gov/
    * NCICB Application Support, http://ncicb.nci.nih.gov/NCICB/support
    * NCICB Download Center, http://ncicb.nci.nih.gov/download/
