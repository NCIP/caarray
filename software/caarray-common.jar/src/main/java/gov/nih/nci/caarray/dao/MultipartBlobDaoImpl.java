//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * DAO to manipulate file objects.
 */
class MultipartBlobDaoImpl extends AbstractCaArrayDaoImpl implements MultipartBlobDao {
    private static final Logger LOG = Logger.getLogger(MultipartBlobDaoImpl.class);

    /**
     * 
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject
    public MultipartBlobDaoImpl(CaArrayHibernateHelper hibernateHelper) {
        super(hibernateHelper);
    }

    private List<Long> getBlobPartIds(Collection<Long> mblobIds) {
        final String sqlProjBlobs = "select blob_parts from multipart_blob_blob_parts mbp "
                + "where mbp.multipart_blob in (:blobIds)";
        @SuppressWarnings("unchecked")
        final List<Long> bpIds = getCurrentSession().createSQLQuery(sqlProjBlobs).setParameterList("blobIds", mblobIds)
                .list();
        return bpIds;
    }

    @Override
    public void deleteByIds(Iterable<Long> ids) {
        // we want to avoid loading the MultipartBlobs into memory
        // and HQL bulk delete doesn't handle associations, so we have to drop to SQL
        // to delete the blob parts
        final List<Long> idList = Lists.newArrayList(ids);
        if (idList.isEmpty()) {
            // nothing to do
            return;
        }

        final List<Long> blobPartIds = getBlobPartIds(idList);
        if (!blobPartIds.isEmpty()) {
            deleteBlobHolderAssociations(blobPartIds);
            deleteBlobHolders(blobPartIds);
        }

        final String hql = "delete from " + MultiPartBlob.class.getName() + " where id in (:blobIds)";
        final Query q = getCurrentSession().createQuery(hql);
        q.setParameterList("blobIds", idList);
        q.executeUpdate();
        this.flushSession();
        this.clearSession();
    }

    private void deleteBlobHolderAssociations(Collection<Long> blobPartIds) {
        final String sql = "delete from multipart_blob_blob_parts where blob_parts in (:bpIds)";
        final Query q = getCurrentSession().createSQLQuery(sql);
        q.setParameterList("bpIds", blobPartIds);
        q.executeUpdate();
    }

    private void deleteBlobHolders(Collection<Long> blobPartIds) {
        final String sql = "delete from blob_holder where id in (:bpIds)";
        final Query q = getCurrentSession().createSQLQuery(sql);
        q.setParameterList("bpIds", blobPartIds);
        q.executeUpdate();
    }
}
