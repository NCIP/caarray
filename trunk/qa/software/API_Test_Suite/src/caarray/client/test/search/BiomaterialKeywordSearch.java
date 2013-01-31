//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;

/**
 * @author vaughng
 * Jul 9, 2009
 */
public class BiomaterialKeywordSearch extends CriteriaSearch
{
    private BiomaterialKeywordSearchCriteria searchCriteria;
    
    /**
     * 
     */
    public BiomaterialKeywordSearch()
    {
        super();
    }

    public BiomaterialKeywordSearchCriteria getSearchCriteria()
    {
        return searchCriteria;
    }

    public void setSearchCriteria(BiomaterialKeywordSearchCriteria searchCriteria)
    {
        this.searchCriteria = searchCriteria;
    }

}
