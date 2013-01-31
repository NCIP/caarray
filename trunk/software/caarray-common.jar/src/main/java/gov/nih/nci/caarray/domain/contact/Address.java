//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.contact;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;

/**
 *
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class Address extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String street1;
    private String street2;
    private String city;
    private String state;
    private String zipCode;

    /**
     * Gets the city.
     *
     * @return the city
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param cityVal the city
     */
    public void setCity(final String cityVal) {
        this.city = cityVal;
    }
    /**
     * Gets the state.
     *
     * @return the state
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param stateVal the state
     */
    public void setState(final String stateVal) {
        this.state = stateVal;
    }
    /**
     * Gets the street1.
     *
     * @return the street1
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getStreet1() {
        return street1;
    }

    /**
     * Sets the street1.
     *
     * @param street1 the street1
     */
    public void setStreet1(final String street1) {
        this.street1 = street1;
    }
    /**
     * Gets the street2.
     *
     * @return the street2
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getStreet2() {
        return street2;
    }

    /**
     * Sets the street2.
     *
     * @param street2 the street2
     */
    public void setStreet2(final String street2) {
        this.street2 = street2;
    }
    /**
     * Gets the zipCode.
     *
     * @return the zipCode
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the zipCode.
     *
     * @param zipCodeVal the zipCode
     */
    public void setZipCode(final String zipCodeVal) {
        this.zipCode = zipCodeVal;
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
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Address)) {
            return false;
        }
        Address rhs = (Address) object;
        return new EqualsBuilder().append(this.street2, rhs.street2).append(this.zipCode, rhs.zipCode).append(
                this.street1, rhs.street1).append(this.state, rhs.state).append(this.city, rhs.city).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF
        return new HashCodeBuilder(-2071693803, -723842543).append(this.street2).append(this.zipCode).append(
                this.street1).append(this.state).append(this.city).toHashCode();
        // CHECKSTYLE:ON
    }
}
