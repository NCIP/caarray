/**
 * 
 */
package caarray.legacy.client.test.suite;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestConfigurationException;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.TestResultReport;
import caarray.legacy.client.test.TestUtils;
import caarray.legacy.client.test.search.CQLSearch;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class CQLTestSuite extends ConfigurableTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "CQL.csv";

    private static final String TARGET = "Target Name";
    private static final String ASSOCIATION = "Association Name";
    private static final String ATTRIBUTE = "Attribute Name";
    private static final String ATTRIBUTE_VALUE = "Attribute Value";
    private static final String ASSOC_ATTRIBUTE = "Association Attribute Name";
    private static final String ASSOC_ATTRIBUTE_VALUE = "Association Attribute Value";
    private static final String ROLE = "Association Role";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
           API, TARGET, ASSOCIATION, ATTRIBUTE, ATTRIBUTE_VALUE, ASSOC_ATTRIBUTE, ASSOC_ATTRIBUTE_VALUE,ROLE, EXPECTED_RESULTS, MIN_RESULTS};
    
    private List<CQLSearch> cqlSearches = new ArrayList<CQLSearch>();
    /**
     * @param apiFacade
     */
    public CQLTestSuite(ApiFacade apiFacade)
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
        CQLSearch search = null;
        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            
            String[] input = TestUtils.split(row, DELIMITER);
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input))
            {
                search = new CQLSearch();
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
                    cqlSearches.add(search);
                } 
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
                    populateAdditionalSearchValues(input, search);
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

    protected void populateSearch(String[] input, CQLSearch search)
    {
        CQLQuery cqlQuery = new CQLQuery();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        Object target = new Object();
        
        if (headerIndexMap.get(TARGET) < input.length && !input[headerIndexMap.get(TARGET)].equals(""))
            target.setName(input[headerIndexMap.get(TARGET)].trim());
        
        Association association = null;
        if (headerIndexMap.get(ASSOCIATION) < input.length
                && !input[headerIndexMap.get(ASSOCIATION)].equals(""))
        {
            association = new Association();
            association.setName(input[headerIndexMap.get(ASSOCIATION)].trim());
        }
        if (headerIndexMap.get(ROLE) < input.length
                && !input[headerIndexMap.get(ROLE)].equals(""))
        {
            association.setRoleName(input[headerIndexMap.get(ROLE)].trim());
        }
        Attribute attribute = null;
        if (headerIndexMap.get(ATTRIBUTE) < input.length
                && !input[headerIndexMap.get(ATTRIBUTE)].equals(""))
        {
            attribute = new Attribute();
            attribute.setName(input[headerIndexMap.get(ATTRIBUTE)].trim());
            attribute.setPredicate(Predicate.EQUAL_TO);
        }
        if (headerIndexMap.get(ATTRIBUTE_VALUE) < input.length
                && !input[headerIndexMap.get(ATTRIBUTE_VALUE)].equals(""))
        {
            attribute.setValue(input[headerIndexMap.get(ATTRIBUTE_VALUE)].trim());
        }
        Attribute associationAttribute = null;
        if (headerIndexMap.get(ASSOC_ATTRIBUTE) < input.length
                && !input[headerIndexMap.get(ASSOC_ATTRIBUTE)].equals(""))
        {
            associationAttribute = new Attribute();
            associationAttribute.setName(input[headerIndexMap.get(ASSOC_ATTRIBUTE)].trim());
            associationAttribute.setPredicate(Predicate.EQUAL_TO);
        }
        if (headerIndexMap.get(ASSOC_ATTRIBUTE_VALUE) < input.length
                && !input[headerIndexMap.get(ASSOC_ATTRIBUTE_VALUE)].equals(""))
        {
            associationAttribute.setValue(input[headerIndexMap.get(ASSOC_ATTRIBUTE_VALUE)].trim());
        }
        
        if (association != null && associationAttribute != null)
        {
            association.setAttribute(associationAttribute);
        }
        if (association != null)
        {
            target.setAssociation(association);
        }
        if (attribute != null)
        {
            target.setAttribute(attribute);
        }
        
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer
                    .parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        
        cqlQuery.setTarget(target);
        search.setCqlQuery(cqlQuery);
    }
    
    private void populateAdditionalSearchValues(String[] input, CQLSearch search)
    {
        CQLQuery query = search.getCqlQuery();
        Object target = query.getTarget();
        Association asc_1 = target.getAssociation();
        
        Association association = new Association();
        
        if (headerIndexMap.get(ASSOCIATION) < input.length
                && !input[headerIndexMap.get(ASSOCIATION)].equals(""))
        {
            association.setName(input[headerIndexMap.get(ASSOCIATION)].trim());
        }
        if (headerIndexMap.get(ROLE) < input.length
                && !input[headerIndexMap.get(ROLE)].equals(""))
        {
            association.setRoleName(input[headerIndexMap.get(ROLE)].trim());
        }
        Attribute associationAttribute = null;
        if (headerIndexMap.get(ASSOC_ATTRIBUTE) < input.length
                && !input[headerIndexMap.get(ASSOC_ATTRIBUTE)].equals(""))
        {
            associationAttribute = new Attribute();
            associationAttribute.setName(input[headerIndexMap.get(ASSOC_ATTRIBUTE)].trim());
            associationAttribute.setPredicate(Predicate.EQUAL_TO);
        }
        if (headerIndexMap.get(ASSOC_ATTRIBUTE_VALUE) < input.length
                && !input[headerIndexMap.get(ASSOC_ATTRIBUTE_VALUE)].equals(""))
        {
            associationAttribute.setValue(input[headerIndexMap.get(ASSOC_ATTRIBUTE_VALUE)].trim());
        }
        if (association != null && associationAttribute != null)
        {
            association.setAttribute(associationAttribute);
        }
        Group group = new Group();
        group.setAssociation(new Association[]{asc_1,association});
        group.setLogicRelation(LogicalOperator.AND);
        target.setAssociation(null);
        target.setGroup(group);
    }

    private void filterSearches()
    {
        String api = TestProperties.getTargetApi();
        if (!api.equalsIgnoreCase(TestProperties.API_ALL))
        {
            List<CQLSearch> filteredSearches = new ArrayList<CQLSearch>();
            for (CQLSearch search : cqlSearches)
            {
                if (search.getApi().equalsIgnoreCase(api))
                    filteredSearches.add(search);
            }
            cqlSearches = filteredSearches;
        }
        List<Float> excludedTests = TestProperties.getExcludedTests();
        if (!excludedTests.isEmpty())
        {
            List<CQLSearch> filteredSearches = new ArrayList<CQLSearch>();
            for (CQLSearch search : cqlSearches)
            {
                if (!excludedTests.contains(search.getTestCase()) && !excludedTests.contains(search.getTestCase()))
                    filteredSearches.add(search);
            }
            cqlSearches = filteredSearches;
        }
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        if (!includeTests.isEmpty())
        {
            List<CQLSearch> filteredSearches = new ArrayList<CQLSearch>();
            for (CQLSearch search : cqlSearches)
            {
                if (includeTests.contains(search.getTestCase()) || includeTests.contains((float)Math.floor(search.getTestCase())))
                    filteredSearches.add(search);
            }
            cqlSearches = filteredSearches; 
        }
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#executeConfiguredTests(caarray.legacy.client.test.TestResultReport)
     */
    @Override
    protected void executeConfiguredTests(TestResultReport resultReport)
    {
        for (CQLSearch search : cqlSearches)
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
                else{ //MT
                	testResult.setType(search.getApi()); //MT
                } //MT

                
                java.lang.Object results = null;
                long startTime = System.currentTimeMillis();
                try
                {
                    results = apiFacade.query(search.getApi(), search.getCqlQuery());
                }
                catch (Throwable t)
                {
                    String detail = "An exception occured executing a CQL search: "
                    + t.getClass() + " " + t.getLocalizedMessage();
                    testResult.addDetail(detail);
                    testResult.setThrowable(t); //MT
                    t.printStackTrace();
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
                        "An exception occured executing a CQL search: "
                                + t.getClass() + " " + t.getLocalizedMessage());
                testResult.setThrowable(t); //MT
                t.printStackTrace();
            }

            resultReport.addTestResult(testResult);
        }
        
        System.out.println(getType() + " tests complete ...");
    }
    
    private void evaluateResults(java.lang.Object results, CQLSearch search, TestResult testResult)
    {
        
        int numResults = 0;
        if (results instanceof CQLQueryResults)
        {
            CQLQueryResults cqlResults = (CQLQueryResults)results;
            if (cqlResults != null && cqlResults.getObjectResult() != null)
            {
                numResults = cqlResults.getObjectResult().length;
            }
        }
        else if (results instanceof List)
        {
            List resultsList = (List)results;
            if (resultsList != null)
            {
                numResults = resultsList.size();
            }
        }
        
        if (search.getExpectedResults() != null)
        {
            
            if (numResults != search.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected: "
                        + search.getExpectedResults()
                        + ", actual number of results: " + numResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + numResults;
                testResult.addDetail(detail);
            }
        }
        if (search.getMinResults() != null)
        {
            
            if (numResults < search.getMinResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected minimum: "
                        + search.getMinResults()
                        + ", actual number of results: " + numResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + numResults;
                testResult.addDetail(detail);
            }
        }
        
    }
    
    /**
     * Convenience method for adding an unexpected error message to a test result.
     * 
     * @param testResult
     * @param search
     * @param errorMessage
     */
    protected void setTestResultFailure(TestResult testResult,
            CQLSearch search, String errorMessage)
    {
        testResult.setPassed(false);
        if (search.getTestCase() != null)
            testResult.setTestCase(search.getTestCase());
        testResult.addDetail(errorMessage);
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
        return "CQL Search";
    }

}
