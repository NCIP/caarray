/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
package gov.nih.nci.caarray.application.file;

import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.DIA;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.FLAGS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.LOG_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.NORMALIZE;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIOS_SD_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_R2_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SNR_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SNR_635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.X;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.Y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedArrayDesignFileHandler;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedDataHandler;
import gov.nih.nci.caarray.plugins.affymetrix.CelHandler;
import gov.nih.nci.caarray.plugins.agilent.AgilentRawTextDataHandler;
import gov.nih.nci.caarray.plugins.agilent.AgilentXmlDesignFileHandler;
import gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType;
import gov.nih.nci.caarray.plugins.genepix.GprHandler;
import gov.nih.nci.caarray.plugins.illumina.CsvDataHandler;
import gov.nih.nci.caarray.plugins.illumina.SampleProbeProfileHandler;
import gov.nih.nci.caarray.plugins.illumina.SampleProbeProfileQuantitationType;
import gov.nih.nci.caarray.plugins.nimblegen.NimblegenQuantitationType;
import gov.nih.nci.caarray.plugins.nimblegen.PairDataHandler;
import gov.nih.nci.caarray.test.data.arraydata.AgilentArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.NimblegenArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Ignore;
import org.junit.Test;

public class FileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @Test
    public void testReimportProjectFiles() throws Exception {
        final ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_MIRNA_1_XML_SMALL,
                        AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        addDesignToExperiment(design);

        final Map<File, FileType> files = new HashMap<File, FileType>();
        files.put(AgilentArrayDataFiles.MIRNA, UnparsedDataHandler.FILE_TYPE_ILLUMINA_RAW_TXT);

        CaArrayFileSet fileSet = uploadFiles(files);

        importFiles(fileSet);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(1, project.getExperiment().getHybridizations().size());
        assertEquals(1, project.getExperiment().getHybridizations().iterator().next().getRawDataCollection().size());

        assertEquals(1, project.getImportedFiles().size());
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, project.getImportedFiles().iterator().next().getFileStatus());
        final CaArrayFile f = project.getImportedFiles().iterator().next();
        f.setFileType(AgilentRawTextDataHandler.RAW_TXT_FILE_TYPE);
        this.hibernateHelper.getCurrentSession().save(f);

        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        fileSet = new CaArrayFileSet();
        fileSet.addAll(project.getImportedFiles());
        this.getFileManagementService().reimportAndParseProjectFiles(project, fileSet);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(1, project.getImportedFiles().size());
        assertEquals(1, project.getExperiment().getHybridizations().size());
        assertEquals(1, project.getExperiment().getHybridizations().iterator().next().getRawDataCollection().size());
        assertEquals("Agilent Raw Text", project.getExperiment().getHybridizations().iterator().next()
                .getRawDataCollection().iterator().next().getType().getName());
        final CaArrayFile imported = project.getImportedFiles().iterator().next();
        assertEquals(FileStatus.IMPORTED, imported.getFileStatus());
        tx.commit();
    }

    @Test
    public void testImportMageTabSpecificationAndUpdateCharacteristics() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        addDesignToExperiment(design);

        final ArrayDesign design2 = importArrayDesign(AffymetrixArrayDesignFiles.HG_FOCUS_CDF);
        addDesignToExperiment(design2);

        importFiles(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("MDR1 overexpression."));
        assertEquals(1, project.getExperiment().getFactors().size());
        assertEquals(8, project.getExperiment().getExperimentContacts().size());
        assertEquals(6, project.getExperiment().getSources().size());
        assertEquals(6, project.getExperiment().getSamples().size());
        assertEquals(6, project.getExperiment().getExtracts().size());
        assertEquals(6, project.getExperiment().getLabeledExtracts().size());
        assertEquals(6, project.getExperiment().getHybridizations().size());
        Source testSource = null;
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast", testSource.getCellType().getValue());
        assertNull(testSource.getTissueSite());
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray()
                .getDesign().getName());
        tx.commit();

        // now try to update annotations of existing biomaterials
        importFiles(TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("This sentence is added to the description."));
        assertEquals(2, project.getExperiment().getFactors().size());
        assertEquals(10, project.getExperiment().getExperimentContacts().size());
        assertEquals(6, project.getExperiment().getSources().size());
        assertEquals(6, project.getExperiment().getSamples().size());
        assertEquals(6, project.getExperiment().getExtracts().size());
        assertEquals(6, project.getExperiment().getLabeledExtracts().size());
        assertEquals(6, project.getExperiment().getHybridizations().size());
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell2", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast2", testSource.getCellType().getValue());
        assertEquals("Pancreas", testSource.getTissueSite().getValue());
        testSource = findSource(project, "TK6neo replicate 2");
        assertEquals("B_lymphoblast", testSource.getCellType().getValue());
        assertNull(findSource(project, "TK6neo replicate 3"));
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray()
                .getDesign().getName());
        tx.commit();

        // now try to add a new biomaterial while update existing biomaterials
        importFiles(TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("This sentence is added to the description."));
        assertEquals(2, project.getExperiment().getFactors().size());
        assertEquals(10, project.getExperiment().getExperimentContacts().size());
        assertEquals(7, project.getExperiment().getSources().size());
        assertEquals(7, project.getExperiment().getSamples().size());
        assertEquals(7, project.getExperiment().getExtracts().size());
        assertEquals(7, project.getExperiment().getLabeledExtracts().size());
        assertEquals(7, project.getExperiment().getHybridizations().size());
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell2", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast2", testSource.getCellType().getValue());
        assertEquals("Pancreas", testSource.getTissueSite().getValue());
        testSource = findSource(project, "TK6neo replicate 2");
        assertEquals("B_lymphoblast3", testSource.getCellType().getValue());
        testSource = findSource(project, "TK6neo replicate 3");
        assertEquals("cell", testSource.getMaterialType().getValue());
        assertEquals("HG-Focus", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray()
                .getDesign().getName());
        tx.commit();
    }

    @Test
    public void testDataMatrixCopyNumberMageTabImportSuccess() throws Exception {
        final ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML,
                        AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        addDesignToExperiment(design);
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_DATA));
        importFiles(fileSet, true);

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        tx.commit();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Ignore
    public void testDataMatrixCopyNumberMageTabImportFailDueToBadSdrfHybCount() throws Exception {
        final ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML,
                        AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        addDesignToExperiment(design);
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_WRONG_HYB_COUNT_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_WRONG_HYB_COUNT_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_DATA));
        importFiles(fileSet, true);
        assertEquals(Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        addDesignToExperiment(design);

        importFiles(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (final Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef2() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        addDesignToExperiment(design);

        final CaArrayFileSet fileSet = uploadFiles(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        for (final CaArrayFile file : fileSet.getFilesByType(CelHandler.CEL_FILE_TYPE)) {
            file.setFileType(UnparsedDataHandler.FILE_TYPE_AFFYMETRIX_DAT);
        }
        tx.commit();

        importFiles(fileSet, null);

        tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (final Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testImportNonMageTabWithoutArrayDesign() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        addDesignToExperiment(design);

        final MageTabFileSet inputFiles = new MageTabFileSet();
        for (final FileRef f : TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET.getAllFiles()) {
            if (f.getName().endsWith("CEL")) {
                inputFiles.addNativeData(f);
            }
        }
        final MageTabDocumentSet docSet =
                MageTabParser.INSTANCE.parse(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        docSet.getIdfDocuments().clear();
        docSet.getSdrfDocuments().clear();

        final CaArrayFileSet fileSet = uploadFiles(inputFiles);

        Transaction tx = this.hibernateHelper.beginTransaction();
        for (final CaArrayFile file : fileSet.getFilesByType(CelHandler.CEL_FILE_TYPE)) {
            file.setFileType(UnparsedDataHandler.FILE_TYPE_AFFYMETRIX_DAT);
        }
        tx.commit();

        importFiles(fileSet, DataImportOptions.getAutoCreatePerFileOptions());

        tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(3, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (final Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testReimport() throws Exception {
        ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML,
                        UnparsedArrayDesignFileHandler.AGILENT_CSV);
        assertNull(design.getDesignDetails());

        Transaction tx = this.hibernateHelper.beginTransaction();
        design.getFirstDesignFile().setFileType(AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(design);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().evict(design);
        this.getFileManagementService().reimportAndParseArrayDesign(design.getId());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        design = (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        assertNotNull(design.getDesignDetails());
        assertEquals(45220, design.getNumberOfFeatures().intValue());
        assertEquals(45220, design.getDesignDetails().getFeatures().size());
        tx.commit();
    }

    @Test
    public void testReimportWithReferencingExperiment() throws Exception {
        ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML,
                        UnparsedArrayDesignFileHandler.AGILENT_CSV);
        assertNull(design.getDesignDetails());

        addDesignToExperiment(design);

        Transaction tx = this.hibernateHelper.beginTransaction();
        design.getFirstDesignFile().setFileType(AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(design);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().evict(design);
        this.getFileManagementService().reimportAndParseArrayDesign(design.getId());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        design = (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        assertNotNull(design.getDesignDetails());
        assertEquals(45220, design.getNumberOfFeatures().intValue());
        assertEquals(45220, design.getDesignDetails().getFeatures().size());
        tx.commit();
    }

    @Test
    public void testIlluminaCsvDataImport() throws Exception {
        final int numberOfProbes = 10;
        importDesignAndDataFilesIntoProject(IlluminaArrayDesignFiles.HUMAN_WG6_CSV, CsvDataHandler.DATA_CSV_FILE_TYPE,
                IlluminaArrayDataFiles.HUMAN_WG6_SMALL);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        final List<AbstractDesignElement> l =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
                        .getDesignElements();
        assertEquals(numberOfProbes, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(19, hdl.size());
        for (final HybridizationData hd : hdl) {
            assertEquals(4, hd.getColumns().size());
            final List<String> intColumns =
                    new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
            final List<String> floatColumns =
                    new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                            SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                            SampleProbeProfileQuantitationType.DETECTION.getName()));
            for (final AbstractDataColumn c : hd.getColumns()) {
                final String name = c.getQuantitationType().getName();
                if (intColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            intColumns.remove(c.getQuantitationType().getName()));
                } else if (floatColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            floatColumns.remove(c.getQuantitationType().getName()));
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", intColumns.isEmpty() && floatColumns.isEmpty());
        }
        tx.commit();
    }

    @Test
    public void testIlluminaSampleProbeProfileImport() throws Exception {
        importDesignAndDataFilesIntoProject(IlluminaArrayDesignFiles.MOUSE_REF_8,
                SampleProbeProfileHandler.SAMPLE_PROBE_PROFILE_FILE_TYPE, IlluminaArrayDataFiles.SAMPLE_PROBE_PROFILE);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        final List<AbstractDesignElement> l =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
                        .getDesignElements();
        assertEquals(3, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(16, hdl.size());
        for (final HybridizationData hd : hdl) {
            assertEquals(8, hd.getColumns().size());
            final List<String> intColumns =
                    new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.NARRAYS.getName(),
                            SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
            final List<String> floatColumns =
                    new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.MIN_SIGNAL.getName(),
                            SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                            SampleProbeProfileQuantitationType.MAX_SIGNAL.getName(),
                            SampleProbeProfileQuantitationType.ARRAY_STDEV.getName(),
                            SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                            SampleProbeProfileQuantitationType.DETECTION.getName()));
            for (final AbstractDataColumn c : hd.getColumns()) {
                final String name = c.getQuantitationType().getName();
                if (intColumns.contains(name)) {
                    assertEquals(3, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            intColumns.remove(c.getQuantitationType().getName()));
                } else if (floatColumns.contains(name)) {
                    assertEquals(3, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            floatColumns.remove(c.getQuantitationType().getName()));
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", intColumns.isEmpty() && floatColumns.isEmpty());
        }
        tx.commit();
    }

    // TODO: ARRAY-1942 follow-on tasks for <ARRAY-1896 Merge dkokotov_storage_osgi_consolidation Branch to trunk>
    // Temporarily comment out the below test. It needs to be moved to vendor plugin specific test package.
    // Otherwise ant targets test:compile:caarray-plugins.test.classes and test:compile:caarray-ejb.test.classes
    // would have circular dependency that cause them to fail.
    // @Test
    // public void testIlluminaGenotypingProcessedMatrixImport() throws Exception {
    // Transaction tx = hibernateHelper.beginTransaction();
    // ArrayDesign design = IlluminaGenotypingProcessedMatrixHandlerIntegrationTest.buildArrayDesign();
    // hibernateHelper.getCurrentSession().save(design);
    // tx.commit();
    //
    // importDataFilesIntoProject(design, GenotypingProcessedMatrixHandler.GENOTYPING_MATRIX_FILE_TYPE,
    // IlluminaArrayDataFiles.ILLUMINA_DERIVED_1_HYB);
    // tx = hibernateHelper.beginTransaction();
    // Project project = getTestProject();
    // design = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
    // Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
    // List<AbstractDesignElement> l =
    // hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
    // assertEquals(3, l.size());
    // for (AbstractDesignElement de : l) {
    // PhysicalProbe p = (PhysicalProbe) de;
    // assertTrue(design.getDesignDetails().getProbes().contains(p));
    // }
    // List<HybridizationData> hdl =
    // hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
    // assertEquals(1, hdl.size());
    // assertEquals(6, hdl.get(0).getColumns().size());
    // List<String> stringColumns = new ArrayList<String>(
    // Arrays.asList(IlluminaGenotypingProcessedMatrixQuantitationType.ALLELE.getName()));
    // List<String> floatColumns = new ArrayList<String>(Arrays.asList(
    // IlluminaGenotypingProcessedMatrixQuantitationType.B_ALLELE_FREQ.getName(),
    // IlluminaGenotypingProcessedMatrixQuantitationType.GC_SCORE.getName(),
    // IlluminaGenotypingProcessedMatrixQuantitationType.LOG_R_RATIO.getName(),
    // IlluminaGenotypingProcessedMatrixQuantitationType.R.getName(),
    // IlluminaGenotypingProcessedMatrixQuantitationType.THETA.getName()));
    // for (AbstractDataColumn c : hdl.get(0).getColumns()) {
    // String name = c.getQuantitationType().getName();
    // if (stringColumns.contains(name)) {
    // assertEquals(3, ((StringColumn) c).getValues().length);
    // assertTrue("missing " + c.getQuantitationType(),
    // stringColumns.remove(c.getQuantitationType().getName()));
    // } else if (floatColumns.contains(name)) {
    // assertEquals(3, ((FloatColumn) c).getValues().length);
    // assertTrue("missing " + c.getQuantitationType(), floatColumns.remove(c.getQuantitationType().getName()));
    // } else {
    // fail("unexpected column: " + name);
    // }
    // }
    // assertTrue("not all columns present", stringColumns.isEmpty() && floatColumns.isEmpty());
    // tx.commit();
    // }

    @Test
    public void testGenepixGprImport() throws Exception {
        importDesignAndDataFilesIntoProject(GenepixArrayDataFiles.JOE_DERISI_FIX, GprHandler.GPR_FILE_TYPE,
                GenepixArrayDataFiles.SMALL_IDF, GenepixArrayDataFiles.SMALL_SDRF, GenepixArrayDataFiles.GPR_4_1_1);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();

        final List<AbstractDesignElement> l =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
                        .getDesignElements();
        assertEquals(6528, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());

        final GenepixQuantitationType[] expectedTypes =
                new GenepixQuantitationType[] {X, Y, DIA, F635_MEDIAN, F635_MEAN, F635_SD, B635_MEDIAN, B635_MEAN,
                        B635_SD, PERCENT_GT_B635_1SD, PERCENT_GT_B635_2SD, F635_PERCENT_SAT, F532_MEDIAN, F532_MEAN,
                        F532_SD, B532_MEDIAN, B532_MEAN, B532_SD, PERCENT_GT_B532_1SD, PERCENT_GT_B532_2SD,
                        F532_PERCENT_SAT, RATIO_OF_MEDIANS_635_532, RATIO_OF_MEANS_635_532, MEDIAN_OF_RATIOS_635_532,
                        MEAN_OF_RATIOS_635_532, RATIOS_SD_635_532, RGN_RATIO_635_532, RGN_R2_635_532, F_PIXELS,
                        B_PIXELS, SUM_OF_MEDIANS_635_532, SUM_OF_MEANS_635_532, LOG_RATIO_635_532, F635_MEDIAN_B635,
                        F532_MEDIAN_B532, F635_MEAN_B635, F532_MEAN_B532, F635_TOTAL_INTENSITY, F532_TOTAL_INTENSITY,
                        SNR_635, SNR_532, FLAGS, NORMALIZE };
        for (final HybridizationData hd : hdl) {
            assertEquals(expectedTypes.length, hd.getColumns().size());
            for (int i = 0; i < expectedTypes.length; i++) {
                assertEquals(expectedTypes[i].getName(), hd.getColumns().get(i).getQuantitationType().getName());
            }
        }
        tx.commit();
    }

    @Test
    public void testNimblegenPairImport() throws Exception {
        importDesignAndDataFilesIntoProject(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN,
                PairDataHandler.NORMALIZED_PAIR_FILE_TYPE, NimblegenArrayDataFiles.SHORT_HUMAN_EXPRESSION);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();

        final List<AbstractDesignElement> l =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
                        .getDesignElements();
        assertEquals(3, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());
        for (final HybridizationData hd : hdl) {
            assertEquals(5, hd.getColumns().size());
            final List<String> intColumns =
                    new ArrayList<String>(Arrays.asList(NimblegenQuantitationType.MATCH_INDEX.getName(),
                            NimblegenQuantitationType.X.getName(), NimblegenQuantitationType.Y.getName()));
            final List<String> floatColumns =
                    new ArrayList<String>(Arrays.asList(NimblegenQuantitationType.MM.getName(),
                            NimblegenQuantitationType.PM.getName()));
            for (final AbstractDataColumn c : hd.getColumns()) {
                final String name = c.getQuantitationType().getName();
                if (intColumns.contains(name)) {
                    assertEquals(3, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            intColumns.remove(c.getQuantitationType().getName()));
                } else if (floatColumns.contains(name)) {
                    assertEquals(3, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            floatColumns.remove(c.getQuantitationType().getName()));
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", intColumns.isEmpty() && floatColumns.isEmpty());
        }
        tx.commit();
    }

    @Test
    public void testUpdateBioMaterialChain() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        addDesignToExperiment(design);

        importFiles(TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_BASELINE_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(2, project.getExperiment().getExtracts().size());
        assertEquals(2, project.getExperiment().getLabeledExtracts().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNull(findSource(project, "Source B"));
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();

        // now try to add new biomaterials in the middle of the existing chains
        importFiles(TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        Set<Extract> extracts = project.getExperiment().getExtracts();
        System.out.println("Updated extracts: " + extracts);
        assertEquals(4, extracts.size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();

        // now try to add a data files to existing hybs
        importFiles(TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        extracts = project.getExperiment().getExtracts();
        System.out.println("Updated extracts: " + extracts);
        assertEquals(4, extracts.size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();
    }

    @Test
    public void testUpdateFiles() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        addDesignToExperiment(design);

        importFiles(TestMageTabSets.UPDATE_FILES_BASELINE_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(1, project.getExperiment().getExtracts().size());
        assertEquals(1, project.getExperiment().getLabeledExtracts().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNull(findSource(project, "Source B"));
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().size());
        RawArrayData raw =
                project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().iterator().next();
        DerivedArrayData derived =
                project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().iterator().next();
        assertEquals(1, derived.getDerivedFromArrayDataCollection().size());
        assertEquals(raw, derived.getDerivedFromArrayDataCollection().iterator().next());
        tx.commit();

        // now try to add data files to existing hybs, which also reference existing data files
        importFiles(TestMageTabSets.UPDATE_FILES_NEW_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());

        assertEquals(3, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().size());
        derived = project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().iterator().next();
        assertEquals("BM1.EXP", derived.getDataFile().getName());
        assertEquals(2, derived.getDerivedFromArrayDataCollection().size());
        for (final RawArrayData oneRaw : project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection()) {
            assertTrue(Arrays.asList("BM1.CEL", "BM1a.CEL").contains(oneRaw.getDataFile().getName()));
            assertTrue("BM1.EXP is not derived from " + oneRaw.getName(), derived.getDerivedFromArrayDataCollection()
                    .contains(oneRaw));
        }

        assertEquals(3, project.getExperiment().getHybridizationByName("Hyb 2").getAllDataFiles().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().size());
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 2").getDerivedDataCollection().size());
        raw = project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().iterator().next();
        assertEquals("BM2.CEL", raw.getDataFile().getName());
        for (final DerivedArrayData oneDerived : project.getExperiment().getHybridizationByName("Hyb 2")
                .getDerivedDataCollection()) {
            assertTrue(Arrays.asList("BM2.EXP", "BM2a.EXP").contains(oneDerived.getDataFile().getName()));
            assertEquals(1, oneDerived.getDerivedFromArrayDataCollection().size());
            assertEquals(raw, oneDerived.getDerivedFromArrayDataCollection().iterator().next());
        }

        tx.commit();
    }
}
