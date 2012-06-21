/**
 * 
 */
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;

/**
 * @author vaughng
 * Jul 15, 2009
 */
public class HybridizationCriteriaSearch extends CriteriaSearch
{

    private HybridizationSearchCriteria searchCriteria;
    
    /**
     * 
     */
    public HybridizationCriteriaSearch()
    {
        // TODO Auto-generated constructor stub
    }

    public HybridizationSearchCriteria getSearchCriteria()
    {
        return searchCriteria;
    }

    public void setSearchCriteria(HybridizationSearchCriteria searchCriteria)
    {
        this.searchCriteria = searchCriteria;
    }

}
