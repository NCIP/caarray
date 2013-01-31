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
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractDownloadTest;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
public class ProjectSamplesActionTest extends AbstractDownloadTest {
    private final ProjectSamplesAction action = new ProjectSamplesAction();

    private static Sample DUMMY_SAMPLE = new Sample();
    private static Source DUMMY_SOURCE = new Source();

    private MockHttpServletResponse mockResponse;

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new LocalProjectManagementService());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_SAMPLE = new Sample();
        DUMMY_SAMPLE.setId(1L);
        DUMMY_SAMPLE.setName("Dummy Sample");
        DUMMY_SOURCE = new Source();
        DUMMY_SOURCE.setName("Dummy Source");
        ServletActionContext.setRequest(new MockHttpServletRequest());
        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPrepare() throws Exception {
        // no current sample id
        action.prepare();
        assertNull(action.getCurrentSample().getId());

        // valid current sample id
        Sample sample = new Sample();
        sample.setId(1L);
        sample.setName("sample1");
        action.setCurrentSample(sample);
        action.prepare();
        assertEquals(DUMMY_SAMPLE, action.getCurrentSample());

        //valid sample external id
        sample = new Sample();
        sample.setName("sample2");
        sample.setExternalSampleId("abc");
        action.setCurrentSample(sample);
        action.prepare();
        assertEquals("abc", action.getCurrentSample().getExternalSampleId());

        // invalid current sample id
        sample = new Sample();
        sample.setId(2L);
        sample.setName("sample3");
        action.setCurrentSample(sample);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {
            assertTrue(pde.getMessage().endsWith("id 2"));
        }
    }

    @Test
    public void testPrepareUsingExternalSampleId() throws Exception {
        // no current sample id
        action.prepare();
        assertNull(action.getCurrentSample().getId());

        //valid sample external id
        Sample sample = new Sample();
        sample.setName("sample4");
        sample.setExternalSampleId("abc");
        action.setCurrentSample(sample);
        action.prepare();
        assertEquals("abc", action.getCurrentSample().getExternalSampleId());

        // invalid current sample id
        sample = new Sample();
        sample.setName("sample5");
        sample.setExternalSampleId("def");
        action.setCurrentSample(sample);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {
            assertTrue(pde.getMessage().endsWith("externalSampleId def"));
        }
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noSampleData", action.download());

        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        Sample s = new Sample();
        Extract e = new Extract();
        s.getExtracts().add(e);
        LabeledExtract le = new LabeledExtract();
        e.getLabeledExtracts().add(le);
        Hybridization h = new Hybridization();
        le.getHybridizations().add(h);
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

        action.setCurrentSample(s);
        action.setProject(p);
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(s.getAllDataFiles());
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
    public void testCopy() {
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", action.copy());
    }

    @Test
    public void testSearchForAssociationValues() {
        assertEquals("associationValues", action.searchForAssociationValues());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSave() {
        // save new
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.created"));

        // update associations
        Source toAdd = new Source();
        toAdd.setName("source1_to_add");
        List<Source> addList = new ArrayList<Source>();
        addList.add(toAdd);
        action.setItemsToAssociate(addList);
        Source toRemove = new Source();
        toRemove.setName("source_to_remove");
        toRemove.getSamples().add(DUMMY_SAMPLE);
        List<Source> removeList = new ArrayList<Source>();
        removeList.add(toRemove);
        action.setItemsToRemove(removeList);

        // protocols
        ProtocolApplication pa = new ProtocolApplication();
        Protocol p = new Protocol();
        p.setId(1L);
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_SAMPLE.addProtocolApplication(pa);

        Protocol p2 = new Protocol();
        p2.setId(2L);
        p2.setName("protocol2");
        List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(p2);
        action.setSelectedProtocols(protocols);

        // update existing sample
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getSamples().contains(DUMMY_SAMPLE));
        assertFalse(toRemove.getSamples().contains(DUMMY_SAMPLE));
        assertEquals(p2, DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol());

        // add another protocol application
        Protocol p3 = new Protocol();
        p3.setId(3L);
        p3.setName("protocol3");
        action.getSelectedProtocols().add(p3);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertEquals(2, DUMMY_SAMPLE.getProtocolApplications().size());
        assertTrue(DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol().equals(p2)
                || DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol().equals(p3));
        assertTrue(DUMMY_SAMPLE.getProtocolApplications().get(1).getProtocol().equals(p2)
                || DUMMY_SAMPLE.getProtocolApplications().get(1).getProtocol().equals(p3));

        // take away a PA
        action.getSelectedProtocols().remove(p2);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertEquals(1, DUMMY_SAMPLE.getProtocolApplications().size());
        assertEquals(p3, DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol());

    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSaveDuplicate() {

        action.getProject().getExperiment().getSamples().add(DUMMY_SAMPLE);

        // add a sample, with duplicate name
        Sample sam = new Sample();
        sam.setName(DUMMY_SAMPLE.getName());
        sam.getSources().add(DUMMY_SOURCE);

        // try to save dup.
        action.setCurrentSample(sam);
        assertEquals(Action.INPUT, action.save());
        assertEquals(1, action.getFieldErrors().size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEditSampleRenameAsDuplicate() {

        action.getProject().getExperiment().getSamples().add(DUMMY_SAMPLE);

        // add another sample, with valid name
        Sample sam = new Sample();
        sam.setId(2L);
        sam.setName("test_name");
        sam.getSources().add(DUMMY_SOURCE);

        action.getProject().getExperiment().getSamples().add(sam);


        // try to edit sam w/ valid name.
        sam.setName("name_is_valid");
        action.setCurrentSample(sam);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));

        // try to edit sam with invalid name
        sam.setName(DUMMY_SAMPLE.getName());
        action.setCurrentSample(sam);
        assertEquals(Action.INPUT, action.save());
        assertEquals(1, action.getFieldErrors().size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEdit() {
        ProtocolApplication pa = new ProtocolApplication();
        Protocol p = new Protocol();
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_SAMPLE.getProtocolApplications().clear();
        DUMMY_SAMPLE.getProtocolApplications().add(pa);
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(Action.INPUT, action.edit());
        assertEquals(1, action.getSelectedProtocols().size());
        assertEquals(p, action.getSelectedProtocols().get(0));

        ProtocolApplication pa2 = new ProtocolApplication();
        Protocol p2 = new Protocol();
        p2.setName("protocol2");
        pa2.setProtocol(p2);
        DUMMY_SAMPLE.getProtocolApplications().add(pa2);
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(Action.INPUT, action.edit());
        assertEquals(2, action.getSelectedProtocols().size());
        assertEquals(p2, action.getSelectedProtocols().get(1));
    }

    public void testDelete() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        DUMMY_SOURCE.getSamples().add(DUMMY_SAMPLE);
        DUMMY_SAMPLE.getSources().add(DUMMY_SOURCE);
        Project p = new Project();
        setProjectForExperiment(p.getExperiment(), p);
        p.getPublicProfile().setSecurityLevel(SecurityLevel.READ_SELECTIVE);
        p.getPublicProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.READ);
        DUMMY_SAMPLE.setExperiment(p.getExperiment());
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", action.delete());
        assertFalse(DUMMY_SOURCE.getSamples().contains(DUMMY_SAMPLE));
        assertTrue(p.getPublicProfile().getSampleSecurityLevels().isEmpty());

        DUMMY_SAMPLE.getExtracts().add(new Extract());
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    private void setProjectForExperiment(Experiment e, Project p) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m = e.getClass().getDeclaredMethod("setProject", Project.class);
        m.setAccessible(true);
        m.invoke(e, p);
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Sample.class) && entityId.equals(1L)) {
                return (T)DUMMY_SAMPLE;
            }
            return null;
        }
    }

    private static class LocalProjectManagementService extends ProjectManagementServiceStub {
        @Override
        public Sample getSampleByExternalId(Project project, String externalSampleId) {
            Sample sampleByExternalId = super.getSampleByExternalId(project, externalSampleId);
            if ("abc".equals(externalSampleId)) {
                return sampleByExternalId;
            }
            return null;
        }
    }
}
