/**
 * 
 */
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.data.QuantitationType;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class QuantitationTypeSearch extends ExampleSearch
{

    private QuantitationType quantitationType;
    /**
     * 
     */
    public QuantitationTypeSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayObject getExample()
    {
        return getQuantitationType();
    }

    /**
     * @return the quantitationType
     */
    public QuantitationType getQuantitationType()
    {
        return quantitationType;
    }

    /**
     * @param quantitationType the quantitationType to set
     */
    public void setQuantitationType(QuantitationType quantitationType)
    {
        this.quantitationType = quantitationType;
    }

}
