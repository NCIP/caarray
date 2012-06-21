/**
 * 
 */
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;

/**
 * @author vaughng
 * Jul 15, 2009
 */
public class QuantitationTypeCriteriaSearch extends CriteriaSearch
{

    private QuantitationTypeSearchCriteria searchCriteria;
    /**
     * 
     */
    public QuantitationTypeCriteriaSearch()
    {
        super();
    }
    public QuantitationTypeSearchCriteria getSearchCriteria()
    {
        return searchCriteria;
    }
    public void setSearchCriteria(QuantitationTypeSearchCriteria searchCriteria)
    {
        this.searchCriteria = searchCriteria;
    }

}
