Release Notes
=============
 
  #Product:#	caArray
  #Version:#	2.1.1
  #Date:#	October 2008

Contents
--------

   1. Introduction
   2. Release History
   3. Anticipated Releases
   4. Defects and Feature Addressed in This Release
   5. Known Issues/Defects
   6. Bug Reports, Feature Requests, and Support
   7. Documentation and Files
   8. NCICB Web Pages

Introduction
---------------------------
 
caArray is an open-source, web and programmatically
 accessible array data management system. caArray guides the 
annotation and exchange of array data using a federated
model of local installations whose results are shareable 
across the cancer Biomedical Informatics Grid (caBIG™). 
caArray furthers translational cancer research through 
acquisition, dissemination and aggregation of semantically 
interoperable array data to support subsequent analysis by 
tools and services on and off the Grid. As array technology 
advances and matures, caArray will extend its logical 
library of assay management.

* https://array.nci.nih.gov/

Release History
------------------------
    * caArray v2.1.0		-- August   2008    
    * caArray v2.0.2		-- May      2008    
    * caArray v2.0.1		-- April    2008
    * caArray v2.0.0		-- February 2008
    * caArray v1.6		-- November 2007
    * caArray v1.5.0.2		-- August   2007
    * caArray v1.5.0.1		-- July     2007
    * caArray v1.5		-- June     2007
    * caArray v1.4		-- October  2006
    * caArray v1.3.1		-- March    2006

Anticipated Releases
------------------------
 
    * caArray v2.2.0		-- December 2008 (new features)

Defects and Feature Addressed in This Release
-----------------------------------------------
This release represents a minor release of the caArray 2.1 software. 

The highlight of caArray 2.1.1 feature is the support of PGF and CLF files for Affymetrix array design import.

Defects and Features Addressed in This Release
----------------------------------------------
* 15164 Disallow Import of Extra Data Files not in MAGE-TAB
* 15416 Intermittent failure to import large sets of data files after successful validation
* 16243 CSS Vulnerability exists in many places in the application
* 16316 Experiment does not save array design
* 16464 Unexpected error: Search All categories from upper right search box
* 11925 Support PGF and CLF files for Affymetrix array design import
* 13809 Allow csm_application name for caArray to be configurable
* 15398 Error - Request-URI Too Large - encountered when trying to sort by file type
* 16026 validated cel files can not pass import for experiment-id-1015897590400415
* 16190 Allow release note link to be controlled from the properties file
* 16431 Delete array design - causes a database lock and unexpected error in UI
* 16471 Unexpected error while deleting an experiement with large set of Data
* 16472 Unexpected error when adding an Array design 
* 13145 Upgrade Installer caArray 2.0.1 : Interactive installer message defect
* 13234 Allow deletion of array designs to which no experiments are associated
* 15165 "Make Manage Data page more usable by sorting  filtering and providing counts."
* 15317 Grid registration needs to be configurable
* 15580 Reorder search category 
* 15828 Bugs in the management of protocols associated with an annotation
* 16257 Don't allow the user to edit a file for an imported array design
* 16367 Add more robust user error propagation
* 16378 "release notes URL config should come from default.properties not tier-specific property files"
* 16396 Display confirmation message before deleting an Array Design.
* 16447 Manage Array Design: delete array design
* 16463 caArray installer should configure serviceMetadata.xml for grid service.
* 16474 Message shown when an attempt to delete an array design is unsuccessful persists on subsequent pages
* 16509 PGF and CLF import failed on local installation
* 16510 Adding a Array design cause unexpected error on UI 
* 16592 Local Installer:  Grid index server property should default to PROD
* 16966 Upgrade installer - csm property required in the upgrade.properties file
* 16990 XSS Vulnerability in manage vocabulary pages

Known Issues/Defects
------------------------
 
See the GForge tracker for the latest use cases (implemented 
and deferred), existing open defects, community requests, 
resolutions and feature requests. The following issues are 
highlighted. 


https://gforge.nci.nih.gov/tracker/?group_id=305

* 16398 Error encountered when trying to edit an experiment while uploading cell files to it.
* 16270 Support uploading Zip files larfger than approximately 1 GB
* 15798 Error during upload while other files were importing
* 15421 Illumina data passes validation but fails import without any message
* 14630 A collaborator that uploads files cannot see them in the manage data tab


Bug Reports, Feature Requests, And Support
------------------------------------------

Send email to ncicb@pop.nci.nih.gov to request technical support.  To 
report a bug or request a new feature, please visit the Molecular Analysis Tools 
Knowledge Center resources at:

https://cabig-kc.nci.nih.gov/Molecular/KC/index.php/Main_Page

Existing requests and resolution may be viewed at the caArray GForge URL:

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
