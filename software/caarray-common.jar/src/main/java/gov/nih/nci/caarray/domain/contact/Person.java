//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.contact;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

/**
 *
 */
@Entity
@DiscriminatorValue("P")
public class Person extends AbstractContact {

    private static final long serialVersionUID = 1234567890L;

    private String firstName;
    private String middleInitials;
    private String lastName;
    private Set<Organization> affiliations = new HashSet<Organization>();

    /**
     * Default constructor.
     */
    public Person() {
        // intentionally empty
    }

    /**
     * Constructor for a Person based on a CSM User instance.
     * @param user the user from which to copy name and contact properties
     */
    public Person(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        setEmail(user.getEmailId());
    }


    /**
     * Gets the firstName.
     *
     * @return the firstName
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the firstName.
     *
     * @param firstNameVal the firstName
     */
    public void setFirstName(final String firstNameVal) {
        this.firstName = firstNameVal;
    }
    /**
     * Gets the lastName.
     *
     * @return the lastName
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the lastName.
     *
     * @param lastNameVal the lastName
     */
    public void setLastName(final String lastNameVal) {
        this.lastName = lastNameVal;
    }
    /**
     * Gets the middleInitials.
     *
     * @return the middleInitials
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getMiddleInitials() {
        return this.middleInitials;
    }

    /**
     * Sets the middleInitials.
     *
     * @param middleInitialsVal the middleInitials
     */
    public void setMiddleInitials(final String middleInitialsVal) {
        this.middleInitials = middleInitialsVal;
    }

    /**
     * Gets the affiliations.
     *
     * @return the affiliations
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "personorganization",
            joinColumns = { @JoinColumn(name = "person_id") },
            inverseJoinColumns = { @JoinColumn(name = "organization_id") }
    )
    @ForeignKey(name = "perorg_person_fk", inverseName = "perorg_organization_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
    public Set<Organization> getAffiliations() {
        return this.affiliations;
    }

    /**
     * Sets the affiliations.
     *
     * @param affiliationsVal the affiliations
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setAffiliations(final Set<Organization> affiliationsVal) {
        this.affiliations = affiliationsVal;
    }

    /**
     * @return the Person's full name
     */
    @Transient
    public String getName() {
        StringBuilder name = new StringBuilder(getLastName()).append(", ").append(getFirstName());
        if (getMiddleInitials() != null) {
            name.append(" ").append(getMiddleInitials());
        }
        return name.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ", name=" + getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Person)) {
            return false;
        }
        Person rhs = (Person) object;
        return new EqualsBuilder().appendSuper(super.equals(object)).append(this.middleInitials, rhs.middleInitials)
                .append(this.affiliations, rhs.affiliations).append(this.firstName, rhs.firstName).append(
                        this.lastName, rhs.lastName).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF
        return new HashCodeBuilder(2030047093, 905760599).appendSuper(super.hashCode()).append(this.middleInitials)
                .append(this.affiliations).append(this.firstName).append(this.lastName).toHashCode();
        // CHECKSTYLE:ON
    }
}
