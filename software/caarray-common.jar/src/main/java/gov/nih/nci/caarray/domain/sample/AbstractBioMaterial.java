//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * AbstractBiomaterial represents a biomaterial at some stage prior to being hybridized to an array.
 * @author dkokotov
 */
@Entity
@Table(name = "biomaterial")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@UniqueConstraint(fields = {
        @UniqueConstraintField(name = "externalId"),
        @UniqueConstraintField(name = "experiment", nullsEqual = false) },
        generateDDLConstraint = false, message = "{biomaterial.externalId.uniqueConstraint}")
public abstract class AbstractBioMaterial extends AbstractExperimentDesignNode {
    private static final long serialVersionUID = 1234567890L;

    private Term tissueSite;
    private Term materialType;
    private Term cellType;
    private Term diseaseState;
    private String name;
    private String description;
    private String externalId;
    private Set<AbstractCharacteristic> characteristics = new HashSet<AbstractCharacteristic>();
    private List<ProtocolApplication> protocolApplications = new ArrayList<ProtocolApplication>();
    private Organism organism;
    private Experiment experiment;
    private Date lastModifiedDataTime = new Date();


    /**
     * {@inheritDoc}
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    @ForeignKey(name = "biomaterial_experiment_fk")
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
     * @return the tissueSite
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_site_fk")
    public Term getTissueSite() {
        return this.tissueSite;
    }

    /**
     * @param tissueSite the tissueSite to set
     */
    public void setTissueSite(Term tissueSite) {
        this.tissueSite = tissueSite;
    }

    /**
     * Gets the materialType.
     *
     * @return the materialType
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_mat_type_fk")
    public Term getMaterialType() {
        return this.materialType;
    }

    /**
     * Sets the materialType.
     *
     * @param materialTypeVal the materialType
     */
    public void setMaterialType(final Term materialTypeVal) {
        this.materialType = materialTypeVal;
    }

    /**
     * @return the cellType
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_cell_type_fk")
    public Term getCellType() {
        return this.cellType;
    }

    /**
     * @param cellType the cellType to set
     */
    public void setCellType(Term cellType) {
        this.cellType = cellType;
    }

