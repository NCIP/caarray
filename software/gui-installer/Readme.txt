Release Notes
=============
 
  #Product:#	caArray
  #Version:#	2.5.2
  #Date:#	January 2014

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
    * caArray v2.5.2		-- January 2014
    * caArray v2.5.1		-- June 2013
    * caArray v2.5.0		-- July 2012
    * caArray v2.4.1		-- May 2011 
    * caArray v2.4.0.2		-- February 2011 
    * caArray v2.4.0.1		-- January 2011 
    * caArray v2.4.0		-- September 2010 
    * caArray v2.3.1		-- February 2010 
    * caArray v2.3.0.1		-- November 2009   
    * caArray v2.2.1		-- May 2009    
    * caArray v2.2.0		-- January 2009    
    * caArray v2.1.1		-- October 2008    
    * caArray v2.1.0		-- August 2008    
    * caArray v2.0.2		-- May 2008    
    * caArray v2.0.1		-- April 2008
    * caArray v2.0.0		-- February 2008


Features and Defects Addressed in this Release
-----------------------------------------------

This release includes enhanced auditing and other features to enable full FISMA compliance.

 * [ARRAY-2008] - Add a warning banner to caArray
 * [ARRAY-2010] - Experiment creation and deletion need to be an audited event
 * [ARRAY-2671] - Audit changes to array designs
 * [ARRAY-2672] - Audit changes to terms
 * [ARRAY-2673] - Audit account logon events
 * [ARRAY-2675] - Add time to the audit log details in the interface
 * [ARRAY-2018] - Add warning to experiment owner when an experiment is made public
 * [ARRAY-2688] - Updated footer
 * [ARRAY-2666] - Audit changes to experiment properties
 * [ARRAY-2668] - Audit experiment file deletion
 * [ARRAY-2619] - Move the caArray Release Notes to the Confluence Wiki
 * [ARRAY-2694] - Change "Help" link to "User Guide"
 * [ARRAY-2693] - There is no warning message if experiment owner is trying to change an experiment public permission from "No Visibility" to "Read Selective"
 * [ARRAY-2695] - MAGE-TAB link on home page leads to 502 Bad Gateway error
 * [ARRAY-2696] - Release notes link is not pointed to the correct release notes
 * [ARRAY-2670] - Changing only an experiment's assay type or array design does not get audited
 * [ARRAY-2645] - Error Adding GenePix Array Design


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

https://wiki.nci.nih.gov/display/MATKC/Molecular+Analysis+Tools+Knowledge+Center

Existing requests and resolution may be viewed at the caArray issue tracker URL:

https://tracker.nci.nih.gov/browse/ARRAY


Documentation And Files
-----------------------
Wiki-based guides are available at the following locations:

   caArray 2.5.0 User's Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+User%27s+Guide
   caArray 2.5.0 Installation Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.5+Data+Portal+Local+Installation+Guide
   caArray 2.5.0 Technical Guide: https://wiki.nci.nih.gov/display/caArray2doc/caArray+2.5.0+Technical+Guide
   caArray 2.5.0 API Guide (unchanged for this release): https://wiki.nci.nih.gov/display/caArray2doc/caArray+API+Guide

Links to all other documentation and files can be found at: 
   https://wiki.nci.nih.gov/display/caArray2doc/caArray2+Documentation
   https://wiki.nci.nih.gov/display/caArray2/caArray+Downloads


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
https://wiki.nci.nih.gov/display/MATKC/Molecular+Analysis+Tools+Knowledge+Center

