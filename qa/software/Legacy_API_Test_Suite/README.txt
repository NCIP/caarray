Legacy API test suite for caArray java and grid clients v1.0
--------------------------------------------------------------

RUNNING THE TEST SUITE
--------------------------------------------------------------

Before building and running the test suite, confirm that the
caArray project on this branch is fully built and deployed (i.e. ant deploy)
		 
To run the test suite via gui: 	in a command line shell, navigate to the
'Legacy_API_Test_Suite' directory and run the 'ant' command. This will compile
all the tests.

To run the test suite with ant: in a command line shell, navigate to the
'Legacy_API_Test_Suite' directory and run one of the following commands:
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


CONFIGURING THE TEST SUITE
--------------------------------------------------------------

The caArray server host names and ports can be set in the build.xml
file in the 'SERVER CONNECTION PROPERTIES' section. The directory
and files to which the test result report will be written can be
set in the build.xml file in the 'TEST RESULT REPORT PROPERTIES'
section.

Parameters for individual tests can be set in .csv files in the 
'config' directory. Configuration files will be added to the directory
as the supporting code is added to the test suite.

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
          
