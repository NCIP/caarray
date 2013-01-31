//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.suite;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.util.ArrayList;
import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestConfigurationException;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.TestResultReport;
import caarray.legacy.client.test.TestUtils;
import caarray.legacy.client.test.search.FileDownloadSearch;

/**
 * @author vaughng
 * Aug 14, 2009
 */
public class FileDownloadTestSuite extends ConfigurableTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + java.io.File.separator + "FileDownload.csv";

    private static final String FILE = "File";
    private static final String ARRAY_DESIGN = "Array Design";

    private static final String EXPECTED_BYTES = "Expected Bytes";
    private static final String MIN_BYTES = "Min Bytes";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, FILE, ARRAY_DESIGN, EXPECTED_BYTES, MIN_BYTES };
    
    private List<FileDownloadSearch> fileSearches = new ArrayList<FileDownloadSearch>();
    /**
     * @param apiFacade
     */
    public FileDownloadTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#constructSearches(java.util.List)
     */
    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException
    {
        int index = 1;
        String row = spreadsheetRows.get(index);
        List<Float> excludeTests = TestProperties.getExcludedTests();
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        FileDownloadSearch search = null;
        
        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            
            String[] input = TestUtils.split(row, DELIMITER);
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input))
            {
                search = new FileDownloadSearch();
                boolean skip = false;
                if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
                {
                    search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                                                              .trim()));
                    if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                    if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                }
                if (!skip)
                {
                    
                    populateSearch(input, search);  
                } 
                if (search != null)
                    fileSearches.add(search);
            }
            else
            {
                boolean skip = false;
                if (search == null)
                    throw new TestConfigurationException(
                            "No test case indicated for row: " + index);
                if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!skip)
                {
                    //TODO are additional values required?
                }
                
            }
    
            
    
            index++;
            if (index < spreadsheetRows.size())
            {
                row = spreadsheetRows.get(index);
            }
            else
            {
                row = null;
            }
        }
        filterSearches();
    }
    
    private void populateSearch(String[] input, FileDownloadSearch search)
    {
        CaArrayFile file = null;
        ArrayDesign arrayDesign = null;
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
       
        
        if (headerIndexMap.get(EXPECTED_BYTES) < input.length
                && !input[headerIndexMap.get(EXPECTED_BYTES)].equals(""))
            search.setExpectedBytes(Integer.parseInt(input[headerIndexMap.get(EXPECTED_BYTES)].trim()));
        
        if (headerIndexMap.get(MIN_BYTES) < input.length
                && !input[headerIndexMap.get(MIN_BYTES)].equals(""))
        {
            String bytes = input[headerIndexMap.get(MIN_BYTES)];
            search.setMinBytes(Integer.parseInt(bytes));
        }
         
        if (headerIndexMap.get(FILE) < input.length && !input[headerIndexMap.get(FILE)].equals(""))
        {
            String name = input[headerIndexMap.get(FILE)].trim();
            
            try
            {
                CaArrayFile exampleFile = new CaArrayFile();
                exampleFile.setName(name);
                List<CaArrayFile> fileList = apiFacade.searchByExample(search.getApi(), exampleFile, false);
                if (!fileList.isEmpty())
                {
                    file = fileList.get(0);
                }
                else
                {
                    System.out.println("No file found for: " + name);
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception retrieving file:" + name);
            }
             
        }
        if (headerIndexMap.get(ARRAY_DESIGN) < input.length && !input[headerIndexMap.get(ARRAY_DESIGN)].equals(""))
        {
            String name = input[headerIndexMap.get(ARRAY_DESIGN)].trim();
            try
            {
                ArrayDesign ad = new ArrayDesign();
                ad.setName(name);
                List<ArrayDesign> adList = apiFacade.searchByExample(search.getApi(), ad, false);
                if (!adList.isEmpty())
                {
                    arrayDesign = adList.get(0);
                }
                else
                {
                    System.out.println("No ArrayDesign found for: " + name);
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception retrieving ArrayDesign:" + name);
            }
            
        }
         
        if (file != null)
            search.setFile(file);
        if (arrayDesign != null)
            search.setArrayDesign(arrayDesign);
    }
    
    private void filterSearches()
    {
        String api = TestProperties.getTargetApi();
        if (!api.equalsIgnoreCase(TestProperties.API_ALL))
        {
            List<FileDownloadSearch> filteredSearches = new ArrayList<FileDownloadSearch>();
            for (FileDownloadSearch search : fileSearches)
            {
                if (search.getApi().equalsIgnoreCase(api))
                    filteredSearches.add(search);
            }
            fileSearches = filteredSearches;
        }
        List<Float> excludedTests = TestProperties.getExcludedTests();
        if (!excludedTests.isEmpty())
        {
            List<FileDownloadSearch> filteredSearches = new ArrayList<FileDownloadSearch>();
            for (FileDownloadSearch search : fileSearches)
            {
                if (!excludedTests.contains(search.getTestCase()) && !excludedTests.contains(search.getTestCase()))
                    filteredSearches.add(search);
            }
            fileSearches = filteredSearches;
        }
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        if (!includeTests.isEmpty())
        {
            List<FileDownloadSearch> filteredSearches = new ArrayList<FileDownloadSearch>();
            for (FileDownloadSearch search : fileSearches)
            {
                if (includeTests.contains(search.getTestCase()) || includeTests.contains((float)Math.floor(search.getTestCase())))
                    filteredSearches.add(search);
            }
            fileSearches = filteredSearches; 
        }
    }


    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#executeConfiguredTests(caarray.legacy.client.test.TestResultReport)
     */
    @Override
    protected void executeConfiguredTests(TestResultReport resultReport)
    {
        for (FileDownloadSearch search : fileSearches)
        {
            TestResult testResult = new TestResult();
            try
            {
                if (search.getApi() == null)
                {
                    setTestResultFailure(testResult, search,
                            "No API indicated for test case: "
                                    + search.getTestCase());
                    resultReport.addTestResult(testResult);
                    continue;
                }

                byte[] results = null;
                long startTime = System.currentTimeMillis();
                if (search.getArrayDesign() != null)
                {
                    try
                    {
                        results = apiFacade.readFile(search.getApi(), search.getArrayDesign().getFirstDesignFile());
                    }
                    catch (Throwable t)
                    {
                        String detail = "An exception occured executing a ArrayDesign download: "
                        + t.getClass() + " " + t.getLocalizedMessage();
                        testResult.addDetail(detail);
                        t.printStackTrace();
                    }
                    
                }
                else if (search.getFile() != null)
                {
                    try
                    {
                        results = apiFacade.readFile(search.getApi(), search.getFile());
                    }
                    catch (Throwable t)
                    {
                        String detail = "An exception occured executing a File download: "
                        + t.getClass() + " " + t.getLocalizedMessage();
                        testResult.addDetail(detail);
                        t.printStackTrace();
                    }
                }
                long elapsedTime = System.currentTimeMillis() - startTime;

                testResult.setElapsedTime(elapsedTime);
                if (search.getTestCase() != null)
                    testResult.setTestCase(search.getTestCase());

                evaluateResults(results, search, testResult);
            }
            catch (Throwable t)
            {

                setTestResultFailure(testResult, search,
                        "An exception occured executing a DataSet search: "
                                + t.getClass() + " " + t.getLocalizedMessage());
                t.printStackTrace();
            }

            resultReport.addTestResult(testResult);
        }
        
        System.out.println(getType() + " tests complete ...");
    }
    
    protected void setTestResultFailure(TestResult testResult,
            FileDownloadSearch search, String errorMessage)
    {
        testResult.setPassed(false);
        if (search.getTestCase() != null)
            testResult.setTestCase(search.getTestCase());
        testResult.addDetail(errorMessage);
    }

    private void evaluateResults(
            byte[] results, FileDownloadSearch search, TestResult testResult)
    {
        
        int size = 0;
        if (results != null)
        {
            size = results.length;
        }
        if (search.getExpectedBytes() != null)
        {
            
            if (size != search.getExpectedBytes())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of bytes, expected: "
                        + search.getExpectedBytes()
                        + ", actual number of bytes: " + size;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of bytes: "
                        + size;
                testResult.addDetail(detail);
            }
        }
        if (search.getMinBytes() != null)
        {
            
            if (size < search.getMinBytes())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of bytes, expected minimum: "
                        + search.getMinBytes()
                        + ", actual number of bytes: " + size;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of bytes: "
                        + size;
                testResult.addDetail(detail);
            }
        }
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        return "File Download";
    }

}
