//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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

    /**
     * Indicates a search should be executed via an ApiUtils method.
     * 
     * @return True if a search should be executed via an ApiUtils method.
     */
    public boolean isApiUtilsSearch()
    {
        return apiUtilsSearch;
    }

    /**
     * Indicates a search should be executed via an ApiUtils method.
     * 
     * @param apiUtilsSearch True if a search should be executed via an ApiUtils method.
     */
    public void setApiUtilsSearch(boolean apiUtilsSearch)
    {
        this.apiUtilsSearch = apiUtilsSearch;
    }

    /**
     * Indicates a search is an API enumeration method.
     * 
     * @return True if a search is an API enumeration method.
     */
    public boolean isEnumerate()
    {
        return enumerate;
    }

    /**
     * Indicates a search is an API enumeration method.
     * 
     * @param enumerate True if a search is an API enumeration method.
     */
    public void setEnumerate(boolean enumerate)
    {
        this.enumerate = enumerate;
    }

    /**
     * The maximum time in ms a search should take to execute in
     * order to pass.
     * 
     * @return The maximum time in ms a search should take to execute in order to pass.
     */
    public Long getMaxTime()
    {
        return maxTime;
    }

    /**
     * The maximum time in ms a search should take to execute in
     * order to pass. 
     * @param maxTime The maximum time in ms a search should take to execute in order to pass.
     */
    public void setMaxTime(Long maxTime)
    {
        this.maxTime = maxTime;
    }

    /**
     * Indicates the test user should be logged in to execute this test.
     * 
     * @return True if the test user should be logged in to execute this test.
     */
    public boolean isLogin()
    {
        return login;
    }

    /**
     * Indicates the test user should be logged in to execute this test.
     * 
     * @param login True if the test user should be logged in to execute this test.
     */
    public void setLogin(boolean login)
    {
        this.login = login;
    }
    
    
}
