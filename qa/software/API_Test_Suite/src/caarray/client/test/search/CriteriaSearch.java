/**
 * 
 */
package caarray.client.test.search;

/**
 * Base class for beans encapsulating details of a search-by-criteria test search.
 * 
 * @author vaughng
 * Jul 1, 2009
 */
public abstract class CriteriaSearch
{

    protected Integer testCase = null;
    protected String api = null;
    protected Integer expectedResults = null, minResults = null;
    
    protected CriteriaSearch(){}

    public Integer getTestCase()
    {
        return testCase;
    }

    public void setTestCase(Integer testCase)
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
    
    
}
