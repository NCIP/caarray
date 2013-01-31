//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;

/**
 * Bean encapsulating details of a search-by-example Experiment search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class ExperimentSearch extends ExampleSearch
{
    private Experiment experiment;
    
    /**
     * 
     */
    public ExperimentSearch()
    {
       super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getExperiment();
    }

    public Experiment getExperiment()
    {
        return experiment;
    }

    public void setExperiment(Experiment experiment)
    {
        this.experiment = experiment;
    }

}
