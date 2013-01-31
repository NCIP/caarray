//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.CaArrayDao;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Base class for all dao stubs.
 */
public class AbstractDaoStub implements CaArrayDao {
    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, int maxResults,
            int firstResult, Order... orders) {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, Order... orders) {
        return queryEntityByExample(criteria, 0, 0, orders);
    }
    
    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> queryEntityByExample(T entityToMatch, Order... orders) {
        return queryEntityByExample(ExampleSearchCriteria.forEntity(entityToMatch), orders);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(PersistentObject caArrayEntity) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void save(PersistentObject caArrayEntity) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void save(Collection<? extends PersistentObject> caArrayEntities) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void flushSession() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void clearSession() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public Object mergeObject(Object object) {
        return object;
    }

    /**
     * {@inheritDoc}
     */
    public void evictObject(Object object) {
        // no-op
    }
}
