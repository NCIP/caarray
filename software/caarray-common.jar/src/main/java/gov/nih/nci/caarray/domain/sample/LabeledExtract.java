//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.ProtectableDescendent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 *
 */
@Entity
@DiscriminatorValue("LA")
public class LabeledExtract extends AbstractBioMaterial implements ProtectableDescendent {
    private static final long serialVersionUID = 1234567890L;

    private Term label;
    private Set<Extract> extracts = new HashSet<Extract>();
    private Set<Hybridization> hybridizations = new HashSet<Hybridization>();
    private Experiment experiment;

    /**
     * Gets the label.
     *
     * @return the label
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_label_fk")
    public Term getLabel() {
        return label;
    }

    /**
     * Sets the label.
     *
     * @param labelVal the label
     */
    public void setLabel(final Term labelVal) {
        this.label = labelVal;
    }

    /**
     * Gets the extracts.
     *
     * @return the extracts
     */
    @ManyToMany(mappedBy = "labeledExtracts")
    @Filter(name = "Project1", condition = Experiment.EXTRACTS_FILTER)
    public Set<Extract> getExtracts() {
        return extracts;
    }

    /**
     * Sets the extracts.
     *
     * @param extractsVal the extracts
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setExtracts(final Set<Extract> extractsVal) {
        this.extracts = extractsVal;
    }

    /**
     * Gets the hybridizations.
     *
     * @return the hybridizations
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "labeledextracthybridization",
            joinColumns = { @javax.persistence.JoinColumn(name = "labeledextract_id") },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "hybridization_id") }
    )
    @ForeignKey(name = "labeledextracthybridization_labeledextract_fk",
            inverseName = "labeledextracthybridization_hybridization_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Hybridization> getHybridizations() {
        return hybridizations;
    }

    /**
     * Sets the hybridizations.
     *
     * @param hybridizationsVal the hybridizations
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setHybridizations(final Set<Hybridization> hybridizationsVal) {
        this.hybridizations = hybridizationsVal;
    }

    /**
     * @return the experiment to which this source belongs
     */
    @ManyToOne
    @JoinTable(name = "experimentlabeledextract",
            joinColumns = {@JoinColumn(name = "labeled_extract_id", insertable = false, updatable = false) },
            inverseJoinColumns = {@JoinColumn(name = "experiment_id", insertable = false, updatable = false) })
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
    public Collection<? extends Protectable> relatedProtectables() {
        Set<Protectable> protectables = new HashSet<Protectable>();
        for (Extract e : getExtracts()) {
            protectables.addAll(e.relatedProtectables());
        }
        return protectables;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Set<Hybridization> getRelatedHybridizations() {
        return getHybridizations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("label", label)
            .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public ExperimentDesignNodeType getNodeType() {
        return ExperimentDesignNodeType.LABELED_EXTRACT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors() {
        return getExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors() {
        return getHybridizations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectPredecessor(AbstractExperimentDesignNode predecessor) {
        Extract extract = (Extract) predecessor;
        getExtracts().add(extract);
        extract.getLabeledExtracts().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectSuccessor(AbstractExperimentDesignNode successor) {
        Hybridization hyb = (Hybridization) successor;
        getHybridizations().add(hyb);
        hyb.getLabeledExtracts().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AbstractExperimentDesignNode node) {
        LabeledExtract labeledExtract = (LabeledExtract) node;
        super.merge(labeledExtract);

        if (this.getLabel() == null) {
            this.setLabel(labeledExtract.getLabel());
        }
    }
}
