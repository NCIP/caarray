//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.CaArrayDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Base class for all dao stubs.
 */
public class AbstractDaoStub implements CaArrayDao {

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> queryEntityAndAssociationsByExample(T entityToMatch, Order... orders) {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> queryEntityByExample(T entityToMatch, Order... order) {
        return queryEntityByExample(entityToMatch, MatchMode.EXACT, order);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> queryEntityByExample(T entityToMatch, MatchMode mode, Order... order) {
        return queryEntityByExample(entityToMatch, mode, true, ArrayUtils.EMPTY_STRING_ARRAY, order);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> queryEntityByExample(T entityToMatch, MatchMode mode, boolean excludeNulls,
            String[] excludeProperties, Order... order) {
        return new ArrayList<T>();
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
