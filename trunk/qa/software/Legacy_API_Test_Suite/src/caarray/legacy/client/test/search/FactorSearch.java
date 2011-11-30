/**
 * 
 */
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.project.Factor;

/**
 * @author vaughng
 * Aug 12, 2009
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
     * @see caarray.legacy.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayObject getExample()
    {
        return getFactor();
    }

    /**
     * @return the factor
     */
    public Factor getFactor()
    {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(Factor factor)
    {
        this.factor = factor;
    }

}
