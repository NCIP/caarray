Java clients to test the caArray Remote Java API and GRID API:
--------------------------------------------------------------
1. The jar dependencies are copied from an unpacked distribution of the caarray_client_legacy_2_5_0_0.zip,
    which is available from Gforge under the caArray 2 project under the "Files" tab.
    By default this is expected to be in ${user.home}/caarray-client-legacy_2_5_0_0.
2. Create a local.properties file from local.properties.example.  Uncomment any values that different from 
   the defaults.
      
   For the GRID API, you must set the right grid server name and grid service port (globoss.*).
   For the Remote Java API, you must set the right server name and jndi port (server.*). The jndi port is usually 
   1099 for a default Jboss installation; but check your Jboss installation's
   server/default/conf/jboss-service.xml for the Port corresponding to org.jboss.naming.NamingService.
     
3. ant targets:
   * TO BUILD:
     clean
     build
   * TO RUN A SEARCH:
     run_search_java
     run_search_grid
     run_search_using_credentials_java
   * TO DOWNLOAD THE BYTE CONTENTS OF A FILE:
     run_download_file_java
     run_download_file_grid
   * TO DOWNLOAD PARSED DATA:
     run_download_parsed_data_java
     run_download_parsed_data_grid
     run_download_parsed_data_from_file_java
   * TO DOWNLOAD ARRAY DESIGN DETAILS:
     run_download_array_design_java
     run_download_array_design_grid

NOTE: The output of the tests depends on certain data being available
in the caArray system you are connecting to. Most tests rely on the
following public experiment being present:
   Name: Affymetrix Experiment with CHP Data 01
   Array design: Test3.cdf
   Data files: Test3-1-121502.CHP and Test3-1-121502.CEL
The data/ directory contains these files if you need them.
