Java clients to test the caArray GRID API v1.0:
-----------------------------------------------
1. The clients connect to array-stage.nci.nih.gov which is the NCI CBIIT Stage instance of caArray.
   If using a different installation of caArray, please set the right SERVER CONNECTION PROPERTIES in build.xml.
   You must set the right grid.server.hostname and grid.server.http.port.
2. All jar dependencies including the caArray client jar should be in the lib/directory.
   They are downloadable from Gforge under the caArray 2 project under the "Files" tab.
   Look for caarray-client-external-v1_0.zip and extract the following from it:
   * caarray-client-external-v1_0.jar
   * lib/grid/
3. ant targets:
   * TO BUILD:
     clean
     build
   * TO RUN VARIOUS TESTS:
     search_experiments_by_criteria
     search_experiments_by_keyword
     search_biomaterials_by_criteria
     search_biomaterials_by_keyword
     select_files
     download_file
     download_file_zip
     download_multiple_files
     download_data_columns_from_file
     download_data_columns_from_hyb
     download_data_columns_from_illumina_file
     download_data_columns_from_genepix_file
     download_sample_annotations
     lookup_entities
     download_magetab_export
     download_magetab_export_with_data
     download_array_design

-----
NOTE FOR THOSE TESTING A LOCAL INSTALLATION OF caArray (as opposed to array-stage):

The output of the tests depends on certain data being available
in the caArray system you are connecting to. Most tests rely on the
following public experiment being present:
   Name: Affymetrix Experiment for API Testing
   Array design: Test3.cdf
   Data files: test3_data.zip
The data/ directory contains these (and other) files if you need them.
-----
KNOWN ISSUES IN RC2 WHICH WILL BE FIXED BEFORE GA:

1. ant select_files: The search for Affymetrix CEL files throws an exception.
   The following WORKAROUND has been introduced in SelectFiles.java to address the problem:
   Replaced the following line:
      CaArrayEntityReference celFileTypeRef = getCelFileType();
   ... with the following line:
      CaArrayEntityReference celFileTypeRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:AFFYMETRIX_CEL");
2. ant lookup_entities: If you look at the file LookUpEntities.java, you will see that 3 methods are commented out:
   * lookupCharacteristicCategories()
   * lookupEntityByReference()
   * lookupEntitiesByReference()
   These methods don't work correctly in RC1.
3. ant download_data_columns_from_file, download_data_columns_from_hyb, download_sample_annotations:
   The search for Affymetrix CHP files throws an exception.
   The following WORKAROUND has been introduced in DownloadDataColumnsFromFile.java, DownloadDataColumnsFromHybridizations.java and DownloadSampleAnnotationsForHybridizations.java to address the problem:
   WORKAROUND:
   Replace the following line:
      CaArrayEntityReference chpFileTypeRef = getChpFileType();
   ... with the following line:
      CaArrayEntityReference chpFileTypeRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:AFFYMETRIX_CHP");
4. ant download_data_columns_from_genepix_file:
   The search for Genepix GPR files throws an exception.
   The following WORKAROUND has been introduced in DownloadDataColumnsFromGenepixFile.java to address the problem:
   WORKAROUND:
   Replace the following line:
      CaArrayEntityReference gprFileTypeRef = getGprFileType();
   ... with the following line:
      CaArrayEntityReference gprFileTypeRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:GENEPIX_GPR");

