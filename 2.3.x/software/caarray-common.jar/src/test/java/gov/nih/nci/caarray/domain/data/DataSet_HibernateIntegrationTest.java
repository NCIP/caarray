//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

public class DataSet_HibernateIntegrationTest extends AbstractCaArrayObject_HibernateIntegrationTest {

    private static final int NUMBER_OF_DATA_ROWS = 100;
    private QuantitationType stringType;
    private QuantitationType floatType;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        stringType = new QuantitationType();
        stringType.setName("string");
        stringType.setTypeClass(String.class);
        floatType = new QuantitationType();
        floatType.setName("float");
        floatType.setTypeClass(Float.class);
    }

    @Test
    @Override
    @SuppressWarnings("PMD")
    public void testSave() {
        super.testSave();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void setValues(AbstractCaArrayObject caArrayObject) {
        DataSet dataSet = (DataSet) caArrayObject;
        StringColumn stringColumn = (StringColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(0);
        stringColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        setValues(stringColumn.getValues());
        FloatColumn floatColumn = (FloatColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(1);
        floatColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        setValues(floatColumn.getValues());

        DesignElementList del = new DesignElementList();
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
    protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject) {
        DataSet original = (DataSet) caArrayObject;
        DataSet retrieved = (DataSet) retrievedCaArrayObject;
        assertEquals(original.getQuantitationTypes().get(0).getName(), retrieved.getQuantitationTypes().get(0).getName());
        StringColumn originalColumn1 = (StringColumn) original.getHybridizationDataList().get(0).getColumns().get(0);
        StringColumn retrievedColumn1 = (StringColumn) retrieved.getHybridizationDataList().get(0).getColumns().get(0);
        assertArrayEquals(originalColumn1.getValues(), retrievedColumn1.getValues());
        assertEquals(original.getQuantitationTypes().get(1).getName(), retrieved.getQuantitationTypes().get(1).getName());
        FloatColumn originalColumn2 = (FloatColumn) original.getHybridizationDataList().get(0).getColumns().get(1);
        FloatColumn retrievedColumn2 = (FloatColumn) retrieved.getHybridizationDataList().get(0).getColumns().get(1);
        assertFloatArrayEquals(originalColumn2.getValues(), retrievedColumn2.getValues());
    }

    private void assertFloatArrayEquals(float[] expected, float[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Override
    protected AbstractCaArrayObject createTestObject() {
        Sample sample = new Sample();
        sample.setName("Foo");
        Extract extract = new Extract();
        extract.setName("Foobar");
        sample.getExtracts().add(extract);
        extract.getSamples().add(sample);
        LabeledExtract le = new LabeledExtract();
        le.setName("Foofoo");
        extract.getLabeledExtracts().add(le);
        le.getExtracts().add(extract);
        Hybridization hybridization = new Hybridization();
        hybridization.setName("Test Hyb");
        le.getHybridizations().add(hybridization);
        hybridization.getLabeledExtracts().add(le);
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().saveOrUpdate(sample);
        tx.commit();
        DataSet dataSet = new DataSet();
        dataSet.addHybridizationData(hybridization);
        dataSet.addQuantitationType(stringType);
        dataSet.addQuantitationType(floatType);
        DesignElementList del = new DesignElementList();
        del.setDesignElementTypeEnum(null);
        dataSet.setDesignElementList(del);
        return dataSet;
    }

    @Override
    protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject) {
        DataSet dataSet = (DataSet) caArrayObject;
        StringColumn stringColumn = (StringColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(0);
        FloatColumn floatColumn = (FloatColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(1);
        stringColumn.setValues(null);
        floatColumn.setValues(null);
        dataSet.setDesignElementList(null);
    }

}
