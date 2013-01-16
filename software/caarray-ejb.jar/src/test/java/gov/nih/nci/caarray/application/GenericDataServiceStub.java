//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author dkokotov
 * 
 */
public class GenericDataServiceStub implements GenericDataService {
    private PersistentObject deletedObject = null;
    private PersistentObject savedObject = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncrementingCopyName(Class<?> entityClass, String fieldName, String name) {
        return name + "2";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
        return null;
    }

    @Override
    public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(PersistentObject object) {
        this.deletedObject = object;
    }

    /**
     * @return the last object passed to delete, if any.
     */
    public PersistentObject getDeletedObject() {
        return this.deletedObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property, String value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> pageCollection(Collection<T> collection,
            PageSortParams<T> pageSortParams) {
        return new ArrayList<T>(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> pageAndFilterCollection(Collection<T> collection, String property,
            List<? extends Serializable> values, PageSortParams<T> pageSortParams) {
        return new ArrayList<T>(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(PersistentObject object) {
        this.savedObject = object;
    }

    /**
     * @return the savedObject
     */
    public PersistentObject getSavedObject() {
        return this.savedObject;
    }

    @Override
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders) {
        return Collections.emptyList();
    }

    @Override
    public void refresh(PersistentObject object) {
        // no op
    }
}
