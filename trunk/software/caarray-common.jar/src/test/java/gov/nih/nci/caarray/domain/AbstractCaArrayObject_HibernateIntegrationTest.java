//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.HibernateIntegrationTestCleanUpUtility;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractCaArrayObject_HibernateIntegrationTest extends AbstractCaarrayTest {

    private static int uniqueIntValue = 0;

    @Before
    public void setUp() {
        HibernateUtil.setFiltersEnabled(true);
    }

    @After
    public void tearDown() {
        HibernateIntegrationTestCleanUpUtility.cleanUp();
    }

    @Test
    public void testSave() {
        AbstractCaArrayObject caArrayObject = createTestObject();
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

    abstract protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject);

    protected final void saveAndCheckRetrieved(AbstractCaArrayObject caArrayObject) {
        save(caArrayObject);
        assertNotNull(caArrayObject.getId());
        assertTrue(caArrayObject.getId() > 0L);
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().evict(caArrayObject);
        AbstractCaArrayObject retrievedCaArrayObject =
            (AbstractCaArrayObject) HibernateUtil.getCurrentSession().get(caArrayObject.getClass(), caArrayObject.getId());
        compareCaArrayObjectValues(caArrayObject, retrievedCaArrayObject);
        tx.commit();
    }

    protected final void save(AbstractCaArrayObject caArrayObject) {
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().saveOrUpdate(caArrayObject);
        tx.commit();
    }

    abstract protected void setValues(AbstractCaArrayObject caArrayObject);

    abstract protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject);

    void setCaArrayObjectValues(AbstractCaArrayObject caArrayObject) {
        caArrayObject.setCaBigId(getUniqueStringValue());
        setValues(caArrayObject);
    }
    void compareCaArrayObjectValues(AbstractCaArrayObject caArrayObject,
            AbstractCaArrayObject retrievedCaArrayObject) {
        assertEquals(caArrayObject.getId(), retrievedCaArrayObject.getId());
        assertEquals(caArrayObject.getCaBigId(), retrievedCaArrayObject.getCaBigId());
        compareValues(caArrayObject, retrievedCaArrayObject);
    }

    abstract protected AbstractCaArrayObject createTestObject();

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
