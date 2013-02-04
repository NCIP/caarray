//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.sample;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

/**
 * AbstractExperimentGraphNode represents a node within the sample-data relationship graph
 * describing the structure of a microarray experiment. 
 * 
 * @author dkokotov
 */
public abstract class AbstractExperimentGraphNode extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;

    private String name;
    private CaArrayEntityReference experiment;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return a reference to the experiment to which this node belongs.
     */
    public CaArrayEntityReference getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the reference to the experiment to which this node belongs
     */
    public void setExperiment(CaArrayEntityReference experiment) {
        this.experiment = experiment;
    }
}
