/**
 * 
 */
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author vaughng
 * Jul 1, 2009
 */
public class BiomaterialCriteriaSearch extends CriteriaSearch
{

    private Collection<AnnotationCriterion> annotationCriterions = new HashSet<AnnotationCriterion>();
    private Collection<String> externalIds = new HashSet<String>();
    private Collection<String> names = new HashSet<String>();
    private Collection<BiomaterialType> types = new HashSet<BiomaterialType>();
    private Experiment experiment = null;
    
    public BiomaterialCriteriaSearch()
    {
        super();
    }

    public Experiment getExperiment()
    {
        return experiment;
    }

    public void setExperiment(Experiment experiment)
    {
        this.experiment = experiment;
    }

    public Collection<AnnotationCriterion> getAnnotationCriterions()
    {
        return annotationCriterions;
    }

    public Collection<String> getExternalIds()
    {
        return externalIds;
    }

    public Collection<String> getNames()
    {
        return names;
    }

    public Collection<BiomaterialType> getTypes()
    {
        return types;
    }

    public void add(AnnotationCriterion arg0)
    {
        annotationCriterions.add(arg0);
    }

    public void add(BiomaterialType o)
    {
        types.add(o);
    }
    
    public void addName(String name)
    {
        names.add(name);
    }
    
    public void addExternalId(String externalId)
    {
        externalIds.add(externalId);
    }
    
}
