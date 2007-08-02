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

package gov.nih.nci.caarray.domain.vocabulary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

  /**

   */
@Entity
@Table(name = "TERM")
public class Term extends AbstractCaArrayEntity {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    /**
     * The description String.
     */
    private String description;

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Column(name = "DESCRIPTION", length = DEFAULT_STRING_COLUMN_SIZE)
    public String getDescription() {
        return description;
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
     * The value String.
     */
    private String value;

    /**
     * Gets the value.
     *
     * @return the value
     */
    @Column(name = "VALUE", length = DEFAULT_STRING_COLUMN_SIZE)
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param valueVal the value
     */
    public void setValue(final String valueVal) {
        this.value = valueVal;
    }

    /**
     * The accession gov.nih.nci.caarray.domain.vocabulary.Accession.
     */
    private Accession accession;

    /**
     * Gets the accession.
     *
     * @return the accession
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "ACCESSION_ID")
    @Index(name = "TERM_ACCESSION_IDX")
    @ForeignKey(name = "ACCESSION_ID")
    public Accession getAccession() {
        return accession;
    }

    /**
     * Sets the accession.
     *
     * @param accessionVal the accession
     */
    public void setAccession(final Accession accessionVal) {
        this.accession = accessionVal;
    }

    /**
     * The category gov.nih.nci.caarray.domain.vocabulary.Category.
     */
    private Category category;

    /**
     * Gets the category.
     *
     * @return the category
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name = "TERM_CATEGORY_IDX")
    @ForeignKey(name = "CATEGORY_ID")
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param categoryVal the category
     */
    public void setCategory(final Category categoryVal) {
        this.category = categoryVal;
    }

    /**
     * The source gov.nih.nci.caarray.domain.vocabulary.Source.
     */
    private TermSource source;

    /**
     * Gets the source.
     *
     * @return the source
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "SOURCE_ID")
    @Index(name = "TERM_SOURCE_IDX")
    @ForeignKey(name = "SOURCE_ID")
    public TermSource getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param sourceVal the source
     */
    public void setSource(final TermSource sourceVal) {
        this.source = sourceVal;
    }

    /**
     * Checks if given object is equal to this object.
     *
     * @param obj the object to compare to this object
     * @return true if they are equal, false if they are not
     */
    @Override
    public boolean equals(final Object obj) {
        boolean theyAreEqual = false;
        if (obj instanceof gov.nih.nci.caarray.domain.vocabulary.Term) {
            final gov.nih.nci.caarray.domain.vocabulary.Term castObject =
                (gov.nih.nci.caarray.domain.vocabulary.Term) obj;
            Long thisId = getId();
            if (thisId != null && thisId.equals(castObject.getId())) {
                theyAreEqual = true;
            }
        }
        return theyAreEqual;
    }

    /**
     * Returns the hashcode for the object.
     *
     * @return the int hashcode
     */
    @Override
    public int hashCode() {
        int theHashCode = 0;
        if (getId() != null) {
            theHashCode += getId().hashCode();
        }
        return theHashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(super.toString());
        stringBuffer.append(" source=");
        if (source == null) {
            stringBuffer.append("null");
        } else {
            stringBuffer.append(source.getName());
        }
        stringBuffer.append(" category=");
        stringBuffer.append(getCategory().getName());
        stringBuffer.append(" value=");
        stringBuffer.append(getValue());
        return stringBuffer.toString();
    }
}
