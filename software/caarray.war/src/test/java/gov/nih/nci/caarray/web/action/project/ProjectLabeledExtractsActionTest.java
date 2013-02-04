//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractDownloadTest;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;

/**
 * @author Winston Cheng
 * 
 */
public class ProjectLabeledExtractsActionTest extends AbstractDownloadTest {
    private final ProjectLabeledExtractsAction action = new ProjectLabeledExtractsAction();

    private static LabeledExtract DUMMY_LABELED_EXTRACT = new LabeledExtract();
    private static Extract DUMMY_EXTRACT = new Extract();

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_LABELED_EXTRACT.setId(1L);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no current extract id
        this.action.prepare();
        assertNull(this.action.getCurrentLabeledExtract().getId());

        // valid current extract id
        LabeledExtract extract = new LabeledExtract();
        extract.setId(1L);
        this.action.setCurrentLabeledExtract(extract);
        this.action.prepare();
        assertEquals(DUMMY_LABELED_EXTRACT, this.action.getCurrentLabeledExtract());

        // invalid current extract id
        extract = new LabeledExtract();
        extract.setId(2L);
        this.action.setCurrentLabeledExtract(extract);
        try {
            this.action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (final PermissionDeniedException pde) {
        }
    }

    @Test
    public void testCopy() {
        this.action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals("list", this.action.copy());
    }

    @Test
    public void testDelete() {
        DUMMY_EXTRACT.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        DUMMY_LABELED_EXTRACT.getExtracts().add(DUMMY_EXTRACT);
        this.action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals("list", this.action.delete());
        assertFalse(DUMMY_EXTRACT.getLabeledExtracts().contains(DUMMY_LABELED_EXTRACT));

        DUMMY_LABELED_EXTRACT.getHybridizations().add(new Hybridization());
        this.action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals("list", this.action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noLabeledExtractData", this.action.download());

        final CaArrayFile rawFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile derivedFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        final LabeledExtract le = new LabeledExtract();
        final Hybridization h1 = new Hybridization();
        le.getHybridizations().add(h1);
        final Hybridization h2 = new Hybridization();
        le.getHybridizations().add(h2);
        final RawArrayData raw = new RawArrayData();
        h1.addArrayData(raw);
        final DerivedArrayData derived = new DerivedArrayData();
        h2.getDerivedDataCollection().add(derived);
        raw.setDataFile(rawFile);
        derived.setDataFile(derivedFile);

        this.action.setCurrentLabeledExtract(le);
        this.action.setProject(p);
        final List<CaArrayFile> files = new ArrayList<CaArrayFile>(le.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(2, files.size());
        assertEquals("missing_term_source.idf", files.get(0).getName());
        assertEquals("missing_term_source.sdrf", files.get(1).getName());

        this.action.download();
        assertEquals("application/zip", this.mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", this.mockResponse.getHeader("Content-disposition"));

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
                this.mockResponse.getContentAsByteArray()));
        ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.idf", ze.getName());
        ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.sdrf", ze.getName());
        assertNull(zis.getNextEntry());
        IOUtils.closeQuietly(zis);
    }

    @Test
    public void testSave() {
        final Extract toAdd = new Extract();
        final List<Extract> addList = new ArrayList<Extract>();
        addList.add(toAdd);
        this.action.setItemsToAssociate(addList);

        final Extract toRemove = new Extract();
        toRemove.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        final List<Extract> removeList = new ArrayList<Extract>();
        removeList.add(toRemove);
        this.action.setItemsToRemove(removeList);

        this.action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getLabeledExtracts().contains(DUMMY_LABELED_EXTRACT));
        assertFalse(toRemove.getLabeledExtracts().contains(DUMMY_LABELED_EXTRACT));
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(LabeledExtract.class) && entityId.equals(1L)) {
                return (T) DUMMY_LABELED_EXTRACT;
            }
            return null;
        }
    }
}
