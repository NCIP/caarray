/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common.jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common.jar Software and any
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
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ServiceType;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.SecurityInterceptor;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.security.authorization.domainobjects.FilterClause;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.domainobjects.UserGroupRoleProtectionGroup;
import gov.nih.nci.security.dao.FilterClauseSearchCriteria;
import gov.nih.nci.security.dao.SearchCriteria;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Project DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class ProjectDaoTest extends AbstractDaoTest {
    private static final Log LOG = LogFactory.getLog(ProjectDaoTest.class);

    // Experiment
    private static final String UNCHECKED = "unchecked";
    private static Project DUMMY_PROJECT_1 = new Project();
    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();
    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();

    // Contacts
    private static ExperimentContact DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
    private static Person DUMMY_PERSON = new Person();
    private static Organization DUMMY_ORGANIZATION = new Organization();

    // Annotations
    private static Term DUMMY_REPLICATE_TYPE = new Term();
    private static Term DUMMY_NORMALIZATION_TYPE = new Term();
    private static Term DUMMY_QUALITY_CTRL_TYPE = new Term();

    // Factors
    private static Term DUMMY_FACTOR_TYPE_1 = new Term();
    private static Term DUMMY_FACTOR_TYPE_2 = new Term();
    private static Factor DUMMY_FACTOR_1 = new Factor();
    private static Factor DUMMY_FACTOR_2 = new Factor();

    // Publications
    private static Publication DUMMY_PUBLICATION_1 = new Publication();
    private static Publication DUMMY_PUBLICATION_2 = new Publication();
    private static Term DUMMY_PUBLICATION_STATUS = new Term();

    private static CaArrayFile DUMMY_FILE_1 = new CaArrayFile();
    private static CaArrayFile DUMMY_FILE_2 = new CaArrayFile();

    private static Source DUMMY_SOURCE;
    private static Sample DUMMY_SAMPLE;
    private static Extract DUMMY_EXTRACT;
    private static LabeledExtract DUMMY_LABELED_EXTRACT;
    private static Hybridization DUMMY_HYBRIDIZATION;
    private static RawArrayData DUMMY_RAW_ARRAY_DATA;
    private static CaArrayFile DUMMY_DATA_FILE;

    private static final ProjectDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getProjectDao();
    private static final VocabularyDao VOCABULARY_DAO = CaArrayDaoFactory.INSTANCE.getVocabularyDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        // Experiment
        DUMMY_PROJECT_1 = new Project();
        DUMMY_EXPERIMENT_1 = new Experiment();
        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();

        // Contacts
        DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
        DUMMY_PERSON = new Person();
        DUMMY_ORGANIZATION = new Organization();
        DUMMY_PERSON.setAddress(new Address());
        DUMMY_ORGANIZATION.setAddress(new Address());

        // Annotations
        DUMMY_REPLICATE_TYPE = new Term();
        DUMMY_NORMALIZATION_TYPE = new Term();
        DUMMY_QUALITY_CTRL_TYPE = new Term();

        // Factors
        DUMMY_FACTOR_TYPE_1 = new Term();
        DUMMY_FACTOR_TYPE_2 = new Term();
        DUMMY_FACTOR_1 = new Factor();
        DUMMY_FACTOR_2 = new Factor();

        // Publications
        DUMMY_PUBLICATION_1 = new Publication();
        DUMMY_PUBLICATION_2 = new Publication();
        DUMMY_PUBLICATION_STATUS = new Term();

        DUMMY_FILE_1 = new CaArrayFile();
        DUMMY_FILE_2 = new CaArrayFile();

        DUMMY_SOURCE = new Source();
        DUMMY_SAMPLE = new Sample();
        DUMMY_EXTRACT = new Extract();
        DUMMY_LABELED_EXTRACT = new LabeledExtract();
        DUMMY_HYBRIDIZATION = new Hybridization();
        DUMMY_RAW_ARRAY_DATA = new RawArrayData();
        DUMMY_DATA_FILE = new CaArrayFile();

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        setExperimentSummary();
        setExperimentContacts();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_CATEGORY.setName("Dummy Category");
        setExperimentAnnotations();
        setExperimentalFactors();
        setPublications();
        setFiles();
        setBioMaterials();
        setHybridizations();
        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
    }

    private static void setHybridizations() {
        DUMMY_LABELED_EXTRACT.getHybridizations().add(DUMMY_HYBRIDIZATION);
        DUMMY_HYBRIDIZATION.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        DUMMY_HYBRIDIZATION.setArrayData(DUMMY_RAW_ARRAY_DATA);
        DUMMY_RAW_ARRAY_DATA.setHybridization(DUMMY_HYBRIDIZATION);
        DUMMY_RAW_ARRAY_DATA.setDataFile(DUMMY_DATA_FILE);
    }

    private static void setBioMaterials() {
        DUMMY_SOURCE.setName("DummySource");
        DUMMY_SAMPLE.setName("DummySample");
        DUMMY_EXTRACT.setName("DummyExtract");
        DUMMY_LABELED_EXTRACT.setName("DummyLabeledExtract");
        DUMMY_EXPERIMENT_1.getSources().add(DUMMY_SOURCE);
        DUMMY_EXPERIMENT_1.getSamples().add(DUMMY_SAMPLE);
        DUMMY_EXPERIMENT_1.getExtracts().add(DUMMY_EXTRACT);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
    }

    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDateOfExperiment(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setAssayType(AssayType.ACGH);
        DUMMY_EXPERIMENT_1.setServiceType(ServiceType.FULL);
    }

    private static void setExperimentContacts() {
        DUMMY_ORGANIZATION.setName("DummyOrganization1");
        DUMMY_PERSON.setFirstName("DummyFirstName1");
        DUMMY_PERSON.setLastName("DummyLastName1");
        DUMMY_PERSON.getAffiliations().add(DUMMY_ORGANIZATION);
        DUMMY_EXPERIMENT_CONTACT.setContact(DUMMY_PERSON);
        DUMMY_EXPERIMENT_1.getExperimentContacts().add(DUMMY_EXPERIMENT_CONTACT);
    }

    private static void setExperimentAnnotations() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_EXPERIMENT_1.getReplicateTypes().add(DUMMY_REPLICATE_TYPE);
        DUMMY_EXPERIMENT_1.getNormalizationTypes().add(DUMMY_NORMALIZATION_TYPE);
        DUMMY_EXPERIMENT_1.getQualityControlTypes().add(DUMMY_QUALITY_CTRL_TYPE);
    }

    private static void setExperimentalFactors() {
        DUMMY_FACTOR_TYPE_1.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_1.setSource(DUMMY_TERM_SOURCE);
        DUMMY_FACTOR_TYPE_1.setValue("Dummy Factor Type 1");
        DUMMY_FACTOR_TYPE_2.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_2.setSource(DUMMY_TERM_SOURCE);
        DUMMY_FACTOR_TYPE_2.setValue("Dummy Factor Type 2");
        DUMMY_FACTOR_1.setName("Dummy Factor 1");
        DUMMY_FACTOR_1.setType(DUMMY_FACTOR_TYPE_1);
        DUMMY_FACTOR_2.setName("Dummy Factor 2");
        DUMMY_FACTOR_2.setType(DUMMY_FACTOR_TYPE_2);
        DUMMY_EXPERIMENT_1.getFactors().add(DUMMY_FACTOR_1);
        DUMMY_EXPERIMENT_1.getFactors().add(DUMMY_FACTOR_2);
    }

    private static void setFiles() {
        DUMMY_FILE_1.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        DUMMY_FILE_1.setType(FileType.MAGE_TAB_IDF);
        DUMMY_FILE_2.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF.getName());
        DUMMY_FILE_1.setType(FileType.MAGE_TAB_SDRF);
        DUMMY_PROJECT_1.getFiles().add(DUMMY_FILE_1);
        DUMMY_PROJECT_1.getFiles().add(DUMMY_FILE_2);
    }

    @SuppressWarnings(UNCHECKED)
    private static void setPublications() {
        DUMMY_PUBLICATION_1.setTitle("DummyPublicationTitle1");
        DUMMY_PUBLICATION_1.setAuthors("DummyAuthors1");
        DUMMY_PUBLICATION_1.setDoi("DummyDoi1");
        DUMMY_PUBLICATION_1.setPubMedId("DummyPubMedId1");
        DUMMY_PUBLICATION_2.setTitle("DummyPublicationTitle2");
        DUMMY_PUBLICATION_2.setAuthors("DummyAuthors2");
        DUMMY_PUBLICATION_2.setDoi("DummyDoi2");
        DUMMY_PUBLICATION_2.setPubMedId("DummyPubMedId2");

        DUMMY_PUBLICATION_STATUS.setCategory(DUMMY_CATEGORY);
        DUMMY_PUBLICATION_STATUS.setSource(DUMMY_TERM_SOURCE);
        DUMMY_PUBLICATION_STATUS.setValue("Dummy Status: Published");
        DUMMY_PUBLICATION_1.setStatus(DUMMY_PUBLICATION_STATUS);
        DUMMY_PUBLICATION_2.setStatus(DUMMY_PUBLICATION_STATUS);

        DUMMY_EXPERIMENT_1.getPublications().add(DUMMY_PUBLICATION_1);
        DUMMY_EXPERIMENT_1.getPublications().add(DUMMY_PUBLICATION_2);
    }

    private static void saveSupportingObjects() {
        VOCABULARY_DAO.save(DUMMY_REPLICATE_TYPE);
        VOCABULARY_DAO.save(DUMMY_QUALITY_CTRL_TYPE);
        VOCABULARY_DAO.save(DUMMY_NORMALIZATION_TYPE);
        VOCABULARY_DAO.save(DUMMY_FACTOR_TYPE_1);
        VOCABULARY_DAO.save(DUMMY_FACTOR_TYPE_2);
    }


    /**
     * Tests retrieving the <code>Project</code> with the given id.
     * Test encompasses save and delete of a <code>Project</code>.
     */
    @Test
    public void testGetProject() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            saveSupportingObjects();
            int size = DAO_OBJECT.getNonPublicProjectsForUser().size();

            DAO_OBJECT.save(DUMMY_PROJECT_1);
            Project retrievedProject = DAO_OBJECT.getProject(DUMMY_PROJECT_1.getId());
            tx.commit();
            if (DUMMY_PROJECT_1.equals(retrievedProject)) {
                checkFiles(DUMMY_PROJECT_1, retrievedProject);
                if (compareExperiments(retrievedProject.getExperiment(), DUMMY_PROJECT_1.getExperiment())) {
                    // The retrieved project is the same as the saved project. Test passed.
                    assertTrue(true);
                }
            } else {
                fail("Retrieved project is different from saved project.");
            }
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            assertEquals(size + 1, DAO_OBJECT.getNonPublicProjectsForUser().size());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
//            fail("DAO exception during save and retrieve of project: " + e.getMessage());
        }
    }

    private void checkFiles(Project project, Project retrievedProject) {
        assertEquals(project.getFiles().size(), retrievedProject.getFiles().size());
        assertEquals(DUMMY_FILE_2, retrievedProject.getFiles().first());
        assertEquals(DUMMY_FILE_1, retrievedProject.getFiles().last());
    }

    /**
     * Compare 2 experiments to check if they are the same.
     *
     * @return true if the 2 experiments are the same and false otherwise.
     */
    @SuppressWarnings("PMD")
    private boolean compareExperiments(Experiment retrievedInv, Experiment dummyInv) {
        checkBioMaterials(dummyInv, retrievedInv);
        checkHybridizations(dummyInv, retrievedInv);

        // Experiment summary.
        if (!dummyInv.getTitle().equals(retrievedInv.getTitle())) {
            return false;
        }
        // Contacts
        Collection<ExperimentContact> contacts = retrievedInv.getExperimentContacts();
        if (contacts.isEmpty() || contacts.size() != 1) {
            return false;
        }
        Iterator<ExperimentContact> i = contacts.iterator();
        Person person = (Person) i.next().getContact();
        if (!DUMMY_PERSON.getFirstName().equals(person.getFirstName())) {
            return false;
        }
        // Annotations
        Collection<Term> retrievedNormTypes = retrievedInv.getNormalizationTypes();
        if (retrievedNormTypes.isEmpty() || retrievedNormTypes.size() != 1) {
            return false;
        }
        Iterator<Term> i2 = retrievedNormTypes.iterator();
        Term retrievedNormType = i2.next();
        if (!DUMMY_NORMALIZATION_TYPE.getValue().equals(retrievedNormType.getValue())) {
            return false;
        }
        // Factors
        Collection<Factor> factors = retrievedInv.getFactors();
        if (factors.isEmpty() || factors.size() != 2) {
            return false;
        }

        // Publications
        Collection<Publication> publications = retrievedInv.getPublications();
        if (publications.isEmpty() || publications.size() != 2) {
            return false;
        }
        return true;
    }

    private void checkHybridizations(Experiment dummyInv, Experiment retrievedInv) {
        LabeledExtract labeledExtract = retrievedInv.getLabeledExtracts().iterator().next();
        Hybridization hybridization = labeledExtract.getHybridizations().iterator().next();
        assertEquals(labeledExtract, hybridization.getLabeledExtracts().iterator().next());
        RawArrayData arrayData = hybridization.getArrayData();
        assertNotNull(arrayData);
        assertEquals(hybridization, arrayData.getHybridization());
    }

    private void checkBioMaterials(Experiment dummyInv, Experiment retrievedInv) {
        assertEquals(dummyInv.getSources().size(), retrievedInv.getSources().size());
        assertEquals(dummyInv.getSamples().size(), retrievedInv.getSamples().size());
        assertEquals(dummyInv.getExtracts().size(), retrievedInv.getExtracts().size());
        assertEquals(dummyInv.getLabeledExtracts().size(), retrievedInv.getLabeledExtracts().size());
    }

    /**
     * Tests searching for a <code>Person</code> by example, including associations
     * in the search.
     */
    @SuppressWarnings(UNCHECKED)
    @Test
    public void testDeepSearchPersonByExample() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PERSON);
            tx.commit();
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            Person examplePerson = new Person();
            examplePerson.setLastName(DUMMY_PERSON.getLastName());
            examplePerson.getAffiliations().add(DUMMY_ORGANIZATION);
            Person retrievedPerson = null;
            List<Person> matchingPersons =
                DAO_OBJECT.queryEntityAndAssociationsByExample(examplePerson);
            if ((matchingPersons != null) && (matchingPersons.size() >= 1)) {
                retrievedPerson = matchingPersons.get(0);
            }
            if (DUMMY_PERSON.equals(retrievedPerson)) {
                // The retrieved person is the same as the saved person. Test passed.
                assertTrue(true);
            } else {
                fail("Retrieved person is different from saved person.");
            }
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search of person: " + e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testValidationMessages() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            File file = new File("test/path/file.txt");
            DUMMY_FILE_1.setName(file.getName());
            FileValidationResult result = new FileValidationResult(file);
            ValidationMessage message1 = result.addMessage(ValidationMessage.Type.INFO, "info message");
            ValidationMessage message2 = result.addMessage(ValidationMessage.Type.ERROR, "error message");
            DUMMY_FILE_1.setValidationResult(result);
            DAO_OBJECT.save(DUMMY_FILE_1);
            tx.commit();
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            CaArrayFile retrievedFile = DAO_OBJECT.queryEntityByExample(DUMMY_FILE_1).iterator().next();
            assertNotNull(retrievedFile.getValidationResult());
            FileValidationResult retrievedResult = retrievedFile.getValidationResult();
            assertEquals(2, retrievedResult.getMessages().size());
            ValidationMessage retrievedMesssage1 = retrievedResult.getMessages().get(0);
            ValidationMessage retrievedMesssage2 = retrievedResult.getMessages().get(1);
            // order of messages should be swapped (ERRORs returned before INFOs)
            assertEquals(message2.getType(), retrievedMesssage1.getType());
            assertEquals(message2.getMessage(), retrievedMesssage1.getMessage());
            assertEquals(message1.getType(), retrievedMesssage2.getType());
            assertEquals(message1.getMessage(), retrievedMesssage2.getMessage());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search of person: " + e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProtectionElements() {
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        saveSupportingObjects();
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = HibernateUtil.getCurrentSession().beginTransaction();
        String str = "FROM " + ProtectionElement.class.getName() + " pe "
                     + "WHERE pe.objectId = :objId "
                     + "  AND pe.attribute = :attr "
                     + "  AND pe.value = :value";
        Query q = HibernateUtil.getCurrentSession().createQuery(str);
        q.setParameter("objId", Project.class.getName());
        q.setParameter("attr", "id");
        q.setParameter("value", DUMMY_PROJECT_1.getId().toString());

        ProtectionElement pe = (ProtectionElement) q.uniqueResult();
        assertNotNull(pe);
        assertEquals("id", pe.getAttribute());
        assertEquals(DUMMY_PROJECT_1.getId().toString(), pe.getValue());
        assertEquals(((User) pe.getOwners().iterator().next()).getLoginName(), UsernameHolder.getUser());

        str = "FROM " + ProtectionGroup.class.getName() + " pg "
              + "WHERE :pe in elements(pg.protectionElements)";
        q = HibernateUtil.getCurrentSession().createQuery(str);
        q.setParameter("pe", pe);

        ProtectionGroup pg = (ProtectionGroup) q.uniqueResult();
        assertNotNull(pg);
        assertEquals(pe, pg.getProtectionElements().iterator().next());
        tx.commit();
}

    @Test
    public void testProjectPermissions() {
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        saveSupportingObjects();
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = HibernateUtil.getCurrentSession().beginTransaction();
        Project p = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertNotNull(p.getPublicProfile());
        assertEquals(p.getPublicProfile().getSecurityLevel(), SecurityLevel.NONE);
        assertEquals(p.getHostProfile().getSecurityLevel(), SecurityLevel.READ_WRITE_SELECTIVE);
        assertTrue(p.isBrowsable());
        p.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
        p.getHostProfile().setSecurityLevel(SecurityLevel.NONE);
        p.setBrowsable(false);

        List<UserGroupRoleProtectionGroup> list = SecurityInterceptor.getUserGroupRoleProtectionGroups(p);
        assertEquals(2, list.size()); // expect the user-only one and the anonymous access one

        tx.commit();

        tx = HibernateUtil.getCurrentSession().beginTransaction();
        p = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(p.getPublicProfile().getSecurityLevel(), SecurityLevel.READ);
        assertEquals(p.getHostProfile().getSecurityLevel(), SecurityLevel.NONE);
        assertTrue(!p.isBrowsable());
        list = SecurityInterceptor.getUserGroupRoleProtectionGroups(p);
        assertEquals(1, list.size()); // expect the user-only one, but not the anonymous access one
        tx.commit();
}

    @Test
    public void testNonBrowsableProject() {
        DUMMY_PROJECT_1.setBrowsable(false);
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        saveSupportingObjects();
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = HibernateUtil.getCurrentSession().beginTransaction();
        Project p = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        List<UserGroupRoleProtectionGroup> list = SecurityInterceptor.getUserGroupRoleProtectionGroups(p);
        assertEquals(1, list.size()); // expect the user-only one, not the anonymous access one
        p.setBrowsable(true);

        tx.commit();
        tx = HibernateUtil.getCurrentSession().beginTransaction();

        list = SecurityInterceptor.getUserGroupRoleProtectionGroups(p);
        assertEquals(2, list.size()); // expect the user-only one and the anonymous access one

        tx.commit();
    }

    @Test
    public void testFilters() {
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();

        FilterClause searchFilterClause = new FilterClause();
        searchFilterClause.setClassName("*");
        SearchCriteria searchCriteria = new FilterClauseSearchCriteria(searchFilterClause);
        List<?> list = SecurityInterceptor.getAuthorizationManager().getObjects(searchCriteria);
        assertTrue(list.size() > 0);
        tx.commit();
    }
}
