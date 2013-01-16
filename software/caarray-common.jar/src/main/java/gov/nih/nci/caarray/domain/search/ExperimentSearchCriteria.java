//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import java.util.HashSet;
import java.util.Set;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.AssayType;

/**
 * Simple bean to hold search criteria for an experiment.
 * 
 * @author dkokotov
 */
public class ExperimentSearchCriteria {
    private String title;
    private String publicIdentifier;
    private Organism organism;
    private Organization arrayProvider;
    private AssayType assayType;
    private Set<Person> principalInvestigators = new HashSet<Person>();
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
    public Organism getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * @return the arrayProvider
     */
    public Organization getArrayProvider() {
        return arrayProvider;
    }

    /**
     * @param arrayProvider the arrayProvider to set
     */
    public void setArrayProvider(Organization arrayProvider) {
        this.arrayProvider = arrayProvider;
    }

    /**
     * @return the assayType
     */
    public AssayType getAssayType() {
        return assayType;
    }

    /**
     * @param assayType the assayType to set
     */
    public void setAssayType(AssayType assayType) {
        this.assayType = assayType;
    }

    /**
     * @return the Set of principal Investigators
     */
    public Set<Person> getPrincipalInvestigators() {
        return principalInvestigators;
    }

    /**
     * @param principalInvestigators the set principal Investigators to set
     */
    public void setPrincipalInvestigators(Set<Person> principalInvestigators) {
        this.principalInvestigators = principalInvestigators;
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
}
