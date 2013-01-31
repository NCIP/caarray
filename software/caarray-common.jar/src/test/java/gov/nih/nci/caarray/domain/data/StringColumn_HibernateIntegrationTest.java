//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.assertArrayEquals;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest;

import org.junit.Before;
import org.junit.Test;

public class StringColumn_HibernateIntegrationTest extends AbstractCaArrayObject_HibernateIntegrationTest {

    private static final int NUMBER_OF_DATA_ROWS = 100;
    private QuantitationType stringType;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        stringType = new QuantitationType();
        stringType.setName("string");
        stringType.setTypeClass(String.class);
    }

    @Test
    @Override
    @SuppressWarnings("PMD")
    public void testSave() {
        super.testSave();
    }

    @Override
    protected void setValues(AbstractCaArrayObject caArrayObject) {
        StringColumn stringColumn = (StringColumn) caArrayObject;
        stringColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        setValues(stringColumn.getValues());
    }

    private void setValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = "test" + getUniqueIntValue();
        }
    }

    @Override
    protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject) {
        StringColumn original = (StringColumn) caArrayObject;
        StringColumn retrieved = (StringColumn) retrievedCaArrayObject;
        assertArrayEquals(original.getValues(), retrieved.getValues());
    }

    @Override
    protected AbstractCaArrayObject createTestObject() {
        StringColumn stringColumn = new StringColumn();
        HybridizationData hybridizationData = new HybridizationData();
        stringColumn.setHybridizationData(hybridizationData);
        hybridizationData.setDataSet(new DataSet());
        save(hybridizationData.getDataSet());
        save(hybridizationData);
        save(stringType);
        stringColumn.setQuantitationType(stringType);
        return stringColumn;
    }

    @Override
    protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject) {
        StringColumn column = (StringColumn) caArrayObject;
        column.setValues(null);
    }

}
