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

package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.AbstractProbe;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.platforms.AbstractHandlerTest;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.AgilentArrayDataFiles;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import com.google.inject.AbstractModule;

/**
 * Tests the AgilentRawTextHandler class
 */
public class AgilentRawTextDataHandlerTest extends AbstractHandlerTest {
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();
    private static final LSID DESIGN_LSID = new LSID("Agilent.com", "PhysicalArrayDesign", "022522_D_F_20090107");
    private static final LSID TINY_DESIGN = new LSID("Agilent.com", "PhysicalArrayDesign", "Agilent_Tiny");
    private ArrayDesign arrayDesign;

    private static final AgilentTextQuantitationType[] ACGH_COLS = {AgilentTextQuantitationType.G_MEDIAN_SIGNAL,
            AgilentTextQuantitationType.G_PROCESSED_SIG_ERROR, AgilentTextQuantitationType.G_PROCESSED_SIGNAL,
            AgilentTextQuantitationType.LOG_RATIO_ERROR, AgilentTextQuantitationType.LOG_RATIO,
            AgilentTextQuantitationType.P_VALUE_LOG_RATIO, AgilentTextQuantitationType.R_MEDIAN_SIGNAL,
            AgilentTextQuantitationType.R_PROCESSED_SIG_ERROR, AgilentTextQuantitationType.R_PROCESSED_SIGNAL };

    private static final AgilentTextQuantitationType[] GEN_EXPRESSION_COLS = {
            AgilentTextQuantitationType.G_PROCESSED_SIGNAL, AgilentTextQuantitationType.G_PROCESSED_SIG_ERROR,
            AgilentTextQuantitationType.G_MEDIAN_SIGNAL };

