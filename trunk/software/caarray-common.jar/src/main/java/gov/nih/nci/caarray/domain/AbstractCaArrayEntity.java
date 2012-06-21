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
package gov.nih.nci.caarray.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Base class for all caArray domain entities.
 *
 * @author ETavela
 */
@MappedSuperclass
public abstract class AbstractCaArrayEntity extends AbstractCaArrayObject {
    private static final long serialVersionUID = 2732929116326299995L;

    /**
     * LSID Authority for CAARRAY. Used for entities which require an LSID but do not have one assigned
     * from elsewhere
     */
    public static final String CAARRAY_LSID_AUTHORITY = "caarray.nci.nih.gov";

    /**
     * LSID Namespace for CAARRAY. Used for entities which require an LSID but do not have one assigned
     * from elsewhere
     */
    public static final String CAARRAY_LSID_NAMESPACE = "domain";

    private String lsidAuthority;
    private String lsidNamespace;
    private String lsidObjectId;

    /**
     * Default constructor.  All lsid information will be blank.
     */
    public AbstractCaArrayEntity() {
        // blank lsid information / hibernate constructor
    }

    /**
     * Constructor with LSID information.
     *
     * @param lsidAuthority authority
     * @param lsidNamespace namespace
     * @param lsidObjectId object id
     */
    protected AbstractCaArrayEntity(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        setLsidAuthority(lsidAuthority);
        setLsidNamespace(lsidNamespace);
        setLsidObjectId(lsidObjectId);
    }

    /**
     * Returns the LSID authority.
     *
     * @return the LSID authority
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getLsidAuthority() {
        return lsidAuthority;
    }

    /**
     * Sets the LSID authority.
     *
     * @param lsidAuthorityVal the LSID authority to set
     */
    private void setLsidAuthority(String lsidAuthorityVal) {
        this.lsidAuthority = lsidAuthorityVal;
    }

    /**
     * Returns the LSID namespace.
     *
     * @return the LSID namespace
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getLsidNamespace() {
        return lsidNamespace;
    }

    /**
     * Sets the LSID namespace.
     *
     * @param lsidNamespaceVal the LSID namespace to set
     */
    private void setLsidNamespace(String lsidNamespaceVal) {
        this.lsidNamespace = lsidNamespaceVal;
    }

    /**
     * Returns the LSID object ID.
     *
     * @return the LSID object ID
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getLsidObjectId() {
        return lsidObjectId;
    }

    /**
     * Sets the LSID object ID.
     *
     * @param lsidObjectIdVal the LSID object ID to set
     */
    private void setLsidObjectId(String lsidObjectIdVal) {
        this.lsidObjectId = lsidObjectIdVal;
    }

    /**
     * Sets the LSID components for this entity.
     * If the authority and namespace are both absent, the default caArray authority
     * and namespace will be used. The LSID string is of the form authority:namespace:objectId
     * where authority can be absent, or authority and namespace can both be absent.
     *
     * @param lsidString the LSID string
     */
    public void setLsidForEntity(String lsidString) {
        setLsid(new LSID(lsidString));
    }

    /**
     * Set the LSID for this entity.
     * @param lsid the LSID to set.
     */
    public void setLsid(LSID lsid) {
        if (lsid != null) {
            setLsidAuthority(lsid.getAuthority());
            setLsidNamespace(lsid.getNamespace());
            setLsidObjectId(lsid.getObjectId());
        } else {
            setLsidAuthority(null);
            setLsidNamespace(null);
            setLsidObjectId(null);
        }
    }

    /**
     * Set the LSID for this entity.
     * @param lsid the LSID to set.
     */
    public void setLsid(String lsid) {
        setLsid(lsid == null ? null : new LSID(lsid));
    }

    /**
     * Returns the concatenated the LSID for this entity.
     *
     * @return the LSID.
     */
    @Transient
    public String getLsid() {
        return "URN:LSID:" + getLsidAuthority() + ":" + getLsidNamespace() + ":" + getLsidObjectId();
    }
}
