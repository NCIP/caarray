//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial;
import gov.nih.nci.caarray.magetab.sdrf.Characteristic;
import gov.nih.nci.caarray.magetab.sdrf.Sample;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for an SDRf biomaterial.
 *
 * @author Rashmi Srinivasa
 */
public class AbstractBioMaterialTest extends AbstractCaarrayTest {
    private static final String TERM_CHARACTERISTIC_CATEGORY = "ClinicalDiagnosis";
    private static final String MEASUREMENT_CHARACTERISTIC_CATEGORY = "SurvivalTime";
    private static final String TERM_CHARACTERISTIC_VALUE = "Glioblastoma";
    private static final String MEASUREMENT_CHARACTERISTIC_VALUE = "14";

    private AbstractBioMaterial biomaterial;

    /**
     * Creates a biomaterial with different types of characteristics.
     */
    @Before
    public void setup(){
        biomaterial = new Sample();

        // Term-based characteristic
        Characteristic termBasedCharacteristic = new Characteristic();
        termBasedCharacteristic.setCategory(TERM_CHARACTERISTIC_CATEGORY);
        OntologyTerm term = new OntologyTerm();
        TermSource mo = new TermSource("MGED Ontology");
        term.setCategory(TERM_CHARACTERISTIC_CATEGORY);
        term.setValue(TERM_CHARACTERISTIC_VALUE);
        term.setTermSource(mo);
        termBasedCharacteristic.setTerm(term);

        // Measurement characteristic
        Characteristic measurementCharacteristic = new Characteristic();
        measurementCharacteristic.setCategory(MEASUREMENT_CHARACTERISTIC_CATEGORY);
        measurementCharacteristic.setValue(MEASUREMENT_CHARACTERISTIC_VALUE);
        OntologyTerm unit = new OntologyTerm();
        unit.setCategory("TermUnit");
        unit.setValue("weeks");
        term.setTermSource(mo);
        measurementCharacteristic.setUnit(unit);

        biomaterial.getCharacteristics().add(termBasedCharacteristic);
        biomaterial.getCharacteristics().add(measurementCharacteristic);
    }

    /**
     * Tests retrieval of all characteristics of a biomaterial.
     */
    @Test
    public void testGetCharacteristics() {
        List<Characteristic> characteristics = biomaterial.getCharacteristics();
        assertEquals(2, characteristics.size());
    }

    /**
     * Tests getting the values of term-based and measurement characteristics.
     */
    @Test
    public void testGetCharacteristicValue() {
        assertEquals(TERM_CHARACTERISTIC_VALUE, biomaterial.getCharacteristicValue(TERM_CHARACTERISTIC_CATEGORY));
        assertEquals(MEASUREMENT_CHARACTERISTIC_VALUE, biomaterial.getCharacteristicValue(MEASUREMENT_CHARACTERISTIC_CATEGORY));
    }

    /**
     * Tests getting a term-based and a measurement characteristic.
     */
    @Test
    public void testGetCharacteristic() {
        Characteristic termBasedCharacteristic = biomaterial.getCharacteristic(TERM_CHARACTERISTIC_CATEGORY);
        assertEquals(TERM_CHARACTERISTIC_VALUE, termBasedCharacteristic.getValue());
        Characteristic measurementCharacteristic = biomaterial.getCharacteristic(MEASUREMENT_CHARACTERISTIC_CATEGORY);
        assertEquals(MEASUREMENT_CHARACTERISTIC_VALUE, measurementCharacteristic.getValue());
        assertNotNull(measurementCharacteristic.getUnit());
    }
}
