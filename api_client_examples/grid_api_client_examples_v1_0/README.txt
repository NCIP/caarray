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
KNOWN ISSUES IN RC3 WHICH WILL BE FIXED BEFORE GA:

* The following ant targets fail because of a known bug where the data type of a QuantitationType nees to be set before doing an example search:
     download_data_columns_from_file
     download_data_columns_from_illumina_file
     download_data_columns_from_genepix_file
* Biomaterial keyword search is very slow. This affects the following ant targets:
     search_biomaterials_by_keyword
     lookup_entities
