//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * A logical grouping of like probes, allowing the specification of a type.
 */
@Entity
@org.hibernate.annotations.Entity(mutable = false)
public class ProbeGroup extends AbstractCaArrayEntity {

    private static final long serialVersionUID = -7001732731586530134L;

    private Set<PhysicalProbe> probes = new HashSet<PhysicalProbe>();
    private String name;
    private ArrayDesignDetails arrayDesignDetails;

    /**
     * @param arrayDesignDetails array design arrayDesignDetails
     */
    public ProbeGroup(ArrayDesignDetails arrayDesignDetails) {
        setArrayDesignDetails(arrayDesignDetails);
    }

    /**
     * @deprecated for castor & hibernate use only
     */
    @Deprecated
    public ProbeGroup() {
        // hibernate constructor
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the probes
     */
    @OneToMany(mappedBy = "probeGroup")
    public Set<PhysicalProbe> getProbes() {
        return probes;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProbes(Set<PhysicalProbe> probes) {
        this.probes = probes;
    }

    /**
     * @return the design arrayDesignDetails
     */
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    public ArrayDesignDetails getArrayDesignDetails() {
        return arrayDesignDetails;
    }

    private void setArrayDesignDetails(ArrayDesignDetails adetails) {
        this.arrayDesignDetails = adetails;
    }
}
