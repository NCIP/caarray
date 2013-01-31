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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 *
 */
@Entity
@DiscriminatorValue("DERIVED")
public class DerivedArrayData extends AbstractArrayData {

    private static final long serialVersionUID = 1234567890L;
    private Set<Hybridization> hybridizations = new HashSet<Hybridization>();
    private Set<AbstractArrayData> derivedFromArrayDataCollection = new HashSet<AbstractArrayData>();

    /**
     * @return the hybridizations
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "derivedarraydata_hybridizations",
            joinColumns = { @javax.persistence.JoinColumn(name = "derivedarraydata_id") },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "hybridization_id") }
    )
    @ForeignKey(name = "derivedarraydata_hybridizations_hybridization_fk",
            inverseName = "derivedarraydata_hybridizations_derivedarraydata_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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
     * @return the hybridizations
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "derivedarraydata_derivedfrom",
            joinColumns = { @javax.persistence.JoinColumn(name = "derivedarraydata_id") },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "derivedfrom_arraydata_id") }
    )
    @ForeignKey(name = "derivedfrom_arraydata_fk", inverseName = "derivedfrom_derivedarraydata_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<AbstractArrayData> getDerivedFromArrayDataCollection() {
        return derivedFromArrayDataCollection;
    }

    /**
     * Sets the derivedFromDatas.
     *
     * @param derivedFromDatasVal the derivedFromDatas
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setDerivedFromArrayDataCollection(final Set<AbstractArrayData> derivedFromArrayDataCollection) {
        this.derivedFromArrayDataCollection = derivedFromArrayDataCollection;
    }
}
