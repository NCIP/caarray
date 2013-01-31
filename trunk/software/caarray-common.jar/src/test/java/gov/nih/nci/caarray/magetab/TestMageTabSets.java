//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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
    public static final MageTabFileSet DEFECT_16421_2 = getDefect16421ErrorInputSet2();

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


    private static MageTabFileSet getValidFeature13141InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.FEATURE_13141_IDF);
        fileSet.addSdrf(MageTabDataFiles.FEATURE_13141_SDRF);
        fileSet.addSdrf(MageTabDataFiles.FEATURE_13141_SDRF2);
        addCelFiles(fileSet, MageTabDataFiles.FEATURE_13141_DIRECTORY);
        return fileSet;
    }
    private static MageTabFileSet getInvalidFeature13141InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.FEATURE_13141_INVALID_IDF);
        fileSet.addSdrf(MageTabDataFiles.FEATURE_13141_INVALID_SDRF);
        fileSet.addSdrf(MageTabDataFiles.FEATURE_13141_INVALID_SDRF2);
        addCelFiles(fileSet, MageTabDataFiles.FEATURE_13141_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getInvalidDuplicateTermSourcesInputSet() {
        MageTabFileSet mageTabFileSet = new MageTabFileSet();
        mageTabFileSet.addIdf(MageTabDataFiles.DUPLICATE_TERM_SOURCES_INVALID_IDF);
        mageTabFileSet.addSdrf(MageTabDataFiles.DUPLICATE_TERM_SOURCES_INVALID_SDRF);
        addCelFiles(mageTabFileSet, MageTabDataFiles.DUPLICATE_TERM_SOURCES_DIRECTORY);
        addChpFiles(mageTabFileSet, MageTabDataFiles.DUPLICATE_TERM_SOURCES_DIRECTORY);
        return mageTabFileSet;
    }

    private static MageTabFileSet getMisplacedFactorValuesInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.MISPLACED_FACTOR_VALUES_IDF);
        fileSet.addSdrf(MageTabDataFiles.MISPLACED_FACTOR_VALUES_SDRF);
        return fileSet;
    }

    private static MageTabFileSet getEbiTemplateInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.EBI_TEMPLATE_IDF);
        fileSet.addSdrf(MageTabDataFiles.EBI_TEMPLATE_SDRF);
        return fileSet;
    }

    private static MageTabFileSet getTcgaBroadInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.TCGA_BROAD_IDF);
        fileSet.addSdrf(MageTabDataFiles.TCGA_BROAD_SDRF);
        fileSet.addDataMatrix(MageTabDataFiles.TCGA_BROAD_DATA_MATRIX);
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
            fileSet.addNativeData(file);
        }
    }

    private static MageTabFileSet getPerformanceTest10InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.PERFORMANCE_10_IDF);
        fileSet.addSdrf(MageTabDataFiles.PERFORMANCE_10_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.PERFORMANCE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationCaseSensitivityInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_SDRF);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_CASE_SENSITIVITY_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationNoArrayDesignInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_ARRAY_DESIGN_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationNoExpDescInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_EXP_DESC_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationWithoutDataMatrixInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getUnsupportedDataInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY);
        fileSet.addNativeData(new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY, "unsupported.mas5.exp"));
        return fileSet;
    }

    private static MageTabFileSet getGedpSpecificationInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.GEDP_IDF);
        fileSet.addSdrf(MageTabDataFiles.GEDP_SDRF);
        return fileSet;
    }

    private static MageTabFileSet getErrorSpecificationInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_SDRF);
        return fileSet;
    }

    private static MageTabFileSet getCaarray1xInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.CAARRAY1X_IDF);
        fileSet.addSdrf(MageTabDataFiles.CAARRAY1X_SDRF);
        addDataFiles(fileSet, MageTabDataFiles.CAARRAY1X_DIRECTORY, "gpr");
        return fileSet;
    }

    private static MageTabFileSet getGskTestSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.GSK_TEST_IDF);
        fileSet.addSdrf(MageTabDataFiles.GSK_TEST_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        addExpFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDefect12537InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.DEFECT_12537_IDF);
        fileSet.addSdrf(MageTabDataFiles.DEFECT_12537_SDRF);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_ABSOLUTE_DATA_MATRIX);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_RMA_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDefect12537ErrorInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.DEFECT_12537_ERROR__IDF);
        fileSet.addSdrf(MageTabDataFiles.DEFECT_12537_ERROR_SDRF);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_ERROR_ABSOLUTE_DATA_MATRIX);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_ERROR_RMA_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_ERROR_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getDerivedDataInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_ADF);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DIRECTORY);
        addChpFiles(fileSet, MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationUpdateAnnotationsInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_SDRF);
        return fileSet;
    }

    private static MageTabFileSet getSpecificationUpdateAnnotationsAddBmInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DIRECTORY);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DATA_MATRIX_FILE);
        return fileSet;
    }

    private static MageTabFileSet getDefect16421ErrorInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.DEFECT_16421_IDF);
        fileSet.addSdrf(MageTabDataFiles.DEFECT_16421_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_16421_CEL);
        return fileSet;
    }

    private static MageTabFileSet getDefect17200InputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.DEFECT_17200_IDF);
        fileSet.addSdrf(MageTabDataFiles.DEFECT_17200_SDRF);
        fileSet.addNativeData(MageTabDataFiles.DEFECT_17200_GPR);
        fileSet.addNativeData(GenepixArrayDataFiles.GPR_3_0_6);
        return fileSet;
    }

    private static MageTabFileSet getDefect16421ErrorInputSet2() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.DEFECT_16421_2_IDF);
        fileSet.addSdrf(MageTabDataFiles.DEFECT_16421_2_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_16421_2_CEL);
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainBaselineInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_BASELINE_IDF);
        fileSet.addSdrf(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_BASELINE_SDRF);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_2);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1);
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainNewBioMaterialsInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_IDF);
        fileSet.addSdrf(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_SDRF);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_3);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_4);
        return fileSet;
    }

    private static MageTabFileSet getUpdateBioMaterialChainNewDataFilesInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_IDF);
        fileSet.addSdrf(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_SDRF);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1A);
        return fileSet;
    }

    private static MageTabFileSet getUpdateFilesBaselineInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.UPDATE_FILES_BASELINE_IDF);
        fileSet.addSdrf(MageTabDataFiles.UPDATE_FILES_BASELINE_SDRF);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_FILES_CEL2);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_FILES_CEL1);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_FILES_EXP1);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_FILES_EXP2);
        return fileSet;
    }

    private static MageTabFileSet getUpdateFilesNewInputSet() {
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(MageTabDataFiles.UPDATE_FILES_NEW_IDF);
        fileSet.addSdrf(MageTabDataFiles.UPDATE_FILES_NEW_SDRF);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_FILES_CEL1A);
        fileSet.addNativeData(MageTabDataFiles.UPDATE_FILES_EXP2A);
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
    
    private static void addFiles(CaArrayFileSet fileSet, Collection<File> files,
            Class<? extends AbstractMageTabDocument> documentType) {
        for (File file : files) {
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
        fileSet.addIdf(MageTabDataFiles.EXTENDED_FACTOR_VALUES_IDF);
        fileSet.addSdrf(MageTabDataFiles.EXTENDED_FACTOR_VALUES_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.EXTENDED_FACTOR_VALUES_DIRECTORY);
        return fileSet;
    }


    private static MageTabFileSet getSdrfTestInputSet(File idf, File sdrf) {
        MageTabFileSet mtfs = new MageTabFileSet();
        mtfs.addIdf(idf);
        mtfs.addSdrf(sdrf);
        mtfs.addNativeData(SdrfTestFiles.TEST_1_CEL);
        mtfs.addNativeData(SdrfTestFiles.TEST_2_CEL);
        mtfs.addDataMatrix(SdrfTestFiles.TEST_1_DATA);
        mtfs.addDataMatrix(SdrfTestFiles.TEST_2_DATA);
        return mtfs;
    }

}
