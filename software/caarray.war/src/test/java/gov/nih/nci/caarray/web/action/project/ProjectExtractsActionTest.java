//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
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
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;

/**
 * @author Winston Cheng
 */
public class ProjectExtractsActionTest extends AbstractDownloadTest {
    private final ProjectExtractsAction action = new ProjectExtractsAction();

    private static Extract DUMMY_EXTRACT = new Extract();
    private static Sample DUMMY_SAMPLE = new Sample();

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_EXTRACT.setId(1L);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no current extract id
        this.action.prepare();
        assertNull(this.action.getCurrentExtract().getId());

        // valid current extract id
        Extract extract = new Extract();
        extract.setId(1L);
        this.action.setCurrentExtract(extract);
        this.action.prepare();
        assertEquals(DUMMY_EXTRACT, this.action.getCurrentExtract());

        // invalid current extract id
        extract = new Extract();
        extract.setId(2L);
        this.action.setCurrentExtract(extract);
        try {
            this.action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (final PermissionDeniedException pde) {
        }
    }

    @Test
    public void testCopy() {
        this.action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals("list", this.action.copy());
    }

    @Test
    public void testDelete() {
        DUMMY_SAMPLE.getExtracts().add(DUMMY_EXTRACT);
        DUMMY_EXTRACT.getSamples().add(DUMMY_SAMPLE);
        this.action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals("list", this.action.delete());
        assertFalse(DUMMY_SAMPLE.getExtracts().contains(DUMMY_EXTRACT));

        DUMMY_EXTRACT.getLabeledExtracts().add(new LabeledExtract());
        this.action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals("list", this.action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    @Test
    public void testDownload() throws Exception {
        final CaArrayFile rawFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile derivedFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        final Extract e1 = new Extract();
        final Extract e2 = new Extract();
        final LabeledExtract le1 = new LabeledExtract();
        e1.getLabeledExtracts().add(le1);
        final LabeledExtract le2 = new LabeledExtract();
        e2.getLabeledExtracts().add(le2);
        final Hybridization h = new Hybridization();
        le2.getHybridizations().add(h);
        final RawArrayData raw = new RawArrayData();
        h.addArrayData(raw);
        final DerivedArrayData derived = new DerivedArrayData();
        h.getDerivedDataCollection().add(derived);
        raw.setDataFile(rawFile);
        derived.setDataFile(derivedFile);

        this.action.setCurrentExtract(e1);
        this.action.setProject(p);
        assertEquals("noExtractData", this.action.download());
        this.action.setCurrentExtract(e2);

        final List<CaArrayFile> files = new ArrayList<CaArrayFile>(e2.getAllDataFiles());
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
        final Sample toAdd = new Sample();
        final List<Sample> addList = new ArrayList<Sample>();
        addList.add(toAdd);
        this.action.setItemsToAssociate(addList);

        final Sample toRemove = new Sample();
        toRemove.getExtracts().add(DUMMY_EXTRACT);
        final List<Sample> removeList = new ArrayList<Sample>();
        removeList.add(toRemove);
        this.action.setItemsToRemove(removeList);

        this.action.setCurrentExtract(DUMMY_EXTRACT);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getExtracts().contains(DUMMY_EXTRACT));
        assertFalse(toRemove.getExtracts().contains(DUMMY_EXTRACT));
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Extract.class) && entityId.equals(1L)) {
                return (T) DUMMY_EXTRACT;
            }
            return null;
        }
    }
}
