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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractCaarrayIntegrationTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.project.ServiceType;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the FileManagementService.
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class FileManagementServiceIntegrationTest extends AbstractCaarrayIntegrationTest {
    @SuppressWarnings("unused")
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
        DUMMY_PROJECT_1.setStatus(ProposalStatus.IN_PROGRESS);
    }

    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setAssayTypeEnum(AssayType.ACGH);
        DUMMY_EXPERIMENT_1.setServiceType(ServiceType.FULL);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private static void saveSupportingObjects() {
        TermSource caarraySource = new TermSource();
        caarraySource.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarraySource.setVersion(ExperimentOntology.CAARRAY.getVersion());

        VocabularyDao vocabularyDao = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
        if (CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(caarraySource,
                MatchMode.EXACT, false, new String[] { "url" }, Order.desc("version"))) == null) {
            HibernateUtil.getCurrentSession().save(caarraySource);
        }

        TermSource mgedOntology = new TermSource();
        mgedOntology.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mgedOntology.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());

        TermSource savedMgedOntology = CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(mgedOntology, MatchMode.EXACT, false,
                new String[] { "url" }, Order.desc("version")));
        if (savedMgedOntology == null) {
            HibernateUtil.getCurrentSession().save(mgedOntology);
            savedMgedOntology = mgedOntology;
        }

        if (vocabularyDao.getTerm(savedMgedOntology, VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME) == null) {
            Term unknownProtocolType = new Term();
            unknownProtocolType.setValue(VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
            unknownProtocolType.setSource(savedMgedOntology);
            HibernateUtil.getCurrentSession().save(unknownProtocolType);
        }

        HibernateUtil.getCurrentSession().save(DUMMY_PROVIDER);
        HibernateUtil.getCurrentSession().save(DUMMY_ORGANISM);
        HibernateUtil.getCurrentSession().save(DUMMY_TERM);
    }

    @Test
    public void testImportMageTabSpecificationAndUpdateCharacteristics() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        importArrayDesign(AffymetrixArrayDesignFiles.HG_FOCUS_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET.getAllFiles(),
                TestMageTabSets.MAGE_TAB_SPECIFICATION_SET);

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        importFiles(project, TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET.getAllFiles(),
                TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        importFiles(project, TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_INPUT_SET.getAllFiles(),
                TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
    public void testUpdateBioMaterialChain() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_BASELINE_INPUT_SET.getAllFiles(),
                TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_BASELINE_SET);

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
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
        importFiles(project, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_INPUT_SET.getAllFiles(),
                TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        assertEquals(4, project.getExperiment().getExtracts().size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();

        // now try to add a data files to existing hybs
        importFiles(project, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_INPUT_SET.getAllFiles(),
                TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        assertEquals(4, project.getExperiment().getExtracts().size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();
    }

    @SuppressWarnings("PMD")
    private void importFiles(Project project, Set<File> files, MageTabDocumentSet documentSet) throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        CaArrayFileSet fileSet = uploadFiles(project, files, documentSet);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, fileSet, null);
        tx.commit();
    }

    /**
     * "Upload" files to a project, returning the CaArrayFileSet containing those files.
     * @param project project to upload to
     * @param files File objects for the files to upload (should correspond to the files in the document set)
     * @param documentSet MageTabDocumentSet containing the files to upload (should correspond to the files in the file set)
     * @return
     */
    private CaArrayFileSet uploadFiles(Project project, Set<File> files, MageTabDocumentSet documentSet) {
        for (File file : files) {
            this.fileAccessService.add(file);
        }
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(documentSet);
        for (CaArrayFile file : fileSet.getFiles()) {
            file.setProject(project);
            project.getFiles().add(file);
            HibernateUtil.getCurrentSession().save(file);
        }
        HibernateUtil.getCurrentSession().update(project);
        return fileSet;
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
            job.getUnexpectedErrorPreparedStatement(HibernateUtil.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    private ArrayDesign importArrayDesign(File designFile) throws IllegalAccessException, InvalidDataFileException {
        ArrayDesign design = new ArrayDesign();
        design.setName(designFile.getName());
        design.setVersion("2.0");
        design.setProvider(DUMMY_PROVIDER);
        design.setLsidForEntity("authority:namespace:" + designFile.getName());
        design.setAssayTypeEnum(AssayType.GENE_EXPRESSION);
        design.addDesignFile(this.fileAccessService.add(designFile));
        design.setTechnologyType(DUMMY_TERM);
        design.setOrganism(DUMMY_ORGANISM);
        HibernateUtil.getCurrentSession().save(design);

        ArrayDesignServiceBean arrayDesignService = new ArrayDesignServiceBean();
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);

        return design;
    }

    private static FileManagementService createFileManagementService(final FileAccessServiceStub fileAccessServiceStub) {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerActualImplementations();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);

        FileManagementServiceBean bean = new FileManagementServiceBean();
        DirectJobSubmitter submitter = new DirectJobSubmitter(new FileManagementMDB());
        bean.setSubmitter(submitter);

        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();

        return bean;
    }

}