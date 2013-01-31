//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;

/**
 * @author Winston Cheng
 *
 */
public class ProjectLabeledExtractsActionTest extends AbstractCaarrayTest {
    private final ProjectLabeledExtractsAction action = new ProjectLabeledExtractsAction();

    private static LabeledExtract DUMMY_LABELED_EXTRACT = new LabeledExtract();
    private static Extract DUMMY_EXTRACT = new Extract();

    private MockHttpServletResponse mockResponse;

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_LABELED_EXTRACT.setId(1L);
        ServletActionContext.setRequest(new MockHttpServletRequest());
        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no current extract id
        action.prepare();
        assertNull(action.getCurrentLabeledExtract().getId());

        // valid current extract id
        LabeledExtract extract = new LabeledExtract();
        extract.setId(1L);
        action.setCurrentLabeledExtract(extract);
        action.prepare();
        assertEquals(DUMMY_LABELED_EXTRACT, action.getCurrentLabeledExtract());

        // invalid current extract id
        extract = new LabeledExtract();
        extract.setId(2L);
        action.setCurrentLabeledExtract(extract);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test
    public void testCopy() {
        action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals("list", action.copy());
    }

    @Test
    public void testDelete() {
        DUMMY_EXTRACT.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        DUMMY_LABELED_EXTRACT.getExtracts().add(DUMMY_EXTRACT);
        action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals("list", action.delete());
        assertFalse(DUMMY_EXTRACT.getLabeledExtracts().contains(DUMMY_LABELED_EXTRACT));

        DUMMY_LABELED_EXTRACT.getHybridizations().add(new Hybridization());
        action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals("list", action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noLabeledExtractData", action.download());

        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        LabeledExtract le = new LabeledExtract();
        Hybridization h1 = new Hybridization();
        le.getHybridizations().add(h1);
        Hybridization h2 = new Hybridization();
        le.getHybridizations().add(h2);
        RawArrayData raw = new RawArrayData();
        h1.addRawArrayData(raw);
        DerivedArrayData derived = new DerivedArrayData();
        h2.getDerivedDataCollection().add(derived);
        CaArrayFile rawFile = new CaArrayFile();
        rawFile.setName("missing_term_source.idf");
        raw.setDataFile(rawFile);
        CaArrayFile derivedFile = new CaArrayFile();
        derivedFile.setName("missing_term_source.sdrf");
        derived.setDataFile(derivedFile);

        action.setCurrentLabeledExtract(le);
        action.setProject(p);
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(le.getAllDataFiles());
        Collections.sort(files, ProjectFilesAction.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(2, files.size());
        assertEquals("missing_term_source.idf", files.get(0).getName());
        assertEquals("missing_term_source.sdrf", files.get(1).getName());

        action.download();
        assertEquals("application/zip", mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", mockResponse.getHeader("Content-disposition"));

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
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
        Extract toAdd = new Extract();
        List<Extract> addList = new ArrayList<Extract>();
        addList.add(toAdd);
        action.setItemsToAssociate(addList);

        Extract toRemove = new Extract();
        toRemove.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        List<Extract> removeList = new ArrayList<Extract>();
        removeList.add(toRemove);
        action.setItemsToRemove(removeList);

        action.setCurrentLabeledExtract(DUMMY_LABELED_EXTRACT);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getLabeledExtracts().contains(DUMMY_LABELED_EXTRACT));
        assertFalse(toRemove.getLabeledExtracts().contains(DUMMY_LABELED_EXTRACT));
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(LabeledExtract.class) && entityId.equals(1L)) {
                return (T)DUMMY_LABELED_EXTRACT;
            }
            return null;
        }
    }
}
