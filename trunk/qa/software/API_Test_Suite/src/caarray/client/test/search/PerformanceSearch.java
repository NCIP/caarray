//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import caarray.client.test.suite.CriteriaPerformanceTestSuite.SearchType;

/**
 * @author vaughng
 * Jul 20, 2009
 */
public class PerformanceSearch extends CriteriaSearch
{

    private SearchType searchType;
    private Integer pages = null;
    
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

    /**
     * @return the pages
     */
    public Integer getPages()
    {
        return pages;
    }

    /**
     * @param pages the pages to set
     */
    public void setPages(Integer pages)
    {
        this.pages = pages;
    }

}
