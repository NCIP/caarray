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

    protected Float testCase = null;
    protected String api = null;
    protected Integer expectedResults = null, minResults = null;
    protected boolean apiUtilsSearch = false, enumerate = false, login = false;
    protected Long maxTime = null;
    
    protected CriteriaSearch(){}

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

    public boolean isApiUtilsSearch()
    {
        return apiUtilsSearch;
    }

    public void setApiUtilsSearch(boolean apiUtilsSearch)
    {
        this.apiUtilsSearch = apiUtilsSearch;
    }

    public boolean isEnumerate()
    {
        return enumerate;
    }

    public void setEnumerate(boolean enumerate)
    {
        this.enumerate = enumerate;
    }

    public Long getMaxTime()
    {
        return maxTime;
    }

    public void setMaxTime(Long minTime)
    {
        this.maxTime = minTime;
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
