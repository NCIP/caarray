//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package edu.georgetown.pir;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Organism that the sample or data comes from.
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@UniqueConstraint(fields = { @UniqueConstraintField(name = "scientificName"), 
        @UniqueConstraintField(name = "termSource") }, message = "{organism.uniqueConstraint}")
public class Organism implements PersistentObject {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long ncbiTaxonomyId;
    private String scientificName;
    private String commonName;
    private String taxonomyRank;
    private String ethnicityStrain;
    private TermSource termSource;
    
    private Set<AdditionalOrganismName> additionalOrganismNameCollection = new HashSet<AdditionalOrganismName>();

    /**
     * @return the additionalOrganismNameCollection
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<AdditionalOrganismName> getAdditionalOrganismNameCollection() {
        return additionalOrganismNameCollection;
    }

    /**
     * @param additionalOrganismNameCollection the additionalOrganismNameCollection to set
     */
    public void setAdditionalOrganismNameCollection(Set<AdditionalOrganismName> additionalOrganismNameCollection) {
        this.additionalOrganismNameCollection = additionalOrganismNameCollection;
    }

    /**
     * @return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName the commonName to set
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * @return the ethnicityStrain
     */
    public String getEthnicityStrain() {
        return ethnicityStrain;
    }

    /**
     * @param ethnicityStrain the ethnicityStrain to set
     */
    public void setEthnicityStrain(String ethnicityStrain) {
        this.ethnicityStrain = ethnicityStrain;
    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the ncbiTaxonomyId
     */
    public Long getNcbiTaxonomyId() {
        return ncbiTaxonomyId;
    }

    /**
     * @param ncbiTaxonomyId the ncbiTaxonomyId to set
     */
    public void setNcbiTaxonomyId(Long ncbiTaxonomyId) {
        this.ncbiTaxonomyId = ncbiTaxonomyId;
    }

    /**
     * @return the scientificName
     */
    @NotNull
    @Length(min = 1, max = AbstractCaArrayObject.DEFAULT_STRING_COLUMN_SIZE)
    public String getScientificName() {
        return scientificName;
    }

    /**
     * @param scientificName the scientificName to set
     */
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    /**
     * @return the taxonomyRank
     */
    public String getTaxonomyRank() {
        return taxonomyRank;
    }

    /**
     * @param taxonomyRank the taxonomyRank to set
     */
    public void setTaxonomyRank(String taxonomyRank) {
        this.taxonomyRank = taxonomyRank;
    }

    /**
     * @return the term source to which this organism belongs
     */
    @ManyToOne(optional = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @NotNull
    public TermSource getTermSource() {
        return termSource;
    }

    /**
     * Set the termSource.
     * @param termSource The termSource to set
     */
    public void setTermSource(TermSource termSource) {
        this.termSource = termSource;
    }

    /**
     * @return the value and the term source of this term, which identify
     * the term unambiguously
     */
    @Transient
    public String getNameAndSource() {
        return new StringBuilder(getScientificName()).append(" (").append(getTermSource().getName()).append(
                ")").toString();
    }
}
