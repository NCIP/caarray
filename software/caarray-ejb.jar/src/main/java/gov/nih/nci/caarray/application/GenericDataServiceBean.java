//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.Inject;

/**
 * Implementation of the GenericDataService.
 * 
 * @author Scott Miller
 */
@Local(GenericDataService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors(InjectionInterceptor.class)
public class GenericDataServiceBean implements GenericDataService {

    private static final Logger LOG = Logger.getLogger(GenericDataServiceBean.class);

    private SearchDao searchDao;
    private ProjectDao projectDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
        LogUtil.logSubsystemEntry(LOG, entityClass, entityId);
        final T result = this.searchDao.retrieve(entityClass, entityId);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
        LogUtil.logSubsystemEntry(LOG, entityClass, ids);
        final List<T> result = this.searchDao.retrieveByIds(entityClass, ids);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncrementingCopyName(Class<?> entityClass, String fieldName, String name) {
        final String alphaPrefix = StringUtils.stripEnd(name, "0123456789");
        final String numericSuffix = StringUtils.substringAfter(name, alphaPrefix);
        int maxSuffix = StringUtils.isEmpty(numericSuffix) ? 1 : Integer.parseInt(numericSuffix);

        final List<String> currentNames = this.searchDao.findValuesWithSamePrefix(entityClass, fieldName, alphaPrefix);
        for (final String currentName : currentNames) {
            final String suffix = StringUtils.substringAfter(currentName, alphaPrefix);
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
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save(PersistentObject entity) {
        if (entity instanceof Protectable && !SecurityUtils.canWrite(entity, CaArrayUsernameHolder.getCsmUser())) {
            throw new IllegalArgumentException("The current user does not have the rights to edit the given object.");
        }
        this.searchDao.save(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
        return this.searchDao.retrieveAll(entityClass, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders) {
        return this.searchDao.retrieveAll(entityClass, maxResults, firstResult, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(PersistentObject object) {
        this.projectDao.remove(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T>
            filterCollection(Collection<T> collection, String property, String value) {
        return this.searchDao.filterCollection(collection, property, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> pageCollection(Collection<T> collection,
            PageSortParams<T> pageSortParams) {
        return this.searchDao.pageCollection(collection, pageSortParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> pageAndFilterCollection(Collection<T> collection, String property,
            List<? extends Serializable> values, PageSortParams<T> pageSortParams) {
        return this.searchDao.pageAndFilterCollection(collection, property, values, pageSortParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        return this.searchDao.collectionSize(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(PersistentObject object) {
        this.searchDao.refresh(object);
    }

    /**
     * @param searchDao the searchDao to set
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * @param projectDao the projectDao to set
     */
    @Inject
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }
}
