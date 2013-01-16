//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.magetab.adf.AdfDocument;
import gov.nih.nci.caarray.magetab.data.DataMatrix;
import gov.nih.nci.caarray.magetab.data.NativeDataFile;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.test.data.magetab.SdrfTestFiles;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Locale;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * MAGE-TAB document sets to be used as test data.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class TestMageTabSets {
    @Inject
    private static FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Sets.<DataFileHandler> newHashSet(),
            Sets.<DesignFileHandler> newHashSet());

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
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_INPUT_SET =
            getSpecificationCaseSensitivityInputSet();

    /**
     * Example set of documents based on the MAGE-TAB specification example, with no array design ref in the SDRF.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET =
            getSpecificationNoArrayDesignInputSet();

    /**
     * Example set of documents based on the MAGE-TAB specification example, with no experiment description in the IDF.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET =
            getSpecificationNoExpDescInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification, minus the data matrix
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_NO_DATA_MATRIX_INPUT_SET =
            getSpecificationWithoutDataMatrixInputSet();

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
     * MAGE-TAB input set containing invalid usage of Characteristics[ExternalSampleId] for Sample(s).
     */
    public static final MageTabFileSet INVALID_FEATURE_13141_INPUT_SET = getInvalidFeature13141InputSet();

    /**
     * Invalid MAGE-TAB input set containing multiple IDF files.
     */
    public static final MageTabFileSet INVALID_DUPLICATE_TERM_SOURCES_INPUT_SET =
            getInvalidDuplicateTermSourcesInputSet();

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
    public static final MageTabFileSet DEFECT_13164_GOOD = getDefect13164GoodInputSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabFileSet DEFECT_13164_BAD = getDefect13164BadInputSet();

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
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET =
            getSpecificationUpdateAnnotationsInputSet();

    /**
     * MAGE-TAB input set based on the base specification set with some changes, as well as an additional biomaterial
     * chain.
     */
    public static final MageTabFileSet MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_INPUT_SET =
            getSpecificationUpdateAnnotationsAddBmInputSet();

    /**
     * MAGE-TAB input set to use as the baseline for later changes to the biomaterial chain.
     */
    public static final MageTabFileSet UPDATE_BIO_MATERIAL_CHAIN_BASELINE_INPUT_SET =
            getUpdateBioMaterialChainBaselineInputSet();

    /**
     * MAGE-TAB input set to use to update the biomaterial chain defined in UPDATE_BIO_MATERIAL_CHAIN_BASELINE_SET.
     */
    public static final MageTabFileSet UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_INPUT_SET =
            getUpdateBioMaterialChainNewBioMaterialsInputSet();

    /**
     * MAGE-TAB input set to use to add new data files to the biomaterial chain defined in
     * UPDATE_BIO_MATERIAL_CHAIN_BASELINE_SET.
     */
    public static final MageTabFileSet UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_INPUT_SET =
            getUpdateBioMaterialChainNewDataFilesInputSet();

    /**
     * MAGE-TAB input set to use as the baseline for later changes to the files
     */
    public static final MageTabFileSet UPDATE_FILES_BASELINE_INPUT_SET = getUpdateFilesBaselineInputSet();

    /**
     * MAGE-TAB input set to use to add new data files to the biomaterial chain defined in
     * UPDATE_BIO_MATERIAL_CHAIN_BASELINE_SET.
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
    
    /**
     * MAGE-TAB input set for testing that a term source with same name and different url results in a new term source, not a rename (jira issue: https://tracker.nci.nih.gov/browse/ARRAY-1927)
     */
    public static final MageTabFileSet GETS_NEW_TERM_SOURCE_INPUT_SET = getTestGetsNewTermSourceInputSet();

    public static final MageTabFileSet MULTI_DERIVED_1_INPUT_SET = getSdrfTestInputSet(
            SdrfTestFiles.MULTI_DERIVED_1_IDF, SdrfTestFiles.MULTI_DERIVED_1_SDRF);
    public static final MageTabFileSet MULTI_NORMALIZATION_1_INPUT_SET = getSdrfTestInputSet(
            SdrfTestFiles.MULTI_NORMALIZATION_1_IDF, SdrfTestFiles.MULTI_NORMALIZATION_1_SDRF);
    public static final MageTabFileSet MULTI_NO_SCAN_1_INPUT_SET = getSdrfTestInputSet(
            SdrfTestFiles.MULTI_NO_SCAN_1_IDF, SdrfTestFiles.MULTI_NO_SCAN_1_SDRF);
    public static final MageTabFileSet MULTI_NO_SCAN_2_INPUT_SET = getSdrfTestInputSet(
            SdrfTestFiles.MULTI_NO_SCAN_2_IDF, SdrfTestFiles.MULTI_NO_SCAN_2_SDRF);
    public static final MageTabFileSet MULTI_NO_SCAN_3_INPUT_SET = getSdrfTestInputSet(
            SdrfTestFiles.MULTI_NO_SCAN_3_IDF, SdrfTestFiles.MULTI_NO_SCAN_3_SDRF);
    public static final MageTabFileSet NO_DERIVED_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NO_DERIVED_1_IDF,
            SdrfTestFiles.NO_DERIVED_1_SDRF);
    public static final MageTabFileSet NO_DERIVED_2_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NO_DERIVED_2_IDF,
            SdrfTestFiles.NO_DERIVED_2_SDRF);
    public static final MageTabFileSet NORMAL_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NORMAL_1_IDF,
            SdrfTestFiles.NORMAL_1_SDRF);
    public static final MageTabFileSet NORMAL_2_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NORMAL_2_IDF,
            SdrfTestFiles.NORMAL_2_SDRF);
    public static final MageTabFileSet NORMAL_3_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.NORMAL_3_IDF,
            SdrfTestFiles.NORMAL_3_SDRF);
    public static final MageTabFileSet EXTERNAL_ID_1_INPUT_SET = getSdrfTestInputSet(SdrfTestFiles.EXTERNAL_ID_1_IDF,
            SdrfTestFiles.EXTERNAL_ID_1_SDRF);

    /**
     * MAGE-TAB input set producing defect 27306
     * ("Second-level derived data does not have its derived_from link set...")
     */
    public static final MageTabFileSet DEFECT_27306_INPUT_SET = getDefect27306InputSet();

    private static MageTabFileSet getValidFeature13141InputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_SDRF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_SDRF2));
        addCelFiles(fileSet, MageTabDataFiles.FEATURE_13141_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getInvalidFeature13141InputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_INVALID_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_INVALID_SDRF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.FEATURE_13141_INVALID_SDRF2));
        addCelFiles(fileSet, MageTabDataFiles.FEATURE_13141_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getInvalidDuplicateTermSourcesInputSet() {
        final MageTabFileSet mageTabFileSet = new MageTabFileSet();
        mageTabFileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DUPLICATE_TERM_SOURCES_INVALID_IDF));
        mageTabFileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DUPLICATE_TERM_SOURCES_INVALID_SDRF));
        addCelFiles(mageTabFileSet, MageTabDataFiles.DUPLICATE_TERM_SOURCES_DIRECTORY);
        addChpFiles(mageTabFileSet, MageTabDataFiles.DUPLICATE_TERM_SOURCES_DIRECTORY);
        return mageTabFileSet;
    }

    private static MageTabFileSet getMisplacedFactorValuesInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.MISPLACED_FACTOR_VALUES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.MISPLACED_FACTOR_VALUES_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getEbiTemplateInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.EBI_TEMPLATE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.EBI_TEMPLATE_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getTcgaBroadInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
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
        final FilenameFilter filter = createExtensionFilter(extension);
        final File[] celFiles = dataFileDirectory.listFiles(filter);
        for (final File file : celFiles) {
            fileSet.addNativeData(new JavaIOFileRef(file));
        }
    }

    private static MageTabFileSet getPerformanceTest10InputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.PERFORMANCE_10_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.PERFORMANCE_10_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.PERFORMANCE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDataMatrixCopyNumberGoodInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_DATA));
        return fileSet;
    }

    private static MageTabFileSet getDataMatrixCopyNumberBadInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_DATA));
        return fileSet;
    }

    private static MageTabFileSet getSpecificationInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationCaseSensitivityInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationNoArrayDesignInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_ARRAY_DESIGN_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationNoExpDescInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_EXP_DESC_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationWithoutDataMatrixInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getUnsupportedDataInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_ADF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY);
        fileSet.addNativeData(new JavaIOFileRef(new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY,
                "unsupported.mas5.exp")));
        return fileSet;
    }

    private static MageTabFileSet getGedpSpecificationInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GEDP_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GEDP_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getErrorSpecificationInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getCaarray1xInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.CAARRAY1X_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.CAARRAY1X_SDRF));
        addDataFiles(fileSet, MageTabDataFiles.CAARRAY1X_DIRECTORY, "gpr");
        return fileSet;
    }

    private static MageTabFileSet getGskTestSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GSK_TEST_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GSK_TEST_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        addExpFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDefect12537InputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ABSOLUTE_DATA_MATRIX));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_RMA_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDefect12537ErrorInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR__IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR_ABSOLUTE_DATA_MATRIX));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.DEFECT_12537_ERROR_RMA_DATA_MATRIX));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_ERROR_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDerivedDataInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_SDRF));
        fileSet.addAdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_ADF));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DIRECTORY);
        addChpFiles(fileSet, MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationUpdateAnnotationsInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_SDRF));
        return fileSet;
    }

    private static MageTabFileSet getSpecificationUpdateAnnotationsAddBmInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DIRECTORY);
        fileSet.addDataMatrix(new JavaIOFileRef(
                MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DATA_MATRIX_FILE));
        return fileSet;
    }

    private static MageTabFileSet getDefect16421ErrorInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_16421_CEL);
        return fileSet;
    }

    private static MageTabFileSet getDefect17200InputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_17200_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_17200_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.DEFECT_17200_GPR));
        fileSet.addNativeData(new JavaIOFileRef(GenepixArrayDataFiles.GPR_3_0_6));
        return fileSet;
    }

    private static MageTabFileSet getDefect16421ErrorInputSet2() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_2_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_16421_2_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.INVALID_COLUMN_ORDER_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getInvalidNodeOrderSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.INVALID_NODE_ORDER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.INVALID_NODE_ORDER_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.INVALID_COLUMN_ORDER_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getNoBiomaterialSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.NO_BIOMATERIAL_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.NO_BIOMATERIAL_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.MISSING_COLUMNS_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getNoHybridizationSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.NO_HYBRIDIZATION_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.NO_HYBRIDIZATION_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.MISSING_COLUMNS_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getNoDataFileSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.NO_DATA_FILE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.NO_DATA_FILE_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.MISSING_COLUMNS_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainBaselineInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_BASELINE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_BASELINE_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_2));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1));
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainNewBioMaterialsInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_3));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_4));
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainNewDataFilesInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1A));
        return fileSet;
    }

    private static MageTabFileSet getUpdateFilesBaselineInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_BASELINE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_BASELINE_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_CEL2));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_CEL1));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_EXP1));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_EXP2));
        return fileSet;
    }

    private static MageTabFileSet getUpdateFilesNewInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_NEW_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_NEW_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_CEL1A));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.UPDATE_FILES_EXP2A));
        return fileSet;
    }

    private static MageTabFileSet getDefect27306InputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
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
        final CaArrayFileSet fileSet = new CaArrayFileSet();
        addFiles(fileSet, documentSet.getIdfDocuments());
        addFiles(fileSet, documentSet.getSdrfDocuments());
        addFiles(fileSet, documentSet.getAdfDocuments());
        addFiles(fileSet, documentSet.getDataMatrixes());
        addFiles(fileSet, documentSet.getNativeDataFiles());
        return fileSet;
    }

    public static CaArrayFileSet getFileSet(MageTabFileSet inputSet) {
        return getFileSet(false, inputSet);
    }

    public static CaArrayFileSet getFileSet(boolean dataMtricesAreCopyNumber, MageTabFileSet inputSet) {
        final CaArrayFileSet fileSet = new CaArrayFileSet();
        addFiles(dataMtricesAreCopyNumber, fileSet, inputSet.getIdfFiles(), IdfDocument.class);
        addFiles(dataMtricesAreCopyNumber, fileSet, inputSet.getSdrfFiles(), SdrfDocument.class);
        addFiles(dataMtricesAreCopyNumber, fileSet, inputSet.getDataMatrixFiles(), DataMatrix.class);
        addFiles(dataMtricesAreCopyNumber, fileSet, inputSet.getNativeDataFiles(), NativeDataFile.class);
        return fileSet;
    }

    private static void
            addFiles(CaArrayFileSet fileSet, Collection<? extends AbstractMageTabDocument> mageTabDocuments) {
        for (final AbstractMageTabDocument mageTabDocument : mageTabDocuments) {
            addFile(fileSet, mageTabDocument.getFile().getName(), mageTabDocument.getClass());
        }
    }

    private static void addFiles(CaArrayFileSet fileSet, Collection<FileRef> files,
            Class<? extends AbstractMageTabDocument> documentType) {
        for (final FileRef file : files) {
            addFile(fileSet, file.getName(), documentType);
        }
    }

    private static void addFiles(boolean dataMatricesAreCopyNumber, CaArrayFileSet fileSet, Collection<FileRef> files,
            Class<? extends AbstractMageTabDocument> documentType) {
        for (final FileRef file : files) {
            addFile(dataMatricesAreCopyNumber, fileSet, file.getName(), documentType);
        }
    }

    // TODO: ARRAY-1942 follow-on tasks for <ARRAY-1896 Merge dkokotov_storage_osgi_consolidation Branch to trunk>
    // This method depends on string literals (such as "MAGE_TAB_ADF" and "MAGE_TAB_DATA_MATRIX_COPY_NUMBER")
    // instead of relying on the appropriate named constants. We could not depend on the named constants because
    // they are defined in modules/packages that this class should not depend on. E.g. MAGE_TAB_ADF is defined in
    // gov.nih.nci.caarray.platforms.unparsed.UnparsedArrayDesignFileHandler in module caarray-ejb.jar
    // Some refactoring needs to be done to correctly align the dependencies.
    // Note that the purpose of the method below is to map a AbstractMageTabDocument type to its corresponding FileType.
    private static FileType guessFileType(boolean dataMatricesAreCopyNumber, String fileName,
            Class<? extends AbstractMageTabDocument> documentType) {
        if (IdfDocument.class.equals(documentType)) {
            return FileTypeRegistry.MAGE_TAB_IDF;
        } else if (SdrfDocument.class.equals(documentType)) {
            return FileTypeRegistry.MAGE_TAB_SDRF;
        } else if (AdfDocument.class.equals(documentType)) {
            return typeRegistry.getTypeByName("MAGE_TAB_ADF");
        } else if (DataMatrix.class.equals(documentType)) {
            if (dataMatricesAreCopyNumber) {
                return typeRegistry.getTypeByName("MAGE_TAB_DATA_MATRIX_COPY_NUMBER");
            } else {
                return typeRegistry.getTypeByName("MAGE_TAB_DATA_MATRIX");
            }
        } else {
            return typeRegistry.getTypeFromExtension(fileName);
        }
    }

    /**
     * @return whether the given fileRef refers to a data matrix file within the given mage-tab fileset
     */
    public static boolean isDataMatrix(FileRef fileRef, MageTabFileSet fileSet) {
        return fileSet.getDataMatrixFiles().contains(fileRef);
    }

    private static void addFile(CaArrayFileSet fileSet, String name,
            Class<? extends AbstractMageTabDocument> documentType) {
        addFile(false, fileSet, name, documentType);
    }

    private static void addFile(boolean dataMatricesAreCopyNumber, CaArrayFileSet fileSet, String name,
            Class<? extends AbstractMageTabDocument> documentType) {
        final CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        caArrayFile.setName(name);
        caArrayFile.setFileType(guessFileType(dataMatricesAreCopyNumber, name, documentType));
        fileSet.add(caArrayFile);
    }

    private static MageTabFileSet getExtendedFactorValuesInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.EXTENDED_FACTOR_VALUES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.EXTENDED_FACTOR_VALUES_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.EXTENDED_FACTOR_VALUES_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getRenamingTermSourcesInputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.RENAMING_TERM_SOURCES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.RENAMING_TERM_SOURCES_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.RENAMING_TERM_SOURCES_DIRECTORY);
        addChpFiles(fileSet, MageTabDataFiles.RENAMING_TERM_SOURCES_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getTestGetsNewTermSourceInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GETS_NEW_TERM_SOURCE_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GETS_NEW_TERM_SOURCE_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.GETS_NEW_TERM_SOURCE_SDRF.getParentFile());
        return fileSet;
    }
     
    private static MageTabFileSet getDefect27959InputSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_27959_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_27959_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.DEFECT_27959_DERIVED_DATA_FILE));
        return fileSet;
    }

    private static MageTabFileSet getDefect13164GoodInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_13164_GOOD_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_13164_GOOD_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_13164_SDRF.getParentFile());
        addChpFiles(fileSet, MageTabDataFiles.DEFECT_13164_SDRF.getParentFile());
        return fileSet;
    }
    
    private static MageTabFileSet getDefect13164BadInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DEFECT_13164_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DEFECT_13164_SDRF));
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_13164_SDRF.getParentFile());
        addChpFiles(fileSet, MageTabDataFiles.DEFECT_13164_SDRF.getParentFile());
        return fileSet;
    }
    
    private static MageTabFileSet getBadVocabularyTermSourcesFileSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.BAD_VOCABULARY_TERM_SOURCES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.BAD_VOCABULARY_TERM_SOURCES_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CEL));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CHP));
        return fileSet;
    }

    private static MageTabFileSet getGoodVocabularyTermSourcesFileSet() {
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GOOD_VOCABULARY_TERM_SOURCES_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GOOD_VOCABULARY_TERM_SOURCES_SDRF));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CEL));
        fileSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.TESTING_VOCABULARY_TERM_SOURCES_CHP));
        return fileSet;
    }

    private static MageTabFileSet getSdrfTestInputSet(File idf, File sdrf) {
        final MageTabFileSet mtfs = new MageTabFileSet();
        mtfs.addIdf(new JavaIOFileRef(idf));
        mtfs.addSdrf(new JavaIOFileRef(sdrf));
        mtfs.addNativeData(new JavaIOFileRef(SdrfTestFiles.TEST_1_CEL));
        mtfs.addNativeData(new JavaIOFileRef(SdrfTestFiles.TEST_2_CEL));
        mtfs.addDataMatrix(new JavaIOFileRef(SdrfTestFiles.TEST_1_DATA));
        mtfs.addDataMatrix(new JavaIOFileRef(SdrfTestFiles.TEST_2_DATA));
        return mtfs;
    }

}
