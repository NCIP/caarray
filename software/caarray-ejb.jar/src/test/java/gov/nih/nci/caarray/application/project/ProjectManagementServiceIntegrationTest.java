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
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Integration Test class for ProjectManagementService subsystem.
 */
@SuppressWarnings("PMD")
public class ProjectManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");

    private final FileAccessServiceStub fileAccessService = new FileAccessServiceStub();
    private ProjectManagementService projectManagementService;
    private GenericDataService genericDataService;
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();

    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();

    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();

    // Contacts
    private static ExperimentContact DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
    private static Person DUMMY_PERSON = new Person();
    private static Organization DUMMY_ORGANIZATION = new Organization();

    private static AssayType DUMMY_ASSAY_TYPE;

    @Before
    public void setUp() {
        this.projectManagementService = createProjectManagementService(this.fileAccessService);

        // Experiment
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();
        DUMMY_ASSAY_TYPE = new AssayType();

        DUMMY_EXPERIMENT_1 = new Experiment();

        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();

        // Contacts
        DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
        DUMMY_PERSON = new Person();
        DUMMY_ORGANIZATION = new Organization();
        DUMMY_PERSON.setAddress(new Address());
        DUMMY_ORGANIZATION.setAddress(new Address());

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
        final Transaction tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAY_TYPE);
        tx.commit();
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        setExperimentSummary();
        setExperimentContacts();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);

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
        final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
        DUMMY_ASSAY_TYPE.setName("Gene Expression");
        assayTypes.add(DUMMY_ASSAY_TYPE);
        DUMMY_EXPERIMENT_1.setAssayTypes(assayTypes);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private static void setExperimentContacts() {
        DUMMY_ORGANIZATION.setName("DummyOrganization1");
        DUMMY_PERSON.setFirstName("DummyFirstName1");
        DUMMY_PERSON.setLastName("DummyLastName1");
        DUMMY_PERSON.getAffiliations().add(DUMMY_ORGANIZATION);
        DUMMY_EXPERIMENT_CONTACT.setContact(DUMMY_PERSON);
        DUMMY_EXPERIMENT_1.getExperimentContacts().add(DUMMY_EXPERIMENT_CONTACT);
    }

    private ProjectManagementService createProjectManagementService(final FileAccessServiceStub fileAccessServiceStub) {
        final CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

        final GenericDataServiceBean genericDataServiceBean = new GenericDataServiceBean();
        genericDataServiceBean.setProjectDao(daoFactory.getProjectDao());
        genericDataServiceBean.setSearchDao(daoFactory.getSearchDao());
        this.genericDataService = genericDataServiceBean;

        final ProjectManagementServiceBean bean = new ProjectManagementServiceBean();
        bean.setProjectDao(daoFactory.getProjectDao());
        bean.setFileDao(daoFactory.getFileDao());
        bean.setSampleDao(daoFactory.getSampleDao());
        bean.setSearchDao(daoFactory.getSearchDao());
        bean.setVocabularyDao(daoFactory.getVocabularyDao());

        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, this.genericDataService);
        final MysqlDataSource ds = new MysqlDataSource();
        final Configuration config = this.hibernateHelper.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);

        return bean;
    }

    @Test
    public void testDeleteProject() throws Exception {
        final File file1 = File.createTempFile("blob1", ".ext");
        file1.deleteOnExit();
        final CaArrayFile caArrayFile1 = new CaArrayFile();
        caArrayFile1.setName("blob1.ext");
        caArrayFile1.setFileStatus(FileStatus.UPLOADED);
        caArrayFile1.setDataHandle(DUMMY_HANDLE);

        final File file2 = File.createTempFile("blob2", ".ext");
        file2.deleteOnExit();
        final CaArrayFile caArrayFile2 = new CaArrayFile();
        caArrayFile2.setName("blob2.ext");
        caArrayFile2.setFileStatus(FileStatus.UPLOADED);
        caArrayFile2.setDataHandle(DUMMY_HANDLE);

        DUMMY_PROJECT_1.getFiles().add(caArrayFile1);
        DUMMY_PROJECT_1.getFiles().add(caArrayFile2);

        caArrayFile1.setProject(DUMMY_PROJECT_1);
        caArrayFile2.setProject(DUMMY_PROJECT_1);

        Transaction t = null;
        try {
            t = this.hibernateHelper.beginTransaction();
            this.projectManagementService.saveProject(DUMMY_PROJECT_1);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            DUMMY_PROJECT_1 =
                    (Project) this.hibernateHelper.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
            DUMMY_PROJECT_1.setLocked(false);
            this.hibernateHelper.getCurrentSession().refresh(DUMMY_PROJECT_1);
            this.projectManagementService.saveProject(DUMMY_PROJECT_1);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            this.projectManagementService.deleteProject(DUMMY_PROJECT_1);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            final Project retrieved =
                    this.genericDataService.getPersistentObject(Project.class, DUMMY_PROJECT_1.getId());
            t.commit();
            assertNull(retrieved);

        } catch (final Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    @Test
    public void testChangeProjectOwner() throws Exception {
        CaArrayUsernameHolder.setUser(STANDARD_USER);
        Transaction tx = this.hibernateHelper.beginTransaction();
        this.projectManagementService.saveProject(DUMMY_PROJECT_1);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        assertNotNull(DUMMY_PROJECT_1.getId());
        Set<User> owners = DUMMY_PROJECT_1.getOwners();
        assertEquals(1, owners.size());
        assertEquals(STANDARD_USER, owners.iterator().next().getLoginName());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.projectManagementService.changeOwner(DUMMY_PROJECT_1.getId(), "caarrayuser");
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().clear();
        CaArrayUsernameHolder.setUser("caarrayuser");
        final Project retrieved = this.genericDataService.getPersistentObject(Project.class, DUMMY_PROJECT_1.getId());
        assertNotNull(retrieved);
        assertNotNull(retrieved.getExperiment());
        assertEquals(DUMMY_PROJECT_1.getExperiment().getTitle(), retrieved.getExperiment().getTitle());
        owners = retrieved.getOwners();
        assertEquals(1, owners.size());
        assertEquals("caarrayuser", owners.iterator().next().getLoginName());
        tx.commit();
    }

    @Test(expected = PermissionDeniedException.class)
    public void testDeleteUnownedProject() throws Exception {
        CaArrayUsernameHolder.setUser("caarrayuser");
        Transaction tx = this.hibernateHelper.beginTransaction();
        this.projectManagementService.saveProject(DUMMY_PROJECT_1);
        tx.commit();

        CaArrayUsernameHolder.setUser(STANDARD_USER);
        tx = this.hibernateHelper.beginTransaction();
        this.projectManagementService.deleteProject(DUMMY_PROJECT_1);
        tx.commit();
    }

    @Test
    public void testPublicId() throws Exception {
        CaArrayUsernameHolder.setUser(STANDARD_USER);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Experiment exp = DUMMY_PROJECT_1.getExperiment();
        assertNull(exp.getPublicIdentifier());
        this.projectManagementService.saveProject(DUMMY_PROJECT_1);
        final String expected = ProjectManagementServiceBean.PUBLIC_ID_PREFIX + exp.getId().toString();
        assertEquals(expected, exp.getPublicIdentifier());

        tx.rollback();
    }
}
