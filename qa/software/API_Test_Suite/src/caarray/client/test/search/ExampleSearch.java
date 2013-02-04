//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.MatchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for beans encapsulating details of a search-by-example test search.
 * 
 * @author vaughng 
 * Jun 27, 2009
 */
public abstract class ExampleSearch extends TestBean
{

    protected String exceptionClass = null;
    protected Integer resultsPerLimitOffset = null, stopResults = null;
    protected List<Integer> resultsReturnedInPage = new ArrayList<Integer>();
    protected MatchMode matchMode;
    protected boolean enumerate = false, apiUtil = false, login = false, excludeZeros = false;

    protected ExampleSearch()
    {
    }
    
    public abstract AbstractCaArrayEntity getExample();

    /**
     * MatchMode to be set in the search criteria.
     * 
     * @return MatchMode to be set in the search criteria.
     */
    public MatchMode getMatchMode()
    {
        return matchMode;
    }

    /**
     * MatchMode to be set in the search criteria.
     * 
     * @param matchMode MatchMode to be set in the search criteria.
     */
    public void setMatchMode(MatchMode matchMode)
    {
        this.matchMode = matchMode;
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
     * Number of per-page results to be set in a LimitOffset.
     * 
     * @return Number of per-page results to be set in a LimitOffset.
     */
    public Integer getResultsPerLimitOffset()
    {
        return resultsPerLimitOffset;
    }

    /**
     * Number of per-page results to be set in a LimitOffset.
     * 
     * @param results Number of per-page results to be set in a LimitOffset.
     */
    public void setResultsPerLimitOffset(Integer results)
    {
        this.resultsPerLimitOffset = results;
    }

    /**
     * A list of the number of results returned per page.
     * 
     * @return A list of the number of results returned per page.
     */
    public List<Integer> getResultsReturnedInPage()
    {
        return resultsReturnedInPage;
    }

    /**
     * Add a number of results returned to the list of results per page.
     * 
     * @param pageReturned A number of results returned added to the list of results per page.
     */
    public void addPageReturned(Integer pageReturned)
    {
        resultsReturnedInPage.add(pageReturned);
    }

    /**
     * Indicates a search should be executed via an API utils search.
     * 
     * @return Indicates a search should be executed via an API utils search.
     */
    public boolean isApiUtil()
    {
        return apiUtil;
    }

    /**
     * Indicates a search should be executed via an API utils search.
     * 
     * @param apiUtil Indicates a search should be executed via an API utils search.
     */
    public void setApiUtil(boolean apiUtil)
    {
        this.apiUtil = apiUtil;
    }

    /**
     * Indicates the test user should be logged in to execute this test.
     * 
     * @return Indicates the test user should be logged in to execute this test.
     */
    public boolean isLogin()
    {
        return login;
    }

    /**
     * Indicates the test user should be logged in to execute this test.
     * 
     * @param login Indicates the test user should be logged in to execute this test.
     */
    public void setLogin(boolean login)
    {
        this.login = login;
    }

    /**
     * Set the excludeZeros property of the search criteria.
     * 
     * @return the excludeZeros property of the search criteria.
     */
    public boolean isExcludeZeros()
    {
        return excludeZeros;
    }

    /**
     * The excludeZeros property of the search criteria.
     * 
     * @param excludeZeros the excludeZeros property of the search criteria.
     */
    public void setExcludeZeros(boolean excludeZeros)
    {
        this.excludeZeros = excludeZeros;
    }

    /**
     * Indicates the number of results at which the search should be terminated.
     * 
     * @return Indicates the number of results at which the search should be terminated.
     */
    public Integer getStopResults()
    {
        return stopResults;
    }

    /**
     * Indicates the number of results at which the search should be terminated.
     * 
     * @param stopResults Indicates the number of results at which the search should be terminated.
     */
    public void setStopResults(Integer stopResults)
    {
        this.stopResults = stopResults;
    }

    /**
     * Indicates the expected class type of an exception thrown by a search.
     * 
     * @return Indicates the expected class type of an exception thrown by a search.
     */
    public String getExceptionClass()
    {
        return exceptionClass;
    }

    /**
     * Indicates the expected class type of an exception thrown by a search.
     * 
     * @param exceptionClass Indicates the expected class type of an exception thrown by a search.
     */
    public void setExceptionClass(String exceptionClass)
    {
        this.exceptionClass = exceptionClass;
    }
    

}
