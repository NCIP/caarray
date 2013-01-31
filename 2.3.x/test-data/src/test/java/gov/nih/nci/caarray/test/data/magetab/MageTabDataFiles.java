//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.magetab;

import java.io.File;

public class MageTabDataFiles {

    public static final File MISSING_TERMSOURCE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_term_source.idf").getFile());

    public static final File MISSING_TERMSOURCE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_term_source.sdrf").getFile());

    public static final File SPECIFICATION_EXAMPLE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification/e-mexp-428_v1.0.idf").getFile());

    public static final File SPECIFICATION_CASE_SENSITIVITY_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_case_sensitivity/e-mexp-428_v1.0.idf").getFile());

    public static final File SPECIFICATION_EXAMPLE_NO_EXP_DESC_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_no_exp_desc/e-mexp-428_v1.0.idf").getFile());

    public static final File SPECIFICATION_EXAMPLE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification/e-mexp-428_v1.0.sdrf").getFile());

    public static final File SPECIFICATION_CASE_SENSITIVITY_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_case_sensitivity/e-mexp-428_v1.0.sdrf").getFile());

    public static final File SPECIFICATION_EXAMPLE_NO_ARRAY_DESIGN_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_no_array_design/e-mexp-428_v1.0.sdrf").getFile());

    public static final File SPECIFICATION_EXAMPLE_ADF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification/a-mexp-58f_excerpt_v1.0.adf").getFile());

    public static final File SPECIFICATION_EXAMPLE_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification/e-mexp-428data_v1.0.data").getFile());

    public static final File SPECIFICATION_CASE_SENSITIVITY_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_case_sensitivity/e-mexp-428data_v1.0.data").getFile());

    public static final File SPECIFICATION_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification/specification.zip").getFile());

    public static final File SPECIFICATION_ZIP_WITH_NEXTED_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/specificiation_with_zip/specification.zip").getFile());

    public static final File SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE = new File(MageTabDataFiles.class.getResource(
            "/magetab/specificiation_with_zip/Test1.txt").getFile());

    public static final File SPECIFICATION_EXAMPLE_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification").getFile());

    public static final File SPECIFICATION_CASE_SENSITIVITY_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_case_sensitivity").getFile());

    public static final File UNSUPPORTED_DATA_EXAMPLE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data/e-mexp-428_v1.0.idf").getFile());

    public static final File UNSUPPORTED_DATA_EXAMPLE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data/e-mexp-428_v1.0.sdrf").getFile());

    public static final File UNSUPPORTED_DATA_EXAMPLE_ADF = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data/a-mexp-58f_excerpt_v1.0.adf").getFile());

    public static final File UNSUPPORTED_DATA_EXAMPLE_EXP = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data/unsupported.mas5.exp").getFile());

    public static final File UNSUPPORTED_DATA_EXAMPLE_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data/e-mexp-428data_v1.0.data").getFile());

    public static final File UNSUPPORTED_DATA_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data/unsupported.zip").getFile());

    public static final File UNSUPPORTED_DATA_EXAMPLE_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data").getFile());

    public static final File EBI_TEMPLATE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/ebi_template/MAGE-TAB_GSK.idf").getFile());

    public static final File EBI_TEMPLATE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/ebi_template/MAGE-TAB_GSK.sdrf").getFile());

    public static final File GSK_TEST_DIRECTORY = new File(MageTabDataFiles.class.getResource("/magetab/GSK_test")
            .getFile());

    public static final File GSK_TEST_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/GSK_test/GSK_Sample_iterate03-RNA.idf").getFile());

    public static final File GSK_TEST_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/GSK_test/GSK_sample_iterate03-RNA_Jan20.sdrf").getFile());

    public static final File TCGA_BROAD_DATA_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/broad").getFile());

    public static final File TCGA_BROAD_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/broad/broad.mit.edu_GBM.HT_HG-U133A.1.idf").getFile());

    public static final File TCGA_BROAD_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/broad/broad.mit.edu_GBM.HT_HG-U133A.1.sdrf").getFile());

    public static final File TCGA_BROAD_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/broad/broad.mit.edu_GBM.HT_HG-U133A.1.data").getFile());

    public static final File TCGA_BROAD_ZIP = new File(MageTabDataFiles.class.getResource(
    "/magetab/tcga/broad/broad.mit.edu_GBM.HT_HG-U133A.1.zip").getFile());

    public static final File GEDP_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/GEDP_Nelson_2007-10-17T20_34_21Z.idf").getFile());

    public static final File GEDP_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/GEDP_Nelson_2007-10-17T20_34_21Z.sdrf").getFile());

