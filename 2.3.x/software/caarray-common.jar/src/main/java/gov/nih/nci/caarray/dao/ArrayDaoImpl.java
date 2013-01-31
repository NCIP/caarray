//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
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
import gov.nih.nci.caarray.util.UnfilteredCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.array</code> package.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CyclomaticComplexity" })
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
        return getCurrentSession().createCriteria(ArrayDesign.class).setResultTransformer(
                Criteria.DISTINCT_ROOT_ENTITY).list();
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
    public List<ArrayDesign> getArrayDesigns(Organization provider, Set<AssayType> assayTypes, boolean importedOnly) {
        boolean containsAssayType = false;
        if (assayTypes != null && !assayTypes.isEmpty()) {
            containsAssayType = true;
        }
        StringBuilder queryStr = generateArrayDesignQuery(provider, containsAssayType, assayTypes, importedOnly);
        Query query = getCurrentSession().createQuery(queryStr.toString());
        if (provider != null) {
            query.setEntity("provider", provider);
        }
        if (containsAssayType) {
            int i = 0;
            for (AssayType type : assayTypes) {
                query.setLong("assayType" + i, type.getId());
                i++;
            }
        }

        if (importedOnly) {
            query.setString("status1", FileStatus.IMPORTED.name());
            query.setString("status2", FileStatus.IMPORTED_NOT_PARSED.name());
        }
        return query.list();
    }
    private StringBuilder generateArrayDesignQuery(Organization provider, boolean containsAssayType,
            Set<AssayType> assayTypes, boolean importedOnly) {
        StringBuilder queryStr = new StringBuilder("select distinct ad from " + ArrayDesign.class.getName()
                + " ad join ad.designFiles designFile join ad.assayTypes assayType where");
        if (provider != null) {
            queryStr.append(" ad.provider = :provider ");
            if (containsAssayType) {
                queryStr.append("and ");
            }
        }
        if (containsAssayType) {
            queryStr.append("(");
            for (int i = 0; i < assayTypes.size(); i++) {
                if (i > 0) {
                    queryStr.append(" or");
                }
                queryStr.append(" assayType = :assayType" + i);
            }
            queryStr.append(")");
        }
        if (importedOnly) {
            queryStr.append(" and (designFile.status = :status1 or designFile.status = :status2) ");
        }
        queryStr.append("order by ad.name asc");
        return queryStr;
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
        Query q = HibernateUtil.getCurrentSession().createQuery(
                "from " + AbstractArrayData.class.getName() + " where id = :id");
        q.setLong("id", id);
        return (AbstractArrayData) q.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Hybridization getHybridization(Long id) {
        Query q = HibernateUtil.getCurrentSession().createQuery(
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
    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return (DesignElementList) getEntityByLsid(DesignElementList.class, lsidAuthority, lsidNamespace, lsidObjectId);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return (ArrayDesign) getEntityByLsid(ArrayDesign.class, lsidAuthority, lsidNamespace, lsidObjectId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isArrayDesignLocked(final Long id) {
        UnfilteredCallback u = new UnfilteredCallback() {
            public Object doUnfiltered(Session s) {
                BrowseCategory cat = BrowseCategory.ARRAY_DESIGNS;
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT COUNT(DISTINCT p) FROM ").append(Project.class.getName()).append(" p JOIN ").append(
                        cat.getJoin()).append(" WHERE ").append(cat.getField()).append(".id = :id");
                Query q = s.createQuery(sb.toString());
                q.setParameter("id", id);
                return q.uniqueResult();
            }
        };
        Number count = (Number) HibernateUtil.doUnfiltered(u);
        return count.intValue() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Map<String, Long> getLogicalProbeNamesToIds(ArrayDesign design, List<String> names) {
        String queryString = "select lp.name, lp.id from " + LogicalProbe.class.getName()
                + " lp where lp.name in (:names) and lp.arrayDesignDetails = :details";
        Query query = getCurrentSession().createQuery(queryString);
        query.setParameterList("names", names);
        query.setParameter("details", design.getDesignDetails());
        List<Object[]> results = query.list();
        Map<String, Long> namesToIds = new HashMap<String, Long>();
        for (Object[] result : results) {
            namesToIds.put((String) result[0], (Long) result[1]);
        }
        return namesToIds;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteArrayDesignDetails(ArrayDesign design) {
         // Deletes array design detail. Records in other tables that are associated
         // with the array design detail are deleted first, then the array design
         // detail is deleted.
        if (design.getDesignDetails() != null && design.getDesignDetails().getId() != null) {
            Long detailsId = design.getDesignDetails().getId();
            List<Long> probeGroupIdsList = getProbeGroupIds(detailsId);
            deleteDesignElementList(detailsId);
            deleteDesignElements(detailsId);
            deleteProbeGroup(probeGroupIdsList);
            StringBuilder unlinkDesignDetailsQuery = new StringBuilder("update ")
                .append(ArrayDesign.class.getName())
                .append(" ad set ad.designDetails = null where ad.id  = :id");
            getCurrentSession().createQuery(unlinkDesignDetailsQuery.toString())
                .setLong("id", design.getId()).executeUpdate();
            remove(design.getDesignDetails());
            design.setDesignDetails(null);
            save(design);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Long> getProbeGroupIds(Long detailsId) {
          StringBuilder probeGroupIdsQuery = new StringBuilder("select id from ").append(ProbeGroup.class.getName())
              .append(" where arrayDesignDetails.id = :detailsId");
          return getCurrentSession().createQuery(probeGroupIdsQuery.toString()).setLong("detailsId", detailsId).list();
    }

    private void deleteProbeGroup(List<Long> probeGroupIdsList) {
        if (!probeGroupIdsList.isEmpty()) {
            String deleteProbeGroup = "delete from " + ProbeGroup.class.getName() + " where  id in (:groupIds)";
            getCurrentSession().createQuery(deleteProbeGroup)
                .setParameterList("groupIds", probeGroupIdsList).executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteDesignElementList(Long detailsId) {
        StringBuilder elementIdsQuery = new StringBuilder("select distinct designelementlist_id from ")
        .append("designelementlist_designelement dd inner join design_element de ")
        .append("on dd.designelement_id=de.id and (de.logicalprobe_details_id = :id")
        .append(" or de.feature_details_id = :id or de.physicalprobe_details_id = :id)");

        List<Long> designElementListIds = getCurrentSession().createSQLQuery(elementIdsQuery.toString())
            .addScalar("designelementlist_id", Hibernate.LONG).setLong("id", detailsId).list();

        if (!designElementListIds.isEmpty()) {
            StringBuilder deleteFromDesignElementListLkup =
                    new StringBuilder("delete from designelementlist_designelement ")
                    .append("where designelementlist_id  in (:designIds)");
            getCurrentSession().createSQLQuery(deleteFromDesignElementListLkup.toString())
                .setParameterList("designIds", designElementListIds).executeUpdate();
            getCurrentSession().createSQLQuery("delete from design_element_list where id in ( :designIds )")
                .setParameterList("designIds", designElementListIds).executeUpdate();
        }
    }

    private void deleteDesignElements(Long detailsId) {
        deleteJoinTable("logicalprobe_physicalprobe", "logical_probe_id", "logicalprobe_details_id", detailsId);
        deleteDesignElement(LogicalProbe.class, detailsId);
        deleteJoinTable("probefeature", "physical_probe_id", "physicalprobe_details_id", detailsId);
        deleteDesignElement(PhysicalProbe.class, detailsId);
        deleteDesignElement(Feature.class, detailsId);
    }

    private void deleteDesignElement(Class<? extends AbstractDesignElement> designElementClass, Long detailsId) {
        String query = "delete from " + designElementClass.getName() + " de where de.arrayDesignDetails = :detailsId";
        getCurrentSession().createQuery(query).setLong("detailsId", detailsId).executeUpdate();
    }

    private void deleteJoinTable(String jointablename, String designElementFkName, String detailsIdColumn,
            Long detailsId) {
        String query = "delete from " + jointablename + " where " + designElementFkName
                + " in (select id from design_element where " + detailsIdColumn + " = :detailsId)";
        getCurrentSession().createSQLQuery(query).setLong("detailsId", detailsId).executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Long> getLogicalProbeIds(ArrayDesign design, PageSortParams<LogicalProbe> params) {
        Query query = getCurrentSession().createQuery("select id from " + LogicalProbe.class.getName()
                + " where arrayDesignDetails = :details order by id");
        query.setParameter("details", design.getDesignDetails());
        query.setFirstResult(params.getIndex());
        query.setMaxResults(params.getPageSize());
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    public void createDesignElementListEntries(DesignElementList designElementList, int startIndex,
            List<Long> logicalProbeIds) {
        Connection conn = getCurrentSession().connection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("insert into designelementlist_designelement "
                    + "(designelementlist_id, designelement_id, designelement_index) values (?, ?, ?)");
            int i = startIndex;
            for (Long probeId : logicalProbeIds) {
                stmt.setLong(1, designElementList.getId());
                stmt.setLong(2, probeId);
                stmt.setInt(3, i++);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Error inserting elements in the design element list", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) { } // NOPMD
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { } // NOPMD
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createFeatures(int rows, int cols, ArrayDesignDetails designDetails) {
        Query query = getCurrentSession().createSQLQuery("call create_features(:rows, :cols, :designDetailsId)");
        query.setInteger("rows", rows);
        query.setInteger("cols", cols);
        query.setLong("designDetailsId", designDetails.getId());
        query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public Long getFirstFeatureId(ArrayDesignDetails designDetails) {
        String queryString = "select min(id) from " + Feature.class.getName() + " where arrayDesignDetails = :details";
        Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("details", designDetails);
        return (Long) query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<ArrayDesign> getArrayDesigns(ArrayDesignDetails arrayDesignDetails) {
        String queryString = "from " + ArrayDesign.class.getName() + " ad where ad.designDetails = :designDetails";
        Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("designDetails", arrayDesignDetails);
        return query.list();
    }

}
