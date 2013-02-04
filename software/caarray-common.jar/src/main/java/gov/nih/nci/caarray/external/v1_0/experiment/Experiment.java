//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.experiment;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Experiment represents a microarray experiment.
 * 
 * @author dkokotov
 */
public class Experiment extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String publicIdentifier;
    private String title;
    private String description;
    private Organism organism;
    private Set<ExperimentalContact> contacts = new HashSet<ExperimentalContact>();
    private Set<Term> experimentalDesigns = new HashSet<Term>();
    private Set<Term> replicateTypes = new HashSet<Term>();
    private Set<Term> normalizationTypes = new HashSet<Term>();
    private Set<Term> qualityControlTypes = new HashSet<Term>();
    private ArrayProvider arrayProvider;
    private Set<AssayType> assayTypes = new HashSet<AssayType>();
    private Set<Factor> factors = new HashSet<Factor>();
    private Set<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();
    private Date lastDataModificationDate;

    /**
     * @return the public identifier for this experiment. This is a human readable permanent identifier for this
     *         experiment that can be used in publications to identify it.
     */
    public String getPublicIdentifier() {
        return publicIdentifier;
    }

    /**
     * @param publicIdentifier the public identifier for this experiment. This is a human readable permanent identifier
     *            for this experiment that can be used in publications to identify it.
     */
    public void setPublicIdentifier(String publicIdentifier) {
        this.publicIdentifier = publicIdentifier;
    }

    /**
     * @return the title of the experiment
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
     * @return the long-form description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the organism from which the biomaterials in the experiment are drawn
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
     * @return a set of ExperimentContacts corresponding to the people and organizations involved with the experiment.
     */
    public Set<ExperimentalContact> getContacts() {
        return contacts;
    }

    /**
     * @param contacts the contacts to set
     */
    public void setContacts(Set<ExperimentalContact> contacts) {
        this.contacts = contacts;
    }

    /**
     * @return the set of MGED ontology terms defining the experimental design of this experiment.
     */
    public Set<Term> getExperimentalDesigns() {
        return experimentalDesigns;
    }

    /**
     * @param experimentalDesigns the experimentalDesigns to set
     */
    public void setExperimentalDesigns(Set<Term> experimentalDesigns) {
        this.experimentalDesigns = experimentalDesigns;
    }

    /**
     * @return the set of MGED ontology terms defining the replication strategy of this experiment.
     */
    public Set<Term> getReplicateTypes() {
        return replicateTypes;
    }

    /**
     * @param replicateTypes the replicateTypes to set
     */
    public void setReplicateTypes(Set<Term> replicateTypes) {
        this.replicateTypes = replicateTypes;
    }

    /**
     * @return the set of MGED ontology terms defining the normalization strategy of this experiment.
     */
    public Set<Term> getNormalizationTypes() {
        return normalizationTypes;
    }

    /**
     * @param normalizationTypes the normalizationTypes to set
     */
    public void setNormalizationTypes(Set<Term> normalizationTypes) {
        this.normalizationTypes = normalizationTypes;
    }

    /**
     * @return the provider of arrays used in this experiment.
     */
    public ArrayProvider getArrayProvider() {
        return arrayProvider;
    }

    /**
     * @param arrayProvider array provider to set
     */
    public void setArrayProvider(ArrayProvider arrayProvider) {
        this.arrayProvider = arrayProvider;
    }

    /**
     * @return the set of assay types associated with this experiment.
     */
    public Set<AssayType> getAssayTypes() {
        return assayTypes;
    }

    /**
     * @param assayTypes the assayTypes to set
     */
    public void setAssayTypes(Set<AssayType> assayTypes) {
        this.assayTypes = assayTypes;
    }

    /**
     * @return the set of experimental factors in this experiment.
     */
    public Set<Factor> getFactors() {
        return factors;
    }

    /**
     * @param factors the factors to set
     */
    public void setFactors(Set<Factor> factors) {
        this.factors = factors;
    }

    /**
     * @return the set of MGED ontology terms defining the quality control strategy of this experiment.
     */
    public Set<Term> getQualityControlTypes() {
        return qualityControlTypes;
    }

    /**
     * @param qualityControlTypes the qualityControlTypes to set
     */
    public void setQualityControlTypes(Set<Term> qualityControlTypes) {
        this.qualityControlTypes = qualityControlTypes;
    }
    
    /**
     * @return the set of array designs used in this experiment.
     */
    public Set<ArrayDesign> getArrayDesigns() {
        return arrayDesigns;
    }

    /**
     * @param arrayDesigns the arrayDesigns to set
     */
    public void setArrayDesigns(Set<ArrayDesign> arrayDesigns) {
        this.arrayDesigns = arrayDesigns;
    }

    /**
     * @return the date when the data of this experiment was last modified.
     */
    public Date getLastDataModificationDate() {
        return this.lastDataModificationDate;
    }

    /**
     * @param lastDataModificationDate the lastDataModificationDate to set.
     */
    public void setLastDataModificationDate(final Date lastDataModificationDate) {
        this.lastDataModificationDate = lastDataModificationDate;
    }

}
