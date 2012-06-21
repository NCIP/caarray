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
