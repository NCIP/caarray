//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.hybridization;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.Image;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.AbstractFactorValue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.ProtectableDescendent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * The act of hybridizing extracted genetic material to the probes on a microarray.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "experiment" }) })
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public class Hybridization extends AbstractExperimentDesignNode implements ProtectableDescendent {
    private static final long serialVersionUID = 1234567890L;
    private static final String MAPPED_BY = "hybridization";

    private String name;
    private String description;
    private float amountOfMaterial;
    private Term amountOfMaterialUnit;
    private Set<RawArrayData> rawDataCollection = new HashSet<RawArrayData>();
    private Array array;
    private Set<Image> images = new HashSet<Image>();
    private Set<DerivedArrayData> derivedDataCollection = new HashSet<DerivedArrayData>();
    private List<ProtocolApplication> protocolApplications = new ArrayList<ProtocolApplication>();
    private Set<LabeledExtract> labeledExtract = new HashSet<LabeledExtract>();
    private Set<AbstractFactorValue> factorValues = new HashSet<AbstractFactorValue>();
    private Experiment experiment;
    private Set<HybridizationData> hybridizationData = new HashSet<HybridizationData>();

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
     * @param nameVal
     *            the name
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * @return the description
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the images.
     *
     * @return the images
     */
    @OneToMany(mappedBy = MAPPED_BY, fetch = FetchType.LAZY)
    @Cascade(CascadeType.DELETE)
    public Set<Image> getImages() {
        return this.images;
    }

    /**
     * Sets the images.
     *
     * @param imagesVal
     *            the images
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setImages(final Set<Image> imagesVal) {
        this.images = imagesVal;
    }

    /**
     * Gets the derivedDatas.
     *
     * @return the derivedDatas
     */
    @ManyToMany(mappedBy = "hybridizations", fetch = FetchType.LAZY)
    @Cascade({ CascadeType.DELETE, CascadeType.SAVE_UPDATE })
    public Set<DerivedArrayData> getDerivedDataCollection() {
        return this.derivedDataCollection;
    }

    /**
     * Sets the derivedDatas.
     *
     * @param derivedDatasVal
     *            the derivedDatasVal
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setDerivedDataCollection(final Set<DerivedArrayData> derivedDatasVal) {
        this.derivedDataCollection = derivedDatasVal;
    }

    /**
     * Gets the array.
     *
     * @return the array
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    @ForeignKey(name = "hybridization_array_fk")
    public Array getArray() {
        return this.array;
    }

    /**
     * Sets the array.
     *
     * @param arrayVal the array
     */
    public void setArray(final Array arrayVal) {
        this.array = arrayVal;
    }

    /**
     * Gets the rawArrayDatas.
     * @return the rawArrayData
     */
    @ManyToMany(mappedBy = "hybridizations", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public Set<RawArrayData> getRawDataCollection() {
        return this.rawDataCollection;
    }

    /**
     * Sets the rawArrayDatas.
     * @param rawDataVals the rawArrayDatas to set
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setRawDataCollection(final Set<RawArrayData> rawDataVals) {
        this.rawDataCollection = rawDataVals;
    }

    /**
     * Add a new raw array data to this hybridization.
     * @param rawArrayData the raw array data to add
     */
    public void addRawArrayData(RawArrayData rawArrayData) {
        this.rawDataCollection.add(rawArrayData);
    }

    /**
     * Gets the factorValues.
     *
     * @return the factorValues
     */
    @OneToMany(mappedBy = MAPPED_BY, fetch = FetchType.LAZY)
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public Set<AbstractFactorValue> getFactorValues() {
        return this.factorValues;
    }

    /**
     * Return the factor value for the factor with given name in this hybridization. 
     * If there is none, return null.
     * @param factorName name of factor for which to find a value.
     * @return the factor value for factor with given name or null if there is none.
     */
    public AbstractFactorValue getFactorValue(final String factorName) {
        return (AbstractFactorValue) CollectionUtils.find(getFactorValues(), new Predicate() {
            public boolean evaluate(Object o) {
                AbstractFactorValue fv = (AbstractFactorValue) o;
                return factorName.equals(fv.getFactor().getName());
            }
        });
    }

    /**
     * Sets the factorValues.
     *
     * @param factorValuesVal
     *            the factorValues
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setFactorValues(final Set<AbstractFactorValue> factorValuesVal) {
        this.factorValues = factorValuesVal;
    }

    /**
     * {@inheritDoc}
     */
    // should really be one-to-many, but hibernate bug HHH-3160/HHH-1296 prevents reordering or deleting from the list
    // with a unique constraint on protocol_application
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hybridization_protocol_application",
            joinColumns = @JoinColumn(name = "hybridization"),
            inverseJoinColumns = @JoinColumn(name = "protocol_application")
    )
    @IndexColumn(name = "protocol_order")
    @ForeignKey(name = "hybridization_protocol_application_hybridization_fk",
            inverseName = "hybridization_protocol_application_protocol_application_fk")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public List<ProtocolApplication> getProtocolApplications() {
        return this.protocolApplications;
    }

    /**
     * Sets the protocolApplications.
     *
     * @param protocolApplicationsList the protocolApplications to set
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProtocolApplications(final List<ProtocolApplication> protocolApplicationsList) {
        this.protocolApplications = protocolApplicationsList;
    }

    /**
     * {@inheritDoc}
     */
    public void addProtocolApplication(ProtocolApplication pa) {
        this.protocolApplications.add(pa);
    }

    /**
     * {@inheritDoc}
     */
    public void clearProtocolApplications() {
        this.protocolApplications.clear();
    }

    /**
     * Gets the labeledExtract.
     *
     * @return the labeledExtract
     */
    @ManyToMany(mappedBy = "hybridizations", fetch = FetchType.LAZY)
    @Filter(name = "Project1", condition = Experiment.LABELED_EXTRACTS_FILTER)
    public Set<LabeledExtract> getLabeledExtracts() {
        return this.labeledExtract;
    }

    /**
     * Sets the labeledExtract.
     *
     * @param labeledExtractVal
     *            the labeledExtract
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setLabeledExtracts(final Set<LabeledExtract> labeledExtractVal) {
        this.labeledExtract = labeledExtractVal;
    }

    /**
     * {@inheritDoc}
     */
    @ManyToOne
    @JoinColumn(name = "experiment", insertable = false, updatable = false)
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append(this.name).toString();
    }

    /**
     * @return the amountOfMaterial
     */
    @Column
    public float getAmountOfMaterial() {
        return this.amountOfMaterial;
    }

    /**
     * @param amountOfMaterial the amountOfMaterial to set
     */
    public void setAmountOfMaterial(float amountOfMaterial) {
        this.amountOfMaterial = amountOfMaterial;
    }

    /**
     * @return the amountOfMaterialUnit
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "hybridizationamount_unit_fk")
    public Term getAmountOfMaterialUnit() {
        return this.amountOfMaterialUnit;
    }

    /**
     * @param amountOfMaterialUnit the amountOfMaterialUnit to set
     */
    public void setAmountOfMaterialUnit(Term amountOfMaterialUnit) {
        this.amountOfMaterialUnit = amountOfMaterialUnit;
    }

    /**
     * @return the hybridizationData
     */
    @OneToMany(mappedBy = MAPPED_BY, fetch = FetchType.LAZY)
    public Set<HybridizationData> getHybridizationData() {
        return hybridizationData;
    }

    /**
     * @param hybridizationData the hybridizationData to set
     */
    public void setHybridizationData(Set<HybridizationData> hybridizationData) {
        this.hybridizationData = hybridizationData;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Set<CaArrayFile> getAllDataFiles() {
        Set<CaArrayFile> files = new HashSet<CaArrayFile>();
        for (RawArrayData rad : getRawDataCollection()) {
            files.add(rad.getDataFile());
        }
        for (DerivedArrayData dad : getDerivedDataCollection()) {
            files.add(dad.getDataFile());
        }
        return files;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<? extends Protectable> relatedProtectables() {
        Set<Protectable> protectables = new HashSet<Protectable>();
        for (LabeledExtract le : getLabeledExtracts()) {
            protectables.addAll(le.relatedProtectables());
        }
        return protectables;
    }

    /**
     * @return the uncompressed size of all associated data files, in bytes
     */
    @Transient
    public int getUncompressedSizeOfDataFiles() {
        int total = 0;
        for (CaArrayFile caArrayFile : getAllDataFiles()) {
            total += caArrayFile.getUncompressedSize();
        }
        return total;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public ExperimentDesignNodeType getNodeType() {
        return ExperimentDesignNodeType.HYBRIDIZATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors() {
        return getLabeledExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectPredecessor(AbstractExperimentDesignNode predecessor) {
        LabeledExtract le = (LabeledExtract) predecessor;
        getLabeledExtracts().add(le);
        le.getHybridizations().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectSuccessor(AbstractExperimentDesignNode successor) {
        throw new IllegalArgumentException("Should never be called as sources don't have predecessors");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AbstractExperimentDesignNode node) {
        Hybridization hyb = (Hybridization) node;
        super.merge(hyb);
        mergeSimpleHybridizationProperties(hyb);
        mergeArrayData(hyb);
        mergeHybridizationData(hyb);
        mergeArray(hyb);
        mergeImages(hyb);
        mergeFactorValues(hyb);
    }

    private void mergeArray(Hybridization hyb) {
        if (this.getArray() == null || this.getArray().getDesign() == null) {
            this.setArray(hyb.getArray());
            hyb.setArray(null);
        }
    }

    private void mergeFactorValues(Hybridization hyb) {
        for (AbstractFactorValue fv : hyb.getFactorValues()) {
            fv.setHybridization(this);
        }
        this.getFactorValues().addAll(hyb.getFactorValues());
        hyb.getFactorValues().clear();
    }

    private void mergeSimpleHybridizationProperties(Hybridization hyb) {
        if (this.getDescription() == null) {
            this.setDescription(hyb.getDescription());
        }
        if (this.getAmountOfMaterialUnit() == null) {
            this.setAmountOfMaterialUnit(hyb.getAmountOfMaterialUnit());
        }
    }

    private void mergeImages(Hybridization hyb) {
        for (Image image : hyb.getImages()) {
            image.setHybridization(this);
        }
        this.getImages().addAll(hyb.getImages());
        hyb.getImages().clear();
    }

    private void mergeArrayData(Hybridization hyb) {
        for (RawArrayData rad : hyb.getRawDataCollection()) {
            rad.getHybridizations().remove(hyb);
            rad.getHybridizations().add(this);
        }
        hyb.getRawDataCollection().clear();
        for (DerivedArrayData dad : hyb.getDerivedDataCollection()) {
            dad.getHybridizations().remove(hyb);
            dad.getHybridizations().add(this);
        }
        hyb.getDerivedDataCollection().clear();
    }

    private void mergeHybridizationData(Hybridization hyb) {
        for (HybridizationData data : hyb.getHybridizationData()) {
            data.setHybridization(this);
        }
        hyb.getHybridizationData().clear();
    }
}
