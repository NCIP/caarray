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
package gov.nih.nci.caarray.application.arraydata;

import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B532_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B532_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B532_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B635_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B635_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B635_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B_PIXELS;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.DIA;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEAN_B532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEDIAN_B532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_PERCENT_SAT;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEAN_B635;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEDIAN_B635;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_PERCENT_SAT;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.FLAGS;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F_PIXELS;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.LOG_RATIO_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.MEAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B532_1SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B532_2SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B635_1SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B635_2SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RATIOS_SD_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RATIO_OF_MEANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RATIO_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RGN_R2_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RGN_RATIO_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.SUM_OF_MEANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.SUM_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.X;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.Y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixCelQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixExpressionChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixSnpAxiomGTChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixSnpBirdseedChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixSnpBrlmmChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixSnpChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaExpressionQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.AffymetrixArrayDesignReadException;
import gov.nih.nci.caarray.application.arraydesign.AffymetrixCdfReader;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParserImplementation;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.cel.FusionCELData;
import affymetrix.fusion.cel.FusionCELFileEntryType;
import affymetrix.fusion.chp.FusionCHPDataReg;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionExpressionProbeSetResults;
import affymetrix.fusion.chp.FusionGenotypeProbeSetResults;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;

/**
 * Tests the ArrayDataService subsystem
 */
@SuppressWarnings("PMD")
public class ArrayDataServiceTest extends AbstractServiceTest {
    private static final String GAL_DERISI_LSID_OBJECT_ID = "JoeDeRisi-fix";
    private static final String GAL_YEAST1_LSID_OBJECT_ID = "Yeast1";
    private static final String AFFY_TEST3_LSID_OBJECT_ID = "Test3";
    private static final String AFFY_TEN_K_LSID_OBJECT_ID = "Mapping10K_Xba131";
    private static final String HG_FOCUS_LSID_OBJECT_ID = "HG-Focus";
    private static final String ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID = "Human_WG-6";
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();

    public ArrayDataService arrayDataService;
    FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    LocalSearchDaoStub searchDaoStub = new LocalSearchDaoStub();
    
    private long fileIdCounter = 1;

