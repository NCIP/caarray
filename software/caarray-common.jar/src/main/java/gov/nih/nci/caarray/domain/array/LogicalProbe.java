//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.array;

import java.util.HashSet;
import java.util.Set;

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
 * A logical probe corresponding to a sequence that is composed of multiple physical probes.
 */
@Entity
@DiscriminatorValue("LP")
public class LogicalProbe extends AbstractProbe {

    private static final long serialVersionUID = 4406463229622624441L;

    private Set<PhysicalProbe> probes = new HashSet<PhysicalProbe>();

    private ArrayDesignDetails arrayDesignDetails;

    /**
     * Creates a new <code>LogicalProbe</code> with its LSID initialized.
     *
     * @param details the array design details
     */
    public LogicalProbe(ArrayDesignDetails details) {
        super();
        setArrayDesignDetails(details);
    }

    /**
     * @deprecated hibernate & castor only
     */
    @Deprecated
    public LogicalProbe() {
        // Hibernate-only constructor
    }

    /**
     * @return the physicalProbes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "logicalprobe_physicalprobe",
            joinColumns = { @JoinColumn(name = "logical_probe_id") },
            inverseJoinColumns = { @JoinColumn(name = "physical_probe_id") }
    )
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE })
    public Set<PhysicalProbe> getProbes() {
        return probes;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProbes(Set<PhysicalProbe> physicalProbes) {
        this.probes = physicalProbes;
    }

    /**
     * Add a physical probe to this logical probe.
     * @param physicalProbe physical probe to add
     */
    public void addProbe(PhysicalProbe physicalProbe) {
        this.probes.add(physicalProbe);
    }

    /**
     * @return the design details
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, name = "logicalprobe_details_id")
    @ForeignKey(name = "logicalprobe_details_fk")
    public ArrayDesignDetails getArrayDesignDetails() {
        return arrayDesignDetails;
    }

    private void setArrayDesignDetails(ArrayDesignDetails arrayDesignDetails) {
        this.arrayDesignDetails = arrayDesignDetails;
    }
}