    public static final File SPECIFICATION_ERROR_EXAMPLE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/error-e-mexp-428_v1.0.idf").getFile());

    public static final File SPECIFICATION_ERROR_EXAMPLE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/error-e-mexp-428_v1.0.sdrf").getFile());

    public static final File MISPLACED_FACTOR_VALUES_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/NCI60_v2.idf").getFile());

    public static final File MISPLACED_FACTOR_VALUES_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/NCI60_v2_short.sdrf").getFile());

    public static final File PERFORMANCE_1_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/performance/performance_test1.idf").getFile());

    public static final File PERFORMANCE_1_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/performance/performance_test1.sdrf").getFile());

    public static final File PERFORMANCE_10_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/performance/performance_test10.idf").getFile());

    public static final File PERFORMANCE_10_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/performance/performance_test10.sdrf").getFile());

    public static final File PERFORMANCE_100_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/performance/performance_test100.idf").getFile());

    public static final File PERFORMANCE_100_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/performance/performance_test100.sdrf").getFile());

    public static final File PERFORMANCE_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/performance").getFile());

    public static final File CAARRAY1X_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/caarray1x/experiment-id-1015897540503881.idf").getFile());

    public static final File CAARRAY1X_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/caarray1x/experiment-id-1015897540503881.sdrf").getFile());

    public static final File CAARRAY1X_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/caarray1x/experiment-id-1015897540503881.magetab.zip").getFile());

    public static final File CAARRAY1X_DIRECTORY = new File(MageTabDataFiles.class.getResource("/magetab/caarray1x")
            .getFile());

    public static final File DEFECT_12537_ERROR_DATA_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537-error").getFile());

    public static final File DEFECT_12537_ERROR__IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537-error/broad.mit.edu_GBM.HT_HG-U133A.1.idf.txt").getFile());

    public static final File DEFECT_12537_ERROR_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537-error/broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt").getFile());

    public static final File DEFECT_12537_ERROR_RMA_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537-error/broad.mit.edu_GBM.HT_HG-U133A.1.rma.data.txt").getFile());

    public static final File DEFECT_12537_ERROR_ABSOLUTE_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537-error/broad.mit.edu_GBM.HT_HG-U133A.1.absolute.data.txt").getFile());

    public static final File DEFECT_12537_DATA_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537").getFile());

    public static final File DEFECT_12537_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537/broad.mit.edu_GBM.HT_HG-U133A.1.idf.txt").getFile());

    public static final File DEFECT_12537_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537/broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt").getFile());

    public static final File DEFECT_12537_RMA_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537/broad.mit.edu_GBM.HT_HG-U133A.1.rma.data.txt").getFile());

    public static final File DEFECT_12537_ABSOLUTE_DATA_MATRIX = new File(MageTabDataFiles.class.getResource(
            "/magetab/tcga/defect-12537/broad.mit.edu_GBM.HT_HG-U133A.1.absolute.data.txt").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_derived_data/part1_e-mexp-428_v1.0.idf").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_derived_data/part1_e-mexp-428_v1.0.sdrf").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_ADF = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_derived_data/a-mexp-58f_excerpt_v1.0.adf").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_DATA_FILE = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_derived_data/Test3.CEL").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_DERIVED_DATA_FILE = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_derived_data/Test3.CHP").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_DERIVED_DATA_2_FILE = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_derived_data/Test3-2.CHP").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/specification_derived_data/specification_derived_data.zip").getFile());

    public static final File SPECIFICATION_DERIVED_DATA_EXAMPLE_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_derived_data").getFile());

    public static final File FEATURE_13141_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id").getFile());
    
    public static final File FEATURE_13141_ZIP = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/valid.zip").getFile());
    
    public static final File FEATURE_13141_IDF = new File(MageTabDataFiles.class 
            .getResource("/magetab/external_sample_id/valid.idf").getFile());
    
    public static final File FEATURE_13141_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/valid1.sdrf").getFile());
            
    public static final File FEATURE_13141_SDRF2 = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/valid2.sdrf").getFile());
    
    public static final File FEATURE_13141_INVALID_ZIP = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/invalid.zip").getFile());
    
    public static final File FEATURE_13141_INVALID_IDF = new File(MageTabDataFiles.class 
            .getResource("/magetab/external_sample_id/invalid.idf").getFile());
    
    public static final File FEATURE_13141_INVALID_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/invalid1.sdrf").getFile());
    
    public static final File FEATURE_13141_INVALID_SDRF2 = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/invalid2.sdrf").getFile());

}
