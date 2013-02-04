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
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
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
import org.junit.Before;
import org.junit.Test;

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

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new LocalProjectManagementService());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_SAMPLE = new Sample();
        DUMMY_SAMPLE.setId(1L);
        DUMMY_SAMPLE.setName("Dummy Sample");
        DUMMY_SOURCE = new Source();
        DUMMY_SOURCE.setName("Dummy Source");
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPrepare() throws Exception {
        // no current sample id
        this.action.prepare();
        assertNull(this.action.getCurrentSample().getId());

        // valid current sample id
        Sample sample = new Sample();
        sample.setId(1L);
        sample.setName("sample1");
        this.action.setCurrentSample(sample);
        this.action.prepare();
        assertEquals(DUMMY_SAMPLE, this.action.getCurrentSample());

        // valid sample external id
        sample = new Sample();
        sample.setName("sample2");
        sample.setExternalId("abc");
        this.action.setCurrentSample(sample);
        this.action.prepare();
        assertEquals("abc", this.action.getCurrentSample().getExternalId());

        // invalid current sample id
        sample = new Sample();
        sample.setId(2L);
        sample.setName("sample3");
        this.action.setCurrentSample(sample);
        try {
            this.action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (final PermissionDeniedException pde) {
            assertTrue(pde.getMessage().endsWith("id 2"));
        }
    }

    @Test
    public void testPrepareUsingExternalSampleId() throws Exception {
        // no current sample id
        this.action.prepare();
        assertNull(this.action.getCurrentSample().getId());

        // valid sample external id
        Sample sample = new Sample();
        sample.setName("sample4");
        sample.setExternalId("abc");
        this.action.setCurrentSample(sample);
        this.action.prepare();
        assertEquals("abc", this.action.getCurrentSample().getExternalId());

        // invalid current sample id
        sample = new Sample();
        sample.setName("sample5");
        sample.setExternalId("def");
        this.action.setCurrentSample(sample);
        try {
            this.action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (final PermissionDeniedException pde) {
            assertTrue(pde.getMessage().endsWith("external id def"));
        }
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noSampleData", this.action.download());

        final CaArrayFile rawFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile derivedFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        final Sample s = new Sample();
        final Extract e = new Extract();
        s.getExtracts().add(e);
        final LabeledExtract le = new LabeledExtract();
        e.getLabeledExtracts().add(le);
        final Hybridization h = new Hybridization();
        le.getHybridizations().add(h);
        final RawArrayData raw = new RawArrayData();
        h.addArrayData(raw);
        final DerivedArrayData derived = new DerivedArrayData();
        h.getDerivedDataCollection().add(derived);
        raw.setDataFile(rawFile);
        derived.setDataFile(derivedFile);

        this.action.setCurrentSample(s);
        this.action.setProject(p);
        final List<CaArrayFile> files = new ArrayList<CaArrayFile>(s.getAllDataFiles());
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
    public void testCopy() {
        this.action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", this.action.copy());
    }

    @Test
    public void testSearchForAssociationValues() {
        assertEquals("associationValues", this.action.searchForAssociationValues());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSave() {
        // save new
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.created"));

        // update associations
        final Source toAdd = new Source();
        toAdd.setName("source1_to_add");
        final List<Source> addList = new ArrayList<Source>();
        addList.add(toAdd);
        this.action.setItemsToAssociate(addList);
        final Source toRemove = new Source();
        toRemove.setName("source_to_remove");
        toRemove.getSamples().add(DUMMY_SAMPLE);
        final List<Source> removeList = new ArrayList<Source>();
        removeList.add(toRemove);
        this.action.setItemsToRemove(removeList);

        // protocols
        final ProtocolApplication pa = new ProtocolApplication();
        final Protocol p = new Protocol();
        p.setId(1L);
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_SAMPLE.addProtocolApplication(pa);

        final Protocol p2 = new Protocol();
        p2.setId(2L);
        p2.setName("protocol2");
        final List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(p2);
        this.action.setSelectedProtocols(protocols);

        // update existing sample
        this.action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getSamples().contains(DUMMY_SAMPLE));
        assertFalse(toRemove.getSamples().contains(DUMMY_SAMPLE));
        assertEquals(p2, DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol());

        // add another protocol application
        final Protocol p3 = new Protocol();
        p3.setId(3L);
        p3.setName("protocol3");
        this.action.getSelectedProtocols().add(p3);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertEquals(2, DUMMY_SAMPLE.getProtocolApplications().size());
        assertTrue(DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol().equals(p2)
                || DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol().equals(p3));
        assertTrue(DUMMY_SAMPLE.getProtocolApplications().get(1).getProtocol().equals(p2)
                || DUMMY_SAMPLE.getProtocolApplications().get(1).getProtocol().equals(p3));

        // take away a PA
        this.action.getSelectedProtocols().remove(p2);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertEquals(1, DUMMY_SAMPLE.getProtocolApplications().size());
        assertEquals(p3, DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol());

    }

    @Test
    public void testSaveDuplicate() {

        this.action.getProject().getExperiment().getSamples().add(DUMMY_SAMPLE);

        // add a sample, with duplicate name
        final Sample sam = new Sample();
        sam.setName(DUMMY_SAMPLE.getName());
        sam.getSources().add(DUMMY_SOURCE);

        // try to save dup.
        this.action.setCurrentSample(sam);
        assertEquals(Action.INPUT, this.action.save());
        assertEquals(1, this.action.getFieldErrors().size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEditSampleRenameAsDuplicate() {

        this.action.getProject().getExperiment().getSamples().add(DUMMY_SAMPLE);

        // add another sample, with valid name
        final Sample sam = new Sample();
        sam.setId(2L);
        sam.setName("test_name");
        sam.getSources().add(DUMMY_SOURCE);

        this.action.getProject().getExperiment().getSamples().add(sam);

        // try to edit sam w/ valid name.
        sam.setName("name_is_valid");
        this.action.setCurrentSample(sam);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));

        // try to edit sam with invalid name
        sam.setName(DUMMY_SAMPLE.getName());
        this.action.setCurrentSample(sam);
        assertEquals(Action.INPUT, this.action.save());
        assertEquals(1, this.action.getFieldErrors().size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEdit() {
        final ProtocolApplication pa = new ProtocolApplication();
        final Protocol p = new Protocol();
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_SAMPLE.getProtocolApplications().clear();
        DUMMY_SAMPLE.getProtocolApplications().add(pa);
        this.action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(Action.INPUT, this.action.edit());
        assertEquals(1, this.action.getSelectedProtocols().size());
        assertEquals(p, this.action.getSelectedProtocols().get(0));

        final ProtocolApplication pa2 = new ProtocolApplication();
        final Protocol p2 = new Protocol();
        p2.setName("protocol2");
        pa2.setProtocol(p2);
        DUMMY_SAMPLE.getProtocolApplications().add(pa2);
        this.action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(Action.INPUT, this.action.edit());
        assertEquals(2, this.action.getSelectedProtocols().size());
        assertEquals(p2, this.action.getSelectedProtocols().get(1));
    }

    public void testDelete() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        DUMMY_SOURCE.getSamples().add(DUMMY_SAMPLE);
        DUMMY_SAMPLE.getSources().add(DUMMY_SOURCE);
        final Project p = new Project();
        setProjectForExperiment(p.getExperiment(), p);
        p.getPublicProfile().setSecurityLevel(SecurityLevel.READ_SELECTIVE);
        p.getPublicProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.READ);
        DUMMY_SAMPLE.setExperiment(p.getExperiment());
        this.action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", this.action.delete());
        assertFalse(DUMMY_SOURCE.getSamples().contains(DUMMY_SAMPLE));
        assertTrue(p.getPublicProfile().getSampleSecurityLevels().isEmpty());

        DUMMY_SAMPLE.getExtracts().add(new Extract());
        this.action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", this.action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    private void setProjectForExperiment(Experiment e, Project p) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final Method m = e.getClass().getDeclaredMethod("setProject", Project.class);
        m.setAccessible(true);
        m.invoke(e, p);
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Sample.class) && entityId.equals(1L)) {
                return (T) DUMMY_SAMPLE;
            }
            return null;
        }
    }

    private static class LocalProjectManagementService extends ProjectManagementServiceStub {
        @Override
        public <T extends AbstractBioMaterial> T getBiomaterialByExternalId(Project project, String externalId,
                Class<T> biomaterialClass) {
            final T bmByExternalId = super.getBiomaterialByExternalId(project, externalId, biomaterialClass);
            if ("abc".equals(externalId)) {
                return bmByExternalId;
            }
            return null;
        }
    }
}
