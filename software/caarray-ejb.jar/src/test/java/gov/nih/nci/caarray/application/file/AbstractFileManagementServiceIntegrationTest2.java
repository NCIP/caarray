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
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceBean;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorBean;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceBean;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
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
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.transaction.UserTransaction;

import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.multibindings.MapBinder;

/**
 * Integration test for the FileManagementService.
 * 
 * @author Steve Lustbader
 */

//TODO: need to consolidate AbstractFileManagementServiceIntegrationTest.java and AbstractFileManagementServiceIntegrationTest2.java? 
//These two files actually share a common ancestor (altho this history is not apparent in a svn revision graph), and still share many 
//commonalities. The common ancestor is /trunk/.../FileManagementServiceIntegrationTest.java@11062. This rev branched off 
//as /branches/gaxzero-jboss-5.1.0.GA@11110, and evolved into /branches/dkokotov-storage-osgi-consolidation, where it was renamed as 
//AbstractFileManagementServiceIntegrationTest.java in rev 11485. Meanwhile on trunk, FileManagementServiceIntegrationTest.java was 
//deleted and refactored into a new AbstractFileManagementServiceIntegrationTest.java and a couple of subclasses. To consolidate 
//AbstractFileManagementServiceIntegrationTest and AbstractFileManagementServiceIntegrationTest2, we suggest doing a 3 way diff-merge 
//using /trunk/.../FileManagementServiceIntegrationTest.java@11062 as the common ancestor. Also, note that consolidating 
//AbstractFileManagementServiceIntegrationTest and AbstractFileManagementServiceIntegrationTest2 will affect their respective 
//subclasses (which are FileImportIntegrationTest and FileValidationIntegrationTest vs. AffymetrixFileManagementServiceIntegrationTest, 
//AgilentFileManagementServiceIntegrationTest, IlluminaFileManagementServiceIntegrationTest respectively). 

@SuppressWarnings("PMD")
public abstract class AbstractFileManagementServiceIntegrationTest2 extends AbstractServiceIntegrationTest {
    protected FileManagementService fileManagementService;
    protected FileAccessServiceStub fileAccessService;

    protected static Organism DUMMY_ORGANISM = new Organism();
    protected static Organization DUMMY_PROVIDER = new Organization();
    protected static Project DUMMY_PROJECT_1 = new Project();

    protected static Experiment DUMMY_EXPERIMENT_1 = new Experiment();

    protected static TermSource DUMMY_TERM_SOURCE = new TermSource();
    protected static Category DUMMY_CATEGORY = new Category();
    protected static Term DUMMY_TERM = new Term();
    protected static AssayType DUMMY_ASSAY_TYPE = new AssayType("Gene Expression");

    @Before
    public void setUp() {
        this.fileManagementService = createFileManagementService();

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final ArrayDataService ads = ServiceLocatorFactory.getArrayDataService();
        ads.initialize();
        tx.commit();

        resetData();
    }