    private static final AgilentTextQuantitationType[] MIRNA_COLS = {AgilentTextQuantitationType.G_PROCESSED_SIGNAL,
            AgilentTextQuantitationType.G_PROCESSED_SIG_ERROR, AgilentTextQuantitationType.G_MEDIAN_SIGNAL,
            AgilentTextQuantitationType.G_TOTAL_PROBE_SIGNAL, AgilentTextQuantitationType.G_TOTAL_PROBE_ERROR,
            AgilentTextQuantitationType.G_TOTAL_GENE_SIGNAL, AgilentTextQuantitationType.G_TOTAL_GENE_ERROR,
            AgilentTextQuantitationType.G_IS_GENE_DETECTED };

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AgilentModule());
        platformModule.addPlatform(new AbstractModule() {
            @Override
            protected void configure() {
                final VocabularyDao vocabDao = mock(VocabularyDao.class);
                final TermSource mo = new TermSource();
                mo.setName("MO");
                final Term mmTerm = new Term();
                mmTerm.setValue("mm");
                mmTerm.setSource(mo);

                when(vocabDao.queryEntityByExample(any(ExampleSearchCriteria.class))).thenReturn(
                        Collections.singletonList(mo));
                when(vocabDao.getTerm(any(TermSource.class), eq("mm"))).thenReturn(mmTerm);
                bind(VocabularyDao.class).toInstance(vocabDao);
            }
        });
    }

    @Test
    public void validIfMageTab() {
        setupArrayDesign(TINY_DESIGN);
        addProbeToDesign("Agilent_Tiny_1");
        addProbeToDesign("Agilent_Tiny_2");

        assertValid(TINY_DESIGN, AgilentArrayDataFiles.TINY_RAW_TEXT, AgilentArrayDataFiles.TINY_IDF,
                AgilentArrayDataFiles.TINY_SDRF);
    }

    @Test
    public void invalidIfNoMageTab() {
        setupArrayDesign(TINY_DESIGN);
        addProbeToDesign("Agilent_Tiny_1");
        addProbeToDesign("Agilent_Tiny_2");

        assertInvalid(new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT }, TINY_DESIGN,
                AgilentArrayDataFiles.TEST_ACGH_RAW_TEXT);
    }

    @Test
    public void invalidIfNoIdf() {
        setupArrayDesign(TINY_DESIGN);
        addProbeToDesign("Agilent_Tiny_1");
        addProbeToDesign("Agilent_Tiny_2");

        assertInvalid(new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT }, TINY_DESIGN,
                AgilentArrayDataFiles.TINY_RAW_TEXT, AgilentArrayDataFiles.TINY_SDRF);
    }

    @Test
    public void invalidIfNoSdrf() {
        setupArrayDesign(TINY_DESIGN);
        addProbeToDesign("Agilent_Tiny_1");
        addProbeToDesign("Agilent_Tiny_2");

        assertInvalid(new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT }, TINY_DESIGN,
                AgilentArrayDataFiles.TINY_RAW_TEXT, AgilentArrayDataFiles.TINY_IDF);
    }

    @Test
    public void invalidIfMissingProbe() {
        setupArrayDesign(TINY_DESIGN);
        addProbeToDesign("Agilent_Tiny_1");

        assertInvalid(new String[] {}, TINY_DESIGN, AgilentArrayDataFiles.TINY_RAW_TEXT,
                AgilentArrayDataFiles.TINY_IDF, AgilentArrayDataFiles.TINY_SDRF);
    }

    public void assertValid(LSID designLsid, final File rawTextFile, final File... files) {
        final List<File> fileList = makeFileList(rawTextFile, files);

        final CaArrayFile caArrayFile = getCaArrayFile(rawTextFile, designLsid.getObjectId());

        testValidFile(caArrayFile, genMageTabDocSet(fileList), true);
    }

    public void assertInvalid(final String[] acceptableErrorMessageFragments, final LSID designLsid,
            final File rawTextFile, final File... files) {
        final List<File> fileList = makeFileList(rawTextFile, files);

        final CaArrayFile caArrayFile = getCaArrayFile(rawTextFile, designLsid.getObjectId());

        testInvalidFile(caArrayFile, genMageTabDocSet(fileList), acceptableErrorMessageFragments);
    }

    @Test
    public void parsesFile() throws InvalidDataFileException {
        setupArrayDesign(DESIGN_LSID);
        addProbeToDesign("HsCGHBrightCorner");
        addProbeToDesign("A_16_P37138757");

        final File rawTextFile = AgilentArrayDataFiles.TEST_ACGH_RAW_TEXT;

        final CaArrayFile caArrayFile = getCaArrayFile(rawTextFile, DESIGN_LSID.getObjectId());

        this.arrayDataService.importData(caArrayFile, true, DEFAULT_IMPORT_OPTIONS);

        final int expectedNumberOfProbes = 177071;

        assertEquals(FileStatus.IMPORTED, caArrayFile.getFileStatus());

        final RawArrayData rawArrayData =
                (RawArrayData) this.daoFactoryStub.getArrayDao().getArrayData(caArrayFile.getId());
        final DataSet dataSet = rawArrayData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());

        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(9, hybridizationData.getColumns().size());

        final List<AbstractDesignElement> designElements = dataSet.getDesignElementList().getDesignElements();
        assertEquals(expectedNumberOfProbes, designElements.size());

        checkColumnLengths(hybridizationData, expectedNumberOfProbes, ACGH_COLS);

        checkValues(hybridizationData, designElements, 0, "HsCGHBrightCorner", ACGH_COLS, 4838.5f, 670.1065f,
                6694.398f, 0.06165362131f, -0.04322179759f, 0.4832766078f, 6834.0f, 607.6589f, 6060.239f);
        checkValues(hybridizationData, designElements, 6527, "A_16_P37138757", ACGH_COLS, 726.0f, 87.78085f, 837.711f,
                0.06633819191f, -0.01558695564f, 0.814238211f, 1218.0f, 89.95938f, 808.1784f);
    }

    private void checkValues(HybridizationData hybridizationData, List<AbstractDesignElement> designElements,
            int index, String expectedProbeName, AgilentTextQuantitationType[] qts, Object... values) {

        assertEquals(qts.length, values.length);
        checkProbeName(designElements, index, expectedProbeName);
        int idx = 0;
        for (final AgilentTextQuantitationType qt : qts) {
            checkValue(hybridizationData, index, qt, values[idx++]);
        }
    }

    private void checkProbeName(List<AbstractDesignElement> designElements, int index, String expectedProbeName) {
        final AbstractProbe probe = (AbstractProbe) designElements.get(index);
        assertEquals(expectedProbeName, probe.getName());
    }

    private void checkValue(HybridizationData hybridizationData, int index,
            AgilentTextQuantitationType quantitationType, Object expected) {
        final AbstractDataColumn col = hybridizationData.getColumn(quantitationType);
        assertNotNull("missing column " + quantitationType, col);
        switch (quantitationType.getDataType()) {
        case FLOAT:
            final double floatTolerance = 0.00000001;
            final FloatColumn column = (FloatColumn) col;
            final float fexpected = ((Float) expected).floatValue();
            assertEquals(quantitationType.toString(), fexpected, column.getValues()[index], floatTolerance);
            break;
        case BOOLEAN:
            final BooleanColumn bcolumn = (BooleanColumn) col;
            assertEquals(quantitationType.toString(), ((Boolean) expected).booleanValue(), bcolumn.getValues()[index]);
            break;
        default:
            fail("please implement " + quantitationType.getDataType());
        }
    }

    private void checkColumnLengths(HybridizationData hybridizationData, int expectedColumnLength,
            AgilentTextQuantitationType... cols) {
        for (final AgilentTextQuantitationType qt : cols) {
            checkColumnLength(hybridizationData, AgilentTextQuantitationType.G_MEDIAN_SIGNAL, expectedColumnLength);
        }
    }

    private void checkColumnLength(HybridizationData hybridizationData, AgilentTextQuantitationType quantitationType,
            int expectedColumnLength) {
        try {
            final AbstractDataColumn column = hybridizationData.getColumn(quantitationType);
            assertNotNull(column);
            final Object array = PropertyUtils.getProperty(column, "values");
            assertEquals(expectedColumnLength, Array.getLength(array));
        } catch (final IllegalAccessException ex) {
            ex.printStackTrace();
            fail(ex.toString());
        } catch (final InvocationTargetException ex) {
            ex.printStackTrace();
            fail(ex.toString());
        } catch (final NoSuchMethodException ex) {
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

    private List<File> makeFileList(final File rawTextFile, final File... files) {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(rawTextFile);

        for (final File file : files) {
            fileList.add(file);
        }
        return fileList;
    }

    private CaArrayFile getCaArrayFile(File file, String designLsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(file, AgilentRawTextDataHandler.RAW_TXT_FILE_TYPE);
        final ArrayDesign arrayDesign =
                this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, designLsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.nih.nci.caarray.platforms.AbstractHandlerTest#createArrayDesign(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        LSID lsid;

        if (null == lsidAuthority) {
            lsid = new LSID("Agilent.com", "PhysicalArrayDesign", lsidObjectId);
        } else {
            lsid = new LSID(lsidAuthority, lsidNamespace, lsidObjectId);
        }
        final String lsidString = lsid.toString();
        final String arrayDesignLsidString = this.arrayDesign.getLsid().toString();

        return this.arrayDesign;
    }

    private void setupArrayDesign(LSID designLsid) {
        this.arrayDesign = new ArrayDesign();
        this.arrayDesign.setDesignDetails(new ArrayDesignDetails());
        this.arrayDesign.setLsid(designLsid);
        this.arrayDesign.setName(designLsid.toString());
        final CaArrayFile f = new CaArrayFile();
        f.setFileStatus(FileStatus.IMPORTED);
        f.setFileType(AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        this.arrayDesign.getDesignFiles().add(f);
    }

    @SuppressWarnings("deprecation")
    private void addProbeToDesign(String probeName) {
        final PhysicalProbe probe = new PhysicalProbe();
        probe.setName(probeName);
        this.arrayDesign.getDesignDetails().getProbes().add(probe);
    }

    @Test
    public void testGeneExpression() throws InvalidDataFileException {
        setupArrayDesign(DESIGN_LSID);
        addProbeToDesign("GE_BrightCorner");
        addProbeToDesign("DarkCorner");

        final CaArrayFile caArrayFile =
                getCaArrayFile(AgilentArrayDataFiles.GENE_EXPRESSION, DESIGN_LSID.getObjectId());
        this.arrayDataService.importData(caArrayFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, caArrayFile.getFileStatus());

        final RawArrayData rawArrayData =
                (RawArrayData) this.daoFactoryStub.getArrayDao().getArrayData(caArrayFile.getId());
        final DataSet dataSet = rawArrayData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());

        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);

        assertEquals(3, hybridizationData.getColumns().size());

        final List<AbstractDesignElement> designElements = dataSet.getDesignElementList().getDesignElements();

        final int expectedNumberOfProbes = 2;
        assertEquals(expectedNumberOfProbes, designElements.size());

        checkColumnLengths(hybridizationData, expectedNumberOfProbes, GEN_EXPRESSION_COLS);

        checkValues(hybridizationData, designElements, 0, "GE_BrightCorner", GEN_EXPRESSION_COLS, 76104.65f, 7610.465f,
                83981.0f);
        checkValues(hybridizationData, designElements, 1, "DarkCorner", GEN_EXPRESSION_COLS, 2.090352f, 2.082407f,
                32.0f);

    }

    @Test
    public void testMiRNA() throws InvalidDataFileException {
        setupArrayDesign(DESIGN_LSID);
        addProbeToDesign("miRNABrightCorner30");
        addProbeToDesign("DarkCorner");
        addProbeToDesign("A_54_P2696");
        addProbeToDesign("NegativeControl");
        addProbeToDesign("A_54_P00004465");

        final CaArrayFile caArrayFile = getCaArrayFile(AgilentArrayDataFiles.MIRNA, DESIGN_LSID.getObjectId());
        this.arrayDataService.importData(caArrayFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, caArrayFile.getFileStatus());

        final RawArrayData rawArrayData =
                (RawArrayData) this.daoFactoryStub.getArrayDao().getArrayData(caArrayFile.getId());
        final DataSet dataSet = rawArrayData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());

        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);

        assertEquals(8, hybridizationData.getColumns().size());
        final List<AbstractDesignElement> designElements = dataSet.getDesignElementList().getDesignElements();

        final int expectedNumberOfProbes = 5;
        assertEquals(expectedNumberOfProbes, designElements.size());

        checkColumnLengths(hybridizationData, expectedNumberOfProbes, MIRNA_COLS);

        checkValues(hybridizationData, designElements, 0, "miRNABrightCorner30", MIRNA_COLS, 352.2096f, 35.41912f,
                403.0f, 198.623f, 20.1593f, 198.623f, 20.1593f, true);
        checkValues(hybridizationData, designElements, 3, "NegativeControl", MIRNA_COLS, 2.194657f, 3.747745f, 55.0f,
                10.9847f, 43.2381f, 10.9847f, 43.2381f, false);
    }

    @Test
    public void testMiRNA_BlankValues() throws InvalidDataFileException {
        setupArrayDesign(DESIGN_LSID);
        addProbeToDesign("miRNABrightCorner30");
        addProbeToDesign("DarkCorner");
        addProbeToDesign("A_54_P2696");
        addProbeToDesign("NegativeControl");
        addProbeToDesign("A_54_P00004465");

        final CaArrayFile caArrayFile = getCaArrayFile(AgilentArrayDataFiles.MIRNA_BLANKS, DESIGN_LSID.getObjectId());
        final FileValidationResult results = this.arrayDataService.validate(caArrayFile, null, false);
        assertEquals(FileStatus.VALIDATION_ERRORS, caArrayFile.getFileStatus());

        final int i = 0;
        final String[] expectedErrors = {"Missing or blank ProbeName", "Missing or blank gTotalProbeSignal" };
        final String[] expectedInfo = {"Processing as miRNA (found gTotalProbeSignal w/o LogRatio)" };
        validateValidationMessages(expectedErrors, results.getMessages(ValidationMessage.Type.ERROR));
        validateValidationMessages(expectedInfo, results.getMessages(ValidationMessage.Type.INFO));
    }

    private static void validateValidationMessages(final String[] expectedMessages,
            final List<ValidationMessage> validationMessages) {
        assertEquals("The validation messages length is incorrect.", expectedMessages.length, validationMessages.size());
        for (final String expectedMessage : expectedMessages) {
            System.out.println("expectedMessage =" + expectedMessage + "=");
            boolean wasFound = false;
            for (final ValidationMessage validationMessage : validationMessages) {
                System.out.println("validationMessage.getMessage().trim() =" + validationMessage.getMessage().trim()
                        + "=");
                if (expectedMessage.equals(validationMessage.getMessage().trim())) {
                    wasFound = true;
                    break;
                }
            }
            assertTrue("This message was not found: " + expectedMessage, wasFound);
        }
    }

    @Test
    public void testFileWithErroneousLineEndings() throws InvalidDataFileException {
        setupArrayDesign(DESIGN_LSID);
        addProbeToDesign("HsCGHBrightCorner");
        addProbeToDesign("DarkCorner");

        final CaArrayFile caArrayFile =
                getCaArrayFile(AgilentArrayDataFiles.ERRONEOUS_LINE_ENDINGS, DESIGN_LSID.getObjectId());
        this.arrayDataService.importData(caArrayFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, caArrayFile.getFileStatus());

        final RawArrayData rawArrayData =
                (RawArrayData) this.daoFactoryStub.getArrayDao().getArrayData(caArrayFile.getId());
        final DataSet dataSet = rawArrayData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());

        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);

        assertEquals(9, hybridizationData.getColumns().size());

        final List<AbstractDesignElement> designElements = dataSet.getDesignElementList().getDesignElements();

        final int expectedNumberOfProbes = 2;
        assertEquals(expectedNumberOfProbes, designElements.size());

        checkColumnLengths(hybridizationData, expectedNumberOfProbes, ACGH_COLS);

        checkValues(hybridizationData, designElements, 0, "HsCGHBrightCorner", ACGH_COLS, 1363.0f, 596.5607f,
                5963.058f, 0.06193424398f, 0.0855067622f, 0.1674002912f, 1292.0f, 726.2786f, 7260.655f);
        checkValues(hybridizationData, designElements, 1, "DarkCorner", ACGH_COLS, 59.0f, 17.4904f, 23.3982f,
                0.4784317876f, -0.03151133335f, 0.9474862651f, 62.5f, 17.67106f, 21.76061f);
    }

    @Test
    public void testUnparsedDesign() throws InvalidDataFileException {
        setupArrayDesign(DESIGN_LSID);
        this.arrayDesign.setDesignDetails(null);
        final CaArrayFile f = new CaArrayFile();
        f.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        f.setFileType(new FileType("MAGE_TAB_ADF", FileCategory.ARRAY_DESIGN, false));
        this.arrayDesign.getDesignFiles().clear();
        this.arrayDesign.getDesignFiles().add(f);
        this.arrayDesign.setName("Foo");

        final CaArrayFile caArrayFile = getCaArrayFile(AgilentArrayDataFiles.MIRNA, DESIGN_LSID.getObjectId());
        final FileValidationResult results = this.arrayDataService.validate(caArrayFile, null, false);
        assertEquals(FileStatus.VALIDATED_NOT_PARSED, caArrayFile.getFileStatus());
    }

}
