//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
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
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
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
import java.util.ArrayList;
import java.util.Collection;
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
 */
@SuppressWarnings("PMD")
public class ProjectSourcesActionTest extends AbstractDownloadTest {
    private final ProjectSourcesAction action = new ProjectSourcesAction();
    private static Source DUMMY_SOURCE;
    private static int NUM_SOURCES = 2;

    private MockHttpServletResponse mockResponse;

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_SOURCE = new Source();
        DUMMY_SOURCE.setId(1L);
        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPrepare() throws Exception {
        // no current source id
        action.prepare();
        assertNull(action.getCurrentSource().getId());

        // valid current source id
        Source source = new Source();
        source.setId(1L);
        action.setCurrentSource(source);
        action.prepare();
        assertEquals(DUMMY_SOURCE, action.getCurrentSource());

        // invalid current source id
        source = new Source();
        source.setId(2L);
        action.setCurrentSource(source);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test
    public void testCopy() {
        action.setCurrentSource(DUMMY_SOURCE);
        assertEquals("list", action.copy());
    }

    @Test
    public void testDelete() {
        ServletActionContext.setRequest(new MockHttpServletRequest());
        assertEquals("list", action.delete());

        ServletActionContext.setRequest(new MockHttpServletRequest());
        DUMMY_SOURCE.getSamples().add(new Sample());
        action.setCurrentSource(DUMMY_SOURCE);
        assertEquals("list", action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noSourceData", action.download());

        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        Source so = new Source();
        Sample s1 = new Sample();
        so.getSamples().add(s1);
        Sample s2 = new Sample();
        so.getSamples().add(s2);
        Extract e = new Extract();
        s1.getExtracts().add(e);
        s2.getExtracts().add(e);
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

        action.setCurrentSource(so);
        action.setProject(p);
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(so.getAllDataFiles());
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
    public void testLoad() {
        Project p = new Project();
        Experiment e = new Experiment();
        p.setExperiment(e);
        for (int i=0; i<NUM_SOURCES; i++) {
            e.getSources().add(new Source());
        }
        action.setProject(p);
        assertEquals("list", action.load());
        assertEquals(NUM_SOURCES, action.getPagedItems().getFullListSize());
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Source.class) && entityId.equals(1L)) {
                return (T)DUMMY_SOURCE;
            }
            return null;
        }
        @Override
        public int collectionSize(Collection<? extends PersistentObject> collection) {
            return collection.size();
        }

    }
}
