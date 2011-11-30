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
