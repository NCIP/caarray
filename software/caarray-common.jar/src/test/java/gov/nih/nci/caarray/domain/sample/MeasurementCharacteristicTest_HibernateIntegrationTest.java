//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

/**
 * @author Jevon Gill
 * 
 */
@SuppressWarnings("PMD")
public class MeasurementCharacteristicTest_HibernateIntegrationTest extends
        AbstractCaArrayEntity_HibernateIntegrationTest<MeasurementCharacteristic> {
    @Override
    protected void setValues(MeasurementCharacteristic measurementCharacteristic) {
        super.setValues(measurementCharacteristic);
        // Set values in AbstractCharacteristic class
        final Category category = new Category();
        category.setName("Survival");
        final TermSource termSource1 = new TermSource();
        termSource1.setName("Source " + getUniqueStringValue());
        category.setSource(termSource1);
        measurementCharacteristic.setCategory(category);

        final Sample sample = new Sample();
        sample.setName("Extract 217");
        saveBiomaterial(sample);
        measurementCharacteristic.setBioMaterial(sample);

        // Set values in MeasurementCharacteristic class
        final TermSource termSource2 = new TermSource();
        termSource2.setName("Source " + getUniqueStringValue());

        final Term unit = new Term();
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest#compareValues(gov.nih.nci.caarray.domain
     * .AbstractCaArrayObject, gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    @Override
    protected void compareValues(MeasurementCharacteristic original, MeasurementCharacteristic retrieved) {
        super.compareValues(original, retrieved);
        assertEquals(original.getCategory().getName(), retrieved.getCategory().getName());
        assertEquals(original.getCategory().getSource().getName(), retrieved.getCategory().getSource().getName());
        assertEquals(original.getBioMaterial().getName(), retrieved.getBioMaterial().getName());
        assertEquals(original.getUnit().getSource().getName(), retrieved.getUnit().getSource().getName());
        assertEquals(original.getValue(), retrieved.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MeasurementCharacteristic createTestObject() {
        return new MeasurementCharacteristic();
    }
}
