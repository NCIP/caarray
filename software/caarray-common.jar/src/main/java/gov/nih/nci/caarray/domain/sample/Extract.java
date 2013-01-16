//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.ProtectableDescendent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

  /**

   */
@Entity
@DiscriminatorValue(Extract.DISCRIMINATOR)
public class Extract extends AbstractBioMaterial implements ProtectableDescendent {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    /** the Hibernate discriminator for this biomaterial subclass. */
    public static final String DISCRIMINATOR = "EX";

    private Set<Sample> samples = new HashSet<Sample>();
    private Set<LabeledExtract> labeledExtracts = new HashSet<LabeledExtract>();

    /**
     * Gets the samples.
     *
     * @return the samples
     */
    @ManyToMany(mappedBy = "extracts")
    @Filter(name = Experiment.SECURITY_FILTER_NAME, condition = Experiment.SAMPLES_FILTER)
    public Set<Sample> getSamples() {
        return samples;
    }

    /**
     * Sets the samples.
     *
     * @param samplesVal the samples
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setSamples(final Set<Sample> samplesVal) {
        this.samples = samplesVal;
    }

    /**
     * Gets the labeledExtracts.
     *
     * @return the labeledExtracts
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "extractlabeledextract",
            joinColumns = { @javax.persistence.JoinColumn(name = "extract_id") },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "labeledextract_id") }
    )
    @ForeignKey(name = "extractlabeled_extract_fk", inverseName = "extractlabeled_labeled_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<LabeledExtract> getLabeledExtracts() {
        return labeledExtracts;
    }

    /**
     * Sets the labeledExtracts.
     *
     * @param labeledExtractsVal the labeledExtracts
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setLabeledExtracts(final Set<LabeledExtract> labeledExtractsVal) {
        this.labeledExtracts = labeledExtractsVal;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<? extends Protectable> relatedProtectables() {
        return Collections.unmodifiableCollection(getSamples());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public ExperimentDesignNodeType getNodeType() {
        return ExperimentDesignNodeType.EXTRACT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors() {
        return getSamples();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors() {
        return getLabeledExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectPredecessor(AbstractExperimentDesignNode predecessor) {
        Sample sample = (Sample) predecessor;
        getSamples().add(sample);
        sample.getExtracts().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectSuccessor(AbstractExperimentDesignNode successor) {
        LabeledExtract le = (LabeledExtract) successor;
        getLabeledExtracts().add(le);
        le.getExtracts().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AbstractExperimentDesignNode node) {
        Extract extract = (Extract) node;
        super.merge(extract);
    }
}
