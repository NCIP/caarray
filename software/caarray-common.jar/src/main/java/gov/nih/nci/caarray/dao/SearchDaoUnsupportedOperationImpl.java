//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Throws UnsupportedOperationException for all SearchDao functions.
 * 
 * Inherit from this class if you need to implement SearchDao in a limited context, such as a test, where not all
 * functions are needed. Override only the functions needed and rely on an exception being thrown if any unimplemented
 * function is called unexpectedly.
 * 
 * @author jscott
 */
public class SearchDaoUnsupportedOperationImpl implements SearchDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T>
            filterCollection(Collection<T> collection, String property, String value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findValuesWithSamePrefix(Class<?> entityClass, String fieldName, String prefix) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractCaArrayEntity> T getEntityByLsid(Class<T> entityClass, LSID lsid) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> pageAndFilterCollection(Collection<T> collection, String property,
            List<? extends Serializable> values, PageSortParams<T> pageSortParams) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> pageCollection(Collection<T> collection,
            PageSortParams<T> pageSortParams) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> query(CQLQuery cqlQuery) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractCaArrayObject> List<T> query(T entityToMatch) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(PersistentObject o) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId, LockMode lockMode) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> T retrieveUnsecured(Class<T> entityClass, Long entityId) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictObject(Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object mergeObject(Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> queryEntityByExample(T entityToMatch, Order... order) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T>
            queryEntityByExample(ExampleSearchCriteria<T> criteria, Order... orders) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, int maxResults,
            int firstResult, Order... orders) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(PersistentObject persistentObject) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(PersistentObject persistentObject) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Collection<? extends PersistentObject> persistentObjects) {
        throw new UnsupportedOperationException();
    }
}
