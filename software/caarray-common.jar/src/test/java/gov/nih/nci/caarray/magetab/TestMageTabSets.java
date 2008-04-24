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
public final class TestMageTabSets {

    private TestMageTabSets() {
        super();
    }

    /**
     * Example set of documents included with MAGE-TAB specification.
     */
    public static final MageTabInputFileSet MAGE_TAB_UNSUPPORTED_DATA_INPUT_SET = getUnsupportedDataInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification.
     */
    public static final MageTabInputFileSet MAGE_TAB_MISPLACED_FACTOR_VALUES_INPUT_SET = getMisplacedFactorValuesInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification.
     */
    public static final MageTabInputFileSet MAGE_TAB_SPECIFICATION_INPUT_SET = getSpecificationInputSet();

    /**
     * Example set of documents based on the MAGE-TAB specification example, with no array design ref in the SDRF.
     */
    public static final MageTabInputFileSet MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET =
        getSpecificationNoArrayDesignInputSet();

    /**
     * Example set of documents based on the MAGE-TAB specification example, with no experiment description in the IDF.
     */
    public static final MageTabInputFileSet MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET =
        getSpecificationNoExpDescInputSet();

    /**
     * Example set of documents included with MAGE-TAB specification, minus the data matrix
     */
    public static final MageTabInputFileSet MAGE_TAB_SPECIFICATION_NO_DATA_MATRIX_INPUT_SET =
        getSpecificationWithoutDataMatrixInputSet();

    /**
     * Example set of documents with ERRORS based on the MAGE-TAB specification.
     */
    public static final MageTabInputFileSet MAGE_TAB_ERROR_SPECIFICATION_INPUT_SET = getErrorSpecificationInputSet();

    /**
     * Example set of documents with ERRORS based on the MAGE-TAB specification.
     */
    public static final MageTabInputFileSet MAGE_TAB_GEDP_INPUT_SET = getGedpSpecificationInputSet();

    /**
     * Example set of MAGE-TAB data with 10 large CEL files and no derived data.
     */
    public static final MageTabInputFileSet PERFORMANCE_TEST_10_INPUT_SET = getPerformanceTest10InputSet();

    /**
     * Error Document set parsed from the MAGE-TAB specification example files.
     */
    public static final MageTabDocumentSet MAGE_TAB_ERROR_SPECIFICATION_SET = getSet(MAGE_TAB_ERROR_SPECIFICATION_INPUT_SET);

    /**
     * Document set parsed from the MAGE-TAB specification example files.
     */
    public static final MageTabDocumentSet MAGE_TAB_SPECIFICATION_SET = getSet(MAGE_TAB_SPECIFICATION_INPUT_SET);

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
    public static final MageTabInputFileSet TCGA_BROAD_INPUT_SET = getTcgaBroadInputSet();

    /**
     * MAGE-TAB input set derived from EBI generated template.
     */
    public static final MageTabInputFileSet EBI_TEMPLATE_INPUT_SET = getEbiTemplateInputSet();

    /**
     * MAGE-TAB input set derived from 1x export data
     */
    public static final MageTabInputFileSet CAARRAY1X_INPUT_SET = getCaarray1xInputSet();

    /**
     * MAGE-TAB input set from TCGA Broad data.
     */
    public static final MageTabInputFileSet DEFECT_12537_INPUT_SET = getDefect12537InputSet();

    /**
     * MAGE-TAB input set from TCGA Broad data.
     */
    public static final MageTabInputFileSet DEFECT_12537_ERROR_INPUT_SET = getDefect12537ErrorInputSet();

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
    public static final MageTabInputFileSet DERIVED_DATA_INPUT_SET = getDerivedDataInputSet();

    /**
     * MAGE-TAB data set containing data derived from other derived data.
     */
    public static final MageTabDocumentSet DERIVED_DATA_SET = getSet(DERIVED_DATA_INPUT_SET);


