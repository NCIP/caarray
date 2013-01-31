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
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.security.Protectable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

  /**
   * 
   */
@Entity
@DiscriminatorValue(Sample.DISCRIMINATOR)
public class Sample extends AbstractBioMaterial implements Protectable {
    private static final long serialVersionUID = 1234567890L;

    /** the Hibernate discriminator for this biomaterial subclass. */
    public static final String DISCRIMINATOR = "SA";

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
