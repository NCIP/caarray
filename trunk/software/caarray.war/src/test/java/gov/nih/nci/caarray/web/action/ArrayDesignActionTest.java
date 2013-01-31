//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignDeleteException;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.UnsupportedAffymetrixCdfFiles;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.web.AbstractDownloadTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
@SuppressWarnings("PMD")
public class ArrayDesignActionTest extends AbstractDownloadTest {
    private final ArrayDesignAction arrayDesignAction = new ArrayDesignAction();
    private final LocalArrayDesignServiceStub arrayDesignServiceStub = new LocalArrayDesignServiceStub();
    private final LocalVocabularyServiceStub vocabularyServiceStub = new LocalVocabularyServiceStub();
    private final LocalFileAccessServiceStub fileAccessServiceStub = new LocalFileAccessServiceStub();
    private final LocalFileManagementServiceStub fileManagementServiceStub = new LocalFileManagementServiceStub();
    private MockHttpServletResponse mockResponse;
    private static final int NUM_DESIGNS = 3;
    private static final Long DESIGN_ID = 1L;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, this.arrayDesignServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, this.vocabularyServiceStub);
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(FileManagementService.JNDI_NAME, this.fileManagementServiceStub);
        ServletActionContext.setRequest(new MockHttpServletRequest());
        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
    }

    @SuppressWarnings("deprecation")
    @Test(expected=PermissionDeniedException.class)
    public void testPrepare() {
        arrayDesignAction.prepare();
        assertNull(arrayDesignAction.getArrayDesign());
        assertEquals(1, arrayDesignAction.getOrganisms().size());
        assertEquals(1, arrayDesignAction.getProviders().size());
        assertEquals(10, arrayDesignAction.getFeatureTypes().size());

        ArrayDesign design = new ArrayDesign();
        design.setId(DESIGN_ID);
        arrayDesignAction.setArrayDesign(design);
        arrayDesignAction.prepare();
        assertEquals(DESIGN_ID, arrayDesignAction.getArrayDesign().getId());

        design.setId(2L);
        arrayDesignAction.setArrayDesign(design);
        arrayDesignAction.prepare();
    }

    @Test
    public void testList() throws Exception {
        String result = arrayDesignAction.list();
        List<ArrayDesign> designs = arrayDesignAction.getArrayDesigns();
        assertEquals(NUM_DESIGNS, designs.size());
        assertEquals("list", result);
    }
    @Test
    public void testView() throws Exception {
        setTargetIdParam();
        String result = arrayDesignAction.view();
        ArrayDesign target = arrayDesignAction.getArrayDesign();
        assertNotNull(target);
        assertFalse(arrayDesignAction.isEditMode());
        assertEquals(Action.INPUT, result);
    }
    @Test
    public void testEdit() throws Exception {
        setTargetIdParam();
        String result = arrayDesignAction.edit();
        ArrayDesign target = arrayDesignAction.getArrayDesign();
        assertNotNull(target);
        assertTrue(arrayDesignAction.isEditMode());
        assertEquals(Action.INPUT, result);
    }

    @Test
    public void testFile() throws Exception {
        setTargetIdParam();
        String result = arrayDesignAction.editFile();
        ArrayDesign target = arrayDesignAction.getArrayDesign();
        assertNotNull(target);
        assertTrue(arrayDesignAction.isEditMode());
        assertEquals("metaValid", result);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSave() throws Exception {
        ArrayDesign design = new ArrayDesign();
        arrayDesignAction.setArrayDesign(design);
        String result = arrayDesignAction.saveMeta();
        assertEquals("metaValid", result);
        List<String> list = new ArrayList<String>();
        list.add(UnsupportedAffymetrixCdfFiles.HUMAN_EXON_1_0_ST.getFilename());
        arrayDesignAction.setUploadFileName(list);
        arrayDesignAction.setUpload(null);
        List<String> flist = new ArrayList<String>();
        flist.add(FileType.UCSF_SPOT_SPT.name());
        arrayDesignAction.setFileFormatType(flist);
        result = arrayDesignAction.save();
        assertEquals("metaValid", result);

        arrayDesignAction.clearErrorsAndMessages();
        // giving the brand new array design an id is a trick to allow for no upload file.
        arrayDesignAction.getArrayDesign().setId(DESIGN_ID);
        arrayDesignAction.setUploadFileName(null);
        result = arrayDesignAction.save();
        assertEquals("importComplete", result);

    }

    @Test
    public void testSaveMeta() throws Exception {
        ServletActionContext.setRequest(new MockHttpServletRequest());
        ArrayDesign arrD = new ArrayDesign();
        arrD.setName("name");
        Organization org = new Organization();
        org.setName("name");
        arrD.setProvider(org);
        arrayDesignAction.setArrayDesign(arrD);
        arrayDesignAction.setEditMode(true);
        arrayDesignAction.setCreateMode(false);
        assertEquals(Action.SUCCESS, arrayDesignAction.saveMeta());
        assertTrue(ActionHelper.getMessages().contains("arraydesign.saved"));
        arrayDesignAction.clearErrorsAndMessages();
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("list", arrayDesignAction.download());

        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(AffymetrixArrayDesignFiles.TEST3_CDF);

        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setName("Test3");

        CaArrayFile rawFile = new CaArrayFile();
        rawFile.writeContents(IOUtils.toInputStream(""));
        rawFile.setName("Test3.CDF");
        CaArrayFileSet designFileSet = new CaArrayFileSet();
        designFileSet.add(rawFile);
        arrayDesign.setDesignFileSet(designFileSet);

        arrayDesignAction.setArrayDesign(arrayDesign);

        arrayDesignAction.download();

        assertEquals("application/zip", mockResponse.getContentType());
        assertEquals("filename=\"caArray_Test3_file.zip\"", mockResponse.getHeader("Content-disposition"));

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
        ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("Test3.CDF", ze.getName());
        assertNull(zis.getNextEntry());
        IOUtils.closeQuietly(zis);
    }

    @Test
    public void testSaveDuplicateMeta() throws Exception {
        ServletActionContext.setRequest(new MockHttpServletRequest());
        ArrayDesign arrD = new ArrayDesign();
        arrD.setName("name");
        Organization org = new Organization();
        org.setName("name");
        arrD.setProvider(org);
        arrayDesignAction.setArrayDesign(arrD);
        arrayDesignAction.setEditMode(true);
        arrayDesignAction.setCreateMode(false);
        assertEquals(Action.SUCCESS, arrayDesignAction.saveMeta());

        ArrayDesign arr2D = new ArrayDesign();
        arr2D.setName("name");
        Organization org2 = new Organization();
        org2.setName("name2");
        arr2D.setProvider(org2);
        arrayDesignAction.setArrayDesign(arr2D);
        arrayDesignAction.setEditMode(true);
        arrayDesignAction.setCreateMode(false);
        assertEquals(Action.SUCCESS, arrayDesignAction.saveMeta());

        arr2D.setProvider(org);
        arr2D.setDescription("duplicate");
        arrayDesignAction.setArrayDesign(arr2D);
        arrayDesignAction.setEditMode(true);
        arrayDesignAction.setCreateMode(false);
        assertEquals(Action.INPUT, arrayDesignAction.saveMeta());

        assertTrue(ActionHelper.getMessages().contains("arraydesign.duplicate"));
        arrayDesignAction.clearErrorsAndMessages();
    }

    @Test
    public void testDelete() throws Exception {
        ServletActionContext.setRequest(new MockHttpServletRequest());
        arrayDesignAction.setArrayDesign(new ArrayDesign());
        arrayDesignServiceStub.throwArrayDesignDeleteException = true;
        assertEquals(arrayDesignAction.SUCCESS, arrayDesignAction.delete());
        arrayDesignServiceStub.throwArrayDesignDeleteException = false;
        assertEquals(arrayDesignAction.SUCCESS, arrayDesignAction.delete());
    }

    @SuppressWarnings("deprecation")
    private void setTargetIdParam() {
        ArrayDesign design = new ArrayDesign();
        design.setId(DESIGN_ID);
        arrayDesignAction.setArrayDesign(design);
    }

    private static class LocalArrayDesignServiceStub extends ArrayDesignServiceStub {

        @Override
        public List<ArrayDesign> getArrayDesigns() {
            List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
            for (int i=0; i < NUM_DESIGNS; i++) {
                designs.add(new ArrayDesign());
            }
            return designs;
        }
        @Override
        @SuppressWarnings("deprecation")
        public ArrayDesign getArrayDesign(Long id) {
            ArrayDesign arrayDesign = null;
            if (DESIGN_ID.equals(id)) {
                arrayDesign = new ArrayDesign();
                arrayDesign.setId(id);
            }
            return arrayDesign;
        }
        @Override
        public List<Organization> getAllProviders() {
            List<Organization> orgs = new ArrayList<Organization>();
            orgs.add(new Organization());
            return orgs;
        }

        boolean throwArrayDesignDeleteException = false;

        @Override
        public void deleteArrayDesign(ArrayDesign arrayDesign)
                throws ArrayDesignDeleteException {
            if (throwArrayDesignDeleteException) {
                throw new ArrayDesignDeleteException(
                        "You cannot delete an array design that is currently being "
                        + "imported or that is associated with one or more experiments.");
            }
            Long id = arrayDesign.getId();
        }

    }
    private static class LocalVocabularyServiceStub extends VocabularyServiceStub {}
    private static class LocalFileAccessServiceStub extends FileAccessServiceStub {
        @Override
        public CaArrayFile add(File file) {
            CaArrayFile caArrayFile = new CaArrayFile();
            return caArrayFile;
        }
    }
    private static class LocalFileManagementServiceStub extends FileManagementServiceStub {
        @Override
        public void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFileSet designFiles) throws InvalidDataFileException {
            FileValidationResult fvr = new FileValidationResult(null);
            fvr.addMessage(Type.ERROR, "asdf");
            throw new InvalidDataFileException(fvr);
        }

    }
}
