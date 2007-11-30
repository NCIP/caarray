/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ArrayDesignServiceTest {

    private ArrayDesignService arrayDesignService;
    private final LocalDaoFactoryStub caArrayDaoFactoryStub = new LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    @Before
    public void setUp() {
        arrayDesignService = createArrayDesignService(caArrayDaoFactoryStub, fileAccessServiceStub, vocabularyServiceStub);
    }

    private static ArrayDesignService createArrayDesignService(DaoFactoryStub caArrayDaoFactoryStub,
            final FileAccessServiceStub fileAccessServiceStub,
            VocabularyServiceStub vocabularyServiceStub) {
        ArrayDesignServiceBean bean = new ArrayDesignServiceBean();
        bean.setDaoFactory(caArrayDaoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        return bean;
    }

    @Test
    public void testImportDesign_ArrayDesign() {
        ArrayDesign design = new ArrayDesign();
        design.setDesignFile(getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        arrayDesignService.importDesign(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
    }

    @Test
    public void testImportDesign_AffymetrixTest3() {
        CaArrayFile designFile = getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        ArrayDesign arrayDesign = arrayDesignService.importDesign(designFile);
        assertEquals("Test3", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Test3", arrayDesign.getLsidObjectId());
        assertEquals(15876, arrayDesign.getNumberOfFeatures());
    }

    @Test
    public void testImportDesign_AffymetrixMapping10K() {
        CaArrayFile designFile = getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.TEN_K_CDF);
        ArrayDesign arrayDesign = arrayDesignService.importDesign(designFile);
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getLsidObjectId());
    }

    @Test
    public void testImportDesign_IlluminaHumanWG6() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        ArrayDesign arrayDesign = arrayDesignService.importDesign(designFile);
        assertEquals("Human_WG-6", arrayDesign.getName());
        assertEquals("illumina.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Human_WG-6", arrayDesign.getLsidObjectId());
        assertEquals(47296, arrayDesign.getNumberOfFeatures());
    }

    @Test
    public void testValidateDesign_AffymetrixTest3() {
        CaArrayFile designFile = getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        FileValidationResult result = arrayDesignService.validateDesign(designFile);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHG_U133_Plus2() {
        CaArrayFile designFile = getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF);
        FileValidationResult result = arrayDesignService.validateDesign(designFile);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_IlluminaHumanWG6() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        FileValidationResult result = arrayDesignService.validateDesign(designFile);
        assertTrue(result.isValid());
        designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_CONTENT);
        result = arrayDesignService.validateDesign(designFile);
        assertFalse(result.isValid());
        designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_HEADERS);
        result = arrayDesignService.validateDesign(designFile);
        assertFalse(result.isValid());
        designFile = getIlluminaCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL);
        result = arrayDesignService.validateDesign(designFile);
        assertFalse(result.isValid());
    }
    
    @Test
    public void testValidateDesign_InvalidFileType() {
        CaArrayFile invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, FileType.AFFYMETRIX_CEL);
        FileValidationResult result = arrayDesignService.validateDesign(invalidDesignFile);
        assertFalse(result.isValid());
        invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, null);
        result = arrayDesignService.validateDesign(invalidDesignFile);
        assertFalse(result.isValid());
    }

    @Test
    public void testDuplicateArrayDesign() {
        ArrayDesign design = new ArrayDesign();
        design.setDesignFile(getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        arrayDesignService.importDesign(design);
        CaArrayFile designFile = getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        FileValidationResult result = arrayDesignService.validateDesign(designFile);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().iterator().next().getMessage().contains("has already been imported"));
        design.setDesignFile(designFile);
        arrayDesignService.importDesign(design);
    }

    @Test
    public void testGetDesignDetails_IlluminaHumanWG6() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        ArrayDesign arrayDesign = arrayDesignService.importDesign(designFile);
        assertEquals("Human_WG-6", arrayDesign.getName());
        ArrayDesignDetails details = arrayDesignService.getDesignDetails(arrayDesign);
        assertNotNull(details);
        assertEquals(arrayDesign.getNumberOfFeatures(), details.getLogicalProbes().size());
    }

    @Test
    public void testGetDesignDetails_AffymetrixTest3() {
        CaArrayFile caArrayFile = getAffymetrixCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        ArrayDesign arrayDesign = arrayDesignService.importDesign(caArrayFile);
        ArrayDesignDetails details = arrayDesignService.getDesignDetails(arrayDesign);
        assertNotNull(details);
        assertEquals(15876, details.getFeatures().size());
    }

    private CaArrayFile getAffymetrixCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.AFFYMETRIX_CDF);
    }

    private CaArrayFile getIlluminaCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.ILLUMINA_DESIGN_CSV);
    }

    private CaArrayFile getCaArrayFile(File file, FileType type) {
        CaArrayFile caArrayFile = fileAccessServiceStub.add(file);
        caArrayFile.setFileType(type);
        return caArrayFile;
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        
        private Map<String, ArrayDesign> lsidDesignMap = new HashMap<String, ArrayDesign>();

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {
                @Override
                public void save(PersistentObject caArrayEntity) {
                    if (caArrayEntity instanceof ArrayDesign) {
                        ArrayDesign arrayDesign = (ArrayDesign) caArrayEntity;
                        lsidDesignMap.put(arrayDesign.getLsid(), arrayDesign);
                    }
                }

                @Override
                public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    // TODO Auto-generated method stub
                    return lsidDesignMap.get("URN:LSID:" + lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                }
            };
        }
    }

}
