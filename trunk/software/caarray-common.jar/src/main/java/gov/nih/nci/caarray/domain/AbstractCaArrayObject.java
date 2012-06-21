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

import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Base class for all persistent caArray domain objects.
 */
@MappedSuperclass
public abstract class AbstractCaArrayObject implements PersistentObject {

    private static final long serialVersionUID = 2732929116326299995L;

    /**
     * The default column size for string columns in the db.
     */
    public static final int DEFAULT_STRING_COLUMN_SIZE = 254;

    /**
     * The column size for large string columns in the db.
     */
    protected static final int LARGE_TEXT_FIELD_LENGTH = 2000;

    private Long id;
    private String caBigId;

    /**
     * Default hibernate batch size.
     */
    public static final int DEFAULT_BATCH_SIZE = 20;

    /**
     * Returns the id.
     *
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     * @deprecated should only be used by castor and hibernate
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Return the set of security policies that should be applied to this object at hibernate load time.
     * These policies will be applied as port of a hibernate post-load event listener, so that by the time
     * a query that results in the load of this object is finished and returns to the program, these policies
     * will have been applied.
     * 
     * Note that because the policies will be applied while hibernate is in the middle of loading the objects
     * from the database, unexpected behavior may occur. For example, asssociated objects and collections may not
     * yet be loaded. The security policies must be designed carefully in awareness of this behavior.
     * 
     * @param currentUser the current user.
     * @return the set of policies to apply to this object, given the current user.
     */
    public Set<SecurityPolicy> getPostLoadSecurityPolicies(User currentUser) {
        return Collections.emptySet();
    }

    /**
     * Return the set of security policies that should be applied to this object prior to it being returned
     * as part of a return value for a remote API call.
     * 
     * @param currentUser the current user.
     * @return the set of policies to apply to this object, given the current user.
     */
    public Set<SecurityPolicy> getRemoteApiSecurityPolicies(User currentUser) {
        return Collections.emptySet();
    }

    /**
     * The default comparison uses the id.
     * @param o other object
     * @return equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof AbstractCaArrayObject)) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (id == null) {
            // by default, two transient instances cannot ever be equal
            return false;
        }

        AbstractCaArrayObject e = (AbstractCaArrayObject) o;
        return (id.equals(e.getId()) && getClass().isInstance(e));
    }

    /**
     * Default hashCode goes off of id.
     * @return hashCode
     */
    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }

        return id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append('[');
        stringBuffer.append(getClass().getSimpleName());
        stringBuffer.append("] id=");
        stringBuffer.append(id);
        return stringBuffer.toString();
    }

    /**
     * @return the gridIdentifier
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE, name = "bigid")
    public String getCaBigId() {
        return caBigId;
    }

    /**
     * @param gridIdentifier the gridIdentifier to set
     */
    public void setCaBigId(String gridIdentifier) {
        this.caBigId = gridIdentifier;
    }

}
