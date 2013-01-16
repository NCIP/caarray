//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;

/**
 * @author vaughng
 * Jul 6, 2009
 */
public class ExperimentKeywordSearch extends CriteriaSearch
{

    private KeywordSearchCriteria searchCriteria;
    /**
     * 
     */
    public ExperimentKeywordSearch()
    {
        super();
    }
    public KeywordSearchCriteria getSearchCriteria()
    {
        return searchCriteria;
    }
    public void setSearchCriteria(KeywordSearchCriteria searchCriteria)
    {
        this.searchCriteria = searchCriteria;
    }

    
}
