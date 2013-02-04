//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationColumn;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationValueSet;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.AnnotationSetSearch;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 13, 2009
 */
public class AnnotationSetTestSuite extends SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "AnnotationSet.csv";

    private static final String SAMPLE = "Sample";
    private static final String HYBRID = "Hybridization";
    private static final String CATEGORY = "Category";
    
    private static final String EXPECTED_CATEGORY = "Expected Category";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, SAMPLE, EXPECTED_RESULTS, MIN_RESULTS,
            HYBRID, CATEGORY, EXPECTED_CATEGORY};
    
    /**
     * @param apiFacade
     */
    public AnnotationSetTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#evaluateResults(java.util.List, caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            Object resultsList,
            TestBean search, TestResult testResult)
    {
        AnnotationSetSearch annoSearch = (AnnotationSetSearch)search;
        AnnotationSet annoResults = (AnnotationSet)resultsList;
        int namedResults = 0;
        Set<String> emptyCategories = new HashSet<String>();
        if (annoResults != null)
        {
            for (AnnotationColumn column : annoResults.getColumns())
            {
                if (column != null)
                {
                    for (AnnotationValueSet valueSet : column.getValueSets())
                    {
                        
                        namedResults += valueSet.getValues().size();
                    }
                }
                    
            }
        }
        
        if (annoSearch.getExpectedResults() != null)
        {
            
            if (namedResults != annoSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + annoSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                
                setTestResultFailure(testResult, annoSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (annoSearch.getMinResults() != null)
        {
            
            if (namedResults < annoSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + annoSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, annoSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (!annoSearch.getExpectedCategories().isEmpty())
        {
            
            for (String expectedName : annoSearch.getExpectedCategories())
            {
                boolean foundName = false;
                for (AnnotationColumn column : annoResults.getColumns())
                {
                    if (column != null)
                    {
                        for (AnnotationValueSet valueSet : column.getValueSets())
                        {
                            if (valueSet != null && valueSet.getCategory() != null)
                            {
                                if (valueSet.getCategory().getName().equalsIgnoreCase(expectedName))
                                {
                                    
                                    if (!valueSet.getValues().isEmpty())
                                    {
                                        foundName = true;
                                        break;
                                    }
                                }
                            }
                        }
                        
                    }
                }
                if (!foundName)
                {
                    emptyCategories.add(expectedName);                 
                }
            }  
            if (!emptyCategories.isEmpty())
            {
                String errorMessage = "The following expected categories were empty or not found: ";
                for (Iterator<String> iter = emptyCategories.iterator(); iter.hasNext();)
                {
                    errorMessage += iter.next() + (iter.hasNext() ? ", " : "");
                }
                setTestResultFailure(testResult, annoSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected categories.";
                testResult.addDetail(detail);
            }
        }
        
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(
            CriteriaSearch search, TestResult testResult) throws Exception
    {
        AnnotationSetSearch criteriaSearch = (AnnotationSetSearch)search;
        AnnotationSet result = null;
        try
        {
            result = apiFacade.getAnnotationSet(search.getApi(), criteriaSearch.getAnnotationSetRequest());
            
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered retrieving annotation set: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            testResult.addDetail("Exception encountered retrieving annotation set: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            log.error("Exception encountered:",e);
        } 
        
        
        
        return result;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new AnnotationSetSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        AnnotationSetSearch search = (AnnotationSetSearch)criteriaSearch;
        AnnotationSetRequest request = search.getAnnotationSetRequest();
        if (headerIndexMap.get(EXPECTED_CATEGORY) < input.length
                && !input[headerIndexMap.get(EXPECTED_CATEGORY)].equals(""))
        {
            search.addExpectedCategory(input[headerIndexMap.get(EXPECTED_CATEGORY)].trim());
        }
        if (headerIndexMap.get(CATEGORY) < input.length && !input[headerIndexMap.get(CATEGORY)].equals(""))
        {
            String name = input[headerIndexMap.get(CATEGORY)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            CaArrayEntityReference ref = null;
            if (name.equals(""))
                ref = new CaArrayEntityReference(name);
            else
            {
                ref = apiFacade.getCategoryReference(search.getApi(), name);
            }
            if (ref == null)
                ref = new CaArrayEntityReference(name);
                    
            request.getCategories().add(ref);
        }    
        if (headerIndexMap.get(HYBRID) < input.length && !input[headerIndexMap.get(HYBRID)].equals(""))
        {
            String name = input[headerIndexMap.get(HYBRID)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            CaArrayEntityReference ref = null;
            try
            {
                if (name.equals(""))
                    ref = new CaArrayEntityReference(name);
                else
                {
                    Hybridization hyb = apiFacade.getHybridization(search
                            .getApi(), name);
                    ref = (hyb != null ? hyb.getReference()
                            : new CaArrayEntityReference(name));
                }
            }
            catch (Exception e)
            {
                System.out.println("No hybrid found for test case: " + search.getTestCase());
                log.error("Exception encountered:",e);
            }
                     
            request.getExperimentGraphNodes().add(ref);
        }
        if (headerIndexMap.get(SAMPLE) < input.length && !input[headerIndexMap.get(SAMPLE)].equals(""))
        {
            String name = input[headerIndexMap.get(SAMPLE)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            CaArrayEntityReference ref = null;
            try
            {
                if (name.equals(""))
                    ref = new CaArrayEntityReference(name);
                else
                {
                    Biomaterial bio = apiFacade.getBiomaterial(search.getApi(), name);
                    ref = (bio != null ? bio.getReference() : new CaArrayEntityReference(name));
                }                
            }
            catch (Exception e)
            {
                System.out.println("No sample found for test case: " + search.getTestCase());
                log.error("Exception encountered:",e);
            }
                     
            request.getExperimentGraphNodes().add(ref);
        }
         

    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        AnnotationSetSearch search = (AnnotationSetSearch)criteriaSearch;
        AnnotationSetRequest criteria = new AnnotationSetRequest();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
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
       
        if (headerIndexMap.get(EXPECTED_CATEGORY) < input.length
                && !input[headerIndexMap.get(EXPECTED_CATEGORY)].equals(""))
            search.addExpectedCategory(input[headerIndexMap.get(EXPECTED_CATEGORY)].trim());
        
            
        
        if (headerIndexMap.get(CATEGORY) < input.length && !input[headerIndexMap.get(CATEGORY)].equals(""))
        {
            CaArrayEntityReference ref = null;
            String title = input[headerIndexMap.get(CATEGORY)].trim();
            if (title.startsWith(VAR_START))
            {
                title = getVariableValue(title);
                ref = new CaArrayEntityReference(title);
            }
            else
            {
                ref = apiFacade.getCategoryReference(search.getApi(), title);
            }
            if (ref == null)
                System.out.println("No category found for test case: " + search.getTestCase());
                    
            criteria.getCategories().add(ref);
        }
            
        if (headerIndexMap.get(HYBRID) < input.length && !input[headerIndexMap.get(HYBRID)].equals(""))
        {
            CaArrayEntityReference ref = null;
            String name = input[headerIndexMap.get(HYBRID)].trim();
            if (name.startsWith(VAR_START))
            {
                name = getVariableValue(name);
                ref = new CaArrayEntityReference(name);
            }  
            else
            {
                try
                {
                    Hybridization hyb = apiFacade.getHybridization(search.getApi(), name);
                    ref = (hyb != null ? hyb.getReference() : new CaArrayEntityReference(name));
                }
                catch (Exception e)
                {
                    System.out.println("No hybridization found for annotation set test case: " + search.getTestCase());
                    log.error("Exception encountered:",e);
                }
            }
            
                     
            criteria.getExperimentGraphNodes().add(ref);
        }
        if (headerIndexMap.get(SAMPLE) < input.length && !input[headerIndexMap.get(SAMPLE)].equals(""))
        {
            CaArrayEntityReference ref = null;
            String name = input[headerIndexMap.get(SAMPLE)].trim();
            if (name.startsWith(VAR_START))
            {
                name = getVariableValue(name); 
                ref = new CaArrayEntityReference(name);
            }
            else
            {
                try
                {
                    Biomaterial bio = apiFacade.getBiomaterial(search.getApi(), name);
                    ref = (bio != null ? bio.getReference() : new CaArrayEntityReference(name));
                }
                catch (Exception e)
                {
                    System.out.println("No sample found for annotation set test case: " + search.getTestCase());
                    log.error("Exception encountered:",e);
                } 
            }        
            criteria.getExperimentGraphNodes().add(ref);
        }
         
        
        search.setAnnotationSetRequest(criteria);    
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
        return "Annotation Set";
    }

}
