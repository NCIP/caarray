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
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
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
public class ProjectHybridizationsActionTest extends AbstractDownloadTest {
    private final ProjectHybridizationsAction action = new ProjectHybridizationsAction();

    private static Hybridization DUMMY_HYBRIDIZATION = new Hybridization();
    private static LabeledExtract DUMMY_LABELED_EXTRACT = new LabeledExtract();

    private MockHttpServletResponse mockResponse;

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_HYBRIDIZATION.setId(1L);
        ServletActionContext.setRequest(new MockHttpServletRequest());
        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no current hybridization id
        action.prepare();
        assertNull(action.getCurrentHybridization().getId());

        // valid current hybridization id
        Hybridization hybridization = new Hybridization();
        hybridization.setId(1L);
        action.setCurrentHybridization(hybridization);
        action.prepare();
        assertEquals(DUMMY_HYBRIDIZATION, action.getCurrentHybridization());

        // invalid current hybridization id
        hybridization = new Hybridization();
        hybridization.setId(2L);
        action.setCurrentHybridization(hybridization);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noHybData", action.download());

        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        Hybridization h1 = new Hybridization();
        Hybridization h2 = new Hybridization();
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

        action.setCurrentHybridization(h1);
        action.setProject(p);
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(h1.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(1, files.size());
        assertEquals("missing_term_source.idf", files.get(0).getName());
        files = new ArrayList<CaArrayFile>(h2.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(1, files.size());
        assertEquals("missing_term_source.sdrf", files.get(0).getName());

        assertEquals(null, action.download());
        assertEquals("application/zip", mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", mockResponse.getHeader("Content-disposition"));

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
        ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.idf", ze.getName());
        ze = zis.getNextEntry();
        assertNull(zis.getNextEntry());
        IOUtils.closeQuietly(zis);
    }

    @Test
    public void testCopy() {
        assertEquals("notYetImplemented", action.copy());
    }

    @Test
    public void testDelete() {
        DUMMY_LABELED_EXTRACT.getHybridizations().add(DUMMY_HYBRIDIZATION);
        DUMMY_HYBRIDIZATION.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        action.setCurrentHybridization(DUMMY_HYBRIDIZATION);
        assertEquals("list", action.delete());
        assertFalse(DUMMY_LABELED_EXTRACT.getHybridizations().contains(DUMMY_HYBRIDIZATION));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSave() {
        LabeledExtract toAdd = new LabeledExtract();
        List<LabeledExtract> addList = new ArrayList<LabeledExtract>();
        addList.add(toAdd);
        action.setItemsToAssociate(addList);

        LabeledExtract toRemove = new LabeledExtract();
        toRemove.getHybridizations().add(DUMMY_HYBRIDIZATION);
        List<LabeledExtract> removeList = new ArrayList<LabeledExtract>();
        removeList.add(toRemove);
        action.setItemsToRemove(removeList);

        ProtocolApplication pa = new ProtocolApplication();
        Protocol p = new Protocol();
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_HYBRIDIZATION.addProtocolApplication(pa);

        Protocol p2 = new Protocol();
        p2.setName("protocol2");
        List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(p2);
        action.setSelectedProtocols(protocols);

        action.setCurrentHybridization(DUMMY_HYBRIDIZATION);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getHybridizations().contains(DUMMY_HYBRIDIZATION));
        assertFalse(toRemove.getHybridizations().contains(DUMMY_HYBRIDIZATION));
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Hybridization.class) && entityId.equals(1L)) {
                return (T)DUMMY_HYBRIDIZATION;
            }
            return null;
        }
    }
}
