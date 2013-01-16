//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

/**
 * Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Test
    public void testSaveArrayDesign() throws Exception {
        final File file = new File("dummyfile.txt");
        final ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design);
        assertEquals(TEST_DESIGN_NAME, design.getName());
        assertEquals(TEST_LSID.getAuthority(), design.getLsidAuthority());
        assertEquals(TEST_LSID.getNamespace(), design.getLsidNamespace());
        assertEquals(TEST_LSID.getObjectId(), design.getLsidObjectId());
        assertNull(design.getDescription());

        design.setDescription("new description");
        this.arrayDesignService.saveArrayDesign(design);
        final ArrayDesign updatedDesign = this.arrayDesignService.getArrayDesign(design.getId());
        assertEquals(TEST_DESIGN_NAME, design.getName());
        assertEquals(TEST_LSID.getAuthority(), design.getLsidAuthority());
        assertEquals(TEST_LSID.getNamespace(), design.getLsidNamespace());
        assertEquals(TEST_LSID.getObjectId(), design.getLsidObjectId());
        assertEquals("new description", updatedDesign.getDescription());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSaveLockedArrayDesignAllowedFields() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // "lock" this design by setting the ID to 2 but can still update description of locked designs
        design.setId(2L);
        this.caArrayDaoFactoryStub.getArrayDao().save(design);
        this.arrayDesignService.importDesign(design);

        design.setDescription("another description");
        design = this.arrayDesignService.saveArrayDesign(design);
        assertEquals(2L, design.getId().longValue());
        final ArrayDesign updatedLockedDesign = this.arrayDesignService.getArrayDesign(design.getId());
        assertEquals(TEST_DESIGN_NAME, design.getName());
        assertEquals(TEST_LSID.getAuthority(), updatedLockedDesign.getLsidAuthority());
        assertEquals(TEST_LSID.getNamespace(), updatedLockedDesign.getLsidNamespace());
        assertEquals(TEST_LSID.getObjectId(), updatedLockedDesign.getLsidObjectId());
        assertEquals("another description", updatedLockedDesign.getDescription());
    }

    @Test(expected = IllegalAccessException.class)
    public void testSaveArrayDesignWhileImporting() throws Exception {
        final File file = new File("dummyfile.txt");
        final ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design);

        design.getFirstDesignFile().setFileStatus(FileStatus.IMPORTING);
        design.setName("new name");
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected = IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeOrganization() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        design.setId(2L);
        design.setProvider(new Organization());
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected = IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeAssayType() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        design.setId(2L);
        final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE);
        design.setAssayTypes(assayTypes);
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected = IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeDesignFile() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        design.setId(2L);
        design.getDesignFiles().clear();
        design.addDesignFile(new CaArrayFile());
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test
    public void testValidateDesign_InvalidFileType() {
        final File file = new File("dummyfile.txt");
        CaArrayFile invalidDesignFile = getCaArrayFile(file, TEST_NONDESIGN_TYPE);
        ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(invalidDesignFile));
        assertFalse(result.isValid());
        invalidDesignFile = getCaArrayFile(file, null);
        result = this.arrayDesignService.validateDesign(Collections.singleton(invalidDesignFile));
        assertFalse(result.isValid());
    }

    @Test
    public void testDuplicateArrayDesign() {
        final File file = new File("dummyfile.txt");
        final ArrayDesign design = createDesign(DUMMY_ORGANIZATION, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design);

        final ArrayDesign design2 = createDesign(DUMMY_ORGANIZATION, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design2);
        final ValidationResult result = this.arrayDesignService.validateDesign(design2);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().iterator().next().getMessage().contains("design already exists with the name"));
    }

    @Test
    public void testOrganizations() {
        assertEquals(0, this.arrayDesignService.getAllProviders().size());
        final Organization o = new Organization();
        o.setName("Foo");
        o.setProvider(true);
        this.caArrayDaoFactoryStub.getSearchDao().save(o);
        assertEquals(1, this.arrayDesignService.getAllProviders().size());
        assertEquals("Foo", this.arrayDesignService.getAllProviders().get(0).getName());
    }
}
