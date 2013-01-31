//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.search;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class CQLSearch
{

    private Float testCase = null;
    private String api = null;
    private Integer expectedResults = null;
    private Integer minResults = null;
    private CQLQuery cqlQuery;
    
    /**
     * 
     */
    public CQLSearch()
    {}

    /**
     * @return the testCase
     */
    public Float getTestCase()
    {
        return testCase;
    }

    /**
     * @param testCase the testCase to set
     */
    public void setTestCase(Float testCase)
    {
        this.testCase = testCase;
    }

    /**
     * @return the api
     */
    public String getApi()
    {
        return api;
    }

    /**
     * @param api the api to set
     */
    public void setApi(String api)
    {
        this.api = api;
    }

    /**
     * @return the expectedResults
     */
    public Integer getExpectedResults()
    {
        return expectedResults;
    }

    /**
     * @param expectedResults the expectedResults to set
     */
    public void setExpectedResults(Integer expectedResults)
    {
        this.expectedResults = expectedResults;
    }

    /**
     * @return the minResults
     */
    public Integer getMinResults()
    {
        return minResults;
    }

    /**
     * @param minResults the minResults to set
     */
    public void setMinResults(Integer minResults)
    {
        this.minResults = minResults;
    }

    /**
     * @return the cqlQuery
     */
    public CQLQuery getCqlQuery()
    {
        return cqlQuery;
    }

    /**
     * @param cqlQuery the cqlQuery to set
     */
    public void setCqlQuery(CQLQuery cqlQuery)
    {
        this.cqlQuery = cqlQuery;
    }

}
