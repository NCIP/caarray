//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

/**
 * Bean encapsulating details of a search-by-example Hybridization search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class HybridizationSearch extends ExampleSearch
{
    private Hybridization hybridization;
    
    /**
     * 
     */
    public HybridizationSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getHybridization();
    }

    public Hybridization getHybridization()
    {
        return hybridization;
    }

    public void setHybridization(Hybridization hybridization)
    {
        this.hybridization = hybridization;
    }

}