    @Before
    public void setUp() throws Exception {
        ArrayDataServiceBean arrayDataServiceBean = new ArrayDataServiceBean();
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        ArrayDesignServiceBean arrayDesignServiceBean = new ArrayDesignServiceBean();
        arrayDesignServiceBean.setDaoFactory(this.daoFactoryStub);
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignServiceBean);
        arrayDataServiceBean.setDaoFactory(this.daoFactoryStub);
        this.arrayDataService = arrayDataServiceBean;
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(this.fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEN_K_CDF);
    }

    @Test
    public void testInitialize() {
        this.arrayDataService.initialize();
        assertTrue(this.daoFactoryStub.dataTypeMap.containsKey(AffymetrixArrayDataTypes.AFFYMETRIX_CEL));
        assertTrue(this.daoFactoryStub.quantitationTypeMap.keySet().containsAll(Arrays.asList(AffymetrixCelQuantitationType.values())));
    }

    @Test
    public void testImportRawAndDerivedSameName() throws InvalidDataFileException {
        // tests that imports of raw and derived data files with same base name go
        // to the same hybridization chain
        CaArrayFile focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID);
        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());
        this.arrayDataService.importData(focusCel, true, DEFAULT_IMPORT_OPTIONS);
        this.arrayDataService.importData(focusCalvinCel, true, DEFAULT_IMPORT_OPTIONS);
        this.arrayDataService.importData(focusChp, true, DEFAULT_IMPORT_OPTIONS);
        this.arrayDataService.importData(focusCalvinChp, true, DEFAULT_IMPORT_OPTIONS);
        checkAnnotation(focusCel, 2);
        Experiment exp = focusCel.getProject().getExperiment();
        assertEquals(2, exp.getHybridizations().size());
        for (Hybridization h : exp.getHybridizations()) {
            assertEquals(1, h.getRawDataCollection().size());
            assertEquals(1, h.getDerivedDataCollection().size());
            if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCel)) {
                assertEquals(focusChp, h.getDerivedDataCollection().iterator().next().getDataFile());
            } else if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCalvinCel)) {
                assertEquals(focusCalvinChp, h.getDerivedDataCollection().iterator().next().getDataFile());
            } else {
                fail("Expected hybridization to be linked to either focus or calvin focus CEL");
            }
        }
    }

    @Test
    public void testImportSingleAnnotationChain() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        CaArrayFile focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID);
        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());
        DataImportOptions options = DataImportOptions.getAutoCreateSingleOptions("TEST_NAME");
        this.arrayDataService.importData(focusCel, true, options);
        this.arrayDataService.importData(focusCalvinCel, true, options);
        this.arrayDataService.importData(focusChp, true, options);
        this.arrayDataService.importData(focusCalvinChp, true, options);
        checkAnnotation(focusCel, 1);
        Experiment exp = focusCel.getProject().getExperiment();
        assertEquals(1, exp.getHybridizations().size());
        Hybridization h = exp.getHybridizations().iterator().next();
        assertEquals(2, h.getRawDataCollection().size());
        assertEquals(2, h.getDerivedDataCollection().size());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testImportToTargetSources() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        CaArrayFile focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID);
        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());

        Source targetSrc1 = new Source();
        targetSrc1.setId(1L);
        targetSrc1.setName("targetSrc1");
        targetSrc1.setExperiment(focusCel.getProject().getExperiment());
        searchDaoStub.save(targetSrc1);
        Source targetSrc2 = new Source();
        targetSrc2.setName("targetSrc2");
        targetSrc2.setId(2L);
        searchDaoStub.save(targetSrc2);
        targetSrc2.setExperiment(focusCel.getProject().getExperiment());
        focusCel.getProject().getExperiment().getSources().addAll(Arrays.asList(targetSrc1, targetSrc2));

        DataImportOptions options = DataImportOptions.getAssociateToBiomaterialsOptions(
                ExperimentDesignNodeType.SOURCE, Arrays.asList(targetSrc1.getId(), targetSrc2.getId()));
        this.arrayDataService.importData(focusCel, true, options);
        this.arrayDataService.importData(focusCalvinCel, true, options);
        this.arrayDataService.importData(focusChp, true, options);
        this.arrayDataService.importData(focusCalvinChp, true, options);
        Experiment exp = focusCel.getProject().getExperiment();
        assertEquals(2, exp.getSources().size());
        assertEquals(2, exp.getSamples().size());
        assertEquals(2, exp.getHybridizations().size());
        assertEquals(2, targetSrc1.getSamples().size());
        assertEquals(2, targetSrc2.getSamples().size());
    }


    @Test
    public void testCreateAnnotation() throws InvalidDataFileException {
        testCreateAnnotationCel();
        testCreateAnnotationChp();
        testCreateAnnotationIllumina();
        testExistingAnnotationNotOverwritten();
    }

    private void testExistingAnnotationNotOverwritten() throws InvalidDataFileException {
        CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        RawArrayData celData = new RawArrayData();
        Hybridization hybridization = new Hybridization();
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

    private void testCreateAnnotationIllumina() throws InvalidDataFileException {
        CaArrayFile illuminaFile = getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID);
        this.arrayDataService.importData(illuminaFile, true, DEFAULT_IMPORT_OPTIONS);
        checkAnnotation(illuminaFile, 19);
    }

    @Test
    public void testUnsupportedDataFile() throws InvalidDataFileException {
        CaArrayFile expFile = getDataCaArrayFile(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_EXP, FileType.AFFYMETRIX_EXP);
        this.arrayDataService.importData(expFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, expFile.getFileStatus());
    }

    private void testCreateAnnotationCel() throws InvalidDataFileException {
        CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        this.arrayDataService.importData(celFile, true, DEFAULT_IMPORT_OPTIONS);
        checkAnnotation(celFile, 1);
    }

    private void testCreateAnnotationChp() throws InvalidDataFileException {
        CaArrayFile chpFile = getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP, AFFY_TEST3_LSID_OBJECT_ID);
        this.arrayDataService.importData(chpFile, true, DEFAULT_IMPORT_OPTIONS);
        checkAnnotation(chpFile, 1);
    }

    private void checkAnnotation(CaArrayFile dataFile, int numberOfSamples) {
        Experiment experiment = dataFile.getProject().getExperiment();
        assertEquals(numberOfSamples, experiment.getSources().size());
        assertEquals(numberOfSamples, experiment.getSamples().size());
        assertEquals(numberOfSamples, experiment.getExtracts().size());
        assertEquals(numberOfSamples, experiment.getLabeledExtracts().size());
    }

    @Test
    public void testImportGenepix() throws InvalidDataFileException {
        QuantitationTypeDescriptor[] expectedTypes = new QuantitationTypeDescriptor[] {
                X, Y, DIA,
                F635_MEDIAN, F635_MEAN, F635_SD, B635_MEDIAN, B635_MEAN, B635_SD, PERCENT_GT_B635_1SD, PERCENT_GT_B635_2SD, F635_PERCENT_SAT,
                F532_MEDIAN, F532_MEAN, F532_SD, B532_MEDIAN, B532_MEAN, B532_SD, PERCENT_GT_B532_1SD, PERCENT_GT_B532_2SD, F532_PERCENT_SAT,
                RATIO_OF_MEDIANS_635_532, RATIO_OF_MEANS_635_532, MEDIAN_OF_RATIOS_635_532, MEAN_OF_RATIOS_635_532, RATIOS_SD_635_532, RGN_RATIO_635_532,
                RGN_R2_635_532, F_PIXELS, B_PIXELS, SUM_OF_MEDIANS_635_532, SUM_OF_MEANS_635_532, LOG_RATIO_635_532,
                F635_MEDIAN_B635, F532_MEDIAN_B532, F635_MEAN_B635, F532_MEAN_B532, FLAGS
        };
        testImportGenepixFile(GenepixArrayDataFiles.GPR_3_0_6, expectedTypes, 1);
    }

    private void testImportGenepixFile(File gprFile, QuantitationTypeDescriptor[] expectedTypes, int expectedNumberOfSamples) throws InvalidDataFileException {
        CaArrayFile gprCaArrayFile = getGprCaArrayFile(gprFile, GAL_DERISI_LSID_OBJECT_ID);
        this.arrayDataService.importData(gprCaArrayFile, true, DEFAULT_IMPORT_OPTIONS);
        DerivedArrayData data = (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(
                gprCaArrayFile.getId());
        assertNotNull(data);
        checkAnnotation(gprCaArrayFile, expectedNumberOfSamples);
        checkColumnTypes(data.getDataSet(), expectedTypes);
    }

    @Test
    public void testImportExpressionChp() throws InvalidDataFileException, AffymetrixArrayDesignReadException {
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CALVIN_CHP);
    }

    private void testImportExpressionChp(File cdfFile, File chpFile) throws InvalidDataFileException, AffymetrixArrayDesignReadException {
        DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(AffymetrixExpressionChpQuantitationType.values().length,
                hybridizationData.getColumns().size());
        assertEquals(AffymetrixExpressionChpQuantitationType.values().length,
                dataSet.getQuantitationTypes().size());
        for (AbstractDesignElement element : dataSet.getDesignElementList().getDesignElements()) {
            assertNotNull(element);
        }
        checkChpExpresionColumnTypes(dataSet);
    }

    private void checkChpExpresionColumnTypes(DataSet dataSet) {
        checkColumnTypes(dataSet, AffymetrixExpressionChpQuantitationType.values());
    }

    private void checkColumnTypes(DataSet dataSet, QuantitationTypeDescriptor[] descriptors) {
        for (int i = 0; i < descriptors.length; i++) {
            checkType(descriptors[i], dataSet.getQuantitationTypes().get(i));
            checkType(descriptors[i], dataSet.getHybridizationDataList().get(0).getColumns().get(i).getQuantitationType());
        }
    }

    private void checkType(QuantitationTypeDescriptor typeDescriptor, QuantitationType type) {
        assertEquals(typeDescriptor.getName(), type.getName());
    }

    @Test
    public void testImportSnpChp() throws InvalidDataFileException, AffymetrixArrayDesignReadException {
        testImportSnpChp(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CHP, AffymetrixSnpChpQuantitationType.values());
        testImportSnpChp(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CALVIN_CHP, AffymetrixSnpChpQuantitationType.values());
        testImportSnpChp(AffymetrixArrayDesignFiles.BIRDSEED_SNP_TEST_CDF, AffymetrixArrayDataFiles.BIRDSEED_SNP_TEST_CHP,
                AffymetrixSnpBirdseedChpQuantitationType.values());
//        testImportSnpChp(AffymetrixArrayDesignFiles.BRLMM_SNP_TEST_CDF, AffymetrixArrayDataFiles.BRLMM_SNP_TEST_CHP,
//                AffymetrixSnpBrlmmChpQuantitationType.values());
//        testImportSnpChp(AffymetrixArrayDesignFiles.AXIOMGT_SNP_TEST_CDF, AffymetrixArrayDataFiles.AXIOMGT_SNP_TEST_CHP,
//                AffymetrixSnpAxiomGTChpQuantitationType.values());
    }

    private void testImportSnpChp(File cdfFile, File chpFile, QuantitationTypeDescriptor[] quantitationTypeDescriptors)
            throws InvalidDataFileException, AffymetrixArrayDesignReadException {
        DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(quantitationTypeDescriptors.length, hybridizationData.getColumns().size());
        assertEquals(quantitationTypeDescriptors.length, dataSet.getQuantitationTypes().size());
        checkChpSnpColumnTypes(dataSet, quantitationTypeDescriptors);
    }

    private void checkChpSnpColumnTypes(DataSet dataSet, QuantitationTypeDescriptor[] descriptors) {
        checkColumnTypes(dataSet, descriptors);
    }

    @Test
    public void testImportCel() throws InvalidDataFileException {
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        assertEquals(FileStatus.UPLOADED, celData.getDataFile().getFileStatus());
        assertNull(celData.getDataSet());
        this.arrayDataService.importData(celData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, celData.getDataFile().getFileStatus());
        assertEquals(AffymetrixArrayDataFiles.TEST3_CEL.getName(), celData.getName());
        assertNotNull(celData.getDataSet());
        DataSet dataSet = celData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(celData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(AffymetrixCelQuantitationType.values().length, hybridizationData.getColumns().size());
        assertEquals(AffymetrixCelQuantitationType.values().length, dataSet.getQuantitationTypes().size());
        checkCelColumnTypes(dataSet);
    }

    // VALIDATION

    @Test
    public void testValidate() {
        testIlluminaValidation();
        testGenepixNoMageTabValidation();
        testGenepixValidation();
        testCelValidation();
        testChpValidation();
    }

    private void testGenepixValidation() {
        List<File> fileList = new ArrayList<File>();
        fileList.add(GenepixArrayDataFiles.GPR_3_0_6);
        fileList.add(GenepixArrayDataFiles.GPR_4_0_1);
        fileList.add(GenepixArrayDataFiles.GPR_4_1_1);
        fileList.add(GenepixArrayDataFiles.GPR_5_0_1);
        fileList.add(GenepixArrayDataFiles.EXPORTED_IDF);
        fileList.add(GenepixArrayDataFiles.EXPORTED_SDRF);
        fileList.add(AffymetrixArrayDataFiles.TEST3_CEL);

        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_3_0_6, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_0_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_1_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1, GAL_YEAST1_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getGprCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
    }

    private void testGenepixNoMageTabValidation() {
        List<File> fileList = new ArrayList<File>();
        fileList.add(GenepixArrayDataFiles.GPR_3_0_6);
        fileList.add(GenepixArrayDataFiles.GPR_4_0_1);
        fileList.add(GenepixArrayDataFiles.GPR_4_1_1);
        fileList.add(GenepixArrayDataFiles.GPR_5_0_1);

        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_3_0_6, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_0_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_1_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1, GAL_YEAST1_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));

    }

    private void testIlluminaValidation() {
        List<File> fileList = new ArrayList<File>();
        fileList.add(IlluminaArrayDataFiles.HUMAN_WG6);
        testValidFile(getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
    }

    private MageTabDocumentSet genMageTabDocSet(List<File> fl) {
        MageTabFileSet mTabFiles = new MageTabFileSet();
        for (File f : fl) {
            if (f.getName().contains(".idf")) {
                mTabFiles.addIdf(new JavaIOFileRef(f));
            } else if (f.getName().contains(".sdrf")) {
                mTabFiles.addSdrf(new JavaIOFileRef(f));
            } else {
                mTabFiles.addNativeData(new JavaIOFileRef(f));
            }
        }
        MageTabDocumentSet mTabSet = new MageTabDocumentSet(mTabFiles, MageTabParserImplementation.CAARRAY_VALIDATION_SET);
        return mTabSet;
    }

    private void testValidFile(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        this.arrayDataService.validate(caArrayFile, mTabSet);
        if (FileStatus.VALIDATION_ERRORS.equals(caArrayFile.getFileStatus())) {
            System.out.println(caArrayFile.getValidationResult());
        }
        assertEquals(FileStatus.VALIDATED, caArrayFile.getFileStatus());
    }

    private void testInvalidFile(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        this.arrayDataService.validate(caArrayFile, mTabSet);
        assertEquals(FileStatus.VALIDATION_ERRORS, caArrayFile.getFileStatus());
    }

    private void testChpValidation() {
        List<File> fileList = new ArrayList<File>();
        fileList.add(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP);
        fileList.add(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP);
        fileList.add(AffymetrixArrayDataFiles.HG_FOCUS_CHP);
        fileList.add(AffymetrixArrayDataFiles.TEST3_CHP);
        fileList.add(AffymetrixArrayDesignFiles.TEST3_CDF);

        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getChpCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
    }

    private void testCelValidation() {
        List<File> fileList = new ArrayList<File>();
        fileList.add(AffymetrixArrayDataFiles.TEST3_CEL);
        fileList.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        fileList.add(AffymetrixArrayDataFiles.TEST3_INVALID_DATA_CEL);
        fileList.add(AffymetrixArrayDataFiles.TEST3_INVALID_HEADER_CEL);

        testValidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_INVALID_DATA_CEL, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_INVALID_HEADER_CEL, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList));
    }

    @Test
    public void testGetData() throws InvalidDataFileException {
        testCelData();
        testExpressionChpData();
        testSnpChpData();
        testIlluminaData();
        testGenepixData();
        // The following test is commented out due to the change to parse on import.
        // It may be re-incorporated when parse on demand is re-instituted.
        // testCelDataForSelectedQuantitationTypes();
    }

    private void testGenepixData() throws InvalidDataFileException {
        CaArrayFile gprFile = getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1, GAL_DERISI_LSID_OBJECT_ID);
        this.arrayDataService.importData(gprFile, true, DEFAULT_IMPORT_OPTIONS);
        DerivedArrayData gprData = (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(gprFile.getId());
        DataSet dataSet = this.arrayDataService.getData(gprData);
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(51, hybridizationData.getColumns().size());
        IntegerColumn f635MedianColumn = (IntegerColumn) hybridizationData.getColumn(GenepixQuantitationType.F635_MEDIAN);
        assertNotNull(f635MedianColumn);
        assertEquals(6528, f635MedianColumn.getValues().length);
        assertEquals(138, f635MedianColumn.getValues()[0]);
        assertEquals(6, f635MedianColumn.getValues()[6527]);
        assertNotNull(hybridizationData.getHybridization().getArray());
    }

    private void testIlluminaData() throws InvalidDataFileException {
        testIlluminaDataSmall();
        testIlluminaDataFull();
    }

    private void testIlluminaDataSmall() throws InvalidDataFileException {
        CaArrayFile illuminaFile = getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6_SMALL, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID);
        this.arrayDataService.importData(illuminaFile, true, DEFAULT_IMPORT_OPTIONS);
        DerivedArrayData illuminaData = (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(
                illuminaFile.getId());
        DataSet dataSet = this.arrayDataService.getData(illuminaData);
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(19, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(4, hybridizationData.getColumns().size());
        FloatColumn signalColumn = (FloatColumn) hybridizationData.getColumn(IlluminaExpressionQuantitationType.AVG_SIGNAL);
        assertNotNull(signalColumn);
        assertEquals(10, signalColumn.getValues().length);
        assertEquals(5.8, signalColumn.getValues()[0]);
        assertEquals(3.6, signalColumn.getValues()[9]);
        assertNotNull(hybridizationData.getHybridization().getArray());
        assertEquals(10, illuminaData.getDataSet().getDesignElementList().getDesignElements().size());
    }

    private void testIlluminaDataFull() throws InvalidDataFileException {
        CaArrayFile illuminaFile = getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID);
        this.arrayDataService.importData(illuminaFile, true, DEFAULT_IMPORT_OPTIONS);
        DerivedArrayData illuminaData = (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(
                illuminaFile.getId());
        assertEquals(19, illuminaData.getHybridizations().size());
        DataSet dataSet = this.arrayDataService.getData(illuminaData);
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(19, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(4, hybridizationData.getColumns().size());
        assertNotNull(hybridizationData.getHybridization().getArray());
        assertEquals(47293, illuminaData.getDataSet().getDesignElementList().getDesignElements().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDataIllegalArguments() {
        this.arrayDataService.getData(null);
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        celData.setDataFile(null);
        this.arrayDataService.getData(celData);

    }

    private void testExpressionChpData() throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        DataSet dataSet = this.arrayDataService.getData(chpData);
        assertNotNull(dataSet.getDesignElementList());
        checkExpressionData(AffymetrixArrayDataFiles.TEST3_CHP, dataSet);
    }

    private void checkExpressionData(File chpFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        FusionCHPLegacyData chpData = FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(chpFile.getAbsolutePath()));
        FusionExpressionProbeSetResults results = new FusionExpressionProbeSetResults();
        FloatColumn signalColumn = (FloatColumn) hybridizationData.getColumn(AffymetrixExpressionChpQuantitationType.CHP_SIGNAL);
        for (int i = 0; i < chpData.getHeader().getNumProbeSets(); i++) {
            try {
                chpData.getExpressionResults(i, results);
            } catch (final UnsignedOutOfLimitsException e) {
                fail(e.toString());
            } catch (final IOException e) {
                fail(e.toString());
            }
           assertEquals(results.getSignal(), signalColumn.getValues()[i]);
        }
        assertNotNull(hybridizationData.getHybridization().getArray());
    }

    private void testSnpChpData() throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CHP);
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        DataSet dataSet = this.arrayDataService.getData(chpData);
        assertNotNull(dataSet.getDesignElementList());
        checkSnpData(AffymetrixArrayDataFiles.TEN_K_1_CHP, dataSet);
    }

    private void checkSnpData(File chpFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        FusionCHPLegacyData chpData = FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(chpFile.getAbsolutePath()));
        FusionGenotypeProbeSetResults results = new FusionGenotypeProbeSetResults();
        StringColumn alleleColumn = (StringColumn) hybridizationData.getColumn(AffymetrixSnpChpQuantitationType.CHP_ALLELE);
        for (int i = 0; i < chpData.getHeader().getNumProbeSets(); i++) {
            try {
                chpData.getGenotypingResults(i, results);
            } catch (final UnsignedOutOfLimitsException e) {
                 fail(e.toString());
            } catch (final IOException e) {
                fail(e.toString());
            }
            assertEquals(results.getAlleleCallString(), alleleColumn.getValues()[i]);
        }
        assertNotNull(hybridizationData.getHybridization().getArray());
    }

    private void testCelData() throws InvalidDataFileException {
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        this.arrayDataService.importData(celData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        DataSet dataSet = this.arrayDataService.getData(celData);
        assertNotNull(dataSet.getDesignElementList());
        checkCelData(AffymetrixArrayDataFiles.TEST3_CEL, dataSet);
    }

    private void checkCelData(File celFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        FusionCELData fusionCelData = new FusionCELData();
        fusionCelData.setFileName(celFile.getAbsolutePath());
        fusionCelData.read();
        FusionCELFileEntryType fusionCelEntry = new FusionCELFileEntryType();
        ShortColumn xColumn = (ShortColumn) hybridizationData.getColumns().get(0);
        ShortColumn yColumn = (ShortColumn) hybridizationData.getColumns().get(1);
        FloatColumn intensityColumn = (FloatColumn) hybridizationData.getColumns().get(2);
        FloatColumn stdDevColumn = (FloatColumn) hybridizationData.getColumns().get(3);
        BooleanColumn isMaskedColumn = (BooleanColumn) hybridizationData.getColumns().get(4);
        BooleanColumn isOutlierColumn = (BooleanColumn) hybridizationData.getColumns().get(5);
        ShortColumn numPixelsColumn = (ShortColumn) hybridizationData.getColumns().get(6);
        assertNotNull(hybridizationData.getHybridization().getArray());
        for (int rowIndex = 0; rowIndex < fusionCelData.getCells(); rowIndex++) {
            try {
                fusionCelData.getEntry(rowIndex, fusionCelEntry);
            } catch (final UnsignedOutOfLimitsException e) {
                fail(e.toString());
            } catch (final IOException e) {
                fail(e.toString());
            }
            assertEquals(fusionCelData.indexToX(rowIndex), xColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.indexToY(rowIndex), yColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getIntensity(), intensityColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getStdv(), stdDevColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.isMasked(rowIndex), isMaskedColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.isOutlier(rowIndex), isOutlierColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getPixels(), numPixelsColumn.getValues()[rowIndex]);
        }
    }

    private void checkCelColumnTypes(DataSet dataSet) {
        checkColumnTypes(dataSet, AffymetrixCelQuantitationType.values());
    }

    private RawArrayData getCelData(File cdf, File cel) {
        Hybridization hybridization = createAffyHybridization(cdf);
        RawArrayData celData = new RawArrayData();
        celData.setType(this.daoFactoryStub.getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_CEL));
        celData.setDataFile(getCelCaArrayFile(cel, getCdfObjectId(cdf)));
        celData.addHybridization(hybridization);
        this.daoFactoryStub.addData(celData);
        hybridization.addArrayData(celData);
        return celData;
    }

    private Hybridization createAffyHybridization(File cdf) {
        return createHybridization(cdf, FileType.AFFYMETRIX_CDF);
    }

    private Hybridization createHybridization(File design, FileType type) {
        ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFile designFile = this.fileAccessServiceStub.add(design);
        designFile.setFileType(type);
        arrayDesign.addDesignFile(designFile);
        Array array = new Array();
        array.setDesign(arrayDesign);
        Hybridization hybridization = new Hybridization();
        hybridization.setArray(array);
        return hybridization;
    }

    private DerivedArrayData getChpData(File cdf, File file) {
        Hybridization hybridization = createAffyHybridization(cdf);
        DerivedArrayData chpData = new DerivedArrayData();
        chpData.setType(this.daoFactoryStub.getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CHP));
        chpData.setDataFile(getChpCaArrayFile(file, getCdfObjectId(cdf)));
        chpData.getHybridizations().add(hybridization);
        hybridization.getDerivedDataCollection().add(chpData);
        this.daoFactoryStub.addData(chpData);
        return chpData;
    }

    private String getCdfObjectId(File cdfFile) {
        try {
            AffymetrixCdfReader reader = AffymetrixCdfReader.create(cdfFile);
            String objectId = reader.getCdfData().getChipType();
            reader.close();
            return objectId;
        } catch (AffymetrixArrayDesignReadException e) {
            throw new IllegalStateException("Couldn't read CDF", e);
        }
    }

    private CaArrayFile getGprCaArrayFile(File gpr, String lsidObjectId) {
        CaArrayFile caArrayFile = getDataCaArrayFile(gpr, FileType.GENEPIX_GPR);
        ArrayDesign arrayDesign = daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getCelCaArrayFile(File cel, String lsidObjectId) {
        CaArrayFile caArrayFile = getDataCaArrayFile(cel, FileType.AFFYMETRIX_CEL);
        ArrayDesign arrayDesign = daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getChpCaArrayFile(File chp, String lsidObjectId) {
        CaArrayFile caArrayFile = getDataCaArrayFile(chp, FileType.AFFYMETRIX_CHP);
        ArrayDesign arrayDesign = daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getIlluminaCaArrayFile(File file, String lsidObjectId) {
        CaArrayFile caArrayFile = getDataCaArrayFile(file, FileType.ILLUMINA_DATA_CSV);
        ArrayDesign arrayDesign = daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    public CaArrayFile getDataCaArrayFile(File file, FileType type) {
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        caArrayFile.setId(fileIdCounter++);
        caArrayFile.setFileType(type);
        caArrayFile.setProject(new Project());
        caArrayFile.getProject().setExperiment(new Experiment());
        return caArrayFile;
    }

    private final class LocalSearchDaoStub extends SearchDaoStub {
        private Map<Long, PersistentObject> objMap = new HashMap<Long, PersistentObject>();

        @Override
        public void save(PersistentObject caArrayEntity) {
            super.save(caArrayEntity);
            objMap.put(caArrayEntity.getId(), caArrayEntity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            Object candidate = objMap.get(entityId);
            if (candidate == null) {
                return null;
            } else {
                return (T) (entityClass.isInstance(candidate) ? candidate : null);
            }
        }
    }

    public final class LocalDaoFactoryStub extends DaoFactoryStub {

        private final Map<ArrayDataTypeDescriptor, ArrayDataType> dataTypeMap =
            new HashMap<ArrayDataTypeDescriptor, ArrayDataType>();

        private final Map<QuantitationTypeDescriptor, QuantitationType> quantitationTypeMap =
            new HashMap<QuantitationTypeDescriptor, QuantitationType>();

        private final Map<Long, AbstractArrayData> fileDataMap = new HashMap<Long, AbstractArrayData>();

        private Map<String, ArrayDesign> arrayDesignMap = new HashMap<String, ArrayDesign>();

        @Override
        public SearchDao getSearchDao() {
            return searchDaoStub;
        }

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {


                @Override
                public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    if (arrayDesignMap.containsKey(lsidObjectId)) {
                        return arrayDesignMap.get(lsidObjectId);
                    } else if (AFFY_TEST3_LSID_OBJECT_ID.equals(lsidObjectId)) {
                        return createAffymetrixArrayDesign(lsidObjectId, AffymetrixArrayDesignFiles.TEST3_CDF);
                    } else if (AFFY_TEN_K_LSID_OBJECT_ID.equals(lsidObjectId)) {
                        return createAffymetrixArrayDesign(lsidObjectId, AffymetrixArrayDesignFiles.TEN_K_CDF);
                    } else if (ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID.equals(lsidObjectId)) {
                        return createArrayDesign(lsidObjectId, 126, 126, IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
                    } else if (GAL_DERISI_LSID_OBJECT_ID.equals(lsidObjectId)) {
                        return createArrayDesign(lsidObjectId, 126, 126, null);
                    } else if (GAL_YEAST1_LSID_OBJECT_ID.equals(lsidObjectId)) {
                        return createArrayDesign(lsidObjectId, 126, 126, null);
                    } else if (HG_FOCUS_LSID_OBJECT_ID.equals(lsidObjectId)) {
                        return createArrayDesign(lsidObjectId, 448, 448, AffymetrixArrayDesignFiles.HG_FOCUS_CDF);
                    } else {
                        return new ArrayDesign();
                    }
                }

                @SuppressWarnings("deprecation")
                private ArrayDesign createAffymetrixArrayDesign(String lsidObjectId, File cdfFile) {
                    try {
                        AffymetrixCdfReader reader = AffymetrixCdfReader.create(cdfFile);
                        int rows = reader.getCdfData().getHeader().getRows();
                        int columns = reader.getCdfData().getHeader().getCols();
                        ArrayDesign design = createArrayDesign(lsidObjectId, rows, columns, cdfFile);
                        // loading probe sets in reverse order to ensure that LogicalProbe DesignElementList loading
                        // in AffymetrixChpHandler sorts list correctly.
                        for (int i = reader.getCdfData().getHeader().getNumProbeSets() - 1; i >= 0; i--) {
                            LogicalProbe probeSet = new LogicalProbe();
                            probeSet.setName(reader.getCdfData().getProbeSetName(i));
                            design.getDesignDetails().getLogicalProbes().add(probeSet );
                        }
                        return design;
                    } catch (AffymetrixArrayDesignReadException e) {
                        throw new IllegalStateException("Unexpected read exception", e);
                    }
                }

                @SuppressWarnings("deprecation")
                private ArrayDesign createArrayDesign(String lsidObjectId, int rows, int columns, File designFile) {
                    ArrayDesign arrayDesign = new ArrayDesign();
                    if (designFile != null) {
                        arrayDesign.addDesignFile(fileAccessServiceStub.add(designFile));
                    }
                    ArrayDesignDetails details = new ArrayDesignDetails();
                    for (short row = 0; row < rows; row++) {
                        for (short column = 0; column < columns; column++) {
                            Feature feature = new Feature();
                            feature.setRow(row);
                            feature.setColumn(column);
                            details.getFeatures().add(feature);
                        }
                    }
                    arrayDesign.setNumberOfFeatures(details.getFeatures().size());
                    arrayDesign.setDesignDetails(details);
                    arrayDesignMap.put(lsidObjectId, arrayDesign);
                    return arrayDesign;
                }

                @Override
                public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.dataTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.dataTypeMap.get(descriptor);
                    }
                    ArrayDataType arrayDataType = new ArrayDataType();
                    arrayDataType.setName(descriptor.getName());
                    arrayDataType.setVersion(descriptor.getVersion());
                    addQuantitationTypes(arrayDataType, descriptor);
                    LocalDaoFactoryStub.this.dataTypeMap.put(descriptor, arrayDataType);
                    return arrayDataType;
                }

                private void addQuantitationTypes(ArrayDataType arrayDataType, ArrayDataTypeDescriptor descriptor) {
                    for (QuantitationTypeDescriptor quantitationTypeDescriptor : descriptor.getQuantitationTypes()) {
                        arrayDataType.getQuantitationTypes().add(getQuantitationType(quantitationTypeDescriptor));
                    }
                }

                @Override
                public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.quantitationTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.quantitationTypeMap.get(descriptor);
                    }
                    QuantitationType quantitationType = new QuantitationType();
                    quantitationType.setName(descriptor.getName());
                    quantitationType.setTypeClass(descriptor.getDataType().getTypeClass());
                    LocalDaoFactoryStub.this.quantitationTypeMap.put(descriptor, quantitationType);
                    return quantitationType;
                }

                @Override
                public AbstractArrayData getArrayData(Long fileId) {
                    return LocalDaoFactoryStub.this.fileDataMap.get(fileId);
                }

                @Override
                public void save(PersistentObject caArrayEntity) {
                    if (caArrayEntity instanceof AbstractArrayData) {
                        addData((AbstractArrayData) caArrayEntity);
                    }
                }

                @Override
                public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    DesignElementList list = new DesignElementList();
                    list.setLsidForEntity(lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                    return list;
                }

            };
        }

        void addData(AbstractArrayData arrayData) {
            this.fileDataMap.put(arrayData.getDataFile().getId(), arrayData);
        }
    }

}
