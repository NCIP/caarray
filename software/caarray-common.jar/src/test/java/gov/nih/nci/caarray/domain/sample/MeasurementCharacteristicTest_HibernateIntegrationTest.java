//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import org.junit.Test;

/**
 * @author Jevon Gill
 *
 */
@SuppressWarnings("PMD")
public class MeasurementCharacteristicTest_HibernateIntegrationTest extends AbstractCaArrayEntity_HibernateIntegrationTest {

    @Test
    @Override
    public void testSave() {
        super.testSave();
    }

    @Override
    protected void setValues(AbstractCaArrayObject caArrayObject) {

        MeasurementCharacteristic measurementCharacteristic = (MeasurementCharacteristic) caArrayObject;

        //Set values in AbstractCharacteristic class
        Category category = new Category();
        category.setName("Survival");
        TermSource termSource1 = new TermSource();
        termSource1.setName("Source 1");
        category.setSource(termSource1);
        measurementCharacteristic.setCategory(category);

        Sample sample = new Sample();
        sample.setName("Extract 217");
        saveBiomaterial(sample);
        measurementCharacteristic.setBioMaterial(sample);

        //Set values in MeasurementCharacteristic class
        TermSource termSource2 = new TermSource();
        termSource2.setName("Source 1");

        Term unit = new Term();
        unit.setSource(termSource2);
        unit.setValue("month");
        measurementCharacteristic.setUnit(unit);

        measurementCharacteristic.setValue(Float.valueOf(4));
    }

    /**
     * @param sample
     */
    private void saveBiomaterial(Sample sample) {
        super.save(sample);
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest#compareValues(gov.nih.nci.caarray.domain.AbstractCaArrayObject, gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    @Override
    protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject) {
        MeasurementCharacteristic original = (MeasurementCharacteristic) caArrayObject;
        MeasurementCharacteristic retrieved = (MeasurementCharacteristic) retrievedCaArrayObject;
        assertEquals(original.getCategory().getName(), retrieved.getCategory().getName());
        assertEquals(original.getCategory().getSource().getName(), retrieved.getCategory().getSource().getName());
        assertEquals(original.getBioMaterial().getName(), retrieved.getBioMaterial().getName());
        assertEquals(original.getUnit().getSource().getName(), retrieved.getUnit().getSource().getName());
        assertEquals(original.getValue(), retrieved.getValue());
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest#createTestObject()
     */
    @Override
    protected AbstractCaArrayObject createTestObject() {
        return new MeasurementCharacteristic();
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest#setNullableValuesToNull(gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    @Override
    protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject) {
        // TODO Auto-generated method stub

    }
}
