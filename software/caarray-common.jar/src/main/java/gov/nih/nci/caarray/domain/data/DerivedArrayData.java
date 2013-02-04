//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.data;

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
@DiscriminatorValue(DerivedArrayData.DISCRIMINATOR)
public class DerivedArrayData extends AbstractArrayData {
    private static final long serialVersionUID = 1234567890L;

    /** the Hibernate discriminator for this ArrayData subclass. */
    public static final String DISCRIMINATOR = "DERIVED";

    private Set<AbstractArrayData> derivedFromArrayDataCollection = new HashSet<AbstractArrayData>();

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
