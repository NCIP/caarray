/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

/**
 * User type for access profiles.
 */
public class AccessProfileUserType implements CompositeUserType {

    /**
     * {@inheritDoc}
     */
    public Object assemble(Serializable cached, SessionImplementor session, Object owner) {
        return cached;
    }

    /**
     * {@inheritDoc}
     */
    public Object deepCopy(Object value) {
        if (value == null) {
            return null;
        }

        AccessProfile ap = (AccessProfile) value;

        AccessProfile result = new AccessProfile();
        result.setDefaultRead(ap.isDefaultRead());
        result.setDefaultWrite(ap.isDefaultWrite());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable disassemble(Object value, SessionImplementor session) {
        return (Serializable) value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object x, Object y) {
        AccessProfile ap1 = (AccessProfile) x;
       if (ap1 == null || x == null) {
            return false;
       }
       if (ap1.getClass() != y.getClass()) {
            return false;
       }

       AccessProfile other = (AccessProfile) y;

       return new EqualsBuilder()
           .append(ap1.isDefaultRead(), other.isDefaultRead())
           .append(ap1.isDefaultWrite(), other.isDefaultWrite())
           .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    public String[] getPropertyNames() {
        return new String[] {"defaultRead", "defaultWrite"};
    }

    /**
     * {@inheritDoc}
     */
    public Type[] getPropertyTypes() {
        return new Type[] {Hibernate.BOOLEAN, Hibernate.BOOLEAN};
    }

    /**
     * {@inheritDoc}
     */
    public Object getPropertyValue(Object component, int property) {
        AccessProfile ap = (AccessProfile) component;
        if (property == 0) {
            return ap.isDefaultRead();
        }

        return ap.isDefaultWrite();
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode(Object x) {
        AccessProfile ap = (AccessProfile) x;
        return new HashCodeBuilder()
        .append(ap.isDefaultRead())
        .append(ap.isDefaultWrite())
        .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMutable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        Boolean read = rs.getBoolean(names[0]);
        Boolean write = rs.getBoolean(names[1]);

        if (rs.wasNull()) {
            return null;
        }

        AccessProfile result = new AccessProfile();
        result.setDefaultRead(read);
        result.setDefaultWrite(write);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Hibernate.BOOLEAN.sqlType());
            st.setNull(index + 1, Hibernate.BOOLEAN.sqlType());
        } else {
            AccessProfile ap = (AccessProfile) value;
            st.setBoolean(index, ap.isDefaultRead());
            st.setBoolean(index + 1, ap.isDefaultWrite());
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object replace(Object original, Object target, SessionImplementor session, Object owner) {
        return deepCopy(original);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Class returnedClass() {
        return AccessProfile.class;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyValue(Object component, int property, Object value) {
        AccessProfile ap = (AccessProfile) component;
        Boolean bVal = (Boolean) value;
        if (property == 0) {
            ap.setDefaultRead(bVal);
        }

        ap.setDefaultWrite(bVal);
    }

}
