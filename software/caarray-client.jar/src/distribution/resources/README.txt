Release Notes
=============

  #Product:#	caArray Client
  #Version:#	2.0.1
  #Date:#	March 2008

Contents
--------

   1. Introduction
   2. Distribution Contents
   3. Invoking Examples
   4. Known Issues/Defects
   5. Bug Reports, Feature Requests, and Support
   6. Documentation and Files
   7. NCICB Web Pages

Introduction
---------------------------

This distribution contains the resources necessary to connect to either the
remote Java API or caGrid API of an existing caArray 2.0 server. Also included
are the XSDs for the caArray grid service as a useful artifact for developers
using Introduce to generate grid applications that must connect to caArray.


Distribution Contents
------------------------

The caArray client distribution includes the following contents:

/ <root directory>
 caarray-client.jar	  Required for both the Java and grid APIs
 build.xml            Runs the provided example clients of the Java and grid
                      APIs via ant (required). This build file may also be
                      used as a reference for required classpaths for grid
                      and Java API client configuration.

/docs/api             Contains the Javadoc for all the caArray client classes

/example/src          Contains Java examples for connecting to the Java and
                      grid APIs

/grid/etc             Contains the grid client mapping configuration file
                      (client-config.wsdd). This directory must be on the
                      classpath for any grid clients using the caArray Java
                      classes.

/grid/lib             Contains JARs required at runtime for any caArray grid
                      service clients written in Java. These JARs are required
                      for grid access in addition to the jars in the /lib
                      directory.

/grid/schema          Contains the XSDs that define the caArray grid service.
                      These may be imported into grid projects in Introduce in
                      order to include caArray data types and services.

/lib                  Contains the JARs required at runtime for both Java API
                      and grid service API clients.


Invoking Examples
------------------------

The distribution includes classes that illustrate connecting to both the caArray
remote Java API and the grid service API. To invoke the examples, open a command
prompt or shell in the root of the unpacked distribution and run:

ant

By default, the examples will connect to the caArray 2.0 instance at NCI/CBIIT,
which has the hostname array.nci.nih.gov. To run these examples against
other caArray instances, you may invoke ant with the following properties:

ant -Dhostname=<hostname> -Djndi.port=<JNDI port> -Dgrid.port=<grid service port>

An existing installation of "ant" is required to run these examples as described
above.

Known Issues/Defects
------------------------

See the GForge tracker for the latest use cases (implemented
and deferred), existing open defects, community requests,
resolutions and feature requests. The following issues are
highlighted.


https://gforge.nci.nih.gov/tracker/?group_id=305

* [#11756] Grid service DataSet retrieval ten times slower than the
  remote Java API.

* Inefficiencies in byte[] marshalling in the grid service lead to
  out-of-memory problems and long transfer times for files of any
  significant size. It's recommended to use the remote Java API
  for remote file retrieval.

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