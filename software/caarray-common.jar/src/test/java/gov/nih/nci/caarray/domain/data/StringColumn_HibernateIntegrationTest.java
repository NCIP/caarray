//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class StringColumn_HibernateIntegrationTest extends AbstractCaArrayObject_HibernateIntegrationTest<StringColumn> {
    private static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");
    private static final int NUMBER_OF_DATA_ROWS = 100;
    private QuantitationType stringType;

    @Before
    public void setUpQuantitationType() {
        this.stringType = new QuantitationType();
        this.stringType.setName("string");
        this.stringType.setTypeClass(String.class);
    }

    @Test
    @Override
    public void testSave() {
        super.testSave();
    }

    @Override
    protected void setValues(StringColumn stringColumn) {
        super.setValues(stringColumn);
        // ARRAY-2282 - column not initialized by Hibernate any more.  Design revisit needed.
        // stringColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        // setValues(stringColumn.getValues());
        stringColumn.setDataHandle(DUMMY_HANDLE);
    }

    private void setValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = "test" + getUniqueIntValue();
        }
    }

    @Override
    protected void compareValues(StringColumn original, StringColumn retrieved) {
        super.compareValues(original, retrieved);
        // assertArrayEquals(original.getValues(), retrieved.getValues());
        assertEquals(original.getDataHandle(), retrieved.getDataHandle());
    }

    @Override
    protected StringColumn createTestObject() {
        final StringColumn stringColumn = new StringColumn();
        final HybridizationData hybridizationData = new HybridizationData();
        stringColumn.setHybridizationData(hybridizationData);
        hybridizationData.setDataSet(new DataSet());
        save(hybridizationData.getDataSet());
        save(hybridizationData);
        save(this.stringType);
        stringColumn.setQuantitationType(this.stringType);
        return stringColumn;
    }

    @Override
    protected void setNullableValuesToNull(StringColumn column) {
        column.setValues(null);
    }
}
