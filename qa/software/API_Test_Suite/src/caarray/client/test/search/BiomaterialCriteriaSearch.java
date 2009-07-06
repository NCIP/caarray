/**
 * 
 */
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;

/**
 * @author vaughng
 * Jul 1, 2009
 */
public class BiomaterialCriteriaSearch extends CriteriaSearch
{

   /* private Collection<AnnotationCriterion> annotationCriterions = new HashSet<AnnotationCriterion>();
    private Collection<String> externalIds = new HashSet<String>();
    private Collection<String> names = new HashSet<String>();
    private Collection<BiomaterialType> types = new HashSet<BiomaterialType>();
    private Experiment experiment = null;*/
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
