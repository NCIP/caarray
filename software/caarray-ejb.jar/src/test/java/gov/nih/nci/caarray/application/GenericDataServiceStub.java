//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
    public String getIncrementingCopyName(Class<?> entityClass, String fieldName, String name) {
        return name + "2";
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
        return null;
    }

    public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids)
            throws IllegalAccessException, InstantiationException {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
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
    public <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property, String value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> pageCollection(Collection<T> collection,
            PageSortParams<T> pageSortParams) {
        return new ArrayList<T>(collection);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders)
            throws IllegalAccessException, InstantiationException {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    public void save(PersistentObject object) {
        this.savedObject = object;
    }

    /**
     * @return the savedObject
     */
    public PersistentObject getSavedObject() {
        return this.savedObject;
    }
}
