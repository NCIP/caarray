//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.net.URI;

import org.hibernate.Transaction;
import org.junit.Before;

public class DataSet_HibernateIntegrationTest extends AbstractCaArrayObject_HibernateIntegrationTest<DataSet> {
    private static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");
    private QuantitationType stringType;
    private QuantitationType floatType;

    @Before
    public void setUpQuantitationTypes() {
        this.stringType = new QuantitationType();
        this.stringType.setName("string");
        this.stringType.setTypeClass(String.class);
        this.floatType = new QuantitationType();
        this.floatType.setName("float");
        this.floatType.setTypeClass(Float.class);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void setValues(DataSet dataSet) {
        super.setValues(dataSet);
        final StringColumn stringColumn = (StringColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(0);
        // ARRAY-2282 - column not initialized by Hibernate any more.  Design revisit needed.
        // stringColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        // setValues(stringColumn.getValues());
        stringColumn.setDataHandle(DUMMY_HANDLE);
        final FloatColumn floatColumn = (FloatColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(1);
        // floatColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        // setValues(floatColumn.getValues());
        floatColumn.setDataHandle(DUMMY_HANDLE);

        final DesignElementList del = new DesignElementList();
        del.setDesignElementTypeEnum(getNextValue(DesignElementType.values(), del.getDesignElementTypeEnum()));
        del.getDesignElements().add(new Feature());
        dataSet.setDesignElementList(del);
    }

    private void setValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = "test" + getUniqueIntValue();
        }
    }

    private void setValues(float[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = getUniqueIntValue() + 0.1f;
        }
    }

    @Override
    protected void compareValues(DataSet original, DataSet retrieved) {
        super.compareValues(original, retrieved);
        assertEquals(original.getQuantitationTypes().get(0).getName(), retrieved.getQuantitationTypes().get(0)
                .getName());
        final StringColumn originalColumn1 = (StringColumn) original.getHybridizationDataList().get(0).getColumns()
                .get(0);
        final StringColumn retrievedColumn1 = (StringColumn) retrieved.getHybridizationDataList().get(0).getColumns()
                .get(0);
        // assertArrayEquals(originalColumn1.getValues(), retrievedColumn1.getValues());
        assertEquals(original.getQuantitationTypes().get(1).getName(), retrieved.getQuantitationTypes().get(1)
                .getName());
        assertEquals(originalColumn1.getDataHandle(), retrievedColumn1.getDataHandle());
        final FloatColumn originalColumn2 = (FloatColumn) original.getHybridizationDataList().get(0).getColumns()
                .get(1);
        final FloatColumn retrievedColumn2 = (FloatColumn) retrieved.getHybridizationDataList().get(0).getColumns()
                .get(1);
        // assertFloatArrayEquals(originalColumn2.getValues(), retrievedColumn2.getValues());
        assertEquals(originalColumn2.getDataHandle(), retrievedColumn2.getDataHandle());
    }

    private void assertFloatArrayEquals(float[] expected, float[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], 0.00001);
        }
    }

    @Override
    protected DataSet createTestObject() {
        final Sample sample = new Sample();
        sample.setName("Foo");
        final Extract extract = new Extract();
        extract.setName("Foobar");
        sample.getExtracts().add(extract);
        extract.getSamples().add(sample);
        final LabeledExtract le = new LabeledExtract();
        le.setName("Foofoo");
        extract.getLabeledExtracts().add(le);
        le.getExtracts().add(extract);
        final Hybridization hybridization = new Hybridization();
        hybridization.setName("Test Hyb");
        le.getHybridizations().add(hybridization);
        hybridization.getLabeledExtracts().add(le);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().saveOrUpdate(sample);
        tx.commit();
        final DataSet dataSet = new DataSet();
        dataSet.addHybridizationData(hybridization);
        dataSet.addQuantitationType(this.stringType);
        dataSet.addQuantitationType(this.floatType);
        final DesignElementList del = new DesignElementList();
        del.setDesignElementTypeEnum(null);
        dataSet.setDesignElementList(del);
        return dataSet;
    }

    @Override
    protected void setNullableValuesToNull(DataSet dataSet) {
        final StringColumn stringColumn = (StringColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(0);
        final FloatColumn floatColumn = (FloatColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(1);
        stringColumn.setValues(null);
        floatColumn.setValues(null);
        dataSet.setDesignElementList(null);
    }

}
