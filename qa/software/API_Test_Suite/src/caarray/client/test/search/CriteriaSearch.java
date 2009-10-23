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
public abstract class CriteriaSearch extends TestBean
{

    protected boolean apiUtilsSearch = false, enumerate = false, login = false;
    protected Long maxTime = null;
    
    protected CriteriaSearch(){}

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
