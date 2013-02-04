//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;

/**
 * @author vaughng
 * Jul 1, 2009
 */
public class BiomaterialCriteriaSearch extends CriteriaSearch
{

    private BiomaterialSearchCriteria searchCriteria;
    
    public BiomaterialCriteriaSearch()
    {
        super();
    }

    public BiomaterialSearchCriteria getSearchCriteria()
    {
        return searchCriteria;
    }

    public void setSearchCriteria(BiomaterialSearchCriteria searchCriteria)
    {
        this.searchCriteria = searchCriteria;
    }
    
    
    
}
