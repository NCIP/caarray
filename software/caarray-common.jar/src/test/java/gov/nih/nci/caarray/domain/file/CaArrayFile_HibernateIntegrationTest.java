//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ServiceType;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;

import org.junit.Test;

public class CaArrayFile_HibernateIntegrationTest extends AbstractCaArrayEntity_HibernateIntegrationTest {

    @Test
    @Override
    @SuppressWarnings("PMD")
    public void testSave() {
        super.testSave();
    }

    @Override
    protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject) {
        CaArrayFile original = (CaArrayFile) caArrayObject;
        CaArrayFile retrieved = (CaArrayFile) retrievedCaArrayObject;
        assertEquals(original.getFileStatus(), retrieved.getFileStatus());
        assertEquals(original.getName(), retrieved.getName());
        assertEquals(original.getProject(), retrieved.getProject());
        assertEquals(original.getStatus(), retrieved.getStatus());
        assertEquals(original.getFileType(), retrieved.getFileType());
        assertEquals(original.getValidationResult(), retrieved.getValidationResult());
    }

    @Override
    protected void setValues(AbstractCaArrayObject caArrayObject) {
        CaArrayFile caArrayFile = (CaArrayFile) caArrayObject;
        caArrayFile.setStatus(getNextValue(FileStatus.values(), caArrayFile.getFileStatus()).name());
        caArrayFile.setName(getUniqueStringValue());
        if (caArrayFile.getProject() == null) {
            TermSource ts = new TermSource();
            ts.setName("Dummy TS");
            ts.setVersion("1.0");
            Organism org = new Organism();
            org.setScientificName("Foo");
            org.setTermSource(ts);
            caArrayFile.setProject(new Project());
            caArrayFile.getProject().getExperiment().setTitle("TestFileExperiment1");
            caArrayFile.getProject().getExperiment().setAssayTypeEnum(AssayType.ACGH);
            caArrayFile.getProject().getExperiment().setServiceType(ServiceType.FULL);
            caArrayFile.getProject().getExperiment().setOrganism(org);
            caArrayFile.getProject().getExperiment().setManufacturer(new Organization());
            save(caArrayFile.getProject());
        }
        caArrayFile.setFileType(FileType.AFFYMETRIX_CDF);
        caArrayFile.setValidationResult(new FileValidationResult(new File(caArrayFile.getName())));
    }

    @Override
    protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject) {
        CaArrayFile caArrayFile = (CaArrayFile) caArrayObject;
        caArrayFile.setFileType(null);
        caArrayFile.setValidationResult(null);
    }

    @Override
    protected AbstractCaArrayObject createTestObject() {
        return new CaArrayFile();
    }

}
