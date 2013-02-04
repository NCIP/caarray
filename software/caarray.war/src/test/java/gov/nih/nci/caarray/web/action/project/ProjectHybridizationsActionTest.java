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
import static org.junit.Assert.assertNotSame;
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
import java.util.Date;
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
public class ProjectHybridizationsActionTest extends AbstractDownloadTest {
    private final ProjectHybridizationsAction action = new ProjectHybridizationsAction();

    private static Hybridization DUMMY_HYBRIDIZATION = new Hybridization();
    private static LabeledExtract DUMMY_LABELED_EXTRACT = new LabeledExtract();
    private static RawArrayData DUMMY_ARRAY_DATA = new RawArrayData();
    private static CaArrayFile DUMMY_FILE = new CaArrayFile();

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_HYBRIDIZATION.setId(1L);
        DUMMY_HYBRIDIZATION.getRawDataCollection().add(DUMMY_ARRAY_DATA);
        DUMMY_ARRAY_DATA.setDataFile(DUMMY_FILE);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no current hybridization id
        this.action.prepare();
        assertNull(this.action.getCurrentHybridization().getId());

        // valid current hybridization id
        Hybridization hybridization = new Hybridization();
        hybridization.setId(1L);
        this.action.setCurrentHybridization(hybridization);
        this.action.prepare();
        assertEquals(DUMMY_HYBRIDIZATION, this.action.getCurrentHybridization());

        // invalid current hybridization id
        hybridization = new Hybridization();
        hybridization.setId(2L);
        this.action.setCurrentHybridization(hybridization);
        try {
            this.action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (final PermissionDeniedException pde) {
        }
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noHybData", this.action.download());

        final CaArrayFile rawFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile derivedFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        final Hybridization h1 = new Hybridization();
        final Hybridization h2 = new Hybridization();
        final RawArrayData raw = new RawArrayData();
        h1.addArrayData(raw);
        final DerivedArrayData derived = new DerivedArrayData();
        h2.getDerivedDataCollection().add(derived);
        raw.setDataFile(rawFile);
        derived.setDataFile(derivedFile);

        this.action.setCurrentHybridization(h1);
        this.action.setProject(p);
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(h1.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(1, files.size());
        assertEquals("missing_term_source.idf", files.get(0).getName());
        files = new ArrayList<CaArrayFile>(h2.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(1, files.size());
        assertEquals("missing_term_source.sdrf", files.get(0).getName());

        assertEquals(null, this.action.download());
        assertEquals("application/zip", this.mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", this.mockResponse.getHeader("Content-disposition"));

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
                this.mockResponse.getContentAsByteArray()));
        ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.idf", ze.getName());
        ze = zis.getNextEntry();
        assertNull(zis.getNextEntry());
        IOUtils.closeQuietly(zis);
    }

    @Test
    public void testCopy() {
        assertEquals("notYetImplemented", this.action.copy());
    }

    @Test
    public void testDelete() {
        Date date = new Date();
        DUMMY_LABELED_EXTRACT.setLastModifiedDataTime(date);
        DUMMY_LABELED_EXTRACT.getHybridizations().add(DUMMY_HYBRIDIZATION);
        DUMMY_HYBRIDIZATION.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        this.action.setCurrentHybridization(DUMMY_HYBRIDIZATION);
        assertEquals("list", this.action.delete());
        assertFalse(DUMMY_LABELED_EXTRACT.getHybridizations().contains(DUMMY_HYBRIDIZATION));
        assertNotSame(date, DUMMY_LABELED_EXTRACT.getLastModifiedDataTime());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSave() {
        final LabeledExtract toAdd = new LabeledExtract();
        final List<LabeledExtract> addList = new ArrayList<LabeledExtract>();
        addList.add(toAdd);
        this.action.setItemsToAssociate(addList);

        final LabeledExtract toRemove = new LabeledExtract();
        toRemove.getHybridizations().add(DUMMY_HYBRIDIZATION);
        final List<LabeledExtract> removeList = new ArrayList<LabeledExtract>();
        removeList.add(toRemove);
        this.action.setItemsToRemove(removeList);

        final ProtocolApplication pa = new ProtocolApplication();
        final Protocol p = new Protocol();
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_HYBRIDIZATION.addProtocolApplication(pa);

        final Protocol p2 = new Protocol();
        p2.setName("protocol2");
        final List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(p2);
        this.action.setSelectedProtocols(protocols);

        this.action.setCurrentHybridization(DUMMY_HYBRIDIZATION);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getHybridizations().contains(DUMMY_HYBRIDIZATION));
        assertFalse(toRemove.getHybridizations().contains(DUMMY_HYBRIDIZATION));
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Hybridization.class) && entityId.equals(1L)) {
                return (T) DUMMY_HYBRIDIZATION;
            }
            return null;
        }
    }
}
