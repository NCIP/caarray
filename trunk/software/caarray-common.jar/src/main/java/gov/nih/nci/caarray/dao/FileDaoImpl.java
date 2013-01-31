//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.BlobHolder;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO to manipulate file objects.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class FileDaoImpl extends AbstractCaArrayDaoImpl implements FileDao {
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

    private int removeAssociationsByBlobHolderId(List<Long> idList) {
        String sql = "delete from CAARRAYFILE_BLOB_PARTS where blob_parts in (:b_parts)";
        Query q = HibernateUtil.getCurrentSession().createSQLQuery(sql);
        q.setParameterList("b_parts", idList);
        q.executeUpdate();

        sql = "delete from DATACOLUMN_BLOB_PARTS where blob_parts in (:b_parts)";
        q = HibernateUtil.getCurrentSession().createSQLQuery(sql);
        q.setParameterList("b_parts", idList);
        return q.executeUpdate();
    }

    private int removeBlobHoldersById(List<Long> idList) {
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
    @SuppressWarnings("unchecked")
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "PMD" })
    public List<CaArrayFile> searchFiles(PageSortParams<CaArrayFile> params, FileSearchCriteria criteria) {        
        Criteria c = HibernateUtil.getCurrentSession().createCriteria(CaArrayFile.class);

        if (criteria.getExperiment() != null) {
            c.add(Restrictions.eq("project", criteria.getExperiment().getProject()));
        }
        
        if (!criteria.getTypes().isEmpty()) {
            c.add(Restrictions.in("type", CaArrayUtils.namesForEnums(criteria.getTypes())));
        }
        
        if (criteria.getExtension() != null) {
            String extension = criteria.getExtension();
            if (!extension.startsWith(".")) {
                extension = "." + extension;
            }
            c.add(Restrictions.ilike("name", "%" + extension));
        }

        if (!criteria.getCategories().isEmpty()) {
            Disjunction categoryCriterion = Restrictions.disjunction();
            if (criteria.getCategories().contains(FileCategory.DERIVED_DATA)) {
                categoryCriterion.add(Restrictions.in("type", CaArrayUtils
                        .namesForEnums(FileType.DERIVED_ARRAY_DATA_FILE_TYPES)));
            }
            if (criteria.getCategories().contains(FileCategory.RAW_DATA)) {            
                categoryCriterion.add(Restrictions.in("type", CaArrayUtils
                        .namesForEnums(FileType.RAW_ARRAY_DATA_FILE_TYPES)));
            }
            if (criteria.getCategories().contains(FileCategory.MAGE_TAB)) {            
                categoryCriterion.add(Restrictions.in("type", CaArrayUtils
                        .namesForEnums(FileType.MAGE_TAB_FILE_TYPES)));
            }
            if (criteria.getCategories().contains(FileCategory.ARRAY_DESIGN)) {
                categoryCriterion.add(Restrictions.in("type", CaArrayUtils
                        .namesForEnums(FileType.ARRAY_DESIGN_FILE_TYPES)));
            }
            if (criteria.getCategories().contains(FileCategory.OTHER)) {
                categoryCriterion.add(Restrictions.isNull("type"));
            }
            c.add(categoryCriterion);            
        }
        
        if (!criteria.getExperimentNodes().isEmpty()) {
            Collection<Long> fileIds = new LinkedList<Long>();        
            for (AbstractExperimentDesignNode node : criteria.getExperimentNodes()) {
                for (CaArrayFile f : node.getAllDataFiles()) {
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
}
