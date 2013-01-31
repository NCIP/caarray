//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.contact.AbstractContact;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;

import java.util.Collections;
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
@DiscriminatorValue("SO")
public class Source extends AbstractBioMaterial {
    private static final long serialVersionUID = 1234567890L;
    private static final String DEFAULT_FK_ID = "source_id";

    private Set<Sample> samples = new HashSet<Sample>();
    private Set<AbstractContact> providers = new HashSet<AbstractContact>();
    private Experiment experiment;

    /**
     * Gets the samples.
     *
     * @return the samples
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sourcesample",
            joinColumns = { @javax.persistence.JoinColumn(name = DEFAULT_FK_ID) },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "sample_id") }
    )
    @ForeignKey(name = "sourcesample_source_fk", inverseName = "sourcesample_sample_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @Filter(name = "Project1", condition = Experiment.SAMPLES_FILTER)
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
     * Gets the providers.
     *
     * @return the providers
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sourceprovider",
            joinColumns = { @javax.persistence.JoinColumn(name = DEFAULT_FK_ID) },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "contact_id") }
    )
    @ForeignKey(name = "sourceprovider_source_fk", inverseName = "sourceprovider_provider_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<AbstractContact> getProviders() {
        return providers;
    }

    /**
     * Sets the providers.
     *
     * @param providersVal the providers
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProviders(final Set<AbstractContact> providersVal) {
        this.providers = providersVal;
    }

    /**
     * {@inheritDoc}
     */
    @ManyToOne
    @JoinTable(name = "experimentsource",
            joinColumns = {@JoinColumn(name = "source_id", insertable = false, updatable = false) },
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
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("providers", providers)
            .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Set<Hybridization> getRelatedHybridizations() {
        Set<Hybridization> hybs = new HashSet<Hybridization>();
        for (Sample s : getSamples()) {
            hybs.addAll(s.getRelatedHybridizations());
        }
        return hybs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public ExperimentDesignNodeType getNodeType() {
        return ExperimentDesignNodeType.SOURCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors() {
        return getSamples();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectPredecessor(AbstractExperimentDesignNode predecessor) {
        throw new IllegalStateException("Should never be called as sources don't have predecessors");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectSuccessor(AbstractExperimentDesignNode successor) {
        Sample sample = (Sample) successor;
        getSamples().add(sample);
        sample.getSources().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AbstractExperimentDesignNode node) {
        Source source = (Source) node;
        super.merge(source);
        this.getProviders().addAll(source.getProviders());
    }
}
