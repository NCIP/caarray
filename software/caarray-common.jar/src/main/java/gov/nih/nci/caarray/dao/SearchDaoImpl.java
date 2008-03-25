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

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.search.PageSortParams;
import gov.nih.nci.caarray.domain.search.SortCriterion;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.sdk32query.CQL2HQL;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * DAO to search for entities using various types of criteria. Supports searching by example, CQL, HQL (Hibernate Query
 * Language) string and Hibernate Detached Criteria.
 * 
 * @author Rashmi Srinivasa
 */
class SearchDaoImpl extends AbstractCaArrayDaoImpl implements SearchDao {
    private static final Logger LOG = Logger.getLogger(SearchDaoImpl.class);
    private static final String UNCHECKED = "unchecked";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractCaArrayObject> List<T> query(final T entityToMatch) {
        if (entityToMatch.getId() != null) {
            String qStr = "from " + entityToMatch.getClass().getName() + " where id = :id";
            Query q = HibernateUtil.getCurrentSession().createQuery(qStr);
            q.setLong("id", entityToMatch.getId());
            return q.list();
        }
        return queryEntityAndAssociationsByExample(entityToMatch);
    }

    /**
     * {@inheritDoc}
     */
    public List<?> query(final CQLQuery cqlQuery) {
        try {
            String s = CQL2HQL.translate(cqlQuery, false, true);
            Query hqlquery = HibernateUtil.getCurrentSession().createQuery(s);
            return hqlquery.list();
        } catch (QueryProcessingException e) {
            getLog().error("Unable to parse CQL query", e);
            throw new DAOException("Unable to parse CQL query", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
        Query q = HibernateUtil.getCurrentSession().createQuery("from " + entityClass.getName() + " where id = :id");
        q.setLong("id", entityId);
        return (T) q.uniqueResult();
    }
        
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId, LockMode lockMode) {
        Query q =
                HibernateUtil.getCurrentSession()
                        .createQuery("from " + entityClass.getName() + " o where o.id = :id");
        q.setLong("id", entityId);
        q.setLockMode("o", lockMode);
        return (T) q.uniqueResult();
    }
    
    /**
     * {@inheritDoc}
     */
    public void refresh(PersistentObject o) {
        HibernateUtil.getCurrentSession().refresh(o);        
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
        Criteria crit = HibernateUtil.getCurrentSession().createCriteria(entityClass);
        for (Order o : orders) {
            crit.addOrder(o);
        }
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property,
            String value) {
        String hql = MessageFormat.format("where lower({0}) like :value order by {0}", property);
        Query q = HibernateUtil.getCurrentSession().createFilter(collection, hql);
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
        Query q = HibernateUtil.getCurrentSession().createFilter(collection, filterQueryStr.toString());
        q.setFirstResult(pageSortParams.getIndex());
        q.setMaxResults(pageSortParams.getPageSize());
        return q.list();
    }
        
    /**
     * {@inheritDoc}
     */
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        return ((Number) HibernateUtil.getCurrentSession().createFilter(collection, "select count(this)").iterate()
                .next()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<String> findValuesWithSamePrefix(Class<?> entityClass, String fieldName, String namePrefix) {
        String queryStr =
                "select " + fieldName + " from " + entityClass.getName() + " where " + fieldName + " like :prefix";
        Query query = HibernateUtil.getCurrentSession().createQuery(queryStr);
        query.setString("prefix", namePrefix + "%");
        return query.list();
    }

    @Override
    Logger getLog() {
        return LOG;
    }
}
