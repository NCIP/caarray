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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

/**
 * Base DAO implementation for all caArray domain DAOs.
 * It provides methods to save, update, remove and query entities by example.
 *
 * @author Rashmi Srinivasa
 */
public abstract class AbstractCaArrayDaoImpl implements CaArrayDao {

    private static final String UNABLE_TO_RETRIEVE_ENTITY_MESSAGE = "Unable to retrieve entity";

    abstract Log getLog();

    /**
     * Returns the current Hibernate Session.
     *
     * @return the current Hibernate Session.
     */
    protected Session getCurrentSession() {
        return HibernateUtil.getCurrentSession();
    }

    /**
     * Saves the entity to persistent storage, updating or inserting
     * as necessary.
     *
     * @param caArrayEntity the entity to save
     */
    public void save(AbstractCaArrayEntity caArrayEntity) {
        try {
            getCurrentSession().saveOrUpdate(caArrayEntity);
        } catch (HibernateException e) {
            getLog().error("Unable to save entity", e);
           throw new DAOException("Unable to save entity", e);
        }
    }

    /**
     * Saves the collection of entities to persistent storage, updating or inserting
     * as necessary.
     *
     * @param caArrayEntities the entity collection to save
     */
    public void save(Collection<? extends AbstractCaArrayEntity> caArrayEntities) {
        try {
            Iterator<? extends AbstractCaArrayEntity> iterator = caArrayEntities.iterator();
            while (iterator.hasNext()) {
                AbstractCaArrayEntity entity = iterator.next();
                getCurrentSession().saveOrUpdate(entity);
            }
        } catch (HibernateException he) {
            getLog().error("Unable to save entity collection", he);
            throw new DAOException("Unable to save entity collection", he);
        }
    }

    /**
     * Returns the <code>AbstractCaArrayEntity</code> matching the given id,
     * or null if none exists.
     *
     * @param entityToMatch get <code>AbstractCaArrayEntity</code> objects matching this id.
     * @return the retrieved <code>AbstractCaArrayEntity</code> or null.
     */
    public AbstractCaArrayEntity queryEntityById(AbstractCaArrayEntity entityToMatch) {
        List hibernateReturnedEntities = null;
        Session mySession = HibernateUtil.getSessionForQueryMethod();

        try {
            // Query database for list of entities matching the given entity.
            hibernateReturnedEntities = mySession.createCriteria(entityToMatch.getClass()).add(
                Restrictions.eq("id", entityToMatch.getId())).list();
        } catch (HibernateException he) {
            getLog().error(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
            throw new DAOException(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
        } finally {
            HibernateUtil.returnSession(mySession);
        }
        if ((hibernateReturnedEntities != null) && (hibernateReturnedEntities.size() >= 1)) {
            return (AbstractCaArrayEntity) hibernateReturnedEntities.get(0);
        } else {
            return null;
        }
    }

    /**
     * Deletes the entity from persistent storage.
     *
     * @param caArrayEntity the entity to be deleted.
     * @if unable to delete the entity.
     */
    public void remove(AbstractCaArrayEntity caArrayEntity) {
        try {
            getCurrentSession().delete(caArrayEntity);
        } catch (HibernateException he) {
            getLog().error("Unable to remove entity", he);
            throw new DAOException("Unable to remove entity", he);
        }
    }

    /**
     * Returns the list of <code>AbstractCaArrayEntity</code> matching the given entity,
     * or null if none exists.
     *
     * @param entityToMatch get <code>AbstractCaArrayEntity</code> objects matching this entity
     * @return the List of <code>AbstractCaArrayEntity</code> objects, or an empty List.
     * @if the list of matching entities could not be retrieved.
     */
    @SuppressWarnings("unchecked")
    public List<AbstractCaArrayEntity> queryEntityByExample(AbstractCaArrayEntity entityToMatch) {
        List<AbstractCaArrayEntity> resultList = new ArrayList<AbstractCaArrayEntity>();
        List hibernateReturnedEntities = null;
        if (entityToMatch == null) {
            return resultList;
        }

        Session mySession = HibernateUtil.getSessionForQueryMethod();
        try {
            // Query database for list of entities matching the given entity's attributes.
            Criteria criteria = mySession.createCriteria(entityToMatch.getClass());
            criteria.add(Example.create(entityToMatch));
            hibernateReturnedEntities = criteria.list();
        } catch (HibernateException he) {
            getLog().error(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
            throw new DAOException(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
        } finally {
            HibernateUtil.returnSession(mySession);
        }

        if (hibernateReturnedEntities != null) {
            resultList.addAll(hibernateReturnedEntities);
        }
        return resultList;
    }


    /**
     * Returns the list of <code>AbstractCaArrayEntity</code> matching the given entity
     * and its associations, or null if none exists.
     *
     * @param entityToMatch get <code>AbstractCaArrayEntity</code> objects matching this entity
     * @return the List of <code>AbstractCaArrayEntity</code> objects, or an empty List if the list
     * of matching entities could not be retrieved.
     */
    @SuppressWarnings("unchecked")
    public List<AbstractCaArrayEntity> queryEntityAndAssociationsByExample(AbstractCaArrayEntity entityToMatch)
      {
        List<AbstractCaArrayEntity> resultList = new ArrayList<AbstractCaArrayEntity>();
        List hibernateReturnedEntities = null;
        if (entityToMatch == null) {
            return resultList;
        }

        Session mySession = HibernateUtil.getSessionForQueryMethod();
        try {
            // Create search-criteria with the given entity's attributes.
            Criteria criteria = mySession.createCriteria(entityToMatch.getClass());
            criteria.add(Example.create(entityToMatch));
            // Add search-criteria with the given entity's associations.
            SearchCriteriaUtil.addCriteriaForAssociations(entityToMatch, criteria);
            hibernateReturnedEntities = criteria.list();
        } catch (HibernateException he) {
            getLog().error(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
            throw new DAOException(UNABLE_TO_RETRIEVE_ENTITY_MESSAGE, he);
        } finally {
            HibernateUtil.returnSession(mySession);
        }

        if (hibernateReturnedEntities != null) {
            resultList.addAll(hibernateReturnedEntities);
        }
        return resultList;
    }

}
