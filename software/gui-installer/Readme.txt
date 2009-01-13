Release Notes
=============
 
  #Product:#    caArray
  #Version:#    2.2.0
  #Date:#   January 2009


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


Introduction
---------------------------
 
caArray is an open-source, web and programmatically
 accessible array data management system. caArray guides the 
annotation and exchange of array data using a federated
model of local installations whose results are shareable 
across the cancer Biomedical Informatics Grid (caBIG�). 
caArray furthers translational cancer research through 
acquisition, dissemination and aggregation of semantically 
interoperable array data to support subsequent analysis by 
tools and services on and off the Grid. As array technology 
advances and matures, caArray will extend its logical 
library of assay management.

* https://array.nci.nih.gov/


Release History
------------------------

    * caArray v2.2.0        -- January  2009    
    * caArray v2.1.1        -- October  2008    
    * caArray v2.1.0        -- August   2008    
    * caArray v2.0.2        -- May      2008    
    * caArray v2.0.1        -- April    2008
    * caArray v2.0.0        -- February 2008


Anticipated Releases
------------------------
 
    * caArray v2.3.0        -- May 2009 (new features)


Features and Defects Addressed in this Release
-----------------------------------------------

This release represents a feature release of the caArray 2 software. 
Approximately 80 features/issues were addressed in this release.

Highlights of caArray 2.2.0 are:

* Sample search:
  * The user can now search for biomaterials (Samples and Sources)
    in the system.
  * The keyword search can be applied to one of a list of standard
    or predefined categories, or to arbitrary categories previously
    imported into the system via Characteristic[] columns in a
    MAGE-TAB SDRF document.

* Export experiment annotations in MAGE-TAB format:
  * The user can generate and download a MAGE-TAB file set describing
    the annotations of an experiment.
  * The generated MAGE-TAB includes the relationship between biomaterials, 
    hybridizations and data files, and also characteristics of the
    biomaterials.
  * The generated MAGE-TAB does not contain information about
    experimental factors, protocols, publications or people.


* Bulk-update of experiment annotations using MAGE-TAB import:
  * When a MAGE-TAB file set is being imported, the System recognizes
    references to existing biomaterials and hybridizations by name.
  * Biomaterial linkages can be modified in an additive way via
    MAGE-TAB import. Existing linkages and biomaterials cannot be
    deleted via MAGE-TAB import.
  * Biomaterial characteristics can be modified or new characteristics
    added via MAGE-TAB import.
  * However, experimental factors and protocols cannot be added/changed
    for existing biomaterials and hybridizations via MAGE-TAB import.
  * As part of the upgrade to 2.2.0, existing data will be scrubbed
    to make biomaterial and hybridization names unique within an experiment.
    In other words, all Sources with the same name (within an experiment) will
    be merged into the same Source. A similar procedure will be followed
    for Samples, Extracts and Labeled Extracts. But multiple Hybridizations
    with the same name (within an experiment) will be renamed to have
    distinct names. (The installer allows the user to override this
    default strategy and merge duplicate Hybridizations instead.)

* Support for GEO and ScanArray data:
  * Data files can be designated as GEO SOFT or GEO GSM and then
    imported (without parsing) and associated with samples.
  * An array provider called ScanArray is now supported, and ScanArray CSV
    data files can be imported (without parsing) and associated with samples.

* Enhanced usability of permissions management workflow:
  * While setting sample-selective access for an experiment, the user can
    do a keyword search for Samples against a set of categories, select
    the Samples of interest, and grant access to the selected Samples as
    a whole.

* Enhancements to the GUI installer to make more properties configurable.


Known Issues/Defects
------------------------
 
See the GForge tracker for existing open defects, community requests, 
resolutions and feature requests. The following issues are 
highlighted. 

https://gforge.nci.nih.gov/tracker/?group_id=305

* Data that was imported before caArray 2.1.0 has an issue where derived
  data files are not associated with the corresponding raw data files.
  A SQL script is provided to scrub pre-2.1.0 data to correct raw-derived
  data relationships. Please download the fix_raw_derived_relationships.zip
  package from the caArray2 Gforge site. It contains a README.txt and a
  SQL script. It is highly recommended that you run this SQL script against
  your database, since otherwise, the MAGE-TAB export of some of your
  pre-2.1.0 experiments can be incorrect.

* The total import job size (defined as sum of uncompressed file sizes)
  must be less than 3GB.

* A collaborator cannot see files in the "Uploaded" state even with
  read-write permissions to the experiment.

* The installer does not support configuring SSL support in JBoss.

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

Links to all documentation and files can be found at: 

http://caarray.nci.nih.gov/


NCICB Web Pages
---------------

    * The NCI Center for Bioinformatics, http://ncicb.nci.nih.gov/
    * NCICB Application Support, http://ncicb.nci.nih.gov/NCICB/support
    * NCICB Download Center, http://ncicb.nci.nih.gov/download/