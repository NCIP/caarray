//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.assertEquals;

public abstract class AbstractCaArrayObject_HibernateIntegrationTest<T extends AbstractCaArrayObject> extends
        AbstractHibernateIntegrationTest<T> {
    @Override
    protected void setValues(T caArrayObject) {
        caArrayObject.setCaBigId(getUniqueStringValue());
    }

    @Override
    protected void compareValues(T caArrayObject, T retrievedCaArrayObject) {
        assertEquals(caArrayObject.getId(), retrievedCaArrayObject.getId());
        assertEquals(caArrayObject.getCaBigId(), retrievedCaArrayObject.getCaBigId());
    }
}
