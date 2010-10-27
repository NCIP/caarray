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

import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.B532_MEAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.B532_MEDIAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.B532_SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.B635_MEAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.B635_MEDIAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.B635_SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.B_PIXELS;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.DIA;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F532_MEAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F532_MEAN_B532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F532_MEDIAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F532_MEDIAN_B532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F532_PERCENT_SAT;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F532_SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F532_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F635_MEAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F635_MEAN_B635;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F635_MEDIAN;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F635_MEDIAN_B635;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F635_PERCENT_SAT;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F635_SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F635_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.FLAGS;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.F_PIXELS;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.LOG_RATIO_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.MEAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.NORMALIZE;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.PERCENT_GT_B532_1SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.PERCENT_GT_B532_2SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.PERCENT_GT_B635_1SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.PERCENT_GT_B635_2SD;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.RATIOS_SD_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.RATIO_OF_MEANS_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.RATIO_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.RGN_R2_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.RGN_RATIO_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.SNR_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.SNR_635;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.SUM_OF_MEANS_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.SUM_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.X;
import static gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType.Y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.platforms.genepix.GenepixQuantitationType;
import gov.nih.nci.caarray.platforms.illumina.IlluminaGenotypingProcessedMatrixHandlerTest;
import gov.nih.nci.caarray.platforms.illumina.IlluminaGenotypingProcessedMatrixQuantitationType;
import gov.nih.nci.caarray.platforms.illumina.SampleProbeProfileQuantitationType;
import gov.nih.nci.caarray.platforms.nimblegen.NimblegenQuantitationType;
import gov.nih.nci.caarray.test.data.arraydata.AgilentArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.NimblegenArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.UserTransaction;

import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Injector;

