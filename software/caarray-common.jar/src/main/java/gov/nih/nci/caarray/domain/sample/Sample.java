//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;

import java.util.Comparator;
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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

  /**

   */
@Entity
@DiscriminatorValue("SA")
@UniqueConstraint(fields = {
        @UniqueConstraintField(name = "externalSampleId"),
        @UniqueConstraintField(name = "experiment", nullsEqual = false) },
        generateDDLConstraint = false, message = "{sample.externalSampleId.uniqueConstraint}")
public class Sample extends AbstractBioMaterial implements Protectable {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    private Experiment experiment;
    private String externalSampleId;
    private Set<Source> sources = new HashSet<Source>();
    private Set<Extract> extracts = new HashSet<Extract>();

    /**
     * Gets the sources.
     *
     * @return the sources
     */
    @ManyToMany(mappedBy = "samples")
    public Set<Source> getSources() {
        return this.sources;
    }

    /**
     * Sets the sources.
     *
     * @param sourcesVal the sources
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setSources(final Set<Source> sourcesVal) {
        this.sources = sourcesVal;
    }

    /**
     * Gets the extracts.
     *
     * @return the extracts
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sampleextract",
            joinColumns = { @javax.persistence.JoinColumn(name = "sample_id") },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "extract_id") }
    )
    @ForeignKey(name = "sampleextract_sample_fk", inverseName = "sampleextract_extract_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Extract> getExtracts() {
        return this.extracts;
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the experiment to which this source belongs
     */
    @ManyToOne
    @JoinTable(name = "experimentsample",
            joinColumns = {@JoinColumn(name = "sample_id", insertable = false, updatable = false) },
            inverseJoinColumns = {@JoinColumn(name = "experiment_id", insertable = false, updatable = false) })
    public Experiment getExperiment() {
        return this.experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * @return the externalSampleId
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getExternalSampleId() {
        return this.externalSampleId;
    }

    /**
     * @param externalSampleId the externalSampleId to set
     */
    public void setExternalSampleId(String externalSampleId) {
        this.externalSampleId = externalSampleId;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Set<Hybridization> getRelatedHybridizations() {
        Set<Hybridization> hybs = new HashSet<Hybridization>();
        for (Extract e : getExtracts()) {
            hybs.addAll(e.getRelatedHybridizations());
        }
        return hybs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
   public ExperimentDesignNodeType getNodeType() {
        return ExperimentDesignNodeType.SAMPLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors() {
        return getSources();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors() {
        return getExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectPredecessor(AbstractExperimentDesignNode predecessor) {
        Source source = (Source) predecessor;
        getSources().add(source);
        source.getSamples().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddDirectSuccessor(AbstractExperimentDesignNode successor) {
        Extract extract = (Extract) successor;
        getExtracts().add(extract);
        extract.getSamples().add(this);
    }

    /**
     * Comparator for samples by name.
     */
    public static class ByNameComparator implements Comparator<Sample> {
        /**
         * Compares samples by name.
         * {@inheritDoc}
         */
        public int compare(Sample s1, Sample s2) {
            return new CompareToBuilder().append(s1.getName(), s2.getName()).toComparison();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AbstractExperimentDesignNode node) {
        Sample sample = (Sample) node;
        super.merge(sample);
        if (this.getExternalSampleId() == null) {
            this.setExternalSampleId(sample.getExternalSampleId());
        }

        for (AccessProfile profile : this.getExperiment().getProject().getAllAccessProfiles()) {
            SampleSecurityLevel thisLevel = profile.getSampleSecurityLevels().get(this);
            SampleSecurityLevel otherLevel = profile.getSampleSecurityLevels().get(sample);
            boolean replaceSecLevel = (thisLevel == null && otherLevel != null)
                    || (thisLevel != null && otherLevel != null
                            && thisLevel.compareTo(profile.getSampleSecurityLevels().get(sample)) > 0);
            if (replaceSecLevel) {
                profile.getSampleSecurityLevels().put(this, otherLevel);
            }
            profile.getSampleSecurityLevels().remove(sample);
        }
    }
}
