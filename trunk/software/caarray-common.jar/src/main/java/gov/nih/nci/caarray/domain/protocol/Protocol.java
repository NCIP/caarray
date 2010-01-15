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

package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Class representing a protocol.
 *
 * @author Scott Miller
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "source" }))
public class Protocol extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1234567890L;

    private String contact;
    private String description;
    private String hardware;
    private String name;
    private String software;
    private Term type;
    private TermSource source;
    private String url;
    private Set<Parameter> parameters = new HashSet<Parameter>();

    /**
     * Constructor for use by hibernate and struts only.
     */
    @Deprecated
    public Protocol() {
        // do nothing
    }

    /**
     * Constructor taking all required fields.
     *
     * @param name the name.
     * @param type the type.
     * @param source the source.
     */
    public Protocol(String name, Term type, TermSource source) {
        this.name = name;
        this.type = type;
        this.source = source;
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getContact() {
        return this.contact;
    }

    /**
     * Sets the contact.
     *
     * @param contactVal the contact
     */
    public void setContact(final String contactVal) {
        this.contact = contactVal;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     *
     * @param descriptionVal the description
     */
    public void setDescription(final String descriptionVal) {
        this.description = descriptionVal;
    }

    /**
     * Gets the hardware.
     *
     * @return the hardware
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getHardware() {
        return this.hardware;
    }

    /**
     * Sets the hardware.
     *
     * @param hardwareVal the hardware
     */
    public void setHardware(final String hardwareVal) {
        this.hardware = hardwareVal;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    @NotNull
    public String getName() {
        return this.name;
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
     * Gets the software.
     *
     * @return the software
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getSoftware() {
        return this.software;
    }

    /**
     * Sets the software.
     *
     * @param softwareVal the software
     */
    public void setSoftware(final String softwareVal) {
        this.software = softwareVal;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "protocol_type_fk")
    @NotNull
    public Term getType() {
        return this.type;
    }

    /**
     * Sets the type.
     *
     * @param typeVal the type
     */
    public void setType(final Term typeVal) {
        this.type = typeVal;
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
     * Gets the parameters.
     *
     * @return the parameters
     */
    @OneToMany(mappedBy = "protocol", fetch = FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    public Set<Parameter> getParameters() {
        return this.parameters;
    }

    /**
     * Sets the parameters.
     *
     * @param parametersVal the parameters
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setParameters(final Set<Parameter> parametersVal) {
        this.parameters = parametersVal;
    }

    /**
     * @return the source
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "protocol_term_source_fk")
    @NotNull
    public TermSource getSource() {
        return this.source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(TermSource source) {
        this.source = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Protocol)) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (getId() == null) {
            // by default, two transient instances cannot ever be equal
            return false;
        }

        Protocol rhs = (Protocol) o;
        return new EqualsBuilder().append(getName(), rhs.getName()).append(getSource(), rhs.getSource()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getSource()).toHashCode();
    }
}
