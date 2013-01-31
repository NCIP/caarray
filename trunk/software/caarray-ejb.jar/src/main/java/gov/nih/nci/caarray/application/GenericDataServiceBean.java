//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Implementation of the GenericDataService.
 * @author Scott Miller
 */
@Local(GenericDataService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GenericDataServiceBean implements GenericDataService {

    private static final Logger LOG = Logger.getLogger(GenericDataServiceBean.class);

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
        LogUtil.logSubsystemEntry(LOG, entityClass, entityId);
        T result = this.daoFactory.getSearchDao().retrieve(entityClass, entityId);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
        LogUtil.logSubsystemEntry(LOG, entityClass, ids);
        List<T> result = this.daoFactory.getSearchDao().retrieveByIds(entityClass, ids);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }
    /**
     * {@inheritDoc}
     */
    public String getIncrementingCopyName(Class<?> entityClass, String fieldName, String name) {
        String alphaPrefix = StringUtils.stripEnd(name, "0123456789");
        String numericSuffix = StringUtils.substringAfter(name, alphaPrefix);
        int maxSuffix = StringUtils.isEmpty(numericSuffix) ? 1 : Integer.parseInt(numericSuffix);

        List<String> currentNames =
            this.daoFactory.getSearchDao().findValuesWithSamePrefix(entityClass, fieldName, alphaPrefix);
        for (String currentName : currentNames) {
            String suffix = StringUtils.substringAfter(currentName, alphaPrefix);
            if (!StringUtils.isNumeric(suffix) || StringUtils.isEmpty(suffix)) {
                continue;
            }
            maxSuffix = Math.max(maxSuffix, Integer.parseInt(suffix));
        }
        return alphaPrefix + (maxSuffix + 1);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save(PersistentObject entity) {
        if (entity instanceof Protectable && !SecurityUtils.canWrite(entity, UsernameHolder.getCsmUser())) {
            throw new IllegalArgumentException("The current user does not have the rights to edit the given object.");
        }
        this.daoFactory.getSearchDao().save(entity);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
        return this.daoFactory.getSearchDao().retrieveAll(entityClass, orders);
    }
    
    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders) {
        return this.daoFactory.getSearchDao().retrieveAll(entityClass, maxResults, firstResult, orders);
    }

    /**
     * Set the current dao factory.
     * @param daoFactory the dao factory.
     */
    public void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(PersistentObject object) {
        this.daoFactory.getProjectDao().remove(object);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property,
            String value) {
        return this.daoFactory.getSearchDao().filterCollection(collection, property, value);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> pageCollection(Collection<T> collection,
            PageSortParams<T> pageSortParams) {
        return this.daoFactory.getSearchDao().pageCollection(collection, pageSortParams);
    }
    
    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> pageAndFilterCollection(Collection<T> collection, String property,
            List<? extends Serializable> values, PageSortParams<T> pageSortParams) {
        return this.daoFactory.getSearchDao().pageAndFilterCollection(collection, property, values, pageSortParams);
    }

    /**
     * {@inheritDoc}
     */
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        return this.daoFactory.getSearchDao().collectionSize(collection);
    }
}
