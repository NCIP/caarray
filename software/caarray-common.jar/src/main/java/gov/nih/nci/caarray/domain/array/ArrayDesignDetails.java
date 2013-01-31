//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.MaxSerializableSize;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Contains the low-level array design details (Features, PhysicalProbes, and LogicalProbes) for a
 * microarray design.
 */
@Entity
@org.hibernate.annotations.Entity(mutable = false)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ArrayDesignDetails extends AbstractCaArrayObject {

    private static final long serialVersionUID = -4810002388725364910L;
    private static final String MAPPED_BY = "arrayDesignDetails";
    
    private static final int MAX_SERIALIZABLE_SIZE = 100000;

    private Set<Feature> features = new HashSet<Feature>();
    private Set<ProbeGroup> probeGroups = new HashSet<ProbeGroup>();
    private Set<PhysicalProbe> probes = new HashSet<PhysicalProbe>();
    private Set<LogicalProbe> logicalProbes = new HashSet<LogicalProbe>();

    /**
     * @return the features
     */
    @Cascade(CascadeType.ALL)
    @OneToMany(mappedBy = MAPPED_BY, fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @MaxSerializableSize(MAX_SERIALIZABLE_SIZE)
    public Set<Feature> getFeatures() {
        return features;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    /**
     * @return the pysicalProbes
     */
    @OneToMany(mappedBy = MAPPED_BY, fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @MaxSerializableSize(MAX_SERIALIZABLE_SIZE)
    public Set<PhysicalProbe> getProbes() {
        return probes;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProbes(Set<PhysicalProbe> pysicalProbes) {
        this.probes = pysicalProbes;
    }

    /**
     * @return the logicalProbes
     */
    @OneToMany(mappedBy = MAPPED_BY, fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @MaxSerializableSize(MAX_SERIALIZABLE_SIZE)
    public Set<LogicalProbe> getLogicalProbes() {
        return logicalProbes;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setLogicalProbes(Set<LogicalProbe> logicalProbes) {
        this.logicalProbes = logicalProbes;
    }

    /**
     * @return the probeGroups
     */
    @OneToMany(mappedBy = MAPPED_BY, fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @MaxSerializableSize(MAX_SERIALIZABLE_SIZE)
    public Set<ProbeGroup> getProbeGroups() {
        return probeGroups;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProbeGroups(Set<ProbeGroup> probeGroups) {
        this.probeGroups = probeGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "id=" + getId();
    }
}
