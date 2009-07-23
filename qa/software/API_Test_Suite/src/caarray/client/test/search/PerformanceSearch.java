/**
 * 
 */
package caarray.client.test.search;

import caarray.client.test.suite.CriteriaPerformanceTestSuite.SearchType;

/**
 * @author vaughng
 * Jul 20, 2009
 */
public class PerformanceSearch extends CriteriaSearch
{

    private SearchType searchType;
    
    /**
     * 
     */
    public PerformanceSearch()
    {
        super();
    }

    public SearchType getSearchType()
    {
        return searchType;
    }

    public void setSearchType(SearchType searchType)
    {
        this.searchType = searchType;
    }

}
