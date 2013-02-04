//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.project.Experiment;

/**
 * @author vaughng
 * Aug 12, 2009
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
     * @see caarray.legacy.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayObject getExample()
    {
        return getExperiment();
    }

    /**
     * @return the experiment
     */
    public Experiment getExperiment()
    {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(Experiment experiment)
    {
        this.experiment = experiment;
    }

}
