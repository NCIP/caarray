Release Notes
=============
 
  #Product:#	caArray
  #Version:#	2.5.0
  #Date:#	July 2012

Contents
--------

   1. Introduction
   2. Release History
   3. Anticipated Releases
   4. Features and Defects Addressed in this Release
   5. Known Issues/Defects
   6. Bug Reports, Feature Requests and Support
   7. Documentation and Files
   8. NCICB Web Pages
   9. Getting Started with the new API


Introduction
---------------------------
 
caArray is an open-source, web and programmatically accessible array data management system. caArray guides the 
annotation and exchange of array data using a federated model of local installations whose results are shareable 
across the cancer Biomedical Informatics Grid (caBIG). caArray furthers translational cancer research through 
acquisition, dissemination and aggregation of semantically interoperable array data to support subsequent analysis by 
tools and services on and off the Grid. As array technology advances and matures, caArray will extend its logical 
library of assay management.

    * NCI-hosted caArray: https://array.nci.nih.gov/


Release History
------------------------
    * caArray v2.5.0		-- July      2012
    * caArray v2.4.1		-- May       2011 
    * caArray v2.4.0.2		-- February  2011 
    * caArray v2.4.0.1		-- January   2011 
    * caArray v2.4.0		-- September 2010 
    * caArray v2.3.1		-- February  2010 
    * caArray v2.3.0.1		-- November  2009   
    * caArray v2.2.1		-- May       2009    
    * caArray v2.2.0		-- January   2009    
    * caArray v2.1.1		-- October   2008    
    * caArray v2.1.0		-- August    2008    
    * caArray v2.0.2		-- May       2008    
    * caArray v2.0.1		-- April     2008
    * caArray v2.0.0		-- February  2008


Anticipated Releases
------------------------
 
    * caArray v2.5.1	--   Q1 2013 (maintenance release)


Features and Defects Addressed in this Release
-----------------------------------------------

This release represents a feature release of the caArray 2 software. Highlights of caArray 2.5.0 are:

* Large data support:
  ** Large import jobs are now transparently split up into smaller transactions, removing the need for the user to manually split large imports into smaller subsets and individually import.
  ** Large filesets can be uploaded easily via multiple file selection, drag and drop, and chunked upload capabilities. (The IE browser does not support multiple file selection and >2GB individual file uploads; so other browsers are recommended for large upload needs.)

* Improved file storage: Uploaded files are now stored on the file system instead of in the database, thus eliminating inefficiencies.

* A plugin architecture allowing the user community to develop and deploy parsers for new data types easily.

* Major technology upgrades: Jboss 5.1, Java 6, caGrid v1.5, Struts 2.3.1.5, and NCI build-and-deploy infrastructure (NCI Nexus repository, AntHill Pro 3, BDALite).

* Ability to restrict access to samples based on sample characteristics (e.g., Treatment Arm).

* Support for automatic refresh of experiments from caIntegrator by providing information about sample and data changes.

* An audit trail of sample and data additions and deletions.

* Ability to configure Single Sign-on between caArray and caIntegrator.

* Section 508 compliance improvements.

* Several bug fixes including ARRAY-1342 (A collaborator cannot see files in the "Uploaded" state even with read-write permissions to the experiment).


Known Issues/Defects
------------------------
 
See the Jira tracker for existing open defects, community requests, resolutions and feature requests.
https://tracker.nci.nih.gov/browse/ARRAY

The following issues are highlighted. 

* If you are still using the Legacy API and your query results in >10,000 results, your transaction
  may time out. The workaround is to modify your query into multiple queries that return smaller result sets.

* The installer does not support configuring SSL support in JBoss.

* After installing a local copy of caArray, you must go to http://<IP_address_or_host_name>:<port>/caarray
  in order to test your installation. Even if you are running the browser on the same machine as your caArray
  installation, you cannot go to http://localhost:<port>/caarray.

* Image files referenced in a MAGE-TAB SDRF cannot be validated or imported.

* Multiple MAGE-TAB imports can result in duplicate persons in the database.

* GUI installer suddenly quits when illegal characters present in password fields.

* The Affymetrix CDF array design parser does not connect probes to features correctly. API clients should be
  aware of this limitation.


Bug Reports, Feature Requests, And Support
------------------------------------------

Send email to ncicb@pop.nci.nih.gov to request technical support. To report a bug or request a new feature,
please visit the Molecular Analysis Tools Knowledge Center resources at:

https://cabig-kc.nci.nih.gov/Molecular/KC/index.php/Main_Page

Existing requests and resolution may be viewed at the caArray issue tracker URL:

https://tracker.nci.nih.gov/browse/ARRAY


Documentation And Files
-----------------------
Wiki-based guides are available at the following locations:

   caArray 2.5.0 User's Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.5+User%27s+Guide
   caArray 2.5.0 Installation Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.5.0+Data+Portal+Local+Installation+Guide+Wiki+Main+Page
   caArray 2.5.0 Technical Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.5.0+Technical+Guide+Wiki+Main+Page
   caArray 2.5.0 API Guide (unchanged for this release): https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.5.0+API+Guide

Links to all other documentation and files can be found at: 

   https://cabig.nci.nih.gov/tools/caArray


NCI CBIIT Web Pages
---------------

    * The NCI Center for Bioinformatics, http://ncicb.nci.nih.gov/
    * NCI CBIIT Application Support, http://ncicb.nci.nih.gov/NCICB/support
    * NCI CBIIT Download Center, http://ncicb.nci.nih.gov/download/


Getting Started with the caArray API
------------------------------------

To get started with the caArray Service API v1.0, please download the client libraries
(caarray-client-external-v1_0.zip). Example client code for the Java API is available
in java_api_client_examples_v1_0.zip. Example client code for the Grid API is available
in grid_api_client_examples_v1_0.zip. Both the example packages contain a README.txt that will get you started.

Please note that applications with no authentication requirement can use the Java API or the Grid API.
But applications needing authenticated access must use the Java API.

If, for some reason, you do not wish to use the new Service API yet, you can continue to use the legacy API,
but you must upgrade to the latest version. To do this, please download the client libraries (caarray-client-legacy.zip).


FEEDBACK

Please post feedback on the Molecular Analysis Tools Knowledge Center forum:
https://cabig-kc.nci.nih.gov/Molecular/KC/index.php/Main_Page