    private static MageTabDocumentSet getSet(MageTabInputFileSet inputSet) {
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

    private static MageTabInputFileSet getMisplacedFactorValuesInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.MISPLACED_FACTOR_VALUES_IDF);
        fileSet.addSdrf(MageTabDataFiles.MISPLACED_FACTOR_VALUES_SDRF);
        return fileSet;
    }

    private static MageTabInputFileSet getEbiTemplateInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.EBI_TEMPLATE_IDF);
        fileSet.addSdrf(MageTabDataFiles.EBI_TEMPLATE_SDRF);
        return fileSet;
    }


    private static MageTabInputFileSet getTcgaBroadInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.TCGA_BROAD_IDF);
        fileSet.addSdrf(MageTabDataFiles.TCGA_BROAD_SDRF);
        fileSet.addDataMatrix(MageTabDataFiles.TCGA_BROAD_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY);
        return fileSet;
    }

    private static void addCelFiles(MageTabInputFileSet fileSet, File dataFileDirectory) {
        addDataFiles(fileSet, dataFileDirectory, "cel");
    }

    private static void addExpFiles(MageTabInputFileSet fileSet, File dataFileDirectory) {
        addDataFiles(fileSet, dataFileDirectory, "exp");
    }

    private static void addChpFiles(MageTabInputFileSet fileSet, File dataFileDirectory) {
        addDataFiles(fileSet, dataFileDirectory, "chp");
    }

    private static void addDataFiles(MageTabInputFileSet fileSet, File dataFileDirectory, String extension) {
        FilenameFilter filter = createExtensionFilter(extension);
        File[] celFiles = dataFileDirectory.listFiles(filter);
        for (File file : celFiles) {
            fileSet.addNativeData(file);
        }
    }

    private static MageTabInputFileSet getPerformanceTest10InputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.PERFORMANCE_10_IDF);
        fileSet.addSdrf(MageTabDataFiles.PERFORMANCE_10_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.PERFORMANCE_DIRECTORY);
        return fileSet;
    }

    private static MageTabInputFileSet getSpecificationInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabInputFileSet getSpecificationNoArrayDesignInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_ARRAY_DESIGN_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabInputFileSet getSpecificationNoExpDescInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_NO_EXP_DESC_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabInputFileSet getSpecificationWithoutDataMatrixInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        addCelFiles(fileSet, MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY);
        return fileSet;
    }

    private static MageTabInputFileSet getUnsupportedDataInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_SDRF);
        fileSet.addAdf(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_ADF);
        fileSet.addDataMatrix(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY);
        fileSet.addNativeData(new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY, "unsupported.mas5.exp"));
        return fileSet;
    }

    private static MageTabInputFileSet getGedpSpecificationInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.GEDP_IDF);
        fileSet.addSdrf(MageTabDataFiles.GEDP_SDRF);
        return fileSet;
    }

    private static MageTabInputFileSet getErrorSpecificationInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF);
        fileSet.addSdrf(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_SDRF);
        return fileSet;
    }

    private static MageTabInputFileSet getCaarray1xInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.CAARRAY1X_IDF);
        fileSet.addSdrf(MageTabDataFiles.CAARRAY1X_SDRF);
        addDataFiles(fileSet, MageTabDataFiles.CAARRAY1X_DIRECTORY, "gpr");
        return fileSet;
    }

    private static MageTabInputFileSet getGskTestSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.GSK_TEST_IDF);
        fileSet.addSdrf(MageTabDataFiles.GSK_TEST_SDRF);
        addCelFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        addExpFiles(fileSet, MageTabDataFiles.GSK_TEST_DIRECTORY);
        return fileSet;
    }


    private static MageTabInputFileSet getDefect12537InputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.DEFECT_12537_IDF);
        fileSet.addSdrf(MageTabDataFiles.DEFECT_12537_SDRF);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_ABSOLUTE_DATA_MATRIX);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_RMA_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabInputFileSet getDefect12537ErrorInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
        fileSet.addIdf(MageTabDataFiles.DEFECT_12537_ERROR__IDF);
        fileSet.addSdrf(MageTabDataFiles.DEFECT_12537_ERROR_SDRF);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_ERROR_ABSOLUTE_DATA_MATRIX);
        fileSet.addDataMatrix(MageTabDataFiles.DEFECT_12537_ERROR_RMA_DATA_MATRIX);
        addCelFiles(fileSet, MageTabDataFiles.DEFECT_12537_ERROR_DATA_DIRECTORY);
        return fileSet;
    }

    private static MageTabInputFileSet getDerivedDataInputSet() {
        MageTabInputFileSet fileSet = new MageTabInputFileSet();
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
