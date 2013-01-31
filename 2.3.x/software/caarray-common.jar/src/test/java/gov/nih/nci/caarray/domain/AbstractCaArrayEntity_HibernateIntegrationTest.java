//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.assertEquals;

public abstract class AbstractCaArrayEntity_HibernateIntegrationTest extends AbstractCaArrayObject_HibernateIntegrationTest {

    @Override
    final void setCaArrayObjectValues(AbstractCaArrayObject caArrayObject) {
        AbstractCaArrayEntity caArrayEntity = (AbstractCaArrayEntity) caArrayObject;
        caArrayEntity.setLsidForEntity(generateUniqueLsid());
        super.setCaArrayObjectValues(caArrayObject);
    }

    private String generateUniqueLsid() {
        return  getUniqueStringValue() + ":" + getUniqueStringValue() + ":" + getUniqueStringValue();
    }

    @Override
    final void compareCaArrayObjectValues(AbstractCaArrayObject caArrayObject,
            AbstractCaArrayObject retrievedCaArrayObject) {
        AbstractCaArrayEntity caArrayEntity = (AbstractCaArrayEntity) caArrayObject;
        AbstractCaArrayEntity retrievedCaArrayEntity = (AbstractCaArrayEntity) retrievedCaArrayObject;
        assertEquals(caArrayEntity.getLsidAuthority(), retrievedCaArrayEntity.getLsidAuthority());
        assertEquals(caArrayEntity.getLsidNamespace(), retrievedCaArrayEntity.getLsidNamespace());
        assertEquals(caArrayEntity.getLsidObjectId(), retrievedCaArrayEntity.getLsidObjectId());
        super.compareCaArrayObjectValues(caArrayObject, retrievedCaArrayObject);
    }
}
