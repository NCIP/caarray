//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dkokotov
 */
public class HybridizationSearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private CaArrayEntityReference experiment;
    private Set<String> names = new HashSet<String>();
    private Set<CaArrayEntityReference> biomaterials = new HashSet<CaArrayEntityReference>();
    
    
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

    /**
     * @return the experiment
     */
    public CaArrayEntityReference getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(CaArrayEntityReference experiment) {
        this.experiment = experiment;
    }

    /**
     * @return the biomaterials
     */
    public Set<CaArrayEntityReference> getBiomaterials() {
        return biomaterials;
    }

    /**
     * @param biomaterials the biomaterials to set
     */
    public void setBiomaterials(Set<CaArrayEntityReference> biomaterials) {
        this.biomaterials = biomaterials;
    }

}
