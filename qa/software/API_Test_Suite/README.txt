External API test suite for caArray java and grid clients v1.0
--------------------------------------------------------------

RUNNING THE TEST SUITE
--------------------------------------------------------------

Before building and running the test suite, confirm that the
caArray project on this branch is fully built and deployed (i.e. ant deploy)
		 
To run the test suite via gui: 	in a command line shell, navigate to the
'API_Test_Suite' directory and run the 'ant' command. This will build.

To run the test suite with ant: in a command line shell, navigate to the
'API_Test_Suite' directory and run one of the following commands:
	1) ant test_all
	   This command will build and run the full test
	   suite against both the java and grid APIs.
	2) ant test_java
	   This command will build and run only the portions of the
	   test suite pertaining to the java API.
	3) ant test_grid
	   This command will build and run only the portions of the
	   test suite pertaining to the grid API.

A test result report .csv file will be written to the 'report' directory
upon completion of the test suite.


LOAD TESTING
--------------------------------------------------------------

Load testing can be initiated via the gui by setting 'Number of Threads'
to the number of concurrent test threads that you wish to run, or by invoking
the ant target load_test and setting the 'threads.num' build property. Instances
of each selected test will be run concurrently in separate threads, and the
results will be written to Load_Tests_API_Test_Results.csv. A simple analysis
of the load tests will be written to Load_Test_Analysis.csv, indicating
any discrepancies between threads, for instance if a particular test passes
in one thread but fails in another, or if a particular test takes significantly
longer to execute in one thread than another.


CONFIGURING THE TEST SUITE
--------------------------------------------------------------

The caArray server host names and ports can be set in the build.xml
file in the 'SERVER CONNECTION PROPERTIES' section, or in the gui. The directory
and files to which the test result report will be written can be
set in the build.xml file in the 'TEST RESULT REPORT PROPERTIES'
section.

Parameters for individual tests can be set in .csv files in the 
'config' directory. Modifications to existing test cases can be
made by updating parameters in the corresponding configuration
file, and new tests can be added by adding rows to configuration
files. However, if additional columns must be added to the configuration
files to accommodate unanticipated test parameters, the corresponding
TestSuite object code must be updated appropriately.

General rules for configuring test parameters:
Parameters for an individual test must be set in one or more rows of
the appropriate .csv file. Most tests will require/allow only a single
row of input; however, for parameters that allow multiple values, additional
values should be set in subsequent rows. For example, a specification for
an ArrayDesign with a single AssayType may look like:

Test Case | API | AssayType | Expected Results
------------------------------------------------
32        |java |  type1    |      20

while a specification for an ArrayDesign with multiple AssayTypes will
look like: 

Test Case | API | AssayType | Expected Results
------------------------------------------------
33        |java |  type1    |      12
------------------------------------------------
          |     |  type2    |                    
          
IMPORTANT: A test case must be specified for each individual test, as an
entry in the 'Test Case' column is used to distinguish a new test from a 
continuation of a test case on multiple rows. If the test does not correspond 
to a documented test case, a test case of '0' should be used.

Configuration parameters common to all or most configuration files:
Test Case: Indicates the Test Case ID of the test case in the 'API_backlog.xls' 
	test plan to which a test corresponds.
API: One of either 'java' or 'grid', indicating the API to be used for the test
Expected Results: Expected number of results for a search. Only an exact match
	will count as a successful test.
Min Results: Minimum number of search results expected, for which any number
	of results greater than or equal to will be considered successful.
	
	
-----------------------------------------------------------------------------

BUILDING THE CLIENT JAR
-----------------------------------------------------------------------------

To build a new client jar for an updated version of caArray:
You can build the client zips by checking out the tag https://gforge.nci.nih.gov/svnroot/caarray2/tags/<release#, e.g. CAARRAY_R2_3_0_RC9>/software 
and running an ant build:caarray-client.zip from the software/build folder.
You will also need to have the docs/analysis_and_design/models directory checked out in addition to the software directory.
Finally, you have to be using Java 5, not Java 6.


          
