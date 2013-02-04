//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.sdk32query.CQL2HQL;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;
import com.google.inject.Inject;

/**
 * DAO to search for entities using various types of criteria. Supports searching by example, CQL, HQL (Hibernate Query
 * Language) string and Hibernate Detached Criteria.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class SearchDaoImpl extends AbstractCaArrayDaoImpl implements SearchDao {
    private static final Logger LOG = Logger.getLogger(SearchDaoImpl.class);
    private static final String UNCHECKED = "unchecked";

    /**
     * 
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject
    public SearchDaoImpl(CaArrayHibernateHelper hibernateHelper) {
        super(hibernateHelper);
    }
   
   /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractCaArrayObject> List<T> query(final T entityToMatch) {
        if (entityToMatch.getId() != null) {
            String qStr = "from " + entityToMatch.getClass().getName() + " where id = :id";
            Query q = getCurrentSession().createQuery(qStr);
            q.setLong("id", entityToMatch.getId());
            return q.list();
        }
        return queryEntityByExample(entityToMatch);
    }

    /**
     * {@inheritDoc}
     */
    public List<?> query(final CQLQuery cqlQuery) {
        try {
            String s = CQL2HQL.translate(cqlQuery, false, true);
            if (cqlQuery.getQueryModifier() == null) {
                // ensure results are distinct
                s = "select distinct " + CQL2HQL.TARGET_ALIAS + " " + s;
            }
            Query hqlquery = getCurrentSession().createQuery(s);
            return hqlquery.list();            
        } catch (QueryProcessingException e) {
            LOG.error("Unable to parse CQL query", e);
            throw new DAOException("Unable to parse CQL query", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
        Query q = getCurrentSession()
        .createQuery("from " + entityClass.getName() + " where id = :id")
        .setLong("id", entityId);
        return (T) q.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId, LockMode lockMode) {
        Query q =
                getCurrentSession()
                        .createQuery("from " + entityClass.getName() + " o where o.id = :id");
        q.setLong("id", entityId);
        q.setLockMode("o", lockMode);
        return (T) q.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> T retrieveUnsecured(Class<T> entityClass, Long entityId) {
        return (T) getCurrentSession().get(entityClass, entityId);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends AbstractCaArrayEntity> T getEntityByLsid(Class<T> entityClass, LSID lsid) {
        Query q = getCurrentSession().createQuery(
                "from " + entityClass.getName()
                        + " where lsidAuthority = :lsidAuthority and lsidNamespace = :lsidNamespace "
                        + "and lsidObjectId = :lsidObjectId");
        q.setString("lsidAuthority", lsid.getAuthority());
        q.setString("lsidNamespace", lsid.getNamespace());
        q.setString("lsidObjectId", lsid.getObjectId());
        return (T) q.uniqueResult();
    } 
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
        // degenerate case:
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        // need to break this up into blocks of 500 to get around bug
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-2166
        StringBuilder queryStr = new StringBuilder("from " + entityClass.getName() + " o where ");
        Map<String, List<? extends Serializable>> idBlocks = new HashMap<String, List<? extends Serializable>>();
        queryStr.append(HibernateHelper.buildInClause(ids, "o.id", idBlocks));
        Query q = getCurrentSession().createQuery(queryStr.toString());
        HibernateHelper.bindInClauseParameters(q, idBlocks);
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(PersistentObject o) {
        getCurrentSession().refresh(o);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
        return retrieveAll(entityClass, 0, 0, orders);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders) {
        Criteria crit = getCurrentSession().createCriteria(entityClass);
        for (Order o : orders) {
            crit.addOrder(o);
        }
        if (maxResults > 0) {
            crit.setMaxResults(maxResults);
        }
        crit.setFirstResult(firstResult);
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property,
            String value) {
        String hql = MessageFormat.format("where lower({0}) like :value order by {0}", property);
        Query q = getCurrentSession().createFilter(collection, hql);
        q.setParameter("value", value.toLowerCase(Locale.ENGLISH) + "%");
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> List<T> pageCollection(Collection<T> collection,
            PageSortParams<T> pageSortParams) {
        StringBuilder filterQueryStr = new StringBuilder();
        SortCriterion<T> sortCrit = pageSortParams.getSortCriterion();
        if (sortCrit != null) {
            filterQueryStr.append(" ORDER BY this.").append(sortCrit.getOrderField());
            if (pageSortParams.isDesc()) {
                filterQueryStr.append(" desc");
            }
        }
        Query q = getCurrentSession().createFilter(collection, filterQueryStr.toString());
        q.setFirstResult(pageSortParams.getIndex());
        if (pageSortParams.getPageSize() > 0) {
            q.setMaxResults(pageSortParams.getPageSize());            
        }
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends PersistentObject> List<T> pageAndFilterCollection(Collection<T> collection, String property,
            List<? extends Serializable> values, PageSortParams<T> pageSortParams) {
        StringBuilder filterQueryStr = new StringBuilder();

        Map<String, List<? extends Serializable>> idBlocks = new HashMap<String, List<? extends Serializable>>();
        // jboss 4.0.5 only has commons-collections 3.1
        // for now, it is easier to downgrade commons that to build an application.xxml with the right jars.
        //if (!org.apache.commons.collections.CollectionUtils.isEmpty(values)) {
        if (values != null && !values.isEmpty()) {
            filterQueryStr.append("where ").append(HibernateHelper.buildInClause(values, property, idBlocks));
        }

        SortCriterion<T> sortCrit = pageSortParams.getSortCriterion();
        if (sortCrit != null) {
            filterQueryStr.append(" ORDER BY this.").append(sortCrit.getOrderField());
            if (pageSortParams.isDesc()) {
                filterQueryStr.append(" desc");
            }
        }
        
        Query q = getCurrentSession().createFilter(collection, filterQueryStr.toString());
        if (!idBlocks.isEmpty()) {
            HibernateHelper.bindInClauseParameters(q, idBlocks);            
        }
        q.setFirstResult(pageSortParams.getIndex());
        if (pageSortParams.getPageSize() > 0) {
            q.setMaxResults(pageSortParams.getPageSize());            
        }
        
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        return ((Number) getCurrentSession().createFilter(collection, "select count(this)")
                .iterate().next()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<String> findValuesWithSamePrefix(Class<?> entityClass, String fieldName, String namePrefix) {
        String queryStr =
                "select " + fieldName + " from " + entityClass.getName() + " where " + fieldName + " like :prefix";
        Query query = getCurrentSession().createQuery(queryStr);
        query.setString("prefix", namePrefix + "%");
        return query.list();
    }
}
