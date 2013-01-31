//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;

/**
 * Bean encapsulating details of a search-by-example Factor search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class FactorSearch extends ExampleSearch
{
    private Factor factor;
    
    /**
     * 
     */
    public FactorSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getFactor();
    }

    public Factor getFactor()
    {
        return factor;
    }

    public void setFactor(Factor factor)
    {
        this.factor = factor;
    }

}
