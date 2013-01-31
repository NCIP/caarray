//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.util.EntityPruner;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Base DAO implementation for all caArray domain DAOs.
 * It provides methods to save, update, remove and query entities by example.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
public abstract class AbstractCaArrayDaoImpl implements CaArrayDao {

    private static final String UNABLE_TO_RETRIEVE_ENTITY_MESSAGE = "Unable to retrieve entity";

    abstract Logger getLog();

    /**
     * Returns the current Hibernate Session.
     *
     * @return the current Hibernate Session.
     */
    protected Session getCurrentSession() {
        return HibernateUtil.getCurrentSession();
    }

    /**
     * {@inheritDoc}
     */
    public void save(PersistentObject persistentObject) {
        try {
            getCurrentSession().saveOrUpdate(persistentObject);
        } catch (HibernateException e) {
            getLog().error("Unable to save entity", e);
            throw new DAOException("Unable to save entity", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save(Collection<? extends PersistentObject> persistentObjects) {
        try {
            Iterator<? extends PersistentObject> iterator = persistentObjects.iterator();
            while (iterator.hasNext()) {
                PersistentObject entity = iterator.next();
                getCurrentSession().saveOrUpdate(entity);
            }
        } catch (HibernateException he) {
            getLog().error("Unable to save entity collection", he);
            throw new DAOException("Unable to save entity collection", he);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(PersistentObject persistentObject) {
        try {
            getCurrentSession().delete(persistentObject);
        } catch (HibernateException he) {
            getLog().error("Unable to remove entity", he);
            throw new DAOException("Unable to remove entity", he);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> queryEntityByExample(T entityToMatch, Order... order) {
        return queryEntityByExample(entityToMatch, MatchMode.EXACT, order);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> queryEntityByExample(T entityToMatch, MatchMode mode, Order... order) {
        return queryEntityByExample(entityToMatch, mode, true, ArrayUtils.EMPTY_STRING_ARRAY, order);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryEntityByExample(T entityToMatch, MatchMode mode, boolean excludeNulls,
            String[] excludeProperties, Order... order) {
        if (entityToMatch == null) {
            return new ArrayList<T>();
        }

        Session mySession = HibernateUtil.getCurrentSession();
        try {
            EntityPruner.blankStringPropsToNull(entityToMatch);
            // Query database for list of entities matching the given entity's attributes.
            Criteria criteria = mySession.createCriteria(entityToMatch.getClass())
                                         .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            Example example = Example.create(entityToMatch).enableLike(mode).ignoreCase();
            if (!excludeNulls) {
                example.excludeNone();
            }
            for (String property : excludeProperties) {
                example.excludeProperty(property);
            }
            criteria.add(example);
            for (Order curretOrder : order) {
                criteria.addOrder(curretOrder);
            }
            return criteria.list();
        } catch (HibernateException he) {
            getLog().error(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
            throw new DAOException(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends PersistentObject> List<T> queryEntityAndAssociationsByExample(T entityToMatch, Order...orders) {
        List<T> resultList = new ArrayList<T>();
        List hibernateReturnedEntities = null;
        if (entityToMatch == null) {
            return resultList;
        }

        Session mySession = HibernateUtil.getCurrentSession();
        try {
            // Create search-criteria with the given entity's attributes.
            Criteria criteria = mySession.createCriteria(entityToMatch.getClass())
                                         .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            criteria.add(Example.create(entityToMatch));
            // Add search-criteria with the given entity's associations.
            SearchCriteriaUtil.addCriteriaForAssociations(entityToMatch, criteria);
            for (Order curretOrder : orders) {
              criteria.addOrder(curretOrder);
            }
            hibernateReturnedEntities = criteria.list();
        } catch (HibernateException he) {
            getLog().error(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
            throw new DAOException(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
        }

        if (hibernateReturnedEntities != null) {
            resultList.addAll(hibernateReturnedEntities);
        }
        return resultList;
    }

    /**
     * {@inheritDoc}
     */
    public void flushSession() {
        HibernateUtil.getCurrentSession().flush();
    }

    /**
     * {@inheritDoc}
     */
    public void clearSession() {
        HibernateUtil.getCurrentSession().clear();
    }

    /**
     * {@inheritDoc}
     */
    public Object mergeObject(Object object) {
        return HibernateUtil.getCurrentSession().merge(object);
    }

    /**
     * {@inheritDoc}
     */
    public void evictObject(Object object) {
        HibernateUtil.getCurrentSession().evict(object);
    }

    AbstractCaArrayEntity getEntityByLsid(Class entityClass, String lsidAuthority, String lsidNamespace,
            String lsidObjectId) {
        Query q = HibernateUtil.getCurrentSession().createQuery(
                    "from "
                    + entityClass.getName()
                    + " where lsidAuthority = :lsidAuthority and lsidNamespace = :lsidNamespace "
                    + "and lsidObjectId = :lsidObjectId");
        q.setString("lsidAuthority", lsidAuthority);
        q.setString("lsidNamespace", lsidNamespace);
        q.setString("lsidObjectId", lsidObjectId);
        return (AbstractCaArrayEntity) q.uniqueResult();
    }

}