    /**
     * @return the diseaseState
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_dis_state_fk")
    public Term getDiseaseState() {
        return this.diseaseState;
    }

    /**
     * @param diseaseState the diseaseState to set
     */
    public void setDiseaseState(Term diseaseState) {
        this.diseaseState = diseaseState;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @NotNull
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_name")
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     *
     * @param nameVal the name
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     *
     * @param descriptionVal the description
     */
    public void setDescription(final String descriptionVal) {
        this.description = descriptionVal;
    }

    /**
     * @return the externalId
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_name")
    public String getExternalId() {
        return this.externalId;
    }

    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Gets the characteristics.
     *
     * @return the characteristics
     */
    @OneToMany(mappedBy = "bioMaterial", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public Set<AbstractCharacteristic> getCharacteristics() {
        return this.characteristics;
    }

    /**
     * Sets the characteristics.
     *
     * @param characteristicsVal the characteristics
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setCharacteristics(final Set<AbstractCharacteristic> characteristicsVal) {
        this.characteristics = characteristicsVal;
    }

    /**
     * @return the date when the data of this sample was last modified.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModifiedDataTime() {
        return this.lastModifiedDataTime;
    }

    /**
     * @param lastModifiedDataTime the lastDataModificationDate to set.
     */
    public void setLastModifiedDataTime(final Date lastModifiedDataTime) {
        this.lastModifiedDataTime = lastModifiedDataTime;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void propagateLastModifiedDataTime(final Date lastModifiedDate) {
        this.lastModifiedDataTime = lastModifiedDate;
        super.propagateLastModifiedDataTime(lastModifiedDate);
    }

    /**
     * Return the characteristic with given category name in this biomaterial. If multiple characteristics
     * have the same category name, return one at random.
     * If there is none, return null.
     *
     * @param categoryName name of category for which to find a characteristic.
     * @return the characteristic with given category name or null if there is none.
     */
    public AbstractCharacteristic getCharacteristic(final String categoryName) {
        return Iterators.find(getCharacteristics().iterator(), new Predicate<AbstractCharacteristic>() {
            public boolean apply(AbstractCharacteristic input) {
                return categoryName.equals(input.getCategory().getName());
            }
        });
    }

    /**
     * Return the characteristics with given category in this biomaterial.
     *
     * @param category category
     * @return the characteristics with given category.
     */
    public Set<AbstractCharacteristic> getCharacteristics(final Category category) {
        Set<AbstractCharacteristic> chars = new HashSet<AbstractCharacteristic>();
        Iterators.addAll(chars, Iterators.filter(characteristics.iterator(), new Predicate<AbstractCharacteristic>() {
            public boolean apply(AbstractCharacteristic input) {
                return category.equals(input.getCategory());
            }
        }));

        chars.addAll(getBuiltInCharacteristics(category));
        addUserDefinedCharacteristic(chars, category);

        return chars;
    }

    private Set<AbstractCharacteristic> getBuiltInCharacteristics(Category category) {
        Set<AbstractCharacteristic> builtInCharacteristics = Sets.newHashSet();
        Map<ExperimentOntologyCategory, Term> builtins = getSpecialCharacteristics();
        for (Map.Entry<ExperimentOntologyCategory, Term> builtin : builtins.entrySet()) {
            if (builtin.getValue() != null && category.getName().equals(builtin.getKey().getCategoryName())) {
                TermBasedCharacteristic c = new TermBasedCharacteristic();
                c.setBioMaterial(this);
                c.setCategory(category);
                c.setTerm(builtin.getValue());
                builtInCharacteristics.add(c);
            }
        }
        return builtInCharacteristics;
    }

    private void addUserDefinedCharacteristic(Set<AbstractCharacteristic> chars, Category category) {
        if (StringUtils.isNotEmpty(this.externalId)
                && category.getName().equals(ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName())) {
            UserDefinedCharacteristic userDefined = new UserDefinedCharacteristic();
            userDefined.setBioMaterial(this);
            userDefined.setCategory(category);
            userDefined.setValue(this.externalId);
            chars.add(userDefined);
        }
    }

    /**
     * Returns the characteristics that are handled specially and have their own fields in AbstractBioMaterial or
     * one of its subclasses, rather than being placed in the general characteristics collection. These are returned
     * as a map, with keys being category constants defining the category of the special characteristic, and the
     * values being the Term values of the characteristic.
     * For AbstractBioMaterial, this will return a map with four entries:
     * <ul>
     * <li>ExperimentOntologyCategory.DISEASE_STATE -> this.diseaseState
     * <li>ExperimentOntologyCategory.CELL_TYPE -> this.cellType
     * <li>ExperimentOntologyCategory.MATERIAL_TYPE -> this.materialType
     * <li>ExperimentOntologyCategory.ORGANISM_PART -> this.tissueSite
     * </ul>
     * Subclasses can override this method to add additional entries if they have other characteristics that they
     * store directly.
     * @return the map of special characterstics as described above
     */
    @Transient
    protected Map<ExperimentOntologyCategory, Term> getSpecialCharacteristics() {
        Map<ExperimentOntologyCategory, Term> builtins = new HashMap<ExperimentOntologyCategory, Term>();
        builtins.put(ExperimentOntologyCategory.DISEASE_STATE, this.diseaseState);
        builtins.put(ExperimentOntologyCategory.CELL_TYPE, this.cellType);
        builtins.put(ExperimentOntologyCategory.MATERIAL_TYPE, this.materialType);
        builtins.put(ExperimentOntologyCategory.ORGANISM_PART, this.tissueSite);
        return builtins;
    }

    /**
     * {@inheritDoc}
     */
    // should really be one-to-many, but hibernate bug HHH-3160/HHH-1296 prevents reordering or deleting from the list
    // with a unique constraint on protocol_application
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "biomaterial_protocol_application",
            joinColumns = @JoinColumn(name = "bio_material"),
            inverseJoinColumns = @JoinColumn(name = "protocol_application")
    )
    @IndexColumn(name = "protocol_order")
    @ForeignKey(name = "biomaterial_protocol_application_bio_material_fk",
            inverseName = "biomaterial_protocol_application_protocol_application_fk")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public List<ProtocolApplication> getProtocolApplications() {
        return this.protocolApplications;
    }

    /**
     * Sets the protocolApplications.
     *
     * @param protocolApplicationsVal the protocolApplications
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProtocolApplications(final List<ProtocolApplication> protocolApplicationsVal) {
        this.protocolApplications = protocolApplicationsVal;
    }

    /**
     * {@inheritDoc}
     */
    public void addProtocolApplication(ProtocolApplication protocolApplication) {
        this.protocolApplications.add(protocolApplication);
    }

    /**
     * {@inheritDoc}
     */
    public void clearProtocolApplications() {
        this.protocolApplications.clear();
    }

    /**
     * @return the organism
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_organism_fk")
    public Organism getOrganism() {
        return this.organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * @return the set of hybridizations related to this biomaterial (via the biomaterial chain)
     */
    @Transient
    @SuppressWarnings("unchecked")
    public Set<Hybridization> getRelatedHybridizations() {
        return (Set<Hybridization>) getSuccessorsOfType(ExperimentDesignNodeType.HYBRIDIZATION);
    }

    /**
     * {@inheritDoc}
     * The data files related to this biomaterial is the set of files
     * that is related to at least one hybridization that is related to this biomaterial
     * (@see getRelatedHybs()).
     */
    @Transient
    public Set<CaArrayFile> getAllDataFiles() {
        Set<CaArrayFile> files = new HashSet<CaArrayFile>();
        Collection<Hybridization> hybridizations = getRelatedHybridizations();
        if (hybridizations != null && !hybridizations.isEmpty()) {
            for (Hybridization h : hybridizations) {
                files.addAll(h.getAllDataFiles());
            }
        }
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AbstractExperimentDesignNode node) {
        AbstractBioMaterial bm = (AbstractBioMaterial) node;
        super.merge(bm);

        mergeBioMaterialTerms(bm);
        if (this.getDescription() == null) {
            this.setDescription(bm.getDescription());
        }
        if (this.getOrganism() == null) {
            this.setOrganism(bm.getOrganism());
        }
        if (this.getExternalId() == null) {
            this.setExternalId(bm.getExternalId());
        }

        mergeCharacteristics(bm);
    }

    private void mergeBioMaterialTerms(AbstractBioMaterial bm) {
        if (this.getTissueSite() == null) {
            this.setTissueSite(bm.getTissueSite());
        }
        if (this.getMaterialType() == null) {
            this.setMaterialType(bm.getMaterialType());
        }
        if (this.getCellType() == null) {
            this.setCellType(bm.getCellType());
        }
        if (this.getDiseaseState() == null) {
            this.setDiseaseState(bm.getDiseaseState());
        }
    }

    private void mergeCharacteristics(AbstractBioMaterial bm) {
        Set<AbstractCharacteristic> newChars = new HashSet<AbstractCharacteristic>();
        for (AbstractCharacteristic characteristic : bm.getCharacteristics()) {
            boolean foundCategory = false;
            for (AbstractCharacteristic currCharacteristic : this.getCharacteristics()) {
                if (characteristic.getCategory().equals(currCharacteristic.getCategory())) {
                    foundCategory = true;
                    break;
                }
            }
            if (!foundCategory) {
                newChars.add(characteristic);
                characteristic.setBioMaterial(this);
            }
        }
        this.getCharacteristics().addAll(newChars);
        bm.getCharacteristics().removeAll(newChars);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ", name=" + getName();
    }

}
