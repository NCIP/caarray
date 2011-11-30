/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
