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
import gov.nih.nci.caarray.domain.sample.Sample;
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
public class ProjectExtractsActionTest extends AbstractDownloadTest {
    private final ProjectExtractsAction action = new ProjectExtractsAction();

    private static Extract DUMMY_EXTRACT = new Extract();
    private static Sample DUMMY_SAMPLE = new Sample();

    private MockHttpServletResponse mockResponse;

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_EXTRACT.setId(1L);
        ServletActionContext.setRequest(new MockHttpServletRequest());
        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no current extract id
        action.prepare();
        assertNull(action.getCurrentExtract().getId());

        // valid current extract id
        Extract extract = new Extract();
        extract.setId(1L);
        action.setCurrentExtract(extract);
        action.prepare();
        assertEquals(DUMMY_EXTRACT, action.getCurrentExtract());

        // invalid current extract id
        extract = new Extract();
        extract.setId(2L);
        action.setCurrentExtract(extract);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test
    public void testCopy() {
        action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals("list", action.copy());
    }

    @Test
    public void testDelete() {
        DUMMY_SAMPLE.getExtracts().add(DUMMY_EXTRACT);
        DUMMY_EXTRACT.getSamples().add(DUMMY_SAMPLE);
        action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals("list", action.delete());
        assertFalse(DUMMY_SAMPLE.getExtracts().contains(DUMMY_EXTRACT));

        DUMMY_EXTRACT.getLabeledExtracts().add(new LabeledExtract());
        action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals("list", action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    @Test
    public void testDownload() throws Exception {

        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        Extract e1 = new Extract();
        Extract e2 = new Extract();
        LabeledExtract le1 = new LabeledExtract();
        e1.getLabeledExtracts().add(le1);
        LabeledExtract le2 = new LabeledExtract();
        e2.getLabeledExtracts().add(le2);
        Hybridization h = new Hybridization();
        le2.getHybridizations().add(h);
        RawArrayData raw = new RawArrayData();
        h.addRawArrayData(raw);
        DerivedArrayData derived = new DerivedArrayData();
        h.getDerivedDataCollection().add(derived);
        CaArrayFile rawFile = new CaArrayFile();
        rawFile.setName("missing_term_source.idf");
        raw.setDataFile(rawFile);
        CaArrayFile derivedFile = new CaArrayFile();
        derivedFile.setName("missing_term_source.sdrf");
        derived.setDataFile(derivedFile);

        action.setCurrentExtract(e1);
        action.setProject(p);
        assertEquals("noExtractData", action.download());
        action.setCurrentExtract(e2);

        List<CaArrayFile> files = new ArrayList<CaArrayFile>(e2.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
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
        Sample toAdd = new Sample();
        List<Sample> addList = new ArrayList<Sample>();
        addList.add(toAdd);
        action.setItemsToAssociate(addList);

        Sample toRemove = new Sample();
        toRemove.getExtracts().add(DUMMY_EXTRACT);
        List<Sample> removeList = new ArrayList<Sample>();
        removeList.add(toRemove);
        action.setItemsToRemove(removeList);

        action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getExtracts().contains(DUMMY_EXTRACT));
        assertFalse(toRemove.getExtracts().contains(DUMMY_EXTRACT));
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Extract.class) && entityId.equals(1L)) {
                return (T)DUMMY_EXTRACT;
            }
            return null;
        }
    }
}
