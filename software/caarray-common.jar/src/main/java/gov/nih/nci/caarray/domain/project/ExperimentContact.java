//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 *
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class ExperimentContact extends AbstractCaArrayEntity {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    /** value of the Term for the PI Role. */
    public static final String PI_ROLE = "investigator";
    /** value of the Term for the POC Role. */
    public static final String MAIN_POC_ROLE = "submitter";

    private Person contact;
    private Set<Term> roles = new HashSet<Term>();
    private Experiment experiment;

    /**
     * Default constructor, mostly for hibernate.
     */
    public ExperimentContact() {
        // intentionally blank
    }

    /**
     * Create a new ExperimentContact for given experiment, contacts, and roles.
     *
     * @param experiment
     *            the experiment for which this is a contact
     * @param contact
     *            the contact
     * @param roles
     *            the roles this contact has on the experiment
     */
    public ExperimentContact(Experiment experiment, Person contact,
            Collection<Term> roles) {
        this.contact = contact;
        this.roles.addAll(roles);
        this.experiment = experiment;
    }

    /**
     * Create a new ExperimentContact for given experiment, contacts, and role.
     *
     * @param experiment
     *            the experiment for which this is a contact
     * @param contact
     *            the contact
     * @param role
     *            the role this contact has on the experiment
     */
    public ExperimentContact(Experiment experiment, Person contact,
            Term role) {
        this(experiment, contact, Arrays.asList(role));
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "experimentcontact_contact_fk")
    public Person getContact() {
        return contact;
    }

    /**
     * Sets the contact.
     *
     * @param contactVal
     *            the contact
     */
    public void setContact(final Person contactVal) {
        this.contact = contactVal;
    }

    /**
     * Gets the person.
     *
     * @return the person
     */
    @Transient
    public Person getPerson() {
        return this.contact;
    }

    /**
     * Sets the person.
     *
     * @param personVal
     *            the person contact
     */
    public void setPerson(final Person personVal) {
        this.contact = personVal;
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
    @JoinTable(name = "experimentcontactrole", joinColumns = {
              @JoinColumn(name = "experimentcontact_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
    @ForeignKey(name = "investcont_contact_fk", inverseName = "investcont_role_fk")
    public Set<Term> getRoles() {
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param rolesVal the roles
     */
    public void setRoles(Set<Term> rolesVal) {
        this.roles = rolesVal;
    }

    /**
     * @return comma delimited list of each role names in
     * roles list.
     */
    @Transient
    public String getRoleNames() {
        StringBuilder sb = new StringBuilder();
        Iterator<Term> it = getRoles().iterator();
        while (it.hasNext()) {
            sb.append(((Term) it.next()).getValue());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * @return the experiment
     */
    @ManyToOne
    @JoinColumn(name = "experiment", insertable = false, updatable = false)
    @ForeignKey(name = "expcontact_invest_fk")
    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * @param experiment
     *            the experiment to set
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * Returns whether this contact is the PI for the experiment (based on
     * whether he has the appropriate role).
     *
     * @return whether this contact is the PI for the experiment
     */
    @Transient
    public boolean isPrimaryInvestigator() {
        return CollectionUtils.exists(roles, new RolePredicate(PI_ROLE));
    }

    /**
     * Returns whether this contact is the main POC for the experiment (based on
     * whether he has the appropriate role).
     *
     * @return whether this contact is the main POC for the experiment
     */
    @Transient
    public boolean isMainPointOfContact() {
        return CollectionUtils.exists(roles, new RolePredicate(MAIN_POC_ROLE));
    }

    /**
     * Predicate that matches role Terms having a given value.
     */
    private static class RolePredicate implements Predicate {
        private final String roleValue;

        /**
         * Create a RolePredicate checking for given value.
         *
         * @param roleValue
         *            value to check for
         */
        public RolePredicate(String roleValue) {
            super();
            this.roleValue = roleValue;
        }

        /**
         * {@inheritDoc}
         */
        public boolean evaluate(Object o) {
            Term role = (Term) o;
            return roleValue.equals(role.getValue());
        }
    }

    /**
     * Returns true if the underlying contact information is the same, ignoring the roles and Experiment.
     * @param experimentContact contact to compare to
     * @return true if contact info is equal
     */
    public boolean equalsBaseContact(ExperimentContact experimentContact) {
        return this.contact.equals(experimentContact.contact);
    }

}
