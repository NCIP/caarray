//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.net.URI;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("PMD")
public class Array_HibernateIntegrationTest extends AbstractCaArrayEntity_HibernateIntegrationTest<Array> {
    private static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");

    @Override
    protected void setValues(Array array) {
        super.setValues(array);
        final TermSource ts = new TermSource();
        ts.setName("TS " + getUniqueStringValue());
        final Term term = new Term();
        term.setValue("term");
        term.setSource(ts);

        final ArrayDesign design = new ArrayDesign();
        design.setName(getUniqueStringValue());
        design.setTechnologyType(term);
        design.addDesignFile(new CaArrayFile());
        design.getFirstDesignFile().setName("File 1");
        design.getFirstDesignFile().setFileStatus(FileStatus.UPLOADED);
        design.getFirstDesignFile().setDataHandle(DUMMY_HANDLE);
        design.setVersion(getUniqueStringValue());
        design.setGeoAccession(getUniqueStringValue());
        design.setProvider(new Organization());
        final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
        final AssayType type = new AssayType();
        save(type);
        assayTypes.add(type);
        design.setAssayTypes(assayTypes);
        design.setOrganism(new Organism());
        design.getOrganism().setScientificName(getUniqueStringValue());
        design.getOrganism().setTermSource(ts);

        array.setBatch(getUniqueStringValue());
        array.setSerialNumber(getUniqueStringValue());
        array.setProduction(new ProtocolApplication());
        array.setDesign(design);
        array.setArrayGroup(new ArrayGroup());
        save(array.getArrayGroup());
    }

    @Override
    protected void compareValues(Array original, Array retrieved) {
        super.compareValues(original, retrieved);
        assertEquals(original.getBatch(), retrieved.getBatch());
        assertEquals(original.getSerialNumber(), retrieved.getSerialNumber());
        assertEquals(original.getProduction(), retrieved.getProduction());
        assertEquals(original.getDesign(), retrieved.getDesign());
        assertEquals(original.getArrayGroup(), retrieved.getArrayGroup());
    }

    @Override
    protected void setNullableValuesToNull(Array array) {
        array.setBatch(null);
        array.setSerialNumber(null);
        array.setProduction(null);
        array.setDesign(null);
        array.setArrayGroup(null);
    }

    @Override
    protected Array createTestObject() {
        return new Array();
    }
}
