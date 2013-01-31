//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.adf.AdfDocument;
import gov.nih.nci.caarray.magetab.data.DataMatrix;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.validation.InvalidDataException;

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
     * Error Document set parsed from the MAGE-TAB specification example files.
     */
    public static final MageTabDocumentSet MAGE_TAB_ERROR_SPECIFICATION_SET = getSet(MAGE_TAB_ERROR_SPECIFICATION_INPUT_SET);

    /**
     * Document set parsed from the MAGE-TAB specification example files.
     */
    public static final MageTabDocumentSet MAGE_TAB_SPECIFICATION_SET = getSet(MAGE_TAB_SPECIFICATION_INPUT_SET);

    /**
     * Document set parsed from the MAGE-TAB specification example files modified to test case sensitivity for term and
     * category import.
     */
    public static final MageTabDocumentSet MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_SET = getSet(MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_INPUT_SET);

    /**
     * Document set parsed from the MAGE-TAB specification example files, with no array design references in the SDRF.
     */
    public static final MageTabDocumentSet MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_SET = getSet(MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET);

    /**
     * Document set parsed from the MAGE-TAB specification example files, with no experiment description in the IDF.
     */
    public static final MageTabDocumentSet MAGE_TAB_SPECIFICATION_NO_EXP_DESC_SET = getSet(MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET);

    /**
     * Document set parsed from the MAGE-TAB specification example files.
     */
    public static final MageTabDocumentSet PERFORMANCE_TEST_10_SET = getSet(PERFORMANCE_TEST_10_INPUT_SET);

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
     * Document set parsed from TCGA Broad data.
     */
    public static final MageTabDocumentSet TCGA_BROAD_SET = getSet(TCGA_BROAD_INPUT_SET);

    /**
     * Example set of MAGE-TAB data.
     */
    public static final MageTabDocumentSet GSK_TEST_SET = getSet(getGskTestSet());

    /**
     * MAGE-TAB data set containing data derived from other derived data.
     */
    public static final MageTabFileSet DERIVED_DATA_INPUT_SET = getDerivedDataInputSet();

    /**
     * MAGE-TAB data set containing data derived from other derived data.
     */
    public static final MageTabDocumentSet DERIVED_DATA_SET = getSet(DERIVED_DATA_INPUT_SET);

    /**
     * MAGE-TAB input set containing valid usage of Characteristics[ExternalSampleId] for Sample(s).
     */
    public static final MageTabFileSet VALID_FEATURE_13141_INPUT_SET = getValidFeature13141InputSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabDocumentSet VALID_FEATURE_13141_DATA_SET = getSet(VALID_FEATURE_13141_INPUT_SET);
    /**
     * MAGE-TAB input set containing valid usage of Characteristics[ExternalSampleId] for Sample(s).
     */
    public static final MageTabFileSet INVALID_FEATURE_13141_INPUT_SET = getInvalidFeature13141InputSet();

    /**
     * Document set parsed ...
     */
    public static final MageTabDocumentSet INVALID_FEATURE_13141_DATA_SET = getSet(INVALID_FEATURE_13141_INPUT_SET);

    private static MageTabDocumentSet getSet(MageTabFileSet inputSet) {
        try {
            return MageTabParser.INSTANCE.parse(inputSet);
        } catch (MageTabParsingException e) {
            e.printStackTrace(System.err);
            return null;
        } catch (InvalidDataException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

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

    private static void addFiles(CaArrayFileSet fileSet, Collection<? extends AbstractMageTabDocument> mageTabDocuments) {
        for (AbstractMageTabDocument mageTabDocument : mageTabDocuments) {
            addFile(fileSet, mageTabDocument);
        }
    }

    private static void addFile(CaArrayFileSet fileSet, AbstractMageTabDocument mageTabDocument) {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(mageTabDocument.getFile().getName());
        if (mageTabDocument instanceof IdfDocument) {
            caArrayFile.setFileType(FileType.MAGE_TAB_IDF);
        } else if (mageTabDocument instanceof SdrfDocument) {
            caArrayFile.setFileType(FileType.MAGE_TAB_SDRF);
        } else if (mageTabDocument instanceof AdfDocument) {
            caArrayFile.setFileType(FileType.MAGE_TAB_ADF);
        } else if (mageTabDocument instanceof DataMatrix) {
            caArrayFile.setFileType(FileType.MAGE_TAB_DATA_MATRIX);
        } else if (mageTabDocument.getFile().getName().toLowerCase().endsWith(".cel")) {
            caArrayFile.setFileType(FileType.AFFYMETRIX_CEL);
        } else if (mageTabDocument.getFile().getName().toLowerCase().endsWith(".exp")) {
            caArrayFile.setFileType(FileType.AFFYMETRIX_EXP);
        } else if (mageTabDocument.getFile().getName().toLowerCase().endsWith(".chp")) {
            caArrayFile.setFileType(FileType.AFFYMETRIX_CHP);
        } else {
            throw new IllegalArgumentException("Unrecognized document file " + mageTabDocument.getFile());
        }
        fileSet.add(caArrayFile);
    }
}
