//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.file;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.net.URI;
import java.util.SortedSet;
import java.util.TreeSet;

public class CaArrayFile_HibernateIntegrationTest extends AbstractCaArrayEntity_HibernateIntegrationTest<CaArrayFile> {
    private static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");

    @Override
    protected void compareValues(CaArrayFile original, CaArrayFile retrieved) {
        super.compareValues(original, retrieved);
        assertEquals(original.getFileStatus(), retrieved.getFileStatus());
        assertEquals(original.getName(), retrieved.getName());
        assertEquals(original.getProject(), retrieved.getProject());
        assertEquals(original.getStatus(), retrieved.getStatus());
        assertEquals(original.getFileType(), retrieved.getFileType());
        assertEquals(original.getValidationResult(), retrieved.getValidationResult());
        assertEquals(original.getDataHandle(), retrieved.getDataHandle());
    }

    @Override
    protected void setValues(CaArrayFile caArrayFile) {
        super.setValues(caArrayFile);
        caArrayFile.setFileStatus(getNextValue(FileStatus.values(), caArrayFile.getFileStatus()));
        caArrayFile.setName(getUniqueStringValue());
        if (caArrayFile.getProject() == null) {
            final TermSource ts = new TermSource();
            ts.setName("Dummy TS");
            ts.setVersion("1.0");
            final Organism org = new Organism();
            org.setScientificName("Foo");
            org.setTermSource(ts);
            Project p = new Project();
            Experiment e = p.getExperiment();
            e.setProject(p);
            caArrayFile.setProject(p);
            e.setTitle("TestFileExperiment1");
            final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
            final AssayType assayType = new AssayType("aCGH");
            assayTypes.add(assayType);
            save(assayType);
            e.setAssayTypes(assayTypes);
            e.setOrganism(org);
            e.setManufacturer(new Organization());
            save(p);
        }
        caArrayFile.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        caArrayFile.setDataHandle(DUMMY_HANDLE);
        caArrayFile.setValidationResult(new FileValidationResult());
    }

    @Override
    protected void setNullableValuesToNull(CaArrayFile caArrayFile) {
        caArrayFile.setFileType(null);
        caArrayFile.setValidationResult(null);
    }

    @Override
    protected CaArrayFile createTestObject() {
        return new CaArrayFile();
    }

}
