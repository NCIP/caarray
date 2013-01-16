//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dkokotov
 * 
 */
public class ExperimentSearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String publicIdentifier;
    private CaArrayEntityReference organism;
    private CaArrayEntityReference arrayProvider;
    private CaArrayEntityReference assayType;
    private Set<CaArrayEntityReference> principalInvestigators = new HashSet<CaArrayEntityReference>();
    private Set<AnnotationCriterion> annotationCriterions = new HashSet<AnnotationCriterion>();

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the organism
     */
    public CaArrayEntityReference getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(CaArrayEntityReference organism) {
        this.organism = organism;
    }

    /**
     * @return the arrayProvider
     */
    public CaArrayEntityReference getArrayProvider() {
        return arrayProvider;
    }

    /**
     * @param arrayProvider the arrayProvider to set
     */
    public void setArrayProvider(CaArrayEntityReference arrayProvider) {
        this.arrayProvider = arrayProvider;
    }

    /**
     * @return the assayType
     */
    public CaArrayEntityReference getAssayType() {
        return assayType;
    }

    /**
     * @param assayType the assayType to set
     */
    public void setAssayType(CaArrayEntityReference assayType) {
        this.assayType = assayType;
    }

    /**
     * @return Set of principal Investigator references.
     */
    public Set<CaArrayEntityReference> getPrincipalInvestigators() {
        return principalInvestigators;
    }

    /**
     * @param principalInvestigators the Set of principal Investigator to set
     */
    public void setPrincipalInvestigators(Set<CaArrayEntityReference> principalInvestigators) {
        this.principalInvestigators = principalInvestigators;
    }

    /**
     * @return the annotations
     */
    public Set<AnnotationCriterion> getAnnotationCriterions() {
        return annotationCriterions;
    }

    /**
     * @param annotations the annotations to set
     */
    public void setAnnotationCriterions(Set<AnnotationCriterion> annotations) {
        this.annotationCriterions = annotations;
    }

    /**
     * @return the publicIdentifier
     */
    public String getPublicIdentifier() {
        return publicIdentifier;
    }

    /**
     * @param publicIdentifier the publicIdentifier to set
     */
    public void setPublicIdentifier(String publicIdentifier) {
        this.publicIdentifier = publicIdentifier;
    }
}
