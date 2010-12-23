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
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileExtension;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.adf.AdfDocument;
import gov.nih.nci.caarray.magetab.data.DataMatrix;
import gov.nih.nci.caarray.magetab.data.NativeDataFile;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import gov.nih.nci.caarray.test.data.magetab.SdrfTestFiles;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Locale;

/**
 * MAGE-TAB document sets to be used as test data.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class TestMageTabSets {

    private TestMageTabSets() {
        super();
    }

    /**
     * Example set of documents included with MAGE-TAB specification.
     */
    public static final MageTabFileSet MAGE_TAB_UNSUPPORTED_DATA_INPUT_SET = getUnsupportedDataInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification.
     */
    public static final MageTabFileSet MAGE_TAB_MISPLACED_FACTOR_VALUES_INPUT_SET = getMisplacedFactorValuesInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_INPUT_SET = getSpecificationInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_INPUT_SET = getSpecificationCaseSensitivityInputSet();

    /**
     * Example set of documents based on the MAGE-TAB specification example, with no array design ref in the SDRF.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET = getSpecificationNoArrayDesignInputSet();

    /**
     * Example set of documents based on the MAGE-TAB specification example, with no experiment description in the IDF.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET = getSpecificationNoExpDescInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification, minus the data matrix
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_NO_DATA_MATRIX_INPUT_SET = getSpecificationWithoutDataMatrixInputSet();

    /**
     * Example set of documents with ERRORS based on the MAGE-TAB specification.
     */
    public static final MageTabFileSet MAGE_TAB_ERROR_SPECIFICATION_INPUT_SET = getErrorSpecificationInputSet();

    /**
     * Example set of documents with ERRORS based on the MAGE-TAB specification.
     */
    public static final MageTabFileSet MAGE_TAB_GEDP_INPUT_SET = getGedpSpecificationInputSet();

    /**
     * Example set of MAGE-TAB data with 10 large CEL files and no derived data.
     */
    public static final MageTabFileSet PERFORMANCE_TEST_10_INPUT_SET = getPerformanceTest10InputSet();
    
    /**
     * good MAGE-TAB data matrix copy number data import files.
     */
    public static final MageTabFileSet GOOD_DATA_MATRIX_COPY_NUMBER_INPUT_SET = getDataMatrixCopyNumberGoodInputSet();
    
    /**
     * bad MAGE-TAB data matrix copy number data import files.
     */
    public static final MageTabFileSet BAD_DATA_MATRIX_COPY_NUMBER_INPUT_SET = getDataMatrixCopyNumberBadInputSet();

    /**
     * MAGE-TAB input set from TCGA Broad data.
     */
    public static final MageTabFileSet TCGA_BROAD_INPUT_SET = getTcgaBroadInputSet();

    /**
     * MAGE-TAB input set derived from EBI generated template.
     */
    public static final MageTabFileSet EBI_TEMPLATE_INPUT_SET = getEbiTemplateInputSet();

    /**
     * MAGE-TAB input set derived from 1x export data
     */
    public static final MageTabFileSet CAARRAY1X_INPUT_SET = getCaarray1xInputSet();

    /**
     * MAGE-TAB input set from TCGA Broad data.
     */
    public static final MageTabFileSet DEFECT_12537_INPUT_SET = getDefect12537InputSet();

    /**
     * MAGE-TAB input set from TCGA Broad data.
     */
    public static final MageTabFileSet DEFECT_12537_ERROR_INPUT_SET = getDefect12537ErrorInputSet();

    /**
     * Example set of MAGE-TAB data.
     */
    public static final MageTabFileSet GSK_TEST_INPUT_SET = getGskTestSet();

    /**
     * MAGE-TAB data set containing data derived from other derived data.
     */
    public static final MageTabFileSet DERIVED_DATA_INPUT_SET = getDerivedDataInputSet();

    /**
     * MAGE-TAB input set containing valid usage of Characteristics[ExternalSampleId] for Sample(s).
     */
    public static final MageTabFileSet VALID_FEATURE_13141_INPUT_SET = getValidFeature13141InputSet();

    /**
     * MAGE-TAB input set containing valid usage of Characteristics[ExternalSampleId] for Sample(s).
     */
    public static final MageTabFileSet INVALID_FEATURE_13141_INPUT_SET = getInvalidFeature13141InputSet();
    
    /**
     * Invalid MAGE-TAB input set containing multiple IDF files.
     */
    public static final MageTabFileSet INVALID_DUPLICATE_TERM_SOURCES_INPUT_SET = getInvalidDuplicateTermSourcesInputSet();
    
    /**
     * Document set parsed ...
     */
    public static final MageTabFileSet DEFECT_16421 = getDefect16421ErrorInputSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabFileSet DEFECT_17200 = getDefect17200InputSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabFileSet DEFECT_27959 = getDefect27959InputSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabFileSet BAD_VOCABULARY_TERM_SOURCES_FILE_SET = getBadVocabularyTermSourcesFileSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabFileSet GOOD_VOCABULARY_TERM_SOURCES_FILE_SET = getGoodVocabularyTermSourcesFileSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabFileSet DEFECT_16421_2 = getDefect16421ErrorInputSet2();

    /**
     * MAGE-TAB input set for testing node column order validation
     */
    public static final MageTabFileSet INVALID_NODE_ORDER_SET = getInvalidNodeOrderSet();

    /**
     * MAGE-TAB input set for testing missing biomaterial columns validation
     */
    public static final MageTabFileSet NO_BIOMATERIAL_SET = getNoBiomaterialSet();

    /**
     * MAGE-TAB input set for testing missing hyb columns validation
     */
    public static final MageTabFileSet NO_HYBRIDIZATION_SET = getNoHybridizationSet();

    /**
     * MAGE-TAB input set for testing missing data file columns validation
     */
    public static final MageTabFileSet NO_DATA_FILE_SET = getNoDataFileSet();

    
    /**
     * MAGE-TAB input set based on the base specification set with some changes.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET = getSpecificationUpdateAnnotationsInputSet();

    /**
     * MAGE-TAB input set based on the base specification set with some changes, as well as an additional biomaterial chain.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_INPUT_SET = getSpecificationUpdateAnnotationsAddBmInputSet();

    /**
     * MAGE-TAB input set to use as the baseline for later changes to the biomaterial chain.
     */
    public static final MageTabFileSet UPDATE_BIO_MATERIAL_CHAIN_BASELINE_INPUT_SET = getUpdateBioMaterialChainBaselineInputSet();
    
    /**
     * MAGE-TAB input set to use to update the biomaterial chain defined in UPDATE_BIO_MATERIAL_CHAIN_BASELINE_SET.
     */
    public static final MageTabFileSet UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_INPUT_SET = getUpdateBioMaterialChainNewBioMaterialsInputSet();

    /**
     * MAGE-TAB input set to use to add new data files to the biomaterial chain defined in UPDATE_BIO_MATERIAL_CHAIN_BASELINE_SET.
     */
    public static final MageTabFileSet UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_INPUT_SET = getUpdateBioMaterialChainNewDataFilesInputSet();

    /**
     * MAGE-TAB input set to use as the baseline for later changes to the files
     */
    public static final MageTabFileSet UPDATE_FILES_BASELINE_INPUT_SET = getUpdateFilesBaselineInputSet();
    
    /**
     * MAGE-TAB input set to use to add new data files to the biomaterial chain defined in UPDATE_BIO_MATERIAL_CHAIN_BASELINE_SET.
     */
    public static final MageTabFileSet UPDATE_FILES_NEW_INPUT_SET = getUpdateFilesNewInputSet();

    /**
     * MAGE-TAB input set containing valid usage of Characteristics[ExternalSampleId] for Sample(s).
     */
    public static final MageTabFileSet EXTENDED_FACTOR_VALUES_INPUT_SET = getExtendedFactorValuesInputSet();
    
    /**
     * MAGE-TAB input set for testing renaming of term sources upon import (GForge 27244)
     */
    public static final MageTabFileSet RENAMING_TERM_SOURCES_INPUT_SET = getRenamingTermSourcesInputSet();

    public static final MageTabFileSet MULTI_DERIVED_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.MULTI_DERIVED_1_IDF, SdrfTestFiles.MULTI_DERIVED_1_SDRF);
    public static final MageTabFileSet MULTI_NORMALIZATION_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.MULTI_NORMALIZATION_1_IDF, SdrfTestFiles.MULTI_NORMALIZATION_1_SDRF);
    public static final MageTabFileSet MULTI_NO_SCAN_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.MULTI_NO_SCAN_1_IDF, SdrfTestFiles.MULTI_NO_SCAN_1_SDRF);
    public static final MageTabFileSet MULTI_NO_SCAN_2_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.MULTI_NO_SCAN_2_IDF, SdrfTestFiles.MULTI_NO_SCAN_2_SDRF);
    public static final MageTabFileSet MULTI_NO_SCAN_3_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.MULTI_NO_SCAN_3_IDF, SdrfTestFiles.MULTI_NO_SCAN_3_SDRF);
    public static final MageTabFileSet NO_DERIVED_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NO_DERIVED_1_IDF, SdrfTestFiles.NO_DERIVED_1_SDRF);
    public static final MageTabFileSet NO_DERIVED_2_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NO_DERIVED_2_IDF, SdrfTestFiles.NO_DERIVED_2_SDRF);
    public static final MageTabFileSet NORMAL_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NORMAL_1_IDF, SdrfTestFiles.NORMAL_1_SDRF);
    public static final MageTabFileSet NORMAL_2_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NORMAL_2_IDF, SdrfTestFiles.NORMAL_2_SDRF);
    public static final MageTabFileSet NORMAL_3_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NORMAL_3_IDF, SdrfTestFiles.NORMAL_3_SDRF);

    /**
     * MAGE-TAB input set producing defect 27306 ("Second-level derived data does not have its derived_from link set...")
     */
    public static final MageTabFileSet DEFECT_27306_INPUT_SET = getDefect27306InputSet();


    private static MageTabFileSet getValidFeature13141InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_SDRF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_SDRF2));
        addCelFiles(fileSet, MageTabDataFiles.FEATURE_13141_DIRECTORY);
        return fileSet;
    }
    private static MageTabFileSet getInvalidFeature13141InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_INVALID_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_INVALID_SDRF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_INVALID_SDRF2));
        addCelFiles(fileSet, MageTabDataFiles.FEATURE_13141_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getInvalidDuplicateTermSourcesInputSet() {
        MageTabFileSet mageTabFileSet = new MageTabFileSet();
        mageTabFileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DUPLICATE_TERM_SOURCES_INVALID_IDF));
        mageTabFileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DUPLICATE_TERM_SOURCES_INVALID_SDRF));
        addCelFiles(mageTabFileSet, MageTabDataFiles.DUPLICATE_TERM_SOURCES_DIRECTORY);
        addChpFiles(mageTabFileSet, MageTabDataFiles.DUPLICATE_TERM_SOURCES_DIRECTORY);
        return mageTabFileSet;
    }

    private static MageTabFileSet getMisplacedFactorValuesInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.MISPLACED_FACTOR_VALUES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.MISPLACED_FACTOR_VALUES_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getEbiTemplateInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.EBI_TEMPLATE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.EBI_TEMPLATE_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getTcgaBroadInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.TCGA_BROAD_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.TCGA_BROAD_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.TCGA_BROAD_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY);
        return fileSet;
    }

    private static void addCelFiles(MageTabFileSet fileSet, File dataFileDirectory) {
        addDataFiles(fileSet, dataFileDirectory, "cel");
    }

    private static void addExpFiles(MageTabFileSet fileSet, File dataFileDirectory) {
        addDataFiles(fileSet, dataFileDirectory, "exp");
    }

    private static void addChpFiles(MageTabFileSet fileSet, File dataFileDirectory) {
        addDataFiles(fileSet, dataFileDirectory, "chp");
    }

    private static void addDataFiles(MageTabFileSet fileSet, File dataFileDirectory, String extension) {
        FilenameFilter filter = createExtensionFilter(extension);
        File[] celFiles = dataFileDirectory.listFiles(filter);
        for (File file : celFiles) {
            fileSet.addNativeData(new JavaIOFileRef(file));
        }
    }

    private static MageTabFileSet getPerformanceTest10InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.PERFORMANCE_10_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.PERFORMANCE_10_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.PERFORMANCE_DIRECTORY);
        return fileSet;
    }
    
    private static MageTabFileSet getDataMatrixCopyNumberGoodInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_DATA));
        return fileSet;
    }
    
    private static MageTabFileSet getDataMatrixCopyNumberBadInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_DATA));
        return fileSet;
    }

    private static MageTabFileSet getSpecificationInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationCaseSensitivityInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationNoArrayDesignInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_ARRAY_DESIGN_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationNoExpDescInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_EXP_DESC_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationWithoutDataMatrixInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getUnsupportedDataInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_ADF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY);
        fileSet.addNativeData(new JavaIOFileRef(new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY, "unsupported.mas5.exp")));
        return fileSet;
    }

    private static MageTabFileSet getGedpSpecificationInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GEDP_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GEDP_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getErrorSpecificationInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getCaarray1xInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.CAARRAY1X_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.CAARRAY1X_SDRF));
        addDataFiles(fileSet, MageTabDataFiles.CAARRAY1X_DIRECTORY, "gpr");
        return fileSet;
    }

    private static MageTabFileSet getGskTestSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GSK_TEST_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GSK_TEST_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        addExpFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDefect12537InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ABSOLUTE_DATA_MATRIX));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_RMA_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDefect12537ErrorInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR__IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR_ABSOLUTE_DATA_MATRIX));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR_RMA_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_ERROR_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDerivedDataInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_ADF));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DIRECTORY);
        addChpFiles(fileSet, MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationUpdateAnnotationsInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getSpecificationUpdateAnnotationsAddBmInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DIRECTORY);
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DATA_MATRIX_FILE));
        return fileSet;
    }

    private static MageTabFileSet getDefect16421ErrorInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_16421_CEL);
        return fileSet;
    }

    private static MageTabFileSet getDefect17200InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_17200_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_17200_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.DEFECT_17200_GPR));
        fileSet.addNativeData(new JavaIOFileRef(GenepixArrayDataFiles.GPR_3_0_6));
        return fileSet;
    }

    private static MageTabFileSet getDefect16421ErrorInputSet2() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_2_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_2_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.INVALID_COLUMN_ORDER_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getInvalidNodeOrderSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.INVALID_NODE_ORDER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.INVALID_NODE_ORDER_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.INVALID_COLUMN_ORDER_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getNoBiomaterialSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.NO_BIOMATERIAL_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.NO_BIOMATERIAL_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.MISSING_COLUMNS_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getNoHybridizationSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.NO_HYBRIDIZATION_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.NO_HYBRIDIZATION_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.MISSING_COLUMNS_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getNoDataFileSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.NO_DATA_FILE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.NO_DATA_FILE_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.MISSING_COLUMNS_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainBaselineInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_BASELINE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_BASELINE_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_2));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1));
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainNewBioMaterialsInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_3));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_4));
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainNewDataFilesInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1A));
        return fileSet;
    }

    private static MageTabFileSet getUpdateFilesBaselineInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_BASELINE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_BASELINE_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_CEL2));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_CEL1));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_EXP1));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_EXP2));
        return fileSet;
    }

    private static MageTabFileSet getUpdateFilesNewInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_NEW_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_NEW_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_CEL1A));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_EXP2A));
        return fileSet;
    }

    private static MageTabFileSet getDefect27306InputSet()
    {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_27306_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_27306_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.DEFECT_27306_CEL));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_27306_LEVEL_2));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_27306_LEVEL_3));
        return fileSet;
    }

    private static FilenameFilter createExtensionFilter(final String extension) {
        return new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase(Locale.getDefault()).endsWith("." + extension.toLowerCase(Locale.getDefault()));
            }
        };
    }

    public static CaArrayFileSet getFileSet(MageTabDocumentSet documentSet) {
        CaArrayFileSet fileSet = new CaArrayFileSet();
        addFiles(fileSet, documentSet.getIdfDocuments());
        addFiles(fileSet, documentSet.getSdrfDocuments());
        addFiles(fileSet, documentSet.getAdfDocuments());
        addFiles(fileSet, documentSet.getDataMatrixes());
        addFiles(fileSet, documentSet.getNativeDataFiles());
        return fileSet;
    }
    
    public static CaArrayFileSet getFileSet(MageTabFileSet inputSet) {
        CaArrayFileSet fileSet = new CaArrayFileSet();
        addFiles(fileSet, inputSet.getIdfFiles(), IdfDocument.class);
        addFiles(fileSet, inputSet.getSdrfFiles(), SdrfDocument.class);
        addFiles(fileSet, inputSet.getDataMatrixFiles(), DataMatrix.class);
        addFiles(fileSet, inputSet.getNativeDataFiles(), NativeDataFile.class);
        return fileSet;
    }

    private static void addFiles(CaArrayFileSet fileSet, Collection<? extends AbstractMageTabDocument> mageTabDocuments) {
        for (AbstractMageTabDocument mageTabDocument : mageTabDocuments) {
            addFile(fileSet, mageTabDocument.getFile().getName(), mageTabDocument.getClass());
        }
    }
    
    private static void addFiles(CaArrayFileSet fileSet, Collection<FileRef> files,
            Class<? extends AbstractMageTabDocument> documentType) {
        for (FileRef file : files) {
            addFile(fileSet, file.getName(), documentType);
        }
    }
    
    private static FileType guessFileType(String fileName, Class<? extends AbstractMageTabDocument> documentType) {
        if (IdfDocument.class.equals(documentType)) {
            return FileType.MAGE_TAB_IDF;
        } else if (SdrfDocument.class.equals(documentType)) {
            return FileType.MAGE_TAB_SDRF;
        } else if (AdfDocument.class.equals(documentType)) {
            return FileType.MAGE_TAB_ADF;
        } else if (DataMatrix.class.equals(documentType)) {
            return FileType.MAGE_TAB_DATA_MATRIX;
        } else {
            return FileExtension.getTypeFromExtension(fileName);
        }         
    }

    private static void addFile(CaArrayFileSet fileSet, String name, Class<? extends AbstractMageTabDocument> documentType) {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        caArrayFile.setName(name);
        caArrayFile.setFileType(guessFileType(name, documentType));
        fileSet.add(caArrayFile);
    }
     
    private static MageTabFileSet getExtendedFactorValuesInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.EXTENDED_FACTOR_VALUES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.EXTENDED_FACTOR_VALUES_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.EXTENDED_FACTOR_VALUES_DIRECTORY);
        return fileSet;
    }
     
    private static MageTabFileSet getRenamingTermSourcesInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.RENAMING_TERM_SOURCES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.RENAMING_TERM_SOURCES_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.RENAMING_TERM_SOURCES_DIRECTORY);
        addChpFiles(fileSet, MageTabDataFiles.RENAMING_TERM_SOURCES_DIRECTORY);
        return fileSet;
    }
     
    private static MageTabFileSet getDefect27959InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_27959_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_27959_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.DEFECT_27959_DERIVED_DATA_FILE));        
        return fileSet;
    }
    
    private static MageTabFileSet getBadVocabularyTermSourcesFileSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.BAD_VOCABULARY_TERM_SOURCES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.BAD_VOCABULARY_TERM_SOURCES_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CEL));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CHP));
        return fileSet;
    }
    
    private static MageTabFileSet getGoodVocabularyTermSourcesFileSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GOOD_VOCABULARY_TERM_SOURCES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GOOD_VOCABULARY_TERM_SOURCES_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CEL));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CHP));
        return fileSet;
    }


    private static MageTabFileSet getSdrfTestInputSet(File idf, File sdrf) {
        MageTabFileSet mtfs = new MageTabFileSet();
        mtfs.addIdf(new JavaIOFileRef(idf));
        mtfs.addSdrf(new JavaIOFileRef(sdrf));
        mtfs.addNativeData(new JavaIOFileRef(SdrfTestFiles.TEST_1_CEL));
        mtfs.addNativeData(new JavaIOFileRef(SdrfTestFiles.TEST_2_CEL));
        mtfs.addDataMatrix(new JavaIOFileRef(SdrfTestFiles.TEST_1_DATA));
        mtfs.addDataMatrix(new JavaIOFileRef(SdrfTestFiles.TEST_2_DATA));
        return mtfs;
    }

}
