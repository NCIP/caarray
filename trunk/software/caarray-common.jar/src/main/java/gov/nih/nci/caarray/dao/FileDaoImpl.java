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

import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.UnfilteredCallback;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * DAO to manipulate file objects.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class FileDaoImpl extends AbstractCaArrayDaoImpl implements FileDao {
    private static final Logger LOG = Logger.getLogger(FileDaoImpl.class);

    private final FileTypeRegistry typeRegistry;

    /**
     *
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject
    public FileDaoImpl(CaArrayHibernateHelper hibernateHelper, FileTypeRegistry typeRegistry) {
        super(hibernateHelper);
        this.typeRegistry = typeRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<URI> getAllFileHandles() {
        @SuppressWarnings("unchecked")
        final List<URI> results = (List<URI>) getHibernateHelper().doUnfiltered(new UnfilteredCallback() {
            @Override
            public Object doUnfiltered(Session s) {
                final String hql = "select dataHandle from " + CaArrayFile.class.getName();
                return s.createQuery(hql).list();
            }
        });
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "PMD" })
    public List<CaArrayFile> searchFiles(PageSortParams<CaArrayFile> params, FileSearchCriteria criteria) {
        final Criteria c = getCurrentSession().createCriteria(CaArrayFile.class);

        if (criteria.getExperiment() != null) {
            c.add(Restrictions.eq("project", criteria.getExperiment().getProject()));
        }

        if (!criteria.getTypes().isEmpty()) {
            c.add(Restrictions.in("type", Sets.newHashSet(FileTypeRegistryImpl.namesForTypes(criteria.getTypes()))));
        }

        if (criteria.getExtension() != null) {
            String extension = criteria.getExtension();
            if (!extension.startsWith(".")) {
                extension = "." + extension;
            }
            c.add(Restrictions.ilike("name", "%" + extension));
        }

        if (!criteria.getCategories().isEmpty()) {
            final Disjunction categoryCriterion = Restrictions.disjunction();
            if (criteria.getCategories().contains(FileCategory.DERIVED_DATA)) {
                categoryCriterion.add(Restrictions.in("type", Sets.newHashSet(FileTypeRegistryImpl
                        .namesForTypes(this.typeRegistry.getDerivedArrayDataTypes()))));
            }
            if (criteria.getCategories().contains(FileCategory.RAW_DATA)) {
                categoryCriterion.add(Restrictions.in("type",
                        Sets.newHashSet(FileTypeRegistryImpl.namesForTypes(this.typeRegistry.getRawArrayDataTypes()))));
            }
            if (criteria.getCategories().contains(FileCategory.MAGE_TAB)) {
                categoryCriterion.add(Restrictions.in("type",
                        Sets.newHashSet(FileTypeRegistryImpl.namesForTypes(this.typeRegistry.getMageTabTypes()))));
            }
            if (criteria.getCategories().contains(FileCategory.ARRAY_DESIGN)) {
                categoryCriterion.add(Restrictions.in("type",
                        Sets.newHashSet(FileTypeRegistryImpl.namesForTypes(this.typeRegistry.getArrayDesignTypes()))));
            }
            if (criteria.getCategories().contains(FileCategory.OTHER)) {
                categoryCriterion.add(Restrictions.isNull("type"));
            }
            c.add(categoryCriterion);
        }

        if (!criteria.getExperimentNodes().isEmpty()) {
            final Collection<Long> fileIds = new LinkedList<Long>();
            for (final AbstractExperimentDesignNode node : criteria.getExperimentNodes()) {
                for (final CaArrayFile f : node.getAllDataFiles()) {
                    fileIds.add(f.getId());
                }
            }
            if (!fileIds.isEmpty()) {
                c.add(Restrictions.in("id", fileIds));
            } else {
                return Collections.emptyList();
            }
        }

        c.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            c.setMaxResults(params.getPageSize());
        }
        c.addOrder(toOrder(params));

        return c.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CaArrayFile> getDeletableFiles(Long projectId) {
        final String hql =
                "from " + CaArrayFile.class.getName()
                        + " f where f.project.id = :projectId and f.status in (:deletableStatuses) "
                        + " and (f.status <> :importedStatus or not exists (select h from "
                        + AbstractArrayData.class.getName()
                        + " ad join ad.hybridizations h where ad.dataFile = f order by f.name))";
        final Query q = getCurrentSession().createQuery(hql);
        q.setLong("projectId", projectId);
        q.setParameterList("deletableStatuses", CaArrayUtils.namesForEnums(FileStatus.DELETABLE_FILE_STATUSES));
        q.setString("importedStatus", FileStatus.IMPORTED.name());
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanupUnreferencedChildren() {
        getHibernateHelper().doUnfiltered(new UnfilteredCallback() {
            @Override
            public Object doUnfiltered(Session s) {
                try {
                    final String hql = "delete from " + CaArrayFile.class.getName() + " where parent is not null";
                    s.createQuery(hql).executeUpdate();
                } catch (final HibernateException he) {
                    LOG.error("Unable to remove entity", he);
                    throw new DAOException("Unable to remove entity", he);
                }
                return null;

            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaArrayFile getPartialFile(Long projectId, String fileName, long fileSize) {
        final String hql = "from " + CaArrayFile.class.getName()
                        + " f where f.project.id = :projectId and f.status =:uploadingStatus "
                        + " and f.uncompressedSize = :fileSize and f.name = :fileName";
        final Query q = getCurrentSession().createQuery(hql);
        q.setLong("projectId", projectId);
        q.setString("uploadingStatus", FileStatus.UPLOADING.name());
        q.setLong("fileSize", fileSize);
        q.setString("fileName", fileName);
        return (CaArrayFile) q.uniqueResult();

    }

}
