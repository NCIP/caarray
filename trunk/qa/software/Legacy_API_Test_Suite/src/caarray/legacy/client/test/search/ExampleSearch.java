//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public abstract class ExampleSearch
{

    protected Float testCase = null;
    protected String api = null;
    protected Integer expectedResults = null;
    protected Integer minResults = null;
    protected boolean login = false;
    
    protected ExampleSearch()
    {
    }

    public Float getTestCase()
    {
        return testCase;
    }

    public void setTestCase(Float testCase)
    {
        this.testCase = testCase;
    }

    public String getApi()
    {
        return api;
    }

    public void setApi(String api)
    {
        this.api = api;
    }
    
    public abstract AbstractCaArrayObject getExample();

    public Integer getExpectedResults()
    {
        return expectedResults;
    }

    public void setExpectedResults(Integer expectedResults)
    {
        this.expectedResults = expectedResults;
    }

    public Integer getMinResults()
    {
        return minResults;
    }

    public void setMinResults(Integer minResults)
    {
        this.minResults = minResults;
    }

    /**
     * @return the login
     */
    public boolean isLogin()
    {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(boolean login)
    {
        this.login = login;
    }

}
