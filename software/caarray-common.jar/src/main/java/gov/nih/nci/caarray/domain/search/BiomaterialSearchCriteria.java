//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple bean to hold search criteria for biomaterials.
 * 
 * @author dkokotov
 */
public class BiomaterialSearchCriteria {
    private Experiment experiment;
    private Set<String> names = new HashSet<String>();
    private Set<String> externalIds = new HashSet<String>();
    private Set<AnnotationCriterion> annotationCriterions = new HashSet<AnnotationCriterion>();
    private Set<Class<? extends AbstractBioMaterial>> biomaterialClasses = 
        new HashSet<Class<? extends AbstractBioMaterial>>();
    
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

    /**
     * @return the externalIds
     */
    public Set<String> getExternalIds() {
        return externalIds;
    }

    /**
     * @param externalIds the externalIds to set
     */
    public void setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
    }

    /**
     * @return the annotationCriterions
     */
    public Set<AnnotationCriterion> getAnnotationCriterions() {
        return annotationCriterions;
    }

    /**
     * @param annotationCriterions the annotationCriterions to set
     */
    public void setAnnotationCriterions(Set<AnnotationCriterion> annotationCriterions) {
        this.annotationCriterions = annotationCriterions;
    }

    /**
     * @return the biomaterialClasses
     */
    public Set<Class<? extends AbstractBioMaterial>> getBiomaterialClasses() {
        return biomaterialClasses;
    }

    /**
     * @param biomaterialClasses the biomaterialClasses to set
     */
    public void setBiomaterialClasses(Set<Class<? extends AbstractBioMaterial>> biomaterialClasses) {
        this.biomaterialClasses = biomaterialClasses;
    }
}
