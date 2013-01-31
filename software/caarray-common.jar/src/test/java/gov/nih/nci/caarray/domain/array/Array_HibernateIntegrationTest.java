//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import org.junit.Test;

@SuppressWarnings("PMD")
public class Array_HibernateIntegrationTest extends AbstractCaArrayEntity_HibernateIntegrationTest {

    @Test
    @Override
    public void testSave() {
        super.testSave();
    }

    @Override
    protected void setValues(AbstractCaArrayObject caArrayObject) {
        TermSource ts = new TermSource();
        ts.setName("TS 1");
        Term term = new Term();
        term.setValue("term");
        term.setSource(ts);
        
        ArrayDesign design = new ArrayDesign();
        design.setName(getUniqueStringValue());
        design.setTechnologyType(term);
        design.addDesignFile(new CaArrayFile());
        design.setVersion(getUniqueStringValue());
        design.setProvider(new Organization());
        design.setAssayTypeEnum(getNextValue(AssayType.values(), design.getAssayTypeEnum()));
        design.setOrganism(new Organism());
        design.getOrganism().setScientificName(getUniqueStringValue());
        design.getOrganism().setTermSource(ts);
        
        Array array = (Array) caArrayObject;
        array.setBatch(getUniqueStringValue());
        array.setSerialNumber(getUniqueStringValue());
        array.setProduction(new ProtocolApplication());
        array.setDesign(design);
        array.setArrayGroup(new ArrayGroup());
        save(array.getArrayGroup());
    }

    @Override
    protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject) {
        Array original = (Array) caArrayObject;
        Array retrieved = (Array) retrievedCaArrayObject;
        assertEquals(original.getBatch(), retrieved.getBatch());
        assertEquals(original.getSerialNumber(), retrieved.getSerialNumber());
        assertEquals(original.getProduction(), retrieved.getProduction());
        assertEquals(original.getDesign(), retrieved.getDesign());
        assertEquals(original.getArrayGroup(), retrieved.getArrayGroup());
    }

    @Override
    protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject) {
        Array array = (Array) caArrayObject;
        array.setBatch(null);
        array.setSerialNumber(null);
        array.setProduction(null);
        array.setDesign(null);
        array.setArrayGroup(null);
    }

    @Override
    protected AbstractCaArrayObject createTestObject() {
        return new Array();
    }

}
