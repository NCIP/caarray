/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common.jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common.jar Software and any
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
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.array</code> package.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class ArrayDaoImpl extends AbstractCaArrayDaoImpl implements ArrayDao {

    private static final Logger LOG = Logger.getLogger(ArrayDaoImpl.class);

    /**
     * {@inheritDoc}
     */
    public ArrayDesign getArrayDesign(long id) {
        return (ArrayDesign) getCurrentSession().get(ArrayDesign.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<ArrayDesign> getArrayDesigns() {
        return getCurrentSession().createCriteria(ArrayDesign.class).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Organization> getArrayDesignProviders() {
        String query = "select distinct ad.provider from " + ArrayDesign.class.getName() + " ad "
                + " where ad.provider is not null order by ad.provider.name asc";
        return getCurrentSession().createQuery(query).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<ArrayDesign> getArrayDesignsForProvider(Organization provider, boolean importedOnly) {
        StringBuilder queryStr = new StringBuilder("from ").append(ArrayDesign.class.getName()).append(
                " ad where ad.provider = :provider ");
        if (importedOnly) {
            queryStr.append(" and ad.designFile.status = :status ");
        }
        queryStr.append("order by name asc");
        Query query = getCurrentSession().createQuery(queryStr.toString());
        query.setEntity("provider", provider);
        if (importedOnly) {
            query.setString("status", FileStatus.IMPORTED.name());
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<ArrayDesign> getArrayDesigns(Organization provider, AssayType assayType, boolean importedOnly) {
        StringBuilder queryStr = new StringBuilder("from ").append(ArrayDesign.class.getName()).append(
                " ad where ad.provider = :provider and ad.assayType = :assayType ");
        if (importedOnly) {
            queryStr.append(" and ad.designFile.status = :status ");
        }
        queryStr.append("order by name asc");
        Query query = getCurrentSession().createQuery(queryStr.toString());
        query.setEntity("provider", provider);
        query.setString("assayType", assayType.name());
        if (importedOnly) {
            query.setString("status", FileStatus.IMPORTED.name());
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    public RawArrayData getRawArrayData(CaArrayFile file) {
        Session session = HibernateUtil.getCurrentSession();
        session.flush();
        Query query = session.createQuery("from " + RawArrayData.class.getName()
                + " arrayData where arrayData.dataFile = :file");
        query.setEntity("file", file);
        return (RawArrayData) query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public DerivedArrayData getDerivedArrayData(CaArrayFile file) {
        Session session = HibernateUtil.getCurrentSession();
        Query query = session.createQuery("from " + DerivedArrayData.class.getName()
                + " arrayData where arrayData.dataFile = :file");
        query.setEntity("file", file);
        return (DerivedArrayData) query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public AbstractArrayData getArrayData(long id) {
        Query q =
                HibernateUtil.getCurrentSession().createQuery(
                        "from " + AbstractArrayData.class.getName() + " where id = :id");
        q.setLong("id", id);
        return (AbstractArrayData) q.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Hybridization getHybridization(Long id) {
        Query q =
                HibernateUtil.getCurrentSession().createQuery(
                        "from " + Hybridization.class.getName() + " where id = :id");
        q.setLong("id", id);
        return (Hybridization) q.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        ArrayDataType example = new ArrayDataType();
        example.setName(descriptor.getName());
        example.setVersion(descriptor.getVersion());
        List<ArrayDataType> matches = queryEntityByExample(example);
        if (matches.isEmpty()) {
            return null;
        } else if (matches.size() == 1) {
            return matches.get(0);
        } else {
            throw new IllegalStateException("Duplicate registration of ArrayDataType " + descriptor);
        }
    }

    /**
     * {@inheritDoc}
     */
    public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        QuantitationType example = new QuantitationType();
        example.setName(descriptor.getName());
        List<QuantitationType> matches = queryEntityByExample(example);
        if (matches.isEmpty()) {
            return null;
        } else if (matches.size() == 1) {
            return matches.get(0);
        } else {
            throw new IllegalStateException("Duplicate registration of ArrayDataType " + descriptor);
        }
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        Query q =            HibernateUtil.getCurrentSession().createQuery(
                    "from "
                    + ArrayDesign.class.getName()
                    + " where lsidAuthority = :lsidAuthority and lsidNamespace = :lsidNamespace "
                    + "and lsidObjectId = :lsidObjectId");
        q.setString("lsidAuthority", lsidAuthority);
        q.setString("lsidNamespace", lsidNamespace);
        q.setString("lsidObjectId", lsidObjectId);
        return (ArrayDesign) q.uniqueResult();
    }

    public boolean isArrayDesignLocked(Long id) {
        BrowseCategory cat = BrowseCategory.ARRAY_DESIGNS;
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(DISTINCT p) FROM ")
          .append(Project.class.getName()).append(" p JOIN ").append(cat.getJoin())
          .append(" WHERE ").append(cat.getField()).append(".id = :id");
        Query q = getUnfilteredSession().createQuery(sb.toString());
        q.setParameter("id", id);
        boolean locked = ((Number) q.uniqueResult()).intValue() > 0;
        // restore filters
        HibernateUtil.getCurrentSession();
        return locked;
    }

    private Session getUnfilteredSession() {
        Session session = HibernateUtil.getCurrentSession();
        Set<String> filters = session.getSessionFactory().getDefinedFilterNames();
        for (String filterName : filters) {
            session.disableFilter(filterName);
        }
        return session;
    }
}
