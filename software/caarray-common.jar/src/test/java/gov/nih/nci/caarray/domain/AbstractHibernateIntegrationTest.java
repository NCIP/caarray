//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractHibernateTest;

import org.hibernate.Transaction;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

public abstract class AbstractHibernateIntegrationTest<T extends PersistentObject> extends AbstractHibernateTest {
    private static int uniqueIntValue = 0;

    public AbstractHibernateIntegrationTest() {
        super(true);
    }

    @Test
    public void testSave() {
        final T caArrayObject = createTestObject();
        // Test once for insert
        setValues(caArrayObject);
        saveAndCheckRetrieved(caArrayObject);
        // ...and again for update
        setValues(caArrayObject);
        saveAndCheckRetrieved(caArrayObject);
        // ...and check that nullable fields work
        setNullableValuesToNull(caArrayObject);
        saveAndCheckRetrieved(caArrayObject);
        // ...and add values again to previously nulled fields
        setValues(caArrayObject);
        saveAndCheckRetrieved(caArrayObject);
    }

    protected void setNullableValuesToNull(T caArrayObject) {
        // no-op in the base case - subclasses should override
    }

    protected final void saveAndCheckRetrieved(T caArrayObject) {
        save(caArrayObject);
        assertNotNull(caArrayObject.getId());
        assertTrue(caArrayObject.getId() > 0L);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().evict(caArrayObject);
        final T retrievedCaArrayObject = (T) this.hibernateHelper.getCurrentSession().get(caArrayObject.getClass(),
                caArrayObject.getId());
        compareValues(caArrayObject, retrievedCaArrayObject);
        tx.commit();
    }

    protected final void save(PersistentObject caArrayObject) {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().saveOrUpdate(caArrayObject);
        tx.commit();
    }

    abstract protected void setValues(T caArrayObject);

    abstract protected void compareValues(T caArrayObject, T retrievedCaArrayObject);

    abstract protected T createTestObject();

    protected String getUniqueStringValue() {
        return String.valueOf(getUniqueIntValue());
    }

    protected int getUniqueIntValue() {
        return uniqueIntValue++;
    }

    protected <E extends Enum<E>> E getNextValue(E[] values, Enum<E> currentValue) {
        if (currentValue == null || currentValue.ordinal() == values.length - 1) {
            return values[0];
        } else {
            return values[currentValue.ordinal() + 1];
        }
    }

}
