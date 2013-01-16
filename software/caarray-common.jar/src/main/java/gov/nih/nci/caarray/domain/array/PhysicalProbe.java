//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 * Represents a physical probe on a microarray.
 */
@Entity
@DiscriminatorValue("PP")
public class PhysicalProbe extends AbstractProbe {

    private static final long serialVersionUID = -7343503650075935784L;

    private ProbeGroup probeGroup;
    private Set<Feature> features = new HashSet<Feature>();
    private ArrayDesignDetails arrayDesignDetails;
    private Term controlType;

    /**
     * Creates a new <code>PhysicalProbe</code>.
     *
     * @param details the array design details
     * @param probeGroup probe group
     */
    public PhysicalProbe(ArrayDesignDetails details, ProbeGroup probeGroup) {
        super();
        this.probeGroup = probeGroup;
        setArrayDesignDetails(details);
    }

    /**
     * @deprecated hibernate & castor only
     */
    @Deprecated
    public PhysicalProbe() {
        // hibernate constructor
    }

    /**
     * @return the probeGroup
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "probe_group")
    @ForeignKey(name = "probe_group_fk")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.EVICT })
    public ProbeGroup getProbeGroup() {
        return probeGroup;
    }

    /**
     * @param probeGroup the probe group this probe belongs to
     */
    public void setProbeGroup(ProbeGroup probeGroup) {
        this.probeGroup = probeGroup;
    }

    /**
     * @return the features
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "probefeature",
            joinColumns = { @JoinColumn(name = "physical_probe_id") },
            inverseJoinColumns = { @JoinColumn(name = "feature_id") }
    )
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE })
    public Set<Feature> getFeatures() {
        return features;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    /**
     * Add a feature to this physical probe.
     * @param feature feature to add
     */
    public void addFeature(Feature feature) {
        this.features.add(feature);
    }

    /**
     * @return the design details
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, name = "physicalprobe_details_id")
    @ForeignKey(name = "physicalprobe_details_fk")
    public ArrayDesignDetails getArrayDesignDetails() {
        return arrayDesignDetails;
    }

    private void setArrayDesignDetails(ArrayDesignDetails arrayDesignDetails) {
        this.arrayDesignDetails = arrayDesignDetails;
    }

    /**
     * @return the controlType
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "physicalprobe_controltype_fk")
    public Term getControlType() {
        return controlType;
    }

    /**
     * @param controlType the controlType to set
     */
    public void setControlType(Term controlType) {
        this.controlType = controlType;
    }

}
