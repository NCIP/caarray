/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gov.nih.nci.caarray.domain.hybridization;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
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
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Where;
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
    @ManyToMany(mappedBy = "hybridizations", targetEntity = AbstractArrayData.class, fetch = FetchType.LAZY)
    @Where(clause = "discriminator = '" + DerivedArrayData.DISCRIMINATOR + "'")
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
    @ManyToMany(mappedBy = "hybridizations", targetEntity = AbstractArrayData.class, fetch = FetchType.LAZY)
    @Where(clause = "discriminator = '" + RawArrayData.DISCRIMINATOR + "'")
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
     * Add a new array data to this hybridization. This will add the array data
     * to the appropriate collection, depending on whether it is raw or derived.
     * @param arrayData the array data to add
     */
    public void addArrayData(AbstractArrayData arrayData) {
        if (arrayData instanceof RawArrayData) {
            this.rawDataCollection.add((RawArrayData) arrayData);
        } else {
            this.derivedDataCollection.add((DerivedArrayData) arrayData);
        }
    }

    /**
     * Remove an array data from this hybridization. This will remove the array data
     * from the appropriate collection, depending on whether it is raw or derived.
     * @param arrayData the array data to remove
     */
    public void removeArrayData(AbstractArrayData arrayData) {
        if (arrayData instanceof RawArrayData) {
            this.rawDataCollection.remove(arrayData);
        } else {
            this.derivedDataCollection.remove(arrayData);
        }
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
    @Filter(name = Experiment.SECURITY_FILTER_NAME, condition = Experiment.LABELED_EXTRACTS_FILTER)
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
        return super.toString() + ", name=" + getName();
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
