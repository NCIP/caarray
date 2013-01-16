//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple bean to hold search criteria for hybridizations.
 * 
 * @author dkokotov
 */
public class HybridizationSearchCriteria {
    private Experiment experiment;
    private Set<AbstractBioMaterial> biomaterials = new HashSet<AbstractBioMaterial>();
    private Set<String> names = new HashSet<String>();

    /**
     * @return the AbstractBioMaterials
     */
    public Set<AbstractBioMaterial> getBiomaterials() {
        return biomaterials;
    }

    /**
     * @param biomaterials the AbstractBioMaterials to set
     */
    public void setBiomaterials(Set<AbstractBioMaterial> biomaterials) {
        this.biomaterials = biomaterials;
    }

    /**
     * @return the experiment
     */
    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * @return the names
     */
    public Set<String> getNames() {
        return names;
    }

    /**
     * @param names the names to set
     */
    public void setNames(Set<String> names) {
        this.names = names;
    }
}
