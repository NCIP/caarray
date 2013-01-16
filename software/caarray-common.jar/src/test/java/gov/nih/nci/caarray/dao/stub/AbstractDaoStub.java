//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
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
    @Override
    public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, int maxResults,
            int firstResult, Order... orders) {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, Order... orders) {
        return queryEntityByExample(criteria, 0, 0, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistentObject> List<T> queryEntityByExample(T entityToMatch, Order... orders) {
        return queryEntityByExample(ExampleSearchCriteria.forEntity(entityToMatch), orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(PersistentObject caArrayEntity) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(PersistentObject caArrayEntity) {
        return 1L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Collection<? extends PersistentObject> caArrayEntities) {
        for (PersistentObject entity : caArrayEntities) {
            save(entity);
        }
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushSession() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSession() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object mergeObject(Object object) {
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictObject(Object object) {
        // no-op
    }
}
