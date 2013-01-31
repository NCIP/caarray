//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.BlobHolder;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;

/**
 * DAO to manipulate file objects.
 */
class FileDaoImpl extends AbstractCaArrayDaoImpl implements FileDao {
    private static final Logger LOG = Logger.getLogger(FileDaoImpl.class);

    @Override
    Logger getLog() {
        return LOG;
    }

    @SuppressWarnings("unchecked")
    private List<Long> getBlobPartIdsForProject(long projectId) {
         List<Long> returnVal = new ArrayList<Long>();
         String sqlProjBlobs = "select b.blob_parts from PROJECT p, CAARRAYFILE c, CAARRAYFILE_BLOB_PARTS b "
                      + "where p.id = c.project AND b.caarrayfile = c.id AND p.id = :p_id";

         String sqlHybBlobs = "select bp.blob_parts from project p, experiment e, hybridization h, "
         + "hybridization_data hd, "
         + "datacolumn d, "
         + "datacolumn_blob_parts bp "
         + "where p.id = :p_id "
         + "and p.experiment = e.id "
         + "and e.id = h.experiment "
         + "and hd.id = d.hybridization_data "
         + "and h.id = hd.hybridization "
         + "and d.id = bp.datacolumn";

        List filesToProject = HibernateUtil.getCurrentSession().createSQLQuery(sqlProjBlobs)
            .setLong("p_id", projectId).list();

        List filesToHyb = HibernateUtil.getCurrentSession().createSQLQuery(sqlHybBlobs)
            .setLong("p_id", projectId).list();

        if ((filesToProject != null && !filesToProject.isEmpty()) || (filesToHyb != null && !filesToHyb.isEmpty())) {
            returnVal.addAll(filesToProject);
            returnVal.addAll(filesToHyb);
        }

        return returnVal;

    }

    private int removeAssociationsByBlobHolderId(List idList) {

        String sql = "delete from CAARRAYFILE_BLOB_PARTS where blob_parts in (:b_parts)";

        Query q = HibernateUtil.getCurrentSession().createSQLQuery(sql);
        q.setParameterList("b_parts", idList);
        q.executeUpdate();

        sql = "delete from DATACOLUMN_BLOB_PARTS where blob_parts in (:b_parts)";

        q = HibernateUtil.getCurrentSession().createSQLQuery(sql);
        q.setParameterList("b_parts", idList);
        return q.executeUpdate();
    }

    private int removeBlobHoldersById(List idList) {

        String sql = "delete from BLOB_HOLDER where id in (:b_parts)";

        Query q = HibernateUtil.getCurrentSession().createSQLQuery(sql);
        q.setParameterList("b_parts", idList);
        return q.executeUpdate();

    }

    /**
     * {@inheritDoc}
     */
    public void deleteSqlBlobsByProjectId(Long projectId) {
        List<Long> list = this.getBlobPartIdsForProject(projectId);
        if (list != null && !list.isEmpty()) {
            this.removeAssociationsByBlobHolderId(list);
            this.removeBlobHoldersById(list);
        }
        this.flushSession();
    }

    /**
     * {@inheritDoc}
     */
    public void deleteHqlBlobsByProjectId(Long projectId) {
        List list = this.getBlobPartIdsForProject(projectId);
        if (list != null && !list.isEmpty()) {
            this.removeAssociationsByBlobHolderId(list);
            for (int i = 0; i < list.size(); i++) {
                String hql = "delete from " + BlobHolder.class.getName() + " where id = :bId";
                Query q = HibernateUtil.getCurrentSession().createQuery(hql);
                q.setBigInteger("bId", (BigInteger) list.get(i));
                q.executeUpdate();
                this.flushSession();
                this.clearSession();
            }
        }

    }

}
