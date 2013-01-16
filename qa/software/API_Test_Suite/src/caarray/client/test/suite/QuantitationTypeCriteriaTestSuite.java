//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.QuantitationTypeCriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 15, 2009
 */
public class QuantitationTypeCriteriaTestSuite extends
        SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "QuantitationTypeCriteria.csv";
    
     
    private static final String HYB = "Hybridization";
    private static final String HYB_REF = "Hybridization Reference";
    private static final String FILE_TYPE_REF = "File Type Reference"; 
    private static final String FILE_TYPE_CATEGORY = "File Type Category";
    private static final String ARRAY_DT_REF = "ArrayDataType Reference";
    
    

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, EXPECTED_RESULTS, FILE_TYPE_REF, HYB, HYB_REF, FILE_TYPE_CATEGORY, ARRAY_DT_REF };

    /**
     * @param apiFacade
     */
    public QuantitationTypeCriteriaTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#evaluateResults(java.lang.Object, caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(Object resultsList, TestBean search,
            TestResult testResult)
    {
        QuantitationTypeCriteriaSearch qSearch = (QuantitationTypeCriteriaSearch)search;
        List<QuantitationType> qResults = (List<QuantitationType>)resultsList;
        int namedResults = 0;
        for (QuantitationType quant : qResults)
        {
            if (quant != null && quant.getName() != null)
                namedResults++;
        }
        if (qSearch.getExpectedResults() != null)
        {
            
            if (namedResults != qSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + qSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, qSearch, errorMessage);
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
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(CriteriaSearch search, TestResult testResult)
            throws Exception
    {
        QuantitationTypeCriteriaSearch criteriaSearch = (QuantitationTypeCriteriaSearch)search;
        List<QuantitationType> resultsList = new ArrayList<QuantitationType>();
        try
        {
            
                List<QuantitationType> results = (List<QuantitationType>) apiFacade
                        .searchForQuantitationTypes(criteriaSearch.getApi(),
                                criteriaSearch.getSearchCriteria(), null);
                resultsList.addAll(results);
               
            
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered executing search: " + e.getMessage());
            testResult.addDetail("Exception encountered executing search: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            log.error("Exception encountered:",e);
        } 
        
        
        
        return resultsList;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new QuantitationTypeCriteriaSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        QuantitationTypeCriteriaSearch search = (QuantitationTypeCriteriaSearch)criteriaSearch;
        QuantitationTypeSearchCriteria criteria = search.getSearchCriteria();
        if (headerIndexMap.get(FILE_TYPE_REF) < input.length && !input[headerIndexMap.get(FILE_TYPE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(FILE_TYPE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getFileTypes().add(new CaArrayEntityReference(ref));
        }
        
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        QuantitationTypeCriteriaSearch search = (QuantitationTypeCriteriaSearch)criteriaSearch;
        QuantitationTypeSearchCriteria criteria = new QuantitationTypeSearchCriteria();
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
        
        
        if (headerIndexMap.get(FILE_TYPE_REF) < input.length && !input[headerIndexMap.get(FILE_TYPE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(FILE_TYPE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getFileTypes().add(new CaArrayEntityReference(ref));
        }
        
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            Hybridization hyb = apiFacade.getHybridization(search.getApi(), name);
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (hyb != null)
            {
                ref = hyb.getReference();
            }
            else
            {
                ref.setId(name);
            }
            criteria.setHybridization(ref);
        }
        if (headerIndexMap.get(HYB_REF) < input.length && !input[headerIndexMap.get(HYB_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(HYB_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.setHybridization(new CaArrayEntityReference(ref));
        }
        
        if (headerIndexMap.get(FILE_TYPE_CATEGORY) < input.length && !input[headerIndexMap.get(FILE_TYPE_CATEGORY)].equals(""))
        {
            String file_type_category = input[headerIndexMap.get(FILE_TYPE_CATEGORY)];
            if (file_type_category.startsWith(VAR_START))
            	file_type_category = getVariableValue(file_type_category);
            criteria.getFileCategories().add(Enum.valueOf(FileCategory.class, file_type_category));
        }
        if (headerIndexMap.get(ARRAY_DT_REF) < input.length && !input[headerIndexMap.get(ARRAY_DT_REF)].equals("")) {
            String name = input[headerIndexMap.get(ARRAY_DT_REF)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            ArrayDataType adt = apiFacade.getArrayDataType(search.getApi(), name);
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (adt != null)
            {
                ref = adt.getReference();
            }
            else
            {
                ref.setId(name);
            }
            Set<CaArrayEntityReference> refs = new HashSet<CaArrayEntityReference> ();
            refs.add(ref);
            criteria.setArrayDataTypes(refs);        	
        }
        search.setSearchCriteria(criteria);    
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
        return "Quantitation Type Criteria";
    }

}
