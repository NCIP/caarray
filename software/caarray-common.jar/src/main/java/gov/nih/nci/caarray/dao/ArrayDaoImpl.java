//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.UnfilteredCallback;

import java.io.Serializable;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.list.SetUniqueList;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.array</code> package.
 * 
 * @author Rashmi Srinivasa
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CyclomaticComplexity", "PMD.TooManyMethods",
    "PMD.ExcessiveClassLength" })
class ArrayDaoImpl extends AbstractCaArrayDaoImpl implements ArrayDao {
    private final SearchDao searchDao;
    private final FileTypeRegistry typeRegistry;

    /**
     * 
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject
    public ArrayDaoImpl(SearchDao searchDao, CaArrayHibernateHelper hibernateHelper, FileTypeRegistry typeRegistry) {
        super(hibernateHelper);
        this.searchDao = searchDao;
        this.typeRegistry = typeRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(long id) {
        return (ArrayDesign) getCurrentSession().get(ArrayDesign.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ArrayDesign> getArrayDesigns(Organization provider, Set<AssayType> assayTypes, boolean importedOnly) {
        boolean containsAssayType = false;
        if (assayTypes != null && !assayTypes.isEmpty()) {
            containsAssayType = true;
        }
        final StringBuilder queryStr = generateArrayDesignQuery(provider, containsAssayType, assayTypes, importedOnly);
        final Query query = getCurrentSession().createQuery(queryStr.toString());
        if (provider != null) {
            query.setEntity("provider", provider);
        }
        if (containsAssayType) {
            int i = 0;
            for (final AssayType type : assayTypes) {
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
        final StringBuilder queryStr =
            new StringBuilder("select distinct ad from " + ArrayDesign.class.getName()
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
    @Override
    public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        final ArrayDataType example = new ArrayDataType();
        example.setName(descriptor.getName());
        example.setVersion(descriptor.getVersion());
        final List<ArrayDataType> matches = queryEntityByExample(example);
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
    @Override
    @SuppressWarnings("unchecked")
    public AbstractArrayData getArrayData(Long fileId) {
        final Session session = getCurrentSession();
        final Query query =
            session.createQuery("select distinct arrayData from " + AbstractArrayData.class.getName()
                    + " arrayData join arrayData.dataFile f where f.id = :fileId");
        query.setLong("fileId", fileId);
        final List<AbstractArrayData> results = query.list();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        final QuantitationType example = new QuantitationType();
        example.setName(descriptor.getName());
        final List<QuantitationType> matches = queryEntityByExample(example);
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
    @Override
    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return this.searchDao.getEntityByLsid(DesignElementList.class, new LSID(lsidAuthority, lsidNamespace,
                lsidObjectId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return this.searchDao.getEntityByLsid(ArrayDesign.class, new LSID(lsidAuthority, lsidNamespace, lsidObjectId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArrayDesignLocked(final Long id) {
        final UnfilteredCallback u = new UnfilteredCallback() {
            @Override
            public Object doUnfiltered(Session s) {
                final BrowseCategory cat = BrowseCategory.ARRAY_DESIGNS;
                final StringBuffer sb = new StringBuffer();
                sb.append("SELECT COUNT(DISTINCT p) FROM ").append(Project.class.getName()).append(" p JOIN ")
                .append(cat.getJoin()).append(" WHERE ").append(cat.getField()).append(".id = :id");
                final Query q = s.createQuery(sb.toString());
                q.setParameter("id", id);
                return q.uniqueResult();
            }
        };
        final Number count = (Number) getHibernateHelper().doUnfiltered(u);
        return count.intValue() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Long> getLogicalProbeNamesToIds(ArrayDesign design, List<String> names) {
        final String queryString =
            "select lp.name, lp.id from " + LogicalProbe.class.getName()
            + " lp where lp.name in (:names) and lp.arrayDesignDetails = :details";
        final Query query = getCurrentSession().createQuery(queryString);
        query.setParameterList("names", names);
        query.setParameter("details", design.getDesignDetails());
        final List<Object[]> results = query.list();
        final Map<String, Long> namesToIds = new HashMap<String, Long>();
        for (final Object[] result : results) {
            namesToIds.put((String) result[0], (Long) result[1]);
        }
        return namesToIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PhysicalProbe> getPhysicalProbeByNames(ArrayDesign design, List<String> names) {
        if (names.isEmpty()) {
            return Collections.emptyList();
        }
        final Map<String, List<? extends Serializable>> inParams = new HashMap<String, List<? extends Serializable>>();
        final String inClause = getHibernateHelper().buildInClauses(names, "pp.name", inParams);
        final String queryString =
            "select pp from " + PhysicalProbe.class.getName() + " pp where pp.arrayDesignDetails = :details and "
            + inClause;
        final Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("details", design.getDesignDetails());
        for (final Map.Entry<String, List<? extends Serializable>> e : inParams.entrySet()) {
            query.setParameterList(e.getKey(), e.getValue());
        }
        @SuppressWarnings("unchecked")
        final List<PhysicalProbe> results = query.list();
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getPhysicalProbeNames(ArrayDesign design) {
        String queryString = "select pp.name from " + PhysicalProbe.class.getName()
                + " pp where pp.arrayDesignDetails = :details";
        Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("details", design.getDesignDetails());
        @SuppressWarnings("unchecked")
        List<String> names = query.list();
        return new HashSet<String>(names);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteArrayDesignDetails(ArrayDesign design) {
        // Deletes array design detail. Records in other tables that are associated
        // with the array design detail are deleted first, then the array design
        // detail is deleted.
        if (design.getDesignDetails() != null && design.getDesignDetails().getId() != null) {
            final Long detailsId = design.getDesignDetails().getId();
            final List<Long> probeGroupIdsList = getProbeGroupIds(detailsId);
            deleteDesignElementList(detailsId);
            deleteDesignElements(detailsId);
            deleteProbeGroup(probeGroupIdsList);
            final StringBuilder unlinkDesignDetailsQuery =
                new StringBuilder("update ").append(ArrayDesign.class.getName()).append(
                " ad set ad.designDetails = null where ad.id  = :id");
            getCurrentSession().createQuery(unlinkDesignDetailsQuery.toString()).setLong("id", design.getId())
            .executeUpdate();
            remove(design.getDesignDetails());
            design.setDesignDetails(null);
            save(design);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Long> getProbeGroupIds(Long detailsId) {
        final StringBuilder probeGroupIdsQuery =
            new StringBuilder("select id from ").append(ProbeGroup.class.getName()).append(
            " where arrayDesignDetails.id = :detailsId");
        return getCurrentSession().createQuery(probeGroupIdsQuery.toString()).setLong("detailsId", detailsId).list();
    }

    private void deleteProbeGroup(List<Long> probeGroupIdsList) {
        if (!probeGroupIdsList.isEmpty()) {
            final String deleteProbeGroup = "delete from " + ProbeGroup.class.getName() + " where  id in (:groupIds)";
            getCurrentSession().createQuery(deleteProbeGroup).setParameterList("groupIds", probeGroupIdsList)
            .executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteDesignElementList(Long detailsId) {
        final StringBuilder elementIdsQuery =
            new StringBuilder("select distinct designelementlist_id from ")
        .append("designelementlist_designelement dd inner join design_element de ")
        .append("on dd.designelement_id=de.id and (de.logicalprobe_details_id = :id")
        .append(" or de.feature_details_id = :id or de.physicalprobe_details_id = :id)");

        final List<Long> designElementListIds =
            getCurrentSession().createSQLQuery(elementIdsQuery.toString())
            .addScalar("designelementlist_id", Hibernate.LONG).setLong("id", detailsId).list();

        if (!designElementListIds.isEmpty()) {
            final StringBuilder deleteFromDesignElementListLkup =
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
        final String query =
            "delete from " + designElementClass.getName() + " de where de.arrayDesignDetails = :detailsId";
        getCurrentSession().createQuery(query).setLong("detailsId", detailsId).executeUpdate();
    }

    private void deleteJoinTable(String jointablename, String designElementFkName, String detailsIdColumn,
            Long detailsId) {
        final String query =
            "delete from " + jointablename + " where " + designElementFkName
            + " in (select id from design_element where " + detailsIdColumn + " = :detailsId)";
        getCurrentSession().createSQLQuery(query).setLong("detailsId", detailsId).executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> getLogicalProbeIds(ArrayDesign design, PageSortParams<LogicalProbe> params) {
        final Query query =
            getCurrentSession().createQuery(
                    "select id from " + LogicalProbe.class.getName()
                    + " where arrayDesignDetails = :details order by id");
        query.setParameter("details", design.getDesignDetails());
        query.setFirstResult(params.getIndex());
        query.setMaxResults(params.getPageSize());
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignElementListEntries(DesignElementList designElementList, int startIndex,
            List<Long> logicalProbeIds) {
        final Connection conn = getCurrentSession().connection();
        PreparedStatement stmt = null;
        try {
            stmt =
                conn.prepareStatement("insert into designelementlist_designelement "
                        + "(designelementlist_id, designelement_id, designelement_index) values (?, ?, ?)");
            int i = startIndex;
            for (final Long probeId : logicalProbeIds) {
                stmt.setLong(1, designElementList.getId());
                stmt.setLong(2, probeId);
                stmt.setInt(3, i++);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (final SQLException e) {
            throw new DAOException("Error inserting elements in the design element list", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) { // NOPMD - close quietly
                    // close quietly
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (final SQLException e) { // NOPMD - close quietly
                    // close quietly
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeatures(int rows, int cols, ArrayDesignDetails designDetails) {
        final Query query = getCurrentSession().createSQLQuery("call create_features(:rows, :cols, :designDetailsId)");
        query.setInteger("rows", rows);
        query.setInteger("cols", cols);
        query.setLong("designDetailsId", designDetails.getId());
        query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getFirstFeatureId(ArrayDesignDetails designDetails) {
        final String queryString =
            "select min(id) from " + Feature.class.getName() + " where arrayDesignDetails = :details";
        final Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("details", designDetails);
        return (Long) query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ArrayDesign> getArrayDesigns(ArrayDesignDetails arrayDesignDetails) {
        final String queryString =
            "from " + ArrayDesign.class.getName() + " ad where ad.designDetails = :designDetails";
        final Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("designDetails", arrayDesignDetails);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    public List<QuantitationType> searchForQuantitationTypes(PageSortParams<QuantitationType> params,
            QuantitationTypeSearchCriteria criteria) {
        final Criteria c = getCurrentSession().createCriteria(HybridizationData.class);
        c.createCriteria("hybridization").add(Restrictions.eq("id", criteria.getHybridization().getId()));
        c.createCriteria("dataSet").createAlias("quantitationTypes", "qt").createAlias("arrayData", "ad");
        c.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

        if (!criteria.getArrayDataTypes().isEmpty()) {
            final List<Long> ids = new LinkedList<Long>();
            for (final ArrayDataType type : criteria.getArrayDataTypes()) {
                ids.add(type.getId());
            }
            c.createCriteria("ad.type").add(Restrictions.in("id", ids));
        }

        if (!criteria.getFileTypes().isEmpty() || !criteria.getFileCategories().isEmpty()) {
            c.createAlias("ad.dataFile", "df");
        }

        if (!criteria.getFileTypes().isEmpty()) {
            c.add(Restrictions.in("df.type",
                    Sets.newHashSet(FileTypeRegistryImpl.namesForTypes(criteria.getFileTypes()))));
        }

        if (!criteria.getFileCategories().isEmpty()) {
            final Disjunction categoryCriterion = Restrictions.disjunction();
            if (criteria.getFileCategories().contains(FileCategory.DERIVED_DATA)) {
                categoryCriterion.add(Restrictions.in("df.type", Sets.newHashSet(FileTypeRegistryImpl
                        .namesForTypes(this.typeRegistry.getDerivedArrayDataTypes()))));
            }
            if (criteria.getFileCategories().contains(FileCategory.RAW_DATA)) {
                categoryCriterion.add(Restrictions.in("df.type",
                        Sets.newHashSet(FileTypeRegistryImpl.namesForTypes(this.typeRegistry.getRawArrayDataTypes()))));
            }
            c.add(categoryCriterion);
        }

        c.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            c.setMaxResults(params.getPageSize());
        }
        c.addOrder(toOrder(params, "qt"));

        final List<Map<String, Object>> results = c.list();
        final List<QuantitationType> qTypes = SetUniqueList.decorate(new LinkedList<QuantitationType>());
        for (final Map<String, Object> row : results) {
            final QuantitationType qt = (QuantitationType) row.get("qt");
            qTypes.add(qt);
        }
        return qTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ArrayDesign> getArrayDesignsWithReImportable() {
        final String q =
            "select distinct a from " + ArrayDesign.class.getName()
            + " a left join a.designFiles f where f.status = :status and f.type in (:types) order by a.id";
        final Query query = getCurrentSession().createQuery(q);
        query.setParameter("status", FileStatus.IMPORTED_NOT_PARSED.name());
        query.setParameterList("types", Sets.newHashSet(Iterables.transform(
                this.typeRegistry.getParseableArrayDesignTypes(), new Function<FileType, String>() {
                    @Override
                    public String apply(FileType ft) {
                        return ft.getName();
                    }
                })));
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<URI> getAllParsedDataHandles() {
        @SuppressWarnings("unchecked")
        final List<URI> results = (List<URI>) getHibernateHelper().doUnfiltered(new UnfilteredCallback() {
            @Override
            public Object doUnfiltered(Session s) {
                final String hql = "select dataHandle from " + AbstractDataColumn.class.getName();
                return s.createQuery(hql).list();
            }
        });
        return results;

    }
}
