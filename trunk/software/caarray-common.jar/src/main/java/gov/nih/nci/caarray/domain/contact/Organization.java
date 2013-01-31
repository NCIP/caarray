//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.contact;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Index;

/**
 * Represents an organization.
 */
@Entity
@DiscriminatorValue("O")
public class Organization extends AbstractContact {

    private static final long serialVersionUID = 1234567890L;

    private String name;
    private boolean provider = false;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param nameVal the name
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * @return whether this organization represents an array provider.
     */
    public boolean isProvider() {
        return this.provider;
    }

    /**
     * Sets whether this organization represents an array provider.
     * 
     * @param provider whether this organization represents an array provider.
     */
    public void setProvider(boolean provider) {
        this.provider = provider;
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
        if (!(object instanceof Organization)) {
            return false;
        }
        Organization rhs = (Organization) object;
        return new EqualsBuilder().appendSuper(super.equals(object)).append(this.provider, rhs.provider).append(
                this.name, rhs.name).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF
        return new HashCodeBuilder(1753948321, 1917926501).appendSuper(super.hashCode()).append(this.provider).append(
                this.name).toHashCode();
        // CHECKSTYLE:ON
    }

    /**
     * Create a new Organization instance that represents an array provider.
     * 
     * @return the new Organization instance 
     */
    public static Organization newArrayProvider() {
        Organization o = new Organization();
        o.setProvider(true);
        return o;
    }
}
