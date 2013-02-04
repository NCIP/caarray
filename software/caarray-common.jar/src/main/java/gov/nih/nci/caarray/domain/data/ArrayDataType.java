//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 * Indicates the source of array data and the complete list of <code>QuantitationTypes</code> that may be
 * associated with the particular type and version.
 */
@Entity
@org.hibernate.annotations.Entity(mutable = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ArrayDataType extends AbstractCaArrayObject {

    private static final long serialVersionUID = -192577620722459309L;

    private String name;
    private String version;
    private Set<QuantitationType> quantitationTypes = new HashSet<QuantitationType>();

    /**
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
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
     * @return the version
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the quantitationTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "arraydatatype_quantitationtypes",
            joinColumns = { @javax.persistence.JoinColumn(name = "arraydatatype_id") },
            inverseJoinColumns = { @javax.persistence.JoinColumn(name = "quantitationtype_id") }
    )
    @ForeignKey(name = "arraydatatype_quantitationtypes_arraydatatype_fk",
            inverseName = "arraydatatype_quantitationtypes_quantitationtype_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<QuantitationType> getQuantitationTypes() {
        return quantitationTypes;
    }

    /**
     * @param quantitationTypes the quantitationTypes to set
     */
    @SuppressWarnings({
            "PMD.UnusedPrivateMethod",
            "unused"
            })
    private void setQuantitationTypes(Set<QuantitationType> quantitationTypes) {
        this.quantitationTypes = quantitationTypes;
    }

}
