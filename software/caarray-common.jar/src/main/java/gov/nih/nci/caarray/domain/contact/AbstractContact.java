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

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

  /**

   */

public abstract class AbstractContact extends AbstractCaArrayEntity {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    /**
     * The email java.lang.String.
     */
    private java.lang.String email;

    /**
     * Gets the email.
     *
     * @return the email
     */
    public java.lang.String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param emailVal the email
     */
    public void setEmail(final java.lang.String emailVal) {
        this.email = emailVal;
    }
    /**
     * The fax String.
     */
    private String fax;

    /**
     * Gets the fax.
     *
     * @return the fax
     */
    public String getFax() {
        return fax;
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
     * The phone String.
     */
    private String phone;

    /**
     * Gets the phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
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
     * The url java.lang.String.
     */
    private java.lang.String url;

    /**
     * Gets the url.
     *
     * @return the url
     */
    public java.lang.String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param urlVal the url
     */
    public void setUrl(final java.lang.String urlVal) {
        this.url = urlVal;
    }

    /**
     * The address gov.nih.nci.caarray.domain.contact.Address.
     */
    private gov.nih.nci.caarray.domain.contact.Address address;

    /**
     * Gets the address.
     *
     * @return the address
     */
    public gov.nih.nci.caarray.domain.contact.Address getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param addressVal the address
     */
    public void setAddress(final
      gov.nih.nci.caarray.domain.contact.Address addressVal) {
        this.address = addressVal;
    }

    /**
     * Checks if given object is equal to this object.
     *
     * @param obj the object to compare to this object
     * @return true if they are equal, false if they are not
     */
    public boolean equals(final Object obj) {
        boolean theyAreEqual = false;
        if (obj instanceof gov.nih.nci.caarray.domain.contact.AbstractContact) {
            final gov.nih.nci.caarray.domain.contact.AbstractContact castObject =
                (gov.nih.nci.caarray.domain.contact.AbstractContact) obj;
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
    public int hashCode() {
        int theHashCode = 0;
        if (getId() != null) {
            theHashCode += getId().hashCode();
        }
        return theHashCode;
    }
}
