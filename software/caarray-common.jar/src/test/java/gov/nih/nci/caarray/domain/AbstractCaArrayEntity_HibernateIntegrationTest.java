//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.assertEquals;

public abstract class AbstractCaArrayEntity_HibernateIntegrationTest<T extends AbstractCaArrayEntity> extends
        AbstractCaArrayObject_HibernateIntegrationTest<T> {
    @Override
    protected void setValues(T caArrayObject) {
        super.setValues(caArrayObject);
        final AbstractCaArrayEntity caArrayEntity = caArrayObject;
        caArrayEntity.setLsidForEntity(generateUniqueLsid());
    }

    private String generateUniqueLsid() {
        return getUniqueStringValue() + ":" + getUniqueStringValue() + ":" + getUniqueStringValue();
    }

    @Override
    protected void compareValues(T caArrayObject, T retrievedCaArrayObject) {
        super.compareValues(caArrayObject, retrievedCaArrayObject);
        final AbstractCaArrayEntity caArrayEntity = caArrayObject;
        final AbstractCaArrayEntity retrievedCaArrayEntity = retrievedCaArrayObject;
        assertEquals(caArrayEntity.getLsidAuthority(), retrievedCaArrayEntity.getLsidAuthority());
        assertEquals(caArrayEntity.getLsidNamespace(), retrievedCaArrayEntity.getLsidNamespace());
        assertEquals(caArrayEntity.getLsidObjectId(), retrievedCaArrayEntity.getLsidObjectId());
    }
}
