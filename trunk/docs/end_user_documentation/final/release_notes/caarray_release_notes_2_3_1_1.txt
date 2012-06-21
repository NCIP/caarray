Release Notes
=============
 
  #Product:#	caArray
  #Version:#	2.3.1.1
  #Date:#	February 2010

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
 
caArray is an open-source, web and programmatically
accessible array data management system. caArray guides the 
annotation and exchange of array data using a federated
model of local installations whose results are shareable 
across the cancer Biomedical Informatics Grid (caBIG). 
caArray furthers translational cancer research through 
acquisition, dissemination and aggregation of semantically 
interoperable array data to support subsequent analysis by 
tools and services on and off the Grid. As array technology 
advances and matures, caArray will extend its logical 
library of assay management.

* https://array.nci.nih.gov/


Release History
------------------------

    * caArray v2.3.1		-- February 2010 
    * caArray v2.3.0		-- November 2009   
    * caArray v2.2.1		-- May	   2009    
    * caArray v2.2.0		-- January  2009    
    * caArray v2.1.1		-- October  2008    
    * caArray v2.1.0		-- August   2008    
    * caArray v2.0.2		-- May      2008    
    * caArray v2.0.1		-- April    2008
    * caArray v2.0.0		-- February 2008


Anticipated Releases
------------------------
 
    * caArray v2.4.0	-- Summer 2010 (new features)


Features and Defects Addressed in this Release
-----------------------------------------------

This release represents a feature release of the caArray 2 software. 
Highlights of caArray 2.3.1 are:

* Export to GEO: Affymetrix single-channel CHP experiments
* 17972: Allow deletion of already imported but not parsed data files

Known Issues/Defects
------------------------
 
See the GForge tracker for existing open defects, community requests, 
resolutions and feature requests. The following issues are 
highlighted. 

https://gforge.nci.nih.gov/tracker/?group_id=305

* The total import job size (defined as sum of uncompressed file sizes)
  must be less than 3GB.

* A collaborator cannot see files in the "Uploaded" state even with
  read-write permissions to the experiment.

* The installer does not support configuring SSL support in JBoss.

* After installing a local copy of caArray, you must go to
  http://<IP_address_or_host_name>:<port>/caarray in order to test
  your installation. Even if you are running the browser on the same
  machine as your caArray installation, you cannot go to
  http://localhost:<port>/caarray.

* Image files referenced in a MAGE-TAB SDRF cannot be validated or
  imported.

* Multiple MAGE-TAB imports can result in duplicate persons in the database.


Bug Reports, Feature Requests, And Support
------------------------------------------

Send email to ncicb@pop.nci.nih.gov to request technical support.  To 
report a bug or request a new feature, please visit the Molecular Analysis Tools 
Knowledge Center resources at:

https://cabig-kc.nci.nih.gov/Molecular/KC/index.php/Main_Page

Existing requests and resolution may be viewed at the caArray GForge URL:

https://gforge.nci.nih.gov/tracker/?group_id=305


Documentation And Files
-----------------------
Please note: new wiki based technical guide and the installation guide can be 
found at 
following locations:

   caArray 2.3.1 Installation Guide: https://wiki.nci.nih.gov/x/QTZyAQ
   caArray 2.3.0 Technical Guide (unchanged for this release): https://wiki.nci.nih.gov/x/eiIhAQ
   caArray 2.3.0 API Guide (unchanged for this release): https://wiki.nci.nih.gov/x/EgBLAQ

Links to all other documentation and files can be found at: 

   http://caarray.nci.nih.gov/


NCICB Web Pages
---------------

    * The NCI Center for Bioinformatics, http://ncicb.nci.nih.gov/
    * NCICB Application Support, http://ncicb.nci.nih.gov/NCICB/support
    * NCICB Download Center, http://ncicb.nci.nih.gov/download/


Getting Started with the new API
--------------------------------

To get started with the new caArray Service API v1.0, please download
the client libraries (caarray-client-external-v1_0.zip). Example client
code for the Java API is available in java_api_client_examples_v1_0.zip.
Example client code for the Grid API is available in grid_api_client_examples_v1_0.zip.
Both the example packages contain a README.txt that will get you started.

Please note that applications with no authentication requirement can use
the Java API or the Grid API. But applications needing authenticated access
must use the Java API. We will add authentication support to the Grid API
in a future release.

If, for some reason, you do not wish to use the new Service API yet, you
can continue to use the legacy API, but you must upgrade to the latest version.
To do this, please download the client libraries (caarray-client-legacy.zip).



FEEDBACK

Please post feedback on the Molecular Analysis Tools Knowledge Center 
forum:
https://cabig-kc.nci.nih.gov/Molecular/KC/index.php/Main_Page