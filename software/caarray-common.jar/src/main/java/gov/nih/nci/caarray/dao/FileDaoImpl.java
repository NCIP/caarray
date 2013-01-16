//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
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
