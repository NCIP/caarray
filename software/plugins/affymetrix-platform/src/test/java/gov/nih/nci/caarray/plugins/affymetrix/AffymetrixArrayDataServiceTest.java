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
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.arraydata.AbstractArrayDataServiceTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;
import org.junit.Before;

/**
 * Affymetrix-specific tests of array data system. These tests need to be refactored to be independent of
 * ArrayDataServiceTest
 */
@SuppressWarnings("PMD")
public class AffymetrixArrayDataServiceTest extends AbstractArrayDataServiceTest {
    private CaArrayFile focusCel;
    private CaArrayFile focusCalvinCel;
    private CaArrayFile focusChp;
    private CaArrayFile focusCalvinChp;
    private Experiment experiment;

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AffymetrixModule());
    }

    @Before
    public void onSetup() {
        focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CALVIN_CEL,
                AFFY_TEST3_LSID_OBJECT_ID);
        focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP, AFFY_TEST3_LSID_OBJECT_ID);
        focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP,
                AFFY_TEST3_LSID_OBJECT_ID);

        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());
    }

    @Test
    public void testImportRawAndDerivedSameName() throws InvalidDataFileException {
        // tests that imports of raw and derived data files with same base name go
        // to the same hybridization chain
        this.importData(DEFAULT_IMPORT_OPTIONS);
        this.assertData(2, 2, 1, 1, true);
    }

    @Test
    public void testImportSingleAnnotationChain() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        final DataImportOptions options = DataImportOptions.getAutoCreateSingleOptions("TEST_NAME");
        this.importData(options);
        this.assertData(1, 1, 2, 2, false);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testImportToTargetSources() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        Source targetSrc1 = this.addSourceToFocusCel(1L, "targetSrc1");
        Source targetSrc2 = this.addSourceToFocusCel(2L, "targetSrc2");
        focusCel.getProject().getExperiment().getSources().addAll(Arrays.asList(targetSrc1, targetSrc2));

        final DataImportOptions options = DataImportOptions.getAssociateToBiomaterialsOptions(
                ExperimentDesignNodeType.SOURCE, Arrays.asList(targetSrc1.getId(), targetSrc2.getId()));
        this.importData(options);

        assertEquals(2, this.experiment.getSources().size());
        assertEquals(2, this.experiment.getSamples().size());
        assertEquals(2, this.experiment.getHybridizations().size());
        assertEquals(2, targetSrc1.getSamples().size());
        assertEquals(2, targetSrc2.getSamples().size());
    }

    private Source addSourceToFocusCel(Long id, String sourceName) {
        Source source = new Source();
        source.setId(id);
        source.setName(sourceName);
        source.setExperiment(this.focusCel.getProject().getExperiment());
        this.searchDaoStub.save(source);

        this.focusCel.getProject().getExperiment().getSources().add(source);
        return source;
    }

    private void importData(DataImportOptions dataImportOptions) throws InvalidDataFileException {
        this.arrayDataService.importData(focusCel, true, dataImportOptions);
        this.arrayDataService.importData(focusCalvinCel, true, dataImportOptions);
        this.arrayDataService.importData(focusChp, true, dataImportOptions);
        this.arrayDataService.importData(focusCalvinChp, true, dataImportOptions);

        this.experiment = focusCel.getProject().getExperiment();
    }

    private void assertData(int expectedSamples, int expectedHybridizations,
        int expectedRawData, int expectedDerivedData, boolean testForImportRawAndDerivedSameName) {
        checkAnnotation(focusCel, expectedSamples);
        assertEquals(expectedHybridizations, this.experiment.getHybridizations().size());
        for (Hybridization h : this.experiment.getHybridizations()) {
            assertEquals(expectedRawData, h.getRawDataCollection().size());
            assertEquals(expectedDerivedData, h.getDerivedDataCollection().size());
            if (testForImportRawAndDerivedSameName) {
                assertForImportRawAndDerivedSameName(h);
            }
        }
    }

    private void assertForImportRawAndDerivedSameName(Hybridization h) {
        if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCel)) {
            assertEquals(focusChp, h.getDerivedDataCollection().iterator().next().getDataFile());
        } else if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCalvinCel)) {
            assertEquals(focusCalvinChp, h.getDerivedDataCollection().iterator().next().getDataFile());
        } else {
            fail("Expected hybridization to be linked to either focus or calvin focus CEL");
        }
    }

    @Test
    public void testCreateAnnotation() throws InvalidDataFileException {
        testExistingAnnotationNotOverwritten();
    }

    private void testExistingAnnotationNotOverwritten() throws InvalidDataFileException {
        final CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        final RawArrayData celData = new RawArrayData();
        final Hybridization hybridization = new Hybridization();
        celData.addHybridization(hybridization);
        hybridization.addArrayData(celData);
        celData.setDataFile(celFile);
        assertNull(celData.getType());
        this.daoFactoryStub.getArrayDao().save(celData);
        this.arrayDataService.importData(celFile, true, DEFAULT_IMPORT_OPTIONS);
        assertNotNull(celData.getType());
        assertEquals(celData, this.daoFactoryStub.getArrayDao().getArrayData(celFile.getId()));
        assertEquals(hybridization, celData.getHybridizations().iterator().next());
    }

    private CaArrayFile getChpCaArrayFile(File chp, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(chp, ChpHandler.CHP_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getCelCaArrayFile(File cel, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(cel, CelHandler.CEL_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }
}
