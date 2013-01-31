//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;

/**
 * Represents the raw image quantitation extracted from an image. In most cases, this can be thought of
 * as representing the values from a single data file, as an example for each Affymetrix CEL file there
 * will be a corresponding <code>RawArrayData</code> instance.
 */
@Entity
@DiscriminatorValue("RAW")
public class RawArrayData extends AbstractArrayData {

    private static final long serialVersionUID = 1234567890L;

    private Set<Hybridization> hybridizations = new HashSet<Hybridization>();
    private Set<Image> sourceImages = new HashSet<Image>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the hybridizations
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rawarraydata_hybridizations",
            joinColumns = { @javax.persistence.JoinColumn(name = "rawarraydata_id") },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "hybridization_id") }
    )
    @ForeignKey(name = "rawarraydata_hybridizations_hybridization_fk",
            inverseName = "rawarraydata_hybridizations_rawarraydata_fk")
    public Set<Hybridization> getHybridizations() {
        return hybridizations;
    }

    /**
     * Add a new hybridization to the collection of associated hybridizations.
     * @param hybridization hybridization to add
     */
    public void addHybridization(Hybridization hybridization) {
        hybridizations.add(hybridization);
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
     * @return source images
     */
    @OneToMany(mappedBy = "rawArrayData")
    @Cascade(CascadeType.DELETE)
    public Set<Image> getSourceImages() {
        return sourceImages;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setSourceImages(Set<Image> sourceImages) {
        this.sourceImages = sourceImages;
    }
}
