//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.PersonSearch;

/**
 * @author vaughng
 * Jul 10, 2009
 */
public class PersonTestSuite extends SearchByExampleTestSuite
{
    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "Person.csv";

    private static final String LAST_NAME = "Last Name";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
        API, LAST_NAME, EXPECTED_RESULTS, MIN_RESULTS };
    
    /**
     * @param apiFacade
     */
    public PersonTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#evaluateResults(java.util.List, caarray.client.test.search.ExampleSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        PersonSearch personSearch = (PersonSearch)search;
        List<Person> personResults = (List<Person>)resultsList;
        int namedResults = 0;
        for (Person person : personResults)
        {
            if (person.getLastName() != null)
                namedResults++;
        }
        if (personSearch.getExpectedResults() != null)
        {           
            if (namedResults != personSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + personSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, personSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (personSearch.getMinResults() != null)
        {
            if (namedResults < personSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + personSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, personSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new PersonSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        //NOOP
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        PersonSearch search = (PersonSearch)exampleSearch;
        Person example = new Person();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(LAST_NAME) < input.length && !input[headerIndexMap.get(LAST_NAME)].equals(""))
        {
            example.setLastName(input[headerIndexMap.get(LAST_NAME)].trim());
        }
            
        
        search.setPerson(example);
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
            search.setMinResults(Integer.parseInt((input[headerIndexMap.get(MIN_RESULTS)].trim())));
        
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        return "Person";
    }

}
