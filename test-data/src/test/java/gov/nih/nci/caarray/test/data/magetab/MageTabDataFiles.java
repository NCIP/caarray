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
            "/magetab/unsupported_data/e-mexp-428_v1.0-unsupported.idf").getFile());

    public static final File UNSUPPORTED_DATA_EXAMPLE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/unsupported_data/e-mexp-428_v1.0-unsupported.sdrf").getFile());

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

    public static final File GOOD_DATA_MATRIX_COPY_NUMER_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.idf").getFile());

    public static final File GOOD_DATA_MATRIX_COPY_NUMER_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.sdrf").getFile());

    public static final File GOOD_DATA_MATRIX_COPY_NUMER_DATA = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.data.txt").getFile());

    public static final File BAD_DATA_MATRIX_COPY_NUMER_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.bad.idf").getFile());

    public static final File BAD_DATA_MATRIX_COPY_NUMER_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.bad.sdrf").getFile());

    public static final File BAD_DATA_MATRIX_COPY_NUMER_BAD_SDRF_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.bad_sdrf.idf").getFile());

    public static final File BAD_DATA_MATRIX_COPY_NUMER_BAD_SDRF_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.bad_sdrf.sdrf").getFile());

    public static final File DATA_MATRIX_COPY_NUMER_HYB_SUBSET_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.hyb_subset.idf").getFile());

    public static final File DATA_MATRIX_COPY_NUMER_HYB_SUBSET_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.hyb_subset.sdrf").getFile());

    public static final File BAD_DATA_MATRIX_COPY_NUMER_DATA = new File(MageTabDataFiles.class.getResource(
            "/magetab/data_matrix_copy_number/data_matrix_copy_number.data.bad.txt").getFile());

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

    public static final File DUPLICATE_TERM_SOURCES_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/duplicate_term_source_refs").getFile());

    public static final File FEATURE_13141_ZIP = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/valid.zip").getFile());

    public static final File FEATURE_13141_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/valid.idf").getFile());

    public static final File VALID_DESCRIPTION_LENGTH_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/experiment_description_length/valid.idf").getFile());

    public static final File INVALID_DESCRIPTION_LENGTH_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/experiment_description_length/invalid.idf").getFile());

    public static final File FEATURE_13141_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/valid1.sdrf").getFile());

    public static final File FEATURE_13141_SDRF2 = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/valid2.sdrf").getFile());

    public static final File FEATURE_13141_INVALID_ZIP = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/invalid.zip").getFile());

    public static final File DUPLICATE_TERM_SOURCES_INVALID_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/duplicate_term_source_refs/"
            + "experiment-id-1015897589852334_01_2sources1extract.idf").getFile());

    public static final File DUPLICATE_TERM_SOURCES_INVALID_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/duplicate_term_source_refs/"
            + "experiment-id-1015897589852334_01_2sources1extract.sdrf").getFile());

    public static final File FEATURE_13141_INVALID_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/invalid.idf").getFile());

    public static final File FEATURE_13141_INVALID_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/invalid1.sdrf").getFile());

    public static final File FEATURE_13141_INVALID_SDRF2 = new File(MageTabDataFiles.class
            .getResource("/magetab/external_sample_id/invalid2.sdrf").getFile());

    public static final File DEFECT_16421_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/blankCharacteristics/rembrandt_snp_NOB_batch5.sdrf").getFile());

    public static final File DEFECT_16421_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/blankCharacteristics/rembrandr_snp_NOB_batch5.idf").getFile());

    public static final File DEFECT_16421_CEL = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/blankCharacteristics").getFile());

    public static final File DEFECT_17200_SDRF = new File(MageTabDataFiles.class.getResource(
        "/magetab/baddata/invalidFileRef/test_gpr.sdrf").getFile());

    public static final File DEFECT_17200_IDF = new File(MageTabDataFiles.class.getResource(
        "/magetab/baddata/invalidFileRef/test_gpr.idf").getFile());

    public static final File BAD_VOCABULARY_TERM_SOURCES_IDF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_term_sources/test.bad.idf").getFile());
    
    public static final File BAD_VOCABULARY_TERM_SOURCES_SDRF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_term_sources/test.bad.sdrf").getFile());

    public static final File GOOD_VOCABULARY_TERM_SOURCES_IDF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_term_sources/test.good.idf").getFile());
    
    public static final File GOOD_VOCABULARY_TERM_SOURCES_SDRF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_term_sources/test.good.sdrf").getFile());
    
    public static final File DEFECT_13164_IDF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_organism_term_source/test.idf").getFile());
    
    public static final File DEFECT_13164_SDRF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_organism_term_source/test.sdrf").getFile());

    public static final File DEFECT_13164_GOOD_IDF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_organism_term_source/test.good.idf").getFile());
    
    public static final File DEFECT_13164_GOOD_SDRF = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_organism_term_source/test.good.sdrf").getFile());
    
    public static final File DEFECT_13164_CEL = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_organism_term_source/Test3.CEL").getFile());
    
    public static final File DEFECT_13164_CHP = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_organism_term_source/Test3.CHP").getFile());

    public static final File TESTING_VOCABULARY_TERM_SOURCES_CEL = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_term_sources/Test3.CEL").getFile());
    
    public static final File TESTING_VOCABULARY_TERM_SOURCES_CHP = new File(MageTabDataFiles.class.getResource(
    "/magetab/test_term_sources/Test3.CHP").getFile());

    public static final File DEFECT_27959_IDF = new File(MageTabDataFiles.class.getResource(
        "/magetab/incorrect_array_data_type/ilmn_test.bad.idf.txt").getFile());

    public static final File DEFECT_27959_SDRF = new File(MageTabDataFiles.class.getResource(
        "/magetab/incorrect_array_data_type/ilmn_test.bad.sdrf.txt").getFile());

    public static final File DEFECT_27959_DERIVED_DATA_FILE = new File(MageTabDataFiles.class.getResource(
        "/magetab/incorrect_array_data_type/GEO_GSE12459_processed_data.txt").getFile());

    public static final File DEFECT_17200_GPR = new File(MageTabDataFiles.class.getResource(
        "/magetab/baddata/invalidFileRef/18258_wap3.gpr").getFile());

    public static final File DEFECT_16421_2_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/invalidColumnOrder/rembrandt_snp_NOB_batch5.sdrf").getFile());

    public static final File DEFECT_16421_2_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/invalidColumnOrder/rembrandr_snp_NOB_batch5.idf").getFile());

    public static final File INVALID_COLUMN_ORDER_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/invalidColumnOrder").getFile());

    public static final File INVALID_NODE_ORDER_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/invalidColumnOrder/invalid_node_order.sdrf.txt").getFile());

    public static final File INVALID_NODE_ORDER_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/invalidColumnOrder/invalid_node_order.idf.txt").getFile());

    public static final File MISSING_COLUMNS_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_columns").getFile());

    public static final File NO_BIOMATERIAL_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_columns/no_biomaterial.sdrf.txt").getFile());

    public static final File NO_BIOMATERIAL_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_columns/no_biomaterial.idf.txt").getFile());

    public static final File NO_HYBRIDIZATION_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_columns/no_hyb.sdrf.txt").getFile());

    public static final File NO_HYBRIDIZATION_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_columns/no_hyb.idf.txt").getFile());

    public static final File NO_DATA_FILE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_columns/no_data_file.sdrf.txt").getFile());

    public static final File NO_DATA_FILE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/baddata/missing_columns/no_data_file.idf.txt").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ZIP = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations/specification_update_annotations.zip").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations/e-mexp-428_v1.0-update_annotations.idf").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations/e-mexp-428_v1.0-update_annotations.sdrf").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations_add_new_bm").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_ZIP = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations_add_new_bm/specification_update_annotations_add_new_bm.zip").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations_add_new_bm/e-mexp-428_v1.0-update_annotations_add_new_bm.idf").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations_add_new_bm/e-mexp-428_v1.0-update_annotations_add_new_bm.sdrf").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DATA_MATRIX_FILE = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations_add_new_bm/e-mexp-428data_v2.0.data").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DATA_FILE1 = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations_add_new_bm/H_TK6 neo replicate 5.CEL").getFile());

    public static final File SPECIFICATION_UPDATE_ANNOTATIONS_ADD_NEW_BM_DATA_FILE2 = new File(MageTabDataFiles.class
            .getResource("/magetab/specification_update_annotations_add_new_bm/H_TK6 neo replicate 6.CEL").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_DIRECTORY = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_BASELINE_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/update_chain_baseline.zip").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_BASELINE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/update_chain_baseline.idf").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_BASELINE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/update_chain_baseline.sdrf").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/BM1.CEL").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_1A = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/BM1a.CEL").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_2 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/BM2.CEL").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_3 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/BM3.CEL").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_DATA_FILE_4 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/BM4.CEL").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_ZIP = new File(MageTabDataFiles.class
            .getResource("/magetab/update_chain/update_chain_new_biomaterials.zip").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/update_chain/update_chain_new_biomaterials.idf").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_NEW_BIOMATERIALS_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/update_chain/update_chain_new_biomaterials.sdrf").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/update_chain_new_data_files.zip").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_chain/update_chain_new_data_files.idf").getFile());

    public static final File UPDATE_BIOMATERIAL_CHAIN_NEW_DATA_FILES_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/update_chain/update_chain_new_data_files.sdrf").getFile());

    public static final File UPDATE_FILES_DIRECTORY = new File(MageTabDataFiles.class.getResource(
    "/magetab/update_files").getFile());

    public static final File UPDATE_FILES_BASELINE_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/update_files_baseline.zip").getFile());

    public static final File UPDATE_FILES_BASELINE_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/update_files_baseline.idf").getFile());

    public static final File UPDATE_FILES_BASELINE_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/update_files_baseline.sdrf").getFile());

    public static final File UPDATE_FILES_NEW_ZIP = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/update_files_new.zip").getFile());

    public static final File UPDATE_FILES_NEW_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/update_files_new.idf").getFile());

    public static final File UPDATE_FILES_NEW_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/update_files_new.sdrf").getFile());

    public static final File UPDATE_FILES_CEL1 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/BM1.CEL").getFile());

    public static final File UPDATE_FILES_CEL1A = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/BM1a.CEL").getFile());

    public static final File UPDATE_FILES_CEL2 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/BM2.CEL").getFile());

    public static final File UPDATE_FILES_EXP1 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/BM1.EXP").getFile());

    public static final File UPDATE_FILES_EXP2A = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/BM2a.EXP").getFile());

    public static final File UPDATE_FILES_EXP2 = new File(MageTabDataFiles.class.getResource(
            "/magetab/update_files/BM2.EXP").getFile());

    public static final File EXTENDED_FACTOR_VALUES_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/extended_factor_values").getFile());

    public static final File EXTENDED_FACTOR_VALUES_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/extended_factor_values/valid.idf").getFile());

    public static final File EXTENDED_FACTOR_VALUES_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/extended_factor_values/valid1.sdrf").getFile());

    public static final File DEFECT_27306_IDF = new File(MageTabDataFiles.class.getResource(
            "/magetab/three_level_data/defect-27306/broad.mit.edu_GBM.HT_HG-U133A.5.idf.txt").getFile());

    public static final File DEFECT_27306_SDRF = new File(MageTabDataFiles.class.getResource(
            "/magetab/three_level_data/defect-27306/broad.mit.edu_GBM.HT_HG-U133A.5.3.0.sdrf.txt").getFile());

    public static final File DEFECT_27306_CEL = new File(MageTabDataFiles.class.getResource(
            "/magetab/three_level_data/defect-27306/5500024035736031208615.D04.CEL").getFile());

    public static final File DEFECT_27306_LEVEL_2 = new File(MageTabDataFiles.class.getResource(
            "/magetab/three_level_data/defect-27306/5500024035736031208615.D04.level2.txt").getFile());

    public static final File DEFECT_27306_LEVEL_3 = new File(MageTabDataFiles.class.getResource(
            "/magetab/three_level_data/defect-27306/5500024035736031208615.D04.level3.txt").getFile());    

    public static final File RENAMING_TERM_SOURCES_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/renaming_term_sources/20297.idf").getFile());

    public static final File RENAMING_TERM_SOURCES_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/renaming_term_sources/20297.sdrf").getFile()); 

    public static final File RENAMING_TERM_SOURCES_DIRECTORY = new File(MageTabDataFiles.class
            .getResource("/magetab/renaming_term_sources").getFile());  

    public static final File GETS_NEW_TERM_SOURCE_IDF = new File(MageTabDataFiles.class
            .getResource("/magetab/test_gets_new_term_source/test.idf").getFile());

    public static final File GETS_NEW_TERM_SOURCE_SDRF = new File(MageTabDataFiles.class
            .getResource("/magetab/test_gets_new_term_source/test.sdrf").getFile());
}
