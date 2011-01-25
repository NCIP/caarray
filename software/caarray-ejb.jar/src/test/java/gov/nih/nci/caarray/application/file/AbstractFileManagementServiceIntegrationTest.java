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
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.mockito.Mockito;

import com.google.inject.Injector;

/**
 * Integration test for the FileManagementService.
 * 
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public abstract class AbstractFileManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private FileManagementService fileManagementService;
    private final FileAccessServiceStub fileAccessService = new FileAccessServiceStub();

    private Organism testOrganism;
    private Organization testOrganization;
    private Project testProject;
    private Experiment testExperiment;
    private TermSource testTermSource;
    private Category testCategory;
    private Term testTerm;
    
    private Organism getTestOrganism() {
        testOrganism = (Organism) hibernateHelper.getCurrentSession().load(Organism.class, testOrganism.getId());
        return testOrganism;
    }

    private Organization getTestOrganization() {
        testOrganization = (Organization) hibernateHelper.getCurrentSession().load(Organization.class, testOrganization.getId());
        return testOrganization;
    }

    protected Project getTestProject() {
        testProject = (Project) hibernateHelper.getCurrentSession().load(Project.class, testProject.getId());
        return testProject;
    }

    protected Experiment getTestExperiment() {
        testExperiment = (Experiment) hibernateHelper.getCurrentSession().load(Experiment.class, testExperiment.getId());
        return testExperiment;
    }

    private Term getTestTerm() {
        testTerm = (Term) hibernateHelper.getCurrentSession().load(Term.class, testTerm.getId());
        return testTerm;
    }

    protected FileManagementService getFileManagementService() {
        return fileManagementService;
    }

    private FileAccessServiceStub getFileAccessService() {
        return fileAccessService;
    }

    @Before
    public void setUp() {
        fileManagementService = createFileManagementService(getFileAccessService());
        
        Transaction tx = hibernateHelper.beginTransaction();
        ArrayDataService ads = ServiceLocatorFactory.getArrayDataService();
        ads.initialize();
        tx.commit();
        
        resetSupportingObjects();
        saveSupportingObjects();       
        reloadSupportingObjects();
    }
    
    private void resetSupportingObjects() {
        testOrganism = new Organism();
        testOrganization = new Organization();
        testProject = new Project();
        testExperiment = new Experiment();
        testTermSource = new TermSource();
        testCategory = new Category();
        testTerm = new Term();

        initializeSupportingObjects();
    }

    private void initializeSupportingObjects() {
        setExperimentSummary();
        testTermSource.setName("dummy source");
        testTermSource.setUrl("test url");
        testCategory.setName("Dummy Category");
        testCategory.setSource(testTermSource);
        testOrganism.setScientificName("Foo");
        testOrganism.setTermSource(testTermSource);
        testTerm.setValue("testval");
        testTerm.setCategory(testCategory);
        testTerm.setSource(testTermSource);

        testProject.setExperiment(testExperiment);
        testExperiment.setOrganism(testOrganism);
        testExperiment.setManufacturer(testOrganization);
        testProject.setLocked(false);
    }

    private void setExperimentSummary() {
        testExperiment.setTitle("DummyExperiment1");
        testExperiment.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        testExperiment.setDate(currDate);
        testExperiment.setPublicReleaseDate(currDate);
        testExperiment.setDesignDescription("Working on it");
    }

    private void saveSupportingObjects() {
        Transaction tx = hibernateHelper.beginTransaction();
        final Session currentSession = hibernateHelper.getCurrentSession();

        TermSource caarraySource = new TermSource();
        caarraySource.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarraySource.setVersion(ExperimentOntology.CAARRAY.getVersion());

        VocabularyDao vocabularyDao = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
        if (CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(ExampleSearchCriteria.forEntity(caarraySource)
                .includeNulls().excludeProperties("url"), Order.desc("version"))) == null) {
            currentSession.save(caarraySource);
        }

        TermSource mgedOntology = new TermSource();
        mgedOntology.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mgedOntology.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());

        TermSource savedMgedOntology = CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(mgedOntology).includeNulls().excludeProperties("url"), Order
                        .desc("version")));
        if (savedMgedOntology == null) {
            currentSession.save(mgedOntology);
            savedMgedOntology = mgedOntology;
        }

        if (vocabularyDao.getTerm(savedMgedOntology, VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME) == null) {
            Term unknownProtocolType = new Term();
            unknownProtocolType.setValue(VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
            unknownProtocolType.setSource(savedMgedOntology);
            currentSession.save(unknownProtocolType);
        }

        if (vocabularyDao.getTerm(savedMgedOntology, "mm") == null) {
            Term mm = new Term();
            mm.setValue("mm");
            mm.setSource(savedMgedOntology);
            currentSession.save(mm);
        }

        currentSession.save(testOrganization);
        currentSession.save(testOrganism);
        currentSession.save(testTerm);
        currentSession.save(testProject);
        
        tx.commit();
    }

    private void reloadSupportingObjects() {
        Transaction tx = hibernateHelper.beginTransaction();
        testOrganism = (Organism) hibernateHelper.getCurrentSession().load(Organism.class, getTestOrganism().getId());
        testOrganization = (Organization) hibernateHelper.getCurrentSession().load(Organization.class, testOrganization.getId());
        testProject = (Project) hibernateHelper.getCurrentSession().load(Project.class, testProject.getId());
        testExperiment = (Experiment) hibernateHelper.getCurrentSession().load(Experiment.class, testExperiment.getId());
        testTermSource = (TermSource) hibernateHelper.getCurrentSession().load(TermSource.class, testTermSource.getId());
        testCategory = (Category) hibernateHelper.getCurrentSession().load(Category.class, testCategory.getId());
        testTerm = (Term) hibernateHelper.getCurrentSession().load(Term.class, testTerm.getId());
        tx.commit();
    }

    protected void importDesignAndDataFilesIntoProject(File arrayDesignFile, FileType dataFilesType, File... dataFiles) throws Exception {
//        Transaction tx = hibernateHelper.beginTransaction();
        ArrayDesign design = importArrayDesign(arrayDesignFile);
//        tx.commit();

        importDataFilesIntoProject(design, dataFilesType, dataFiles);
    }

    protected void importDataFilesIntoProject(ArrayDesign design, FileType dataFilesType, File... dataFiles) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        testExperiment = (Experiment) hibernateHelper.getCurrentSession().load(Experiment.class, testExperiment.getId());
        testProject = (Project) hibernateHelper.getCurrentSession().load(Project.class, testProject.getId());
        design = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        testExperiment.getArrayDesigns().add(design);
        hibernateHelper.getCurrentSession().save(testProject);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        Project project = (Project) hibernateHelper.getCurrentSession().load(Project.class, getTestProject().getId());
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
    
    protected void importFiles(Project project, MageTabFileSet fileSet, boolean dataMatricesAreCopyNumber) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        CaArrayFileSet caarrayFileSet = uploadFiles(project, fileSet, dataMatricesAreCopyNumber);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        project = (Project) hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, caarrayFileSet, null);
        hibernateHelper.getCurrentSession().getTransaction().commit();
    }

    protected void uploadAndValidateFiles(Map<File, FileType> files) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        Project project = getTestProject();
        CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();

        helpValidateFiles(project, fileSet);
    }

    private void helpValidateFiles(Project project, CaArrayFileSet fileSet) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
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
    protected CaArrayFileSet uploadFiles(Project project, MageTabFileSet fileSet) {
        return uploadFiles(project, fileSet, false);
    }
    
    protected CaArrayFileSet uploadFiles(Project project, MageTabFileSet fileSet, boolean dataMatricesAreCopyNumber) {
        for (FileRef file : fileSet.getAllFiles()) {
            this.getFileAccessService().add(file.getAsFile());
        }
        CaArrayFileSet caarrayFileSet = TestMageTabSets.getFileSet(dataMatricesAreCopyNumber, fileSet);
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

    protected CaArrayFileSet uploadFiles(Project project, Map<File, FileType> files) {
        for (File file : files.keySet()) {
            CaArrayFile caArrayFile = this.getFileAccessService().add(file);
            caArrayFile.setProject(project);
            caArrayFile.setFileType(files.get(file));
            project.getFiles().add(caArrayFile);
            hibernateHelper.getCurrentSession().save(caArrayFile);
        }

        hibernateHelper.getCurrentSession().update(project);
        return project.getFileSet();
    }



    protected Source findSource(Project project, String name) {
        return CaArrayDaoFactory.INSTANCE.getProjectDao().getSourceForExperiment(project.getExperiment(), name);
    }

    protected void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions) throws Exception {
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

    protected ArrayDesign importArrayDesign(File designFile) throws IllegalAccessException, InvalidDataFileException {
        return importArrayDesign(designFile, null);
    }

    protected ArrayDesign importArrayDesign(File designFile, FileType fileType) throws IllegalAccessException, InvalidDataFileException {
        return importArrayDesign(designFile, fileType, "authority:namespace:");
    }

    protected ArrayDesign importArrayDesign(File designFile, FileType fileType, String namespace) throws IllegalAccessException, InvalidDataFileException {
        Transaction tx = hibernateHelper.beginTransaction();
        try {
            return doImportDesign(designFile, fileType, namespace);
        } finally {
            tx.commit();
        }
    }

    private ArrayDesign doImportDesign(File designFile, FileType fileType, String namespace) {
        ArrayDesign design = new ArrayDesign();
        design.setName(designFile.getName());
        design.setVersion("2.0");
        design.setGeoAccession("GPL0000");
        design.setProvider(getTestOrganization());
        design.setLsidForEntity(namespace + designFile.getName());
        CaArrayFile caArrayDesignFile = this.getFileAccessService().add(designFile);
        if (fileType != null) {
            caArrayDesignFile.setFileType(fileType);
        }
        design.addDesignFile(caArrayDesignFile);
        design.setTechnologyType(getTestTerm());
        design.setOrganism(getTestOrganism());
        hibernateHelper.getCurrentSession().save(design);

        ArrayDesignService arrayDesignService = ServiceLocatorFactory.getArrayDesignService();
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        design = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
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

    protected void importFiles(MageTabFileSet fileSet) throws Exception {
        importFiles(testProject, fileSet, false);
    }

    protected void importFiles(MageTabFileSet fileSet, boolean dataMatricesAreCopyNumber) throws Exception {
        importFiles(testProject, fileSet, dataMatricesAreCopyNumber);
    }

    protected void addDesignToExperiment(ArrayDesign design) {
        Transaction tx = hibernateHelper.beginTransaction();
        ArrayDesign sessionArrayDesign = (ArrayDesign) hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        getTestExperiment().getArrayDesigns().add(sessionArrayDesign);
        hibernateHelper.getCurrentSession().save(getTestProject());
        tx.commit();
    }

    protected CaArrayFileSet uploadFiles(Map<File, FileType> files) {
        Transaction tx = hibernateHelper.beginTransaction();
        Project project = getTestProject();
        CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();
        return fileSet;
    }

    protected CaArrayFileSet uploadFiles(MageTabFileSet mageTabFileSet) {
        Transaction tx = hibernateHelper.beginTransaction();
        Project project = getTestProject();
        CaArrayFileSet fileSet = uploadFiles(project, mageTabFileSet);
        tx.commit();
        return fileSet;
    }

    protected void importFiles(CaArrayFileSet fileSet) throws Exception {
        importFiles(fileSet, DataImportOptions.getAutoCreatePerFileOptions());
    }

    protected void importFiles(CaArrayFileSet fileSet, DataImportOptions dataImportOptions) throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        importFiles(getTestProject(), fileSet, dataImportOptions);
        tx.commit();
    }
}
