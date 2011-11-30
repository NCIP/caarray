/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
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
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 */
@SuppressWarnings("PMD")
public class ArrayDesignActionTest extends AbstractDownloadTest {
    private final ArrayDesignAction arrayDesignAction = new ArrayDesignAction();
    private final LocalArrayDesignServiceStub arrayDesignServiceStub = new LocalArrayDesignServiceStub();
    private final LocalVocabularyServiceStub vocabularyServiceStub = new LocalVocabularyServiceStub();
    private final LocalFileAccessServiceStub fileAccessServiceStub = new LocalFileAccessServiceStub();
    private final LocalFileManagementServiceStub fileManagementServiceStub = new LocalFileManagementServiceStub();
    private static final int NUM_DESIGNS = 3;
    private static final Long DESIGN_ID = 1L;

    @Before
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, this.arrayDesignServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, this.vocabularyServiceStub);
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(FileManagementService.JNDI_NAME, this.fileManagementServiceStub);
        this.fileManagementServiceStub.reimportCount = 0;
    }

    @SuppressWarnings("deprecation")
    @Test(expected = PermissionDeniedException.class)
    public void testPrepare() {
        this.arrayDesignAction.prepare();
        assertNull(this.arrayDesignAction.getArrayDesign());
        assertEquals(1, this.arrayDesignAction.getOrganisms().size());
        assertEquals(1, this.arrayDesignAction.getProviders().size());
        assertEquals(10, this.arrayDesignAction.getFeatureTypes().size());

        final ArrayDesign design = new ArrayDesign();
        design.setId(DESIGN_ID);
        this.arrayDesignAction.setArrayDesign(design);
        this.arrayDesignAction.prepare();
        assertEquals(DESIGN_ID, this.arrayDesignAction.getArrayDesign().getId());

        design.setId(2L);
        this.arrayDesignAction.setArrayDesign(design);
        this.arrayDesignAction.prepare();
    }

    @Test
    public void testList() throws Exception {
        final String result = this.arrayDesignAction.list();
        final List<ArrayDesign> designs = this.arrayDesignAction.getArrayDesigns();
        assertEquals(NUM_DESIGNS, designs.size());
        assertEquals("list", result);
    }

    @Test
    public void testView() throws Exception {
        setTargetIdParam();
        final String result = this.arrayDesignAction.view();
        final ArrayDesign target = this.arrayDesignAction.getArrayDesign();
        assertNotNull(target);
        assertFalse(this.arrayDesignAction.isEditMode());
        assertEquals(Action.INPUT, result);
    }

    @Test
    public void testEdit() throws Exception {
        setTargetIdParam();
        final String result = this.arrayDesignAction.edit();
        final ArrayDesign target = this.arrayDesignAction.getArrayDesign();
        assertNotNull(target);
        assertTrue(this.arrayDesignAction.isEditMode());
        assertEquals(Action.INPUT, result);
    }

    @Test
    public void testFile() throws Exception {
        setTargetIdParam();
        final String result = this.arrayDesignAction.editFile();
        final ArrayDesign target = this.arrayDesignAction.getArrayDesign();
        assertNotNull(target);
        assertTrue(this.arrayDesignAction.isEditMode());
        assertEquals("metaValid", result);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSave() throws Exception {
        final ArrayDesign design = new ArrayDesign();
        this.arrayDesignAction.setArrayDesign(design);
        String result = this.arrayDesignAction.saveMeta();
        assertEquals("metaValid", result);
        final List<String> list = new ArrayList<String>();
        list.add(UnsupportedAffymetrixCdfFiles.HUMAN_EXON_1_0_ST.getFilename());
        this.arrayDesignAction.setUploadFileName(list);
        this.arrayDesignAction.setUpload(null);
        final List<String> flist = new ArrayList<String>();
        flist.add("UCSF_SPOT_SPT");
        this.arrayDesignAction.setFileFormatType(flist);
        result = this.arrayDesignAction.save();
        assertEquals("metaValid", result);

        this.arrayDesignAction.clearErrorsAndMessages();
        // giving the brand new array design an id is a trick to allow for no upload file.
        this.arrayDesignAction.getArrayDesign().setId(DESIGN_ID);
        this.arrayDesignAction.setUploadFileName(null);
        result = this.arrayDesignAction.save();
        assertEquals("importComplete", result);

    }

    @Test
    public void testSaveMeta() throws Exception {
        final ArrayDesign arrD = new ArrayDesign();
        arrD.setName("name");
        final Organization org = new Organization();
        org.setName("name");
        arrD.setProvider(org);
        this.arrayDesignAction.setArrayDesign(arrD);
        this.arrayDesignAction.setEditMode(true);
        this.arrayDesignAction.setCreateMode(false);
        assertEquals(Action.SUCCESS, this.arrayDesignAction.saveMeta());
        assertTrue(ActionHelper.getMessages().contains("arraydesign.saved"));
        this.arrayDesignAction.clearErrorsAndMessages();
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("list", this.arrayDesignAction.download());

        final CaArrayFile rawFile = this.fasStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);

        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setName("Test3");

        final CaArrayFileSet designFileSet = new CaArrayFileSet();
        designFileSet.add(rawFile);
        arrayDesign.setDesignFileSet(designFileSet);

        this.arrayDesignAction.setArrayDesign(arrayDesign);

        this.arrayDesignAction.download();

        assertEquals("application/zip", this.mockResponse.getContentType());
        assertEquals("filename=\"caArray_Test3_file.zip\"", this.mockResponse.getHeader("Content-disposition"));

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
                this.mockResponse.getContentAsByteArray()));
        final ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("Test3.CDF", ze.getName());
        assertNull(zis.getNextEntry());
        IOUtils.closeQuietly(zis);
    }

    @Test
    public void testSaveDuplicateMeta() throws Exception {
        final ArrayDesign arrD = new ArrayDesign();
        arrD.setName("name");
        final Organization org = new Organization();
        org.setName("name");
        arrD.setProvider(org);
        this.arrayDesignAction.setArrayDesign(arrD);
        this.arrayDesignAction.setEditMode(true);
        this.arrayDesignAction.setCreateMode(false);
        assertEquals(Action.SUCCESS, this.arrayDesignAction.saveMeta());

        final ArrayDesign arr2D = new ArrayDesign();
        arr2D.setName("name");
        final Organization org2 = new Organization();
        org2.setName("name2");
        arr2D.setProvider(org2);
        this.arrayDesignAction.setArrayDesign(arr2D);
        this.arrayDesignAction.setEditMode(true);
        this.arrayDesignAction.setCreateMode(false);
        assertEquals(Action.SUCCESS, this.arrayDesignAction.saveMeta());

        arr2D.setProvider(org);
        arr2D.setDescription("duplicate");
        this.arrayDesignAction.setArrayDesign(arr2D);
        this.arrayDesignAction.setEditMode(true);
        this.arrayDesignAction.setCreateMode(false);
        assertEquals(Action.INPUT, this.arrayDesignAction.saveMeta());

        assertTrue(ActionHelper.getMessages().contains("arraydesign.duplicate"));
        this.arrayDesignAction.clearErrorsAndMessages();
    }

    @Test
    public void testDelete() throws Exception {
        this.arrayDesignAction.setArrayDesign(new ArrayDesign());
        this.arrayDesignServiceStub.throwArrayDesignDeleteException = true;
        assertEquals(this.arrayDesignAction.SUCCESS, this.arrayDesignAction.delete());
        this.arrayDesignServiceStub.throwArrayDesignDeleteException = false;
        assertEquals(this.arrayDesignAction.SUCCESS, this.arrayDesignAction.delete());
    }

    @Test
    public void testReimportNoDesign() throws Exception {
        this.arrayDesignAction.setArrayDesign(new ArrayDesign());
        assertEquals(this.arrayDesignAction.SUCCESS, this.arrayDesignAction.reimport());
        assertEquals(1, ActionHelper.getMessages().size());
        assertEquals("arrayDesign.noDesignSelected", ActionHelper.getMessages().get(0));
        assertEquals(0, this.fileManagementServiceStub.reimportCount);
    }

    @Test
    public void testReimportOk() throws Exception {
        this.arrayDesignAction.setArrayDesign(new ArrayDesign());
        this.arrayDesignAction.getArrayDesign().setId(1L);
        assertEquals(this.arrayDesignAction.SUCCESS, this.arrayDesignAction.reimport());
        assertEquals(1, ActionHelper.getMessages().size());
        assertEquals("arrayDesign.importing", ActionHelper.getMessages().get(0));
        assertEquals(1, this.fileManagementServiceStub.reimportCount);
    }

    @Test
    public void testReimportInvalidFile() throws Exception {
        this.arrayDesignAction.setArrayDesign(new ArrayDesign());
        this.arrayDesignAction.getArrayDesign().setId(2L);
        assertEquals(this.arrayDesignAction.SUCCESS, this.arrayDesignAction.reimport());
        assertEquals(1, ActionHelper.getMessages().size());
        assertEquals("arrayDesign.invalid", ActionHelper.getMessages().get(0));
        assertEquals(1, this.fileManagementServiceStub.reimportCount);
    }

    @Test
    public void testReimportIllegal() throws Exception {
        this.arrayDesignAction.setArrayDesign(new ArrayDesign());
        this.arrayDesignAction.getArrayDesign().setId(3L);
        assertEquals(this.arrayDesignAction.SUCCESS, this.arrayDesignAction.reimport());
        assertEquals(1, ActionHelper.getMessages().size());
        assertEquals("arrayDesign.cannotReimport", ActionHelper.getMessages().get(0));
        assertEquals(1, this.fileManagementServiceStub.reimportCount);
    }

    @SuppressWarnings("deprecation")
    private void setTargetIdParam() {
        final ArrayDesign design = new ArrayDesign();
        design.setId(DESIGN_ID);
        this.arrayDesignAction.setArrayDesign(design);
    }

    private static class LocalArrayDesignServiceStub extends ArrayDesignServiceStub {

        @Override
        public List<ArrayDesign> getArrayDesigns() {
            final List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
            for (int i = 0; i < NUM_DESIGNS; i++) {
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
            final List<Organization> orgs = new ArrayList<Organization>();
            orgs.add(new Organization());
            return orgs;
        }

        boolean throwArrayDesignDeleteException = false;

        @Override
        public void deleteArrayDesign(ArrayDesign arrayDesign) throws ArrayDesignDeleteException {
            if (this.throwArrayDesignDeleteException) {
                throw new ArrayDesignDeleteException("You cannot delete an array design that is currently being "
                        + "imported or that is associated with one or more experiments.");
            }
            final Long id = arrayDesign.getId();
        }

    }

    private static class LocalVocabularyServiceStub extends VocabularyServiceStub {
    }

    private static class LocalFileAccessServiceStub extends FileAccessServiceStub {
        @Override
        public CaArrayFile add(File file) {
            final CaArrayFile caArrayFile = new CaArrayFile();
            return caArrayFile;
        }
    }

    private static class LocalFileManagementServiceStub extends FileManagementServiceStub {
        private int reimportCount = 0;

        @Override
        public void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFileSet designFiles)
                throws InvalidDataFileException {
            final FileValidationResult fvr = new FileValidationResult();
            fvr.addMessage(Type.ERROR, "asdf");
            throw new InvalidDataFileException(fvr);
        }

        @Override
        public void reimportAndParseArrayDesign(Long arrayDesignId) throws InvalidDataFileException,
                IllegalAccessException {
            this.reimportCount++;
            switch (arrayDesignId.intValue()) {
            case 2:
                throw new InvalidDataFileException(new FileValidationResult());
            case 3:
                throw new IllegalAccessException("Cannot reimport this design");
            }
        }
    }
}
