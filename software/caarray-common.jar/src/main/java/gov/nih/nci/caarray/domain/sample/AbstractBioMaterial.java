//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.AttributeMutator;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import javax.persistence.Transient;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@Table(name = "biomaterial")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractBioMaterial extends AbstractExperimentDesignNode {
    private static final long serialVersionUID = 1234567890L;

    private Term tissueSite;
    private Term materialType;
    private Term cellType;
    private Term diseaseState;
    private String name;
    private String description;
    private Set<AbstractCharacteristic> characteristics = new HashSet<AbstractCharacteristic>();
    private List<ProtocolApplication> protocolApplications = new ArrayList<ProtocolApplication>();
    private Organism organism;

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
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    @AttributePolicy(deny = SecurityPolicy.TCGA_POLICY_NAME)
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
     * Gets the characteristics.
     *
     * @return the characteristics
     */
    @OneToMany(mappedBy = "bioMaterial", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    @AttributePolicy(mutators = @AttributeMutator(policies = SecurityPolicy.TCGA_POLICY_NAME,
            mutator = TcgaCharacteristicFilter.class))
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
    @AttributePolicy(deny = SecurityPolicy.TCGA_POLICY_NAME)
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
     * @return all the data files related to this biomaterial. This is the set of files
     * that is related to at least one hybridization that is related to this biomaterial
     * (@see getRelatedHybs()).
     */
    @Transient
    public Collection<CaArrayFile> getAllDataFiles() {
        Collection<CaArrayFile> files = new HashSet<CaArrayFile>();
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
     * Attribute filter for characteristics under the TCGA Policy.
     *
     * @author dkokotov
     */
    public static class TcgaCharacteristicFilter implements Closure {
        private static final String[] ALLOWED_CHARACTERISTIC_CATEGORIES =
                {ExperimentOntologyCategory.CLINICAL_DIAGNOSIS.getCategoryName(),
                        ExperimentOntologyCategory.HISTOLOGIC_DIAGNOSIS.getCategoryName(),
                        ExperimentOntologyCategory.PATHOLOGIC_STATUS.getCategoryName(),
                        ExperimentOntologyCategory.TISSUE_ANATOMIC_SITE.getCategoryName()};

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public void execute(Object o) {
            Set<AbstractCharacteristic> characteristics = (Set<AbstractCharacteristic>) o;
            for (Iterator<AbstractCharacteristic> it = characteristics.iterator(); it.hasNext();) {
                AbstractCharacteristic absChar = it.next();
                if (!(absChar instanceof TermBasedCharacteristic)) {
                    it.remove();
                } else {
                    TermBasedCharacteristic termChar = (TermBasedCharacteristic) absChar;
                    if (!isCharacteristicAllowed(termChar)) {
                        it.remove();
                    }
                }
            }
        }

        private static boolean isCharacteristicAllowed(TermBasedCharacteristic termChar) {
            return ArrayUtils.contains(ALLOWED_CHARACTERISTIC_CATEGORIES, termChar.getCategory().getName());
        }
    }
}