/**
 * Integration test for the FileManagementService.
 * 
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class FileManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private FileManagementService fileManagementService;
    private final FileAccessServiceStub fileAccessService = new FileAccessServiceStub();

    private static Organism DUMMY_ORGANISM = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();

    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();

    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();
    private static Term DUMMY_TERM = new Term();

    @Before
    public void setUp() {
        this.fileManagementService = createFileManagementService(fileAccessService);
        
        Transaction tx = hibernateHelper.beginTransaction();
        ArrayDataService ads = ServiceLocatorFactory.getArrayDataService();
        ads.initialize();
        tx.commit();
        
        resetData();
    }
    
    private static void resetData() {
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();

        DUMMY_EXPERIMENT_1 = new Experiment();

        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();
        DUMMY_TERM = new Term();

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    private static void initializeProjects() {
        setExperimentSummary();
        DUMMY_TERM_SOURCE.setName("dummy source");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);
        DUMMY_TERM.setValue("testval");
        DUMMY_TERM.setCategory(DUMMY_CATEGORY);
        DUMMY_TERM.setSource(DUMMY_TERM_SOURCE);

        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
        DUMMY_PROJECT_1.setLocked(false);
    }

    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private void saveSupportingObjects() {
        TermSource caarraySource = new TermSource();
        caarraySource.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarraySource.setVersion(ExperimentOntology.CAARRAY.getVersion());

        VocabularyDao vocabularyDao = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
        if (CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(ExampleSearchCriteria.forEntity(caarraySource)
                .includeNulls().excludeProperties("url"), Order.desc("version"))) == null) {
            hibernateHelper.getCurrentSession().save(caarraySource);
        }

        TermSource mgedOntology = new TermSource();
        mgedOntology.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mgedOntology.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());

        TermSource savedMgedOntology = CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(mgedOntology).includeNulls().excludeProperties("url"), Order
                        .desc("version")));
        if (savedMgedOntology == null) {
            hibernateHelper.getCurrentSession().save(mgedOntology);
            savedMgedOntology = mgedOntology;
        }

        if (vocabularyDao.getTerm(savedMgedOntology, VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME) == null) {
            Term unknownProtocolType = new Term();
            unknownProtocolType.setValue(VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
            unknownProtocolType.setSource(savedMgedOntology);
            hibernateHelper.getCurrentSession().save(unknownProtocolType);
        }

        if (vocabularyDao.getTerm(savedMgedOntology, "mm") == null) {
            Term mm = new Term();
            mm.setValue("mm");
            mm.setSource(savedMgedOntology);
            hibernateHelper.getCurrentSession().save(mm);
        }

        hibernateHelper.getCurrentSession().save(DUMMY_PROVIDER);
        hibernateHelper.getCurrentSession().save(DUMMY_ORGANISM);
        hibernateHelper.getCurrentSession().save(DUMMY_TERM);
    }

    @Test
    public void testReimportProjectFiles() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AgilentArrayDesignFiles.TEST_MIRNA_1_XML_SMALL, FileType.AGILENT_XML);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        Map<File, FileType> files = new HashMap<File, FileType>();
        files.put(AgilentArrayDataFiles.MIRNA, FileType.ILLUMINA_RAW_TXT);

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        importFiles(project, fileSet, DataImportOptions.getAutoCreatePerFileOptions());
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        assertEquals(1, project.getExperiment().getHybridizations().size());
        assertEquals(1, project.getExperiment().getHybridizations().iterator().next().getRawDataCollection().size());

        assertEquals(1, project.getImportedFiles().size());
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, project.getImportedFiles().iterator().next().getFileStatus());
        CaArrayFile f  = project.getImportedFiles().iterator().next();
        f.setFileType(FileType.AGILENT_RAW_TXT);
        hibernateHelper.getCurrentSession().save(f);

        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        fileSet = new CaArrayFileSet();
        fileSet.addAll(project.getImportedFiles());
        this.fileManagementService.reimportAndParseProjectFiles(project, fileSet);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        assertEquals(1, project.getImportedFiles().size());
        assertEquals(1, project.getExperiment().getHybridizations().size());
        assertEquals(1, project.getExperiment().getHybridizations().iterator().next().getRawDataCollection().size());
        assertEquals("Agilent Raw Text", project.getExperiment().getHybridizations().iterator().next()
                .getRawDataCollection().iterator().next().getType().getName());
        CaArrayFile imported = project.getImportedFiles().iterator().next();
        assertEquals(FileStatus.IMPORTED, imported.getFileStatus());
        tx.commit();
    }


    @Test
    public void testImportMageTabSpecificationAndUpdateCharacteristics() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        importArrayDesign(AffymetrixArrayDesignFiles.HG_FOCUS_CDF);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray().getDesign().getName());
        tx.commit();

        // now try to update annotations of existing biomaterials
        importFiles(project, TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray().getDesign().getName());
        tx.commit();

        // now try to add a new biomaterial while update existing biomaterials
        importFiles(project, TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        assertEquals("HG-Focus", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray().getDesign().getName());
        tx.commit();
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();        
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef2() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        CaArrayFileSet fileSet = uploadFiles(project, TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        for (CaArrayFile file : fileSet.getFilesByType(FileType.AFFYMETRIX_CEL)) {
            file.setFileType(FileType.AFFYMETRIX_DAT);
        }
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, fileSet, null);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }
    
    @Test
    public void testImportNonMageTabWithoutArrayDesign() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        MageTabFileSet inputFiles = new MageTabFileSet();
        for (FileRef f : TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET.getAllFiles()) {
            if (f.getName().endsWith("CEL")) {
                inputFiles.addNativeData(f);
            }
        }
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET); 
        docSet.getIdfDocuments().clear();
        docSet.getSdrfDocuments().clear();

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        CaArrayFileSet fileSet = uploadFiles(project, inputFiles);
        for (CaArrayFile file : fileSet.getFilesByType(FileType.AFFYMETRIX_CEL)) {
            file.setFileType(FileType.AFFYMETRIX_DAT);
        }
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, fileSet, DataImportOptions.getAutoCreatePerFileOptions());
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        assertEquals(3, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testValidateDefect18625Hybes() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();
        Map<File, FileType> files = new HashMap<File, FileType>();
        files.put(IlluminaArrayDataFiles.DEFECT_18652_IDF, FileType.MAGE_TAB_IDF);
        files.put(IlluminaArrayDataFiles.DEFECT_18652_SDRF, FileType.MAGE_TAB_SDRF);
        files.put(IlluminaArrayDataFiles.HUMAN_WG6_SMALL, FileType.ILLUMINA_DATA_CSV);

        uploadAndValidateFiles(DUMMY_PROJECT_1, files);

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        for (CaArrayFile file : project.getFiles()) {
            if (!file.getFileType().equals(FileType.MAGE_TAB_SDRF)) {
                assertEquals(FileStatus.VALIDATED, file.getFileStatus());
            } else {
                assertEquals(FileStatus.VALIDATION_ERRORS, file.getFileStatus());
                assertEquals(1, file.getValidationResult().getMessages().size());
                assertTrue(file.getValidationResult()
                        .getMessages().get(0).getMessage().contains("WRONG"));
            }
        }
        tx.commit();

    }

    @Test
    public void testUpdateBioMaterialChain() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_BASELINE_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        importFiles(project, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        importFiles(project, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.UPDATE_FILES_BASELINE_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        RawArrayData raw = project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().iterator().next();
        DerivedArrayData derived = project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().iterator().next();
        assertEquals(1, derived.getDerivedFromArrayDataCollection().size());
        assertEquals(raw, derived.getDerivedFromArrayDataCollection().iterator().next());
        tx.commit();

        // now try to add data files to existing hybs, which also reference existing data files
        importFiles(project, TestMageTabSets.UPDATE_FILES_NEW_INPUT_SET);

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        for (RawArrayData oneRaw : project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection()) {
            assertTrue(Arrays.asList("BM1.CEL", "BM1a.CEL").contains(oneRaw.getDataFile().getName()));
            assertTrue("BM1.EXP is not derived from " + oneRaw.getName(), derived.getDerivedFromArrayDataCollection()
                    .contains(oneRaw));
        }

        assertEquals(3, project.getExperiment().getHybridizationByName("Hyb 2").getAllDataFiles().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().size());
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 2").getDerivedDataCollection().size());
        raw = project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().iterator().next();
        assertEquals("BM2.CEL", raw.getDataFile().getName());
        for (DerivedArrayData oneDerived : project.getExperiment().getHybridizationByName("Hyb 2")
                .getDerivedDataCollection()) {
            assertTrue(Arrays.asList("BM2.EXP", "BM2a.EXP").contains(oneDerived.getDataFile().getName()));
            assertEquals(1, oneDerived.getDerivedFromArrayDataCollection().size());
            assertEquals(raw, oneDerived.getDerivedFromArrayDataCollection().iterator().next());
        }

        tx.commit();
    }

    @Test
    public void testReimport() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML, FileType.AGILENT_CSV);
        assertNull(design.getDesignDetails());
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        design.getFirstDesignFile().setFileType(FileType.AGILENT_XML);
        hibernateHelper.getCurrentSession().saveOrUpdate(design);
        tx.commit();
        
        tx = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().evict(design);        
        this.fileManagementService.reimportAndParseArrayDesign(design.getId());        
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        design = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        assertNotNull(design.getDesignDetails());
        assertEquals(45220, design.getNumberOfFeatures().intValue());
        assertEquals(45220, design.getDesignDetails().getFeatures().size());
        tx.commit();
    }

    @Test
    public void testReimportWithReferencingExperiment() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML, FileType.AGILENT_CSV);
        assertNull(design.getDesignDetails());
        tx.commit();
        
        tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        design.getFirstDesignFile().setFileType(FileType.AGILENT_XML);
        hibernateHelper.getCurrentSession().saveOrUpdate(design);
        tx.commit();
        
        tx = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().evict(design);        
        this.fileManagementService.reimportAndParseArrayDesign(design.getId());        
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        design = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        assertNotNull(design.getDesignDetails());
        assertEquals(45220, design.getNumberOfFeatures().intValue());
        assertEquals(45220, design.getDesignDetails().getFeatures().size());
        tx.commit();
    }
    
    private void importDesignAndDataFilesIntoProject(File arrayDesignFile, FileType dataFilesType, File... dataFiles) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(arrayDesignFile);
        tx.commit();

        importDataFilesIntoProject(design, dataFilesType, dataFiles);
    }

    private void importDataFilesIntoProject(ArrayDesign design, FileType dataFilesType, File... dataFiles) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        CaArrayFileSet fileSet = uploadFiles(project, dataFiles);
        for (CaArrayFile file : fileSet.getFiles()) {
            if (!file.getName().endsWith(".sdrf") && !file.getName().endsWith(".idf")) {
                file.setFileType(dataFilesType);                
            }
        }
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, fileSet, DataImportOptions.getAutoCreatePerFileOptions());
        tx.commit();        
    }
    
    @Test
    public void testIlluminaCsvDataImport() throws Exception {
        final int numberOfProbes = 10;
        importDesignAndDataFilesIntoProject(IlluminaArrayDesignFiles.HUMAN_WG6_CSV,
                FileType.ILLUMINA_DATA_CSV, IlluminaArrayDataFiles.HUMAN_WG6_SMALL);
        Transaction tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        List<AbstractDesignElement> l = hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
        assertEquals(numberOfProbes, l.size());
        for (AbstractDesignElement de : l) {
            PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        List<HybridizationData> hdl = hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(19, hdl.size());
        for (HybridizationData hd : hdl){
            assertEquals(4, hd.getColumns().size());
            List<String> intColumns = new ArrayList<String>(Arrays.asList(
                    SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
            List<String> floatColumns = new ArrayList<String>(Arrays.asList(
                    SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                    SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                    SampleProbeProfileQuantitationType.DETECTION.getName()));
            for (AbstractDataColumn c : hd.getColumns()) {
                String name = c.getQuantitationType().getName();
                if (intColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(), intColumns.remove(c.getQuantitationType().getName()));                                        
                } else if (floatColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(), floatColumns.remove(c.getQuantitationType().getName()));                    
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
                FileType.ILLUMINA_SAMPLE_PROBE_PROFILE_TXT, IlluminaArrayDataFiles.SAMPLE_PROBE_PROFILE);
        Transaction tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        List<AbstractDesignElement> l = hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
        assertEquals(3, l.size());
        for (AbstractDesignElement de : l) {
            PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        List<HybridizationData> hdl = hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(16, hdl.size());
        for (HybridizationData hd : hdl){
            assertEquals(8, hd.getColumns().size());
            List<String> intColumns = new ArrayList<String>(Arrays.asList(
                    SampleProbeProfileQuantitationType.NARRAYS.getName(),
                    SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
            List<String> floatColumns = new ArrayList<String>(Arrays.asList(
                    SampleProbeProfileQuantitationType.MIN_SIGNAL.getName(),
                    SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                    SampleProbeProfileQuantitationType.MAX_SIGNAL.getName(),
                    SampleProbeProfileQuantitationType.ARRAY_STDEV.getName(),
                    SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                    SampleProbeProfileQuantitationType.DETECTION.getName()));
            for (AbstractDataColumn c : hd.getColumns()) {
                String name = c.getQuantitationType().getName();
                if (intColumns.contains(name)) {
                    assertEquals(3, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(), intColumns.remove(c.getQuantitationType().getName()));                                        
                } else if (floatColumns.contains(name)) {
                    assertEquals(3, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(), floatColumns.remove(c.getQuantitationType().getName()));                    
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", intColumns.isEmpty() && floatColumns.isEmpty());
        }
        tx.commit();        
     }

    @Test
    public void testIlluminaGenotypingProcessedMatrixImport() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = IlluminaGenotypingProcessedMatrixHandlerTest.buildArrayDesign();
        hibernateHelper.getCurrentSession().save(design);        
        tx.commit();
        
        importDataFilesIntoProject(design, FileType.ILLUMINA_GENOTYPING_PROCESSED_MATRIX_TXT,
                IlluminaArrayDataFiles.ILLUMINA_DERIVED_1_HYB);
        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        design = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();        
        List<AbstractDesignElement> l = hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
        assertEquals(3, l.size());
        for (AbstractDesignElement de : l) {
            PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        List<HybridizationData> hdl = hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());
        assertEquals(6, hdl.get(0).getColumns().size());
        List<String> stringColumns = new ArrayList<String>(
                Arrays.asList(IlluminaGenotypingProcessedMatrixQuantitationType.ALLELE.getName()));
        List<String> floatColumns = new ArrayList<String>(Arrays.asList(
                IlluminaGenotypingProcessedMatrixQuantitationType.B_ALLELE_FREQ.getName(),
                IlluminaGenotypingProcessedMatrixQuantitationType.GC_SCORE.getName(),
                IlluminaGenotypingProcessedMatrixQuantitationType.LOG_R_RATIO.getName(),
                IlluminaGenotypingProcessedMatrixQuantitationType.R.getName(),
                IlluminaGenotypingProcessedMatrixQuantitationType.THETA.getName()));
        for (AbstractDataColumn c : hdl.get(0).getColumns()) {
            String name = c.getQuantitationType().getName();
            if (stringColumns.contains(name)) {
                assertEquals(3, ((StringColumn) c).getValues().length);
                assertTrue("missing " + c.getQuantitationType(),
                        stringColumns.remove(c.getQuantitationType().getName()));
            } else if (floatColumns.contains(name)) {
                assertEquals(3, ((FloatColumn) c).getValues().length);
                assertTrue("missing " + c.getQuantitationType(), floatColumns.remove(c.getQuantitationType().getName()));
            } else {
                fail("unexpected column: " + name);
            }
        }
        assertTrue("not all columns present", stringColumns.isEmpty() && floatColumns.isEmpty());
        tx.commit();                
     }

    @Test
    public void testGenepixGprImport() throws Exception {
        importDesignAndDataFilesIntoProject(GenepixArrayDataFiles.JOE_DERISI_FIX, FileType.GENEPIX_GPR,
                GenepixArrayDataFiles.SMALL_IDF, GenepixArrayDataFiles.SMALL_SDRF, GenepixArrayDataFiles.GPR_4_1_1);
        Transaction tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        
        List<AbstractDesignElement> l = hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
        assertEquals(6528, l.size());
        for (AbstractDesignElement de : l) {
            PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        List<HybridizationData> hdl = hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());
        
        GenepixQuantitationType[] expectedTypes = new GenepixQuantitationType[] {
                X, Y, DIA, F635_MEDIAN, F635_MEAN,
                F635_SD, B635_MEDIAN, B635_MEAN, B635_SD, PERCENT_GT_B635_1SD, PERCENT_GT_B635_2SD, F635_PERCENT_SAT,
                F532_MEDIAN, F532_MEAN, F532_SD, B532_MEDIAN, B532_MEAN, B532_SD, PERCENT_GT_B532_1SD,
                PERCENT_GT_B532_2SD, F532_PERCENT_SAT, RATIO_OF_MEDIANS_635_532, RATIO_OF_MEANS_635_532,
                MEDIAN_OF_RATIOS_635_532, MEAN_OF_RATIOS_635_532, RATIOS_SD_635_532, RGN_RATIO_635_532, RGN_R2_635_532,
                F_PIXELS, B_PIXELS, SUM_OF_MEDIANS_635_532, SUM_OF_MEANS_635_532, LOG_RATIO_635_532, F635_MEDIAN_B635,
                F532_MEDIAN_B532, F635_MEAN_B635, F532_MEAN_B532, F635_TOTAL_INTENSITY, F532_TOTAL_INTENSITY, SNR_635,
                SNR_532, FLAGS, NORMALIZE };

        for (HybridizationData hd : hdl){
            assertEquals(expectedTypes.length, hd.getColumns().size());
            for (int i = 0; i < expectedTypes.length; i++) {
                assertEquals(expectedTypes[i].getName(), hd.getColumns().get(i).getQuantitationType().getName());
            }
        }
        tx.commit();                
     }

    @Test
    public void testNimblegenPairImport() throws Exception {
        importDesignAndDataFilesIntoProject(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN, FileType.NIMBLEGEN_NORMALIZED_PAIR,
                NimblegenArrayDataFiles.SHORT_HUMAN_EXPRESSION);
        Transaction tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        
        List<AbstractDesignElement> l = hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
        assertEquals(3, l.size());
        for (AbstractDesignElement de : l) {
            PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        List<HybridizationData> hdl = hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());
        for (HybridizationData hd : hdl){
            assertEquals(5, hd.getColumns().size());
            List<String> intColumns = new ArrayList<String>(Arrays.asList(
                    NimblegenQuantitationType.MATCH_INDEX.getName(),
                    NimblegenQuantitationType.X.getName(),
                    NimblegenQuantitationType.Y.getName()));
            List<String> floatColumns = new ArrayList<String>(Arrays.asList(
                    NimblegenQuantitationType.MM.getName(),
                    NimblegenQuantitationType.PM.getName()));
            for (AbstractDataColumn c : hd.getColumns()) {
                String name = c.getQuantitationType().getName();
                if (intColumns.contains(name)) {
                    assertEquals(3, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(), intColumns.remove(c.getQuantitationType().getName()));                                        
                } else if (floatColumns.contains(name)) {
                    assertEquals(3, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(), floatColumns.remove(c.getQuantitationType().getName()));                    
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", intColumns.isEmpty() && floatColumns.isEmpty());
        }
        tx.commit();                
     }

    @SuppressWarnings("PMD")
    private void importFiles(Project project, MageTabFileSet fileSet) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        CaArrayFileSet caarrayFileSet = uploadFiles(project, fileSet);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, caarrayFileSet, null);
        hibernateHelper.getCurrentSession().getTransaction().commit();
    }

    @SuppressWarnings("PMD")
    private void uploadAndValidateFiles(Project project, Map<File, FileType> files) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();

        helpValidateFiles(tx, project, fileSet);
    }

    @SuppressWarnings("PMD")
    private void helpValidateFiles(Transaction tx, Project project, CaArrayFileSet fileSet) throws Exception {
        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        validateFiles(project, fileSet);
        tx.commit();
    }

    /**
     * "Upload" files to a project, returning the CaArrayFileSet containing those files.
     * @param project project to upload to
     * @param fileSet MageTabFileSet containing the files to upload (should correspond to the files in the file set)
     * @return
     */
    private CaArrayFileSet uploadFiles(Project project, MageTabFileSet fileSet) {
        for (FileRef file : fileSet.getAllFiles()) {
            this.fileAccessService.add(file.getAsFile());
        }
        CaArrayFileSet caarrayFileSet = TestMageTabSets.getFileSet(fileSet);
        for (CaArrayFile file : caarrayFileSet.getFiles()) {
            file.setProject(project);
            project.getFiles().add(file);
            hibernateHelper.getCurrentSession().save(file);
        }
        hibernateHelper.getCurrentSession().update(project);
        return caarrayFileSet;
    }
    
    private CaArrayFileSet uploadFiles(Project project, File... files) {
        MageTabFileSet inputFiles = new MageTabFileSet();
        for (File f : files) {
            inputFiles.addNativeData(new JavaIOFileRef(f));            
        }
        return uploadFiles(project, inputFiles);
    }

    private CaArrayFileSet uploadFiles(Project project, Map<File, FileType> files) {
        for (File file : files.keySet()) {
            CaArrayFile caArrayFile = this.fileAccessService.add(file);
            caArrayFile.setProject(project);
            caArrayFile.setFileType(files.get(file));
            project.getFiles().add(caArrayFile);
            hibernateHelper.getCurrentSession().save(caArrayFile);
        }

        hibernateHelper.getCurrentSession().update(project);
        return project.getFileSet();
    }



    private Source findSource(Project project, String name) {
        return CaArrayDaoFactory.INSTANCE.getProjectDao().getSourceForExperiment(project.getExperiment(), name);
    }

    private void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions) throws Exception {
        ProjectFilesImportJob job = new ProjectFilesImportJob(UsernameHolder.getUser(), targetProject, fileSet,
                dataImportOptions);
        job.setDaoFactory(CaArrayDaoFactory.INSTANCE);
        try {
            job.execute();
        } catch (Exception e) {
            job.getUnexpectedErrorPreparedStatement(hibernateHelper.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    private void validateFiles(Project targetProject, CaArrayFileSet fileSet) throws Exception {
        ProjectFilesValidationJob job = new ProjectFilesValidationJob(UsernameHolder.getUser(), targetProject, fileSet);
        job.setDaoFactory(CaArrayDaoFactory.INSTANCE);
        try {
            job.execute();
        } catch (Exception e) {
            job.getUnexpectedErrorPreparedStatement(hibernateHelper.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    private ArrayDesign importArrayDesign(File designFile) throws IllegalAccessException, InvalidDataFileException {
        return importArrayDesign(designFile, null);
    }

    private ArrayDesign importArrayDesign(File designFile, FileType fileType) throws IllegalAccessException, InvalidDataFileException {
        ArrayDesign design = new ArrayDesign();
        design.setName(designFile.getName());
        design.setVersion("2.0");
        design.setGeoAccession("GPL0000");
        design.setProvider(DUMMY_PROVIDER);
        design.setLsidForEntity("authority:namespace:" + designFile.getName());
        CaArrayFile caArrayDesignFile = this.fileAccessService.add(designFile);
        if (fileType != null) {
            caArrayDesignFile.setFileType(fileType);
        }
        design.addDesignFile(caArrayDesignFile);
        design.setTechnologyType(DUMMY_TERM);
        design.setOrganism(DUMMY_ORGANISM);
        hibernateHelper.getCurrentSession().save(design);

        ArrayDesignService arrayDesignService = ServiceLocatorFactory.getArrayDesignService();
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        design = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        resetData();
        return design;
    }
    
    @Override
    protected Injector createInjector() {
        return InjectorFactory.getInjector();       
    }

    private FileManagementService createFileManagementService(final FileAccessServiceStub fileAccessServiceStub) {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerActualImplementations();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);

        FileManagementServiceBean bean = (FileManagementServiceBean) injector.getInstance(FileManagementService.class);
        FileManagementMDB mdb = new FileManagementMDB();
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        mdb.setTransaction(ut);
        DirectJobSubmitter submitter = new DirectJobSubmitter(mdb);
        bean.setSubmitter(submitter);

        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));

        return bean;
    }



}