    protected static void resetData() {
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();

        DUMMY_EXPERIMENT_1 = new Experiment();

        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();
        DUMMY_TERM = new Term();
        DUMMY_ASSAY_TYPE = new AssayType("Gene Expression");

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    private static void initializeProjects() {
        setExperimentSummary();
        DUMMY_TERM_SOURCE.setName("dummy source " + new Random().nextInt());
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
        final Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    protected void saveSupportingObjects() {
        final TermSource caarraySource = new TermSource();
        caarraySource.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarraySource.setVersion(ExperimentOntology.CAARRAY.getVersion());

        final VocabularyDao vocabularyDao = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
        if (CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(ExampleSearchCriteria.forEntity(caarraySource)
                .includeNulls().excludeProperties("url"), Order.desc("version"))) == null) {
            this.hibernateHelper.getCurrentSession().save(caarraySource);
        }

        final TermSource mgedOntology = new TermSource();
        mgedOntology.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mgedOntology.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());

        TermSource savedMgedOntology = CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(mgedOntology).includeNulls().excludeProperties("url"),
                Order.desc("version")));
        if (savedMgedOntology == null) {
            this.hibernateHelper.getCurrentSession().save(mgedOntology);
            savedMgedOntology = mgedOntology;
        }

        if (vocabularyDao.getTerm(savedMgedOntology, VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME) == null) {
            final Term unknownProtocolType = new Term();
            unknownProtocolType.setValue(VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
            unknownProtocolType.setSource(savedMgedOntology);
            this.hibernateHelper.getCurrentSession().save(unknownProtocolType);
        }

        if (vocabularyDao.getTerm(savedMgedOntology, "mm") == null) {
            final Term mm = new Term();
            mm.setValue("mm");
            mm.setSource(savedMgedOntology);
            this.hibernateHelper.getCurrentSession().save(mm);
        }

        this.hibernateHelper.getCurrentSession().save(DUMMY_PROVIDER);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ORGANISM);
        this.hibernateHelper.getCurrentSession().save(DUMMY_TERM);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAY_TYPE);
    }

    @SuppressWarnings("PMD")
    protected void importFiles(Project project, MageTabFileSet fileSet) throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        final CaArrayFileSet caarrayFileSet = uploadFiles(project, fileSet);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, caarrayFileSet, null);
        this.hibernateHelper.getCurrentSession().getTransaction().commit();
    }

    @SuppressWarnings("PMD")
    protected void uploadAndValidateFiles(Project project, Map<File, FileType> files) throws Exception {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        final CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();

        helpValidateFiles(tx, project, fileSet);
    }

    @SuppressWarnings("PMD")
    protected void helpValidateFiles(Transaction tx, Project project, CaArrayFileSet fileSet) throws Exception {
        tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        validateFiles(project, fileSet);
        tx.commit();
    }

    /**
     * "Upload" files to a project, returning the CaArrayFileSet containing those files.
     * 
     * @param project project to upload to
     * @param fileSet MageTabFileSet containing the files to upload (should correspond to the files in the file set)
     * @return
     */
    protected CaArrayFileSet uploadFiles(Project project, MageTabFileSet fileSet) {
        final CaArrayFileSet fset = new CaArrayFileSet(project);
        for (final FileRef file : fileSet.getAllFiles()) {
            final CaArrayFile caArrayFile = this.fileAccessService.add(file.getAsFile());
            caArrayFile.setProject(project);
            project.getFiles().add(caArrayFile);
            this.hibernateHelper.getCurrentSession().save(caArrayFile);
            fset.add(caArrayFile);
        }
        this.hibernateHelper.getCurrentSession().update(project);
        return fset;
    }

    protected CaArrayFileSet uploadFiles(Project project, Map<File, FileType> files) {
        for (final File file : files.keySet()) {
            final CaArrayFile caArrayFile = this.fileAccessService.add(file);
            caArrayFile.setProject(project);
            caArrayFile.setFileType(files.get(file));
            project.getFiles().add(caArrayFile);
            this.hibernateHelper.getCurrentSession().save(caArrayFile);
        }

        this.hibernateHelper.getCurrentSession().update(project);
        return project.getFileSet();
    }

    protected Source findSource(Project project, String name) {
        return CaArrayDaoFactory.INSTANCE.getProjectDao().getSourceForExperiment(project.getExperiment(), name);
    }

    protected void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions)
            throws Exception {
        final ProjectFilesImportJob job = new ProjectFilesImportJob(UsernameHolder.getUser(), targetProject, fileSet,
                dataImportOptions);
        job.setDaoFactory(CaArrayDaoFactory.INSTANCE);
        job.setMageTabImporter(this.injector.getInstance(MageTabImporter.class));
        try {
            job.execute();
        } catch (final Exception e) {
            job.getUnexpectedErrorPreparedStatement(this.hibernateHelper.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    protected void validateFiles(Project targetProject, CaArrayFileSet fileSet) throws Exception {
        final ProjectFilesValidationJob job = new ProjectFilesValidationJob(UsernameHolder.getUser(), targetProject,
                fileSet);
        job.setDaoFactory(CaArrayDaoFactory.INSTANCE);
        job.setMageTabImporter(this.injector.getInstance(MageTabImporter.class));
        try {
            job.execute();
        } catch (final Exception e) {
            job.getUnexpectedErrorPreparedStatement(this.hibernateHelper.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    protected ArrayDesign importArrayDesign(File designFile, FileType fileType) throws IllegalAccessException,
            InvalidDataFileException {
        ArrayDesign design = new ArrayDesign();
        design.setName(designFile.getName());
        design.setVersion("2.0");
        design.setGeoAccession("GPL0000");
        design.setProvider(DUMMY_PROVIDER);
        design.setLsidForEntity("authority:namespace:" + designFile.getName());
        final CaArrayFile caArrayDesignFile = this.fileAccessService.add(designFile);
        if (fileType != null) {
            caArrayDesignFile.setFileType(fileType);
        }
        design.addDesignFile(caArrayDesignFile);
        design.setTechnologyType(DUMMY_TERM);
        design.setOrganism(DUMMY_ORGANISM);
        design.getAssayTypes().add(DUMMY_ASSAY_TYPE);
        this.hibernateHelper.getCurrentSession().save(design);

        final ArrayDesignService arrayDesignService = ServiceLocatorFactory.getArrayDesignService();
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);

        // the import likely cleared the session, so we need to reload/reconstruct some supporting items,
        // to avoid session reassociation errors
        design = (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        resetData();
        return design;
    }

    @Override
    protected Injector createInjector() {
        return InjectorFactory.getInjector();
    }

    @BeforeClass
    public static void regiserFileAccessServiceStub() {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, new FileAccessServiceStub());
        InjectorFactory.addPlatform(new AbstractModule() {
            @Override
            protected void configure() {
                final MapBinder<String, DataStorage> mapBinder = MapBinder.newMapBinder(binder(), String.class,
                        DataStorage.class);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME)
                        .toProvider(
                                ServiceLocatorFactory.serviceProvider(FileAccessServiceStub.class,
                                        FileAccessService.JNDI_NAME));
            }
        });
    }

    private FileManagementService createFileManagementService() {
        this.fileAccessService = this.injector.getInstance(FileAccessServiceStub.class);
        final ServiceLocatorStub locatorStub = (ServiceLocatorStub) ServiceLocatorFactory.getLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessService);

        final MageTabTranslator mtt = new MageTabTranslatorBean();
        this.injector.injectMembers(mtt);
        locatorStub.addLookup(MageTabTranslator.JNDI_NAME, mtt);

        final ArrayDesignService ads = new ArrayDesignServiceBean();
        this.injector.injectMembers(ads);
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, ads);

        final ArrayDataService datas = new ArrayDataServiceBean();
        this.injector.injectMembers(datas);
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, datas);

        final VocabularyService vs = new VocabularyServiceBean();
        this.injector.injectMembers(vs);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vs);

        final FileManagementServiceBean bean = new FileManagementServiceBean();
        this.injector.injectMembers(bean);
        final FileManagementMDB mdb = new FileManagementMDB();
        this.injector.injectMembers(mdb);
        final UserTransaction ut = Mockito.mock(UserTransaction.class);
        mdb.setTransaction(ut);
        final DirectJobSubmitter submitter = new DirectJobSubmitter(mdb);
        bean.setSubmitter(submitter);

        return bean;
    }

}
