/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caArray2 Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2 Software and any 
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
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydesign.AbstractArrayDesignServiceTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Affymetrix-specific tests of ArrayDesignService. Temporary - need to factor our to tests independent of
 * ArrayDesignService.
 * 
 * @author dkokotov
 */
public class AffymetrixArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AffymetrixModule());
    }

    @Test
    public void testValidateDesign_AffymetrixTest3() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        final ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHG_U133_Plus2() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF);
        final ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHuEx() {
        final CaArrayFile pgfDesignFile = getAffymetrixPgfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_PGF);
        final CaArrayFile clfDesignFile = getAffymetrixClfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_CLF);
        final Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>();
        designFiles.add(pgfDesignFile);
        designFiles.add(clfDesignFile);
        final ValidationResult result = this.arrayDesignService.validateDesign(designFiles);
        assertTrue(result.isValid());
    }

    @Test
    public void testImportDesign_AffymetrixTest3() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        this.arrayDesignService.importDesign(arrayDesign);
        assertEquals("Test3", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Test3", arrayDesign.getLsidObjectId());
        assertEquals(15876, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesignDetails_AffymetrixTest3() throws PlatformFileReadException {
        final String name = "Test3";
        final File testFile = AffymetrixArrayDesignFiles.TEST3_CDF;
        final int expectedFeatureCount = 15876;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgcc2() throws PlatformFileReadException {
        final String name = "AGCC_2.x_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_2_X_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgcc3() throws PlatformFileReadException {
        final String name = "AGCC_3.x_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_3_X_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgccGcos() throws PlatformFileReadException {
        final String name = "AGCC_GCOS_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_GCOS_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @SuppressWarnings("deprecation")
    private void testImportDesignDetails(final String name, final File testFile, final int expectedFeatureCount) {
        final ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(getAffymetrixCdfCaArrayFile(testFile));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        assertEquals(name, design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(name, design.getLsidObjectId());
        assertEquals(expectedFeatureCount, design.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixMapping10K() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEN_K_CDF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        this.arrayDesignService.importDesign(arrayDesign);
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getLsidObjectId());
        assertEquals(506944, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixHuEx() {
        final CaArrayFile clfDesignFile = getAffymetrixClfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        final CaArrayFile pgfDesignFile = getAffymetrixPgfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(clfDesignFile);
        arrayDesign.addDesignFile(pgfDesignFile);
        this.arrayDesignService.importDesign(arrayDesign);
        assertTrue(pgfDesignFile.getValidationResult().isValid());
        assertTrue(clfDesignFile.getValidationResult().isValid());
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getLsidObjectId());
        assertEquals(1024, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testImportDesignDetails_AffymetrixHuEx() throws PlatformFileReadException {
        final CaArrayFile clfDesignFile = getAffymetrixClfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        final CaArrayFile pgfDesignFile = getAffymetrixPgfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        final ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(clfDesignFile);
        design.addDesignFile(pgfDesignFile);
        this.arrayDesignService.importDesign(design);
        assertTrue(pgfDesignFile.getValidationResult().isValid());
        assertTrue(clfDesignFile.getValidationResult().isValid());
        this.arrayDesignService.importDesignDetails(design);
        final String arrayDesignName = "HuEx-1_0-st-v1-test";
        assertEquals(arrayDesignName, design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(arrayDesignName, design.getLsidObjectId());
        assertEquals(1024, design.getNumberOfFeatures().intValue());

        assertEquals(94, design.getDesignDetails().getLogicalProbes().size());
        assertEquals(364, design.getDesignDetails().getProbes().size());
        assertEquals(1024, design.getDesignDetails().getFeatures().size());

        for (final PhysicalProbe probe : design.getDesignDetails().getProbes()) {
            assertTrue(probe.getFeatures().size() > 0);
            assertEquals(design.getDesignDetails(), probe.getArrayDesignDetails());
            for (final Feature abstractFeature : probe.getFeatures()) {
                final Feature feature = abstractFeature;
                assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
            }
        }

        for (final LogicalProbe logicalProbe : design.getDesignDetails().getLogicalProbes()) {
            assertEquals(design.getDesignDetails(), logicalProbe.getArrayDesignDetails());
            assertTrue(logicalProbe.getProbes().size() > 0);
            for (final PhysicalProbe physicalProbe : logicalProbe.getProbes()) {
                assertEquals(design.getDesignDetails(), physicalProbe.getArrayDesignDetails());
                assertTrue(physicalProbe.getFeatures().size() > 0);
                for (final Feature abstractFeature : physicalProbe.getFeatures()) {
                    final Feature feature = abstractFeature;
                    assertEquals(design.getDesignDetails(), feature.getArrayDesignDetails());
                    assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                    assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
                }
            }
        }
    }

    private CaArrayFile getAffymetrixCdfCaArrayFile(final File file) {
        return getCaArrayFile(file, CdfHandler.CDF_FILE_TYPE);
    }

    private CaArrayFile getAffymetrixPgfCaArrayFile(final File file) {
        return getCaArrayFile(file, PgfClfDesignHandler.PGF_FILE_TYPE);
    }

    private CaArrayFile getAffymetrixClfCaArrayFile(final File file) {
        return getCaArrayFile(file, PgfClfDesignHandler.CLF_FILE_TYPE);
    }
}