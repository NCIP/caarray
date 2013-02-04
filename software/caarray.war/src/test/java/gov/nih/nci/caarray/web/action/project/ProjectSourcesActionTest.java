//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
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
import org.junit.Before;
import org.junit.Test;

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

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_SOURCE = new Source();
        DUMMY_SOURCE.setId(1L);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPrepare() throws Exception {
        // no current source id
        this.action.prepare();
        assertNull(this.action.getCurrentSource().getId());

        // valid current source id
        Source source = new Source();
        source.setId(1L);
        this.action.setCurrentSource(source);
        this.action.prepare();
        assertEquals(DUMMY_SOURCE, this.action.getCurrentSource());

        // invalid current source id
        source = new Source();
        source.setId(2L);
        this.action.setCurrentSource(source);
        try {
            this.action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (final PermissionDeniedException pde) {
        }
    }

    @Test
    public void testCopy() {
        this.action.setCurrentSource(DUMMY_SOURCE);
        assertEquals("list", this.action.copy());
    }

    @Test
    public void testDelete() {
        assertEquals("list", this.action.delete());
        DUMMY_SOURCE.getSamples().add(new Sample());
        this.action.setCurrentSource(DUMMY_SOURCE);
        assertEquals("list", this.action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noSourceData", this.action.download());

        final CaArrayFile rawFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile derivedFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        final Source so = new Source();
        final Sample s1 = new Sample();
        so.getSamples().add(s1);
        final Sample s2 = new Sample();
        so.getSamples().add(s2);
        final Extract e = new Extract();
        s1.getExtracts().add(e);
        s2.getExtracts().add(e);
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

        this.action.setCurrentSource(so);
        this.action.setProject(p);
        final List<CaArrayFile> files = new ArrayList<CaArrayFile>(so.getAllDataFiles());
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
    public void testLoad() {
        final Project p = new Project();
        final Experiment e = new Experiment();
        p.setExperiment(e);
        for (int i = 0; i < NUM_SOURCES; i++) {
            e.getSources().add(new Source());
        }
        this.action.setProject(p);
        assertEquals("list", this.action.load());
        assertEquals(NUM_SOURCES, this.action.getPagedItems().getFullListSize());
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Source.class) && entityId.equals(1L)) {
                return (T) DUMMY_SOURCE;
            }
            return null;
        }

        @Override
        public int collectionSize(Collection<? extends PersistentObject> collection) {
            return collection.size();
        }

    }
}
