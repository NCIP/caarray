Release Notes
=============
 
  #Product:#	caArray
  #Version:#	2.4.1
  #Date:#	September  2011

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

* https://array.nci.nih.gov/


Release History
------------------------
    * caArray v2.4.1		-- September 2011 
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
 
    * caArray v2.5.0	--   Q1 2012 (new features)


Features and Defects Addressed in this Release
-----------------------------------------------

This release represents a feature release of the caArray 2 software. Highlights of caArray 2.4.1 are:

* Visibility into the queue of import and validation jobs, and where your job is in relation to other jobs in the system. Ability to cancel a queued job.
  For details about this feature, please refer to the chapter "Submitting Data" in the caArray User's Guide.

* Search for experiments by publication author or PubMed ID.

* Organisms are now restricted to terms from the NCBI Taxonomy.

* Bug fix to allow proper import of Agilent array design files where the chromosome name is not in a strictly numeric or X/Y format.

* The installer no longer depends on an internet connection.

* Fix to bug where experiments with multiple array designs may cause copy number data to get imported as "Imported (Not Parsed)".


Known Issues/Defects
------------------------
 
See the Jira tracker for existing open defects, community requests, resolutions and feature requests.
https://tracker.nci.nih.gov/browse/ARRAY

The following issues are highlighted. 

* The total import job size (defined as sum of uncompressed file sizes) must be less than 3GB.

* A collaborator cannot see files in the "Uploaded" state even with read-write permissions to the experiment.

* If you are still using the Legacy API and your query results in >10,000 results, your transaction
  may time out. The workaround is to modify your query into multiple queries that return smaller result sets.

* The installer does not support configuring SSL support in JBoss.

* After installing a local copy of caArray, you must go to http://<IP_address_or_host_name>:<port>/caarray
  in order to test your installation. Even if you are running the browser on the same machine as your caArray
  installation, you cannot go to http://localhost:<port>/caarray.

* Image files referenced in a MAGE-TAB SDRF cannot be validated or imported.

* Multiple MAGE-TAB imports can result in duplicate persons in the database.

* Experiments with multiple array designs may cause copy number data to be Imported (not parsed).  For more
  information, please go to https://tracker.nci.nih.gov/browse/ARRAY-1811.

* Data import can fail if experiment is associated with multiple array designs (https://tracker.nci.nih.gov/browse/ARRAY-1837).

* GUI installer suddenly quits when illegal characters present in password fields.

* For Agilent GEML Array Design files for miRNA, accessions are not created. See https://tracker.nci.nih.gov/browse/ARRAY-2041

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

   caArray 2.4.0 User's Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.4+User%27s+Guide
   caArray 2.4.0 Installation Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.4.0+Data+Portal+Local+Installation+Guide+Wiki+Main+Page
   caArray 2.4.0 Technical Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.4.0+Technical+Guide+Wiki+Main+Page
   caArray 2.4.0 API Guide (unchanged for this release): https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.4.0+API+Guide

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
But applications needing authenticated access must use the Java API. We will add authentication support to the Grid API
in a future release.

If, for some reason, you do not wish to use the new Service API yet, you can continue to use the legacy API,
but you must upgrade to the latest version. To do this, please download the client libraries (caarray-client-legacy.zip).


FEEDBACK

Please post feedback on the Molecular Analysis Tools Knowledge Center forum:
https://cabig-kc.nci.nih.gov/Molecular/KC/index.php/Main_Page
