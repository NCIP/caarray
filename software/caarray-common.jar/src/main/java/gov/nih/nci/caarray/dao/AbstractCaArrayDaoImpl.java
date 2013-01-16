//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Base DAO implementation for all caArray domain DAOs. It provides methods to save, update, remove and query entities
 * by example.
 * 
 * @author Rashmi Srinivasa
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
public abstract class AbstractCaArrayDaoImpl implements CaArrayDao {
    private static final Logger LOG = Logger.getLogger(AbstractCaArrayDaoImpl.class);

    private final CaArrayHibernateHelper hibernateHelper;

    /**
     * 
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    public AbstractCaArrayDaoImpl(CaArrayHibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    /**
     * @return the hibernate helper
     */
    protected CaArrayHibernateHelper getHibernateHelper() {
        return this.hibernateHelper;
    }

    /**
     * Returns the current Hibernate Session.
     * 
     * @return the current Hibernate Session.
     */
    protected Session getCurrentSession() {
        return getHibernateHelper().getCurrentSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(PersistentObject persistentObject) {
        try {
            getCurrentSession().saveOrUpdate(persistentObject);
            return persistentObject.getId();
        } catch (final HibernateException e) {
            LOG.error("Unable to save entity", e);
            throw new DAOException("Unable to save entity", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Collection<? extends PersistentObject> persistentObjects) {
        try {
            final Iterator<? extends PersistentObject> iterator = persistentObjects.iterator();
            while (iterator.hasNext()) {
                final PersistentObject entity = iterator.next();
                getCurrentSession().saveOrUpdate(entity);
            }
        } catch (final HibernateException he) {
            LOG.error("Unable to save entity collection", he);
            throw new DAOException("Unable to save entity collection", he);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(PersistentObject persistentObject) {
        try {
            getCurrentSession().delete(persistentObject);
        } catch (final HibernateException he) {
            LOG.error("Unable to remove entity", he);
            throw new DAOException("Unable to remove entity", he);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> queryEntityByExample(T entityToMatch, Order... order) {
        return queryEntityByExample(new ExampleSearchCriteria<T>(entityToMatch), order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T>
            queryEntityByExample(ExampleSearchCriteria<T> criteria, Order... orders) {
        return queryEntityByExample(criteria, 0, 0, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, int maxResults,
            int firstResult, Order... orders) {
        if (criteria.getExample() == null) {
            return Collections.emptyList();
        }

        final Criteria c = getCriteriaForExampleQuery(criteria);
        for (final Order order : orders) {
            c.addOrder(order);
        }
        if (maxResults > 0) {
            c.setMaxResults(maxResults);
        }
        c.setFirstResult(firstResult);

        return c.list();
    }

    private <T extends PersistentObject> Criteria getCriteriaForExampleQuery(ExampleSearchCriteria<T> criteria) {
        final T entityToMatch = criteria.getExample();
        CaArrayUtils.blankStringPropsToNull(entityToMatch);

        final Criteria c =
                getCurrentSession().createCriteria(getPersistentClass(entityToMatch.getClass())).setResultTransformer(
                        CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        c.add(createExample(entityToMatch, criteria.getMatchMode(), criteria.isExcludeNulls(),
                criteria.isExcludeZeroes(), criteria.getExcludeProperties()));
        new SearchCriteriaHelper<T>(this, c, criteria).addCriteriaForAssociations();

        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushSession() {
        getCurrentSession().flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSession() {
        getCurrentSession().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object mergeObject(Object object) {
        return getCurrentSession().merge(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictObject(Object object) {
        getCurrentSession().evict(object);
    }

    @SuppressWarnings("deprecation")
    static Order toOrder(PageSortParams<?> params) {
        final String orderField = params.getSortCriterion().getOrderField();
        return params.isDesc() ? Order.desc(orderField) : Order.asc(orderField);
    }

    @SuppressWarnings("deprecation")
    static String toHqlOrder(PageSortParams<?> params) {
        return new StringBuilder("ORDER BY ").append(params.getSortCriterion().getOrderField())
                .append(params.isDesc() ? " desc" : " asc").toString();
    }

    @SuppressWarnings("deprecation")
    static Order toOrder(PageSortParams<?> params, String alias) {
        String orderField = params.getSortCriterion().getOrderField();
        if (!StringUtils.isEmpty(alias)) {
            orderField = alias + "." + orderField;
        }
        return params.isDesc() ? Order.desc(orderField) : Order.asc(orderField);
    }

    Criterion createExample(Object entity, MatchMode matchMode, boolean excludeNulls, boolean excludeZeroes,
            Collection<String> excludeProperties) {
        final Example example = Example.create(entity).enableLike(matchMode).ignoreCase();
        if (excludeZeroes) {
            example.excludeZeroes();
        } else if (!excludeNulls) {
            example.excludeNone();
        }
        for (final String property : excludeProperties) {
            example.excludeProperty(property);
        }

        // ID property is not handled by Example, so we have to special case it
        final PersistentClass pclass = getClassMapping(entity.getClass());
        Object idVal = null;
        if (pclass != null && pclass.hasIdentifierProperty()) {
            try {
                idVal = PropertyUtils.getProperty(entity, pclass.getIdentifierProperty().getName());
            } catch (final Exception e) {
                LOG.warn("Could not retrieve identifier value in a by example query, ignoring it", e);
            }
        }
        if (idVal == null) {
            return example;
        } else {
            return Restrictions.and(Restrictions.idEq(idVal), example);
        }
    }

    private PersistentClass getClassMapping(Class<?> exampleClass) {
        final Class<?> persistentClass = getPersistentClass(exampleClass);
        return persistentClass == null ? null : getHibernateHelper().getConfiguration().getClassMapping(
                persistentClass.getName());
    }

    private Class<?> getPersistentClass(Class<?> exampleClass) {
        final Configuration hcfg = getHibernateHelper().getConfiguration();
        for (Class<?> klass = exampleClass; !Object.class.equals(klass); klass = klass.getSuperclass()) {
            if (hcfg.getClassMapping(klass.getName()) != null) {
                return klass;
            }
        }
        return null;
    }

    /**
     * Provides helper methods for search DAOs.
     * 
     * @author Rashmi Srinivasa
     */
    private final class SearchCriteriaHelper<T extends PersistentObject> {
        private static final String UNABLE_TO_GET_ASSOCIATION_VAL = "Unable to get association value";

        private final Criteria hibCriteria;
        private final ExampleSearchCriteria<T> exampleCriteria;
        private final AbstractCaArrayDaoImpl dao;

        /**
         * @param abstractCaArrayDaoImpl
         * @param criteria
         * @param excludeNulls
         * @param matchMode
         */
        public SearchCriteriaHelper(AbstractCaArrayDaoImpl dao, Criteria hibCriteria,
                ExampleSearchCriteria<T> exampleCriteria) {
            this.dao = dao;
            this.hibCriteria = hibCriteria;
            this.exampleCriteria = exampleCriteria;
        }

        /**
         * Update the provided criteria object with values from the associated entites. For collection-valued
         * associations, the criteria will contain a disjunction using collection members as examples.
         * 
         * @param entityToMatch the entity to match
         */
        @SuppressWarnings("unchecked")
        public void addCriteriaForAssociations() {
            try {
                final PersistentClass pclass = getClassMapping(this.exampleCriteria.getExample().getClass());
                if (pclass == null) {
                    throw new DAOException("Could not find hibernate class mapping in hierarchy of class "
                            + this.exampleCriteria.getExample().getClass().getName());
                }
                final Iterator<Property> properties = pclass.getPropertyClosureIterator();
                while (properties.hasNext()) {
                    final Property prop = properties.next();
                    if (prop.getType().isAssociationType()) {
                        addCriterionForAssociation(prop);
                    }
                }
            } catch (final IllegalAccessException iae) {
                LOG.error(UNABLE_TO_GET_ASSOCIATION_VAL, iae);
                throw new DAOException(UNABLE_TO_GET_ASSOCIATION_VAL, iae);
            } catch (final InvocationTargetException ite) {
                LOG.error(UNABLE_TO_GET_ASSOCIATION_VAL, ite);
                throw new DAOException(UNABLE_TO_GET_ASSOCIATION_VAL, ite);
            }
        }

        /**
         * Add one search criterion based on the association to be matched.
         * 
         * @param entityToMatch the root entity being searched on.
         * @param hibCriteria the root Criteria to add to.
         * @param property the association to be matched.
         */
        private void addCriterionForAssociation(Property property) throws IllegalAccessException,
                InvocationTargetException {
            Object valueOfAssociation = null;

            try {
                valueOfAssociation = PropertyUtils.getProperty(this.exampleCriteria.getExample(), property.getName());
            } catch (final NoSuchMethodException e) {
                LOG.error("No getter method for property " + property.getName(), e);
            }

            if (valueOfAssociation == null) {
                return;
            }
            if (valueOfAssociation instanceof Collection<?>) {
                final Collection<?> collValue = (Collection<?>) valueOfAssociation;
                if (!collValue.isEmpty()) {
                    final Disjunction or = Restrictions.disjunction();
                    for (final Object value : collValue) {
                        or.add(createExample(value));
                    }
                    this.hibCriteria.createCriteria(property.getName()).add(or);
                }
            } else {
                this.hibCriteria.createCriteria(property.getName()).add(createExample(valueOfAssociation));
            }

        }

        private Criterion createExample(Object value) {
            return this.dao.createExample(value, this.exampleCriteria.getMatchMode(),
                    this.exampleCriteria.isExcludeNulls(), this.exampleCriteria.isExcludeZeroes(),
                    this.exampleCriteria.getExcludeProperties());
        }
    }
}
