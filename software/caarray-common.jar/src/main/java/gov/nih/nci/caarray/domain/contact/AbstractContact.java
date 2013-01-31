//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.contact;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;

/**
 *
 */
@Entity
@Table(name = "contact")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "discriminator",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class AbstractContact extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String email;
    private String fax;
    private String phone;
    private String url;
    private Address address;

    /**
     * Gets the email.
     *
     * @return the email
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    @Email
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email.
     *
     * @param emailVal the email
     */
    public void setEmail(final String emailVal) {
        this.email = emailVal;
    }
    /**
     * Gets the fax.
     *
     * @return the fax
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getFax() {
        return this.fax;
    }

    /**
     * Sets the fax.
     *
     * @param faxVal the fax
     */
    public void setFax(final String faxVal) {
        this.fax = faxVal;
    }
    /**
     * Gets the phone.
     *
     * @return the phone
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getPhone() {
        return this.phone;
    }

    /**
     * Sets the phone.
     *
     * @param phoneVal the phone
     */
    public void setPhone(final String phoneVal) {
        this.phone = phoneVal;
    }
    /**
     * Gets the url.
     *
     * @return the url
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets the url.
     *
     * @param urlVal the url
     */
    public void setUrl(final String urlVal) {
        this.url = urlVal;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "contact_address_fk")
    public Address getAddress() {
        return this.address;
    }

    /**
     * Sets the address.
     *
     * @param addressVal the address
     */
    public void setAddress(final Address addressVal) {
        this.address = addressVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof AbstractContact)) {
            return false;
        }
        AbstractContact rhs = (AbstractContact) object;
        return new EqualsBuilder().append(this.phone, rhs.phone).append(this.address, rhs.address).append(this.url,
                rhs.url).append(this.email, rhs.email).append(this.fax, rhs.fax).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF
        return new HashCodeBuilder(77196439, 410917995).append(this.phone).append(this.address).append(this.url)
                .append(this.email).append(this.fax).toHashCode();
        // CHECKSTYLE:ON
    }
}
