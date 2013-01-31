//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.project.ServiceType;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.FilterClause;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.domainobjects.UserGroupRoleProtectionGroup;
import gov.nih.nci.security.dao.FilterClauseSearchCriteria;
import gov.nih.nci.security.dao.SearchCriteria;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Unit tests for the Project DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class ProjectDaoTest extends AbstractProjectDaoTest {
    private static final Logger LOG = Logger.getLogger(ProjectDaoTest.class);



    private static void checkVisible(Project p) {
        // per Change Request 13332, when projects are created in Draft status
        // they are by default NO_VISIBILITY
        if (p.getStatus().equals(ProposalStatus.DRAFT)) {
            assertEquals(SecurityLevel.NO_VISIBILITY, p.getPublicProfile().getSecurityLevel());
        } else {
            assertEquals(SecurityLevel.VISIBLE, p.getPublicProfile().getSecurityLevel());
        }
    }


    /**
     * Tests retrieving the <code>Project</code> with the given id. Test encompasses save and delete of a
     * <code>Project</code>.
     */
    @Test
    public void testGetProject() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            int size = DAO_OBJECT.getProjectCountForCurrentUser(false);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            Project retrievedProject = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertTrue(DUMMY_PROJECT_1.equals(retrievedProject));
            checkFiles(DUMMY_PROJECT_1, retrievedProject);
            assertTrue(compareExperiments(retrievedProject.getExperiment(), DUMMY_PROJECT_1.getExperiment()));
            assertEquals(size + 1, DAO_OBJECT.getProjectCountForCurrentUser(false));
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            retrievedProject = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            DAO_OBJECT.remove(retrievedProject);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            Project deletedProject = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNull(deletedProject);
            assertEquals(size, DAO_OBJECT.getProjectCountForCurrentUser(false));
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Project</code> with the given id. Test encompasses save and delete of a
     * <code>Project</code>.
     * @throws CSTransactionException
     */
    @Test
    public void testGetWorkspaceProjects() throws CSTransactionException {
        Transaction tx = null;

        Group group = null;
        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(false, ALL_BY_ID).size());
            assertEquals(0, DAO_OBJECT.getProjectsForCurrentUser(true, ALL_BY_ID).size());
            tx.commit();

            UsernameHolder.setUser("caarrayuser");
            tx = HibernateUtil.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser(false));
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser(true));
            tx.commit();

            UsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = HibernateUtil.beginTransaction();
            Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.setStatus(ProposalStatus.IN_PROGRESS);
            tx.commit();

            UsernameHolder.setUser("caarrayuser");
            tx = HibernateUtil.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser(false));
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser(true));
            tx.commit();

            UsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = HibernateUtil.beginTransaction();
             p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            UsernameHolder.setUser("caarrayuser");
            tx = HibernateUtil.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser(false));
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser(true));
            tx.commit();

            UsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = HibernateUtil.beginTransaction();
            AuthorizationManager am = SecurityUtils.getAuthorizationManager();
            group = new Group();
            group.setGroupName("Foo");
            group.setGroupDesc("Collaborator Group");
            group.setApplication(SecurityUtils.getApplication());
            am.createGroup(group);
            String[] groupMembers = { am.getUser("caarrayuser").getUserId().toString() };
            am.assignUsersToGroup(group.getGroupId().toString(), groupMembers);
            CollaboratorGroup cg = new CollaboratorGroup(group, UsernameHolder.getCsmUser());
            COLLAB_DAO.save(cg);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.addGroupProfile(cg).setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            UsernameHolder.setUser("caarrayuser");
            tx = HibernateUtil.beginTransaction();
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(false, ALL_BY_ID).size());
            assertEquals(0, DAO_OBJECT.getProjectsForCurrentUser(true, ALL_BY_ID).size());
            tx.commit();

            UsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = HibernateUtil.beginTransaction();
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.setStatus(ProposalStatus.PUBLIC);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectsForCurrentUser(false, ALL_BY_ID).size());
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(true, ALL_BY_ID).size());
            tx.commit();

            UsernameHolder.setUser("caarrayuser");
            tx = HibernateUtil.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectsForCurrentUser(false, ALL_BY_ID).size());
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(true, ALL_BY_ID).size());
            tx.commit();

            UsernameHolder.setUser("biostatistician");
            tx = HibernateUtil.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectsForCurrentUser(false, ALL_BY_ID).size());
            assertEquals(0, DAO_OBJECT.getProjectsForCurrentUser(true, ALL_BY_ID).size());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
            // fail("DAO exception during save and retrieve of project: " + e.getMessage());
        } finally {
            if (group != null && group.getGroupId() != null) {
                SecurityUtils.getAuthorizationManager().removeGroup(group.getGroupId().toString());
            }
        }
    }

    private void checkFiles(Project project, Project retrievedProject) {
        assertEquals(project.getFiles().size(), retrievedProject.getFiles().size());
        assertTrue(retrievedProject.getFiles().contains(DUMMY_FILE_1));
        assertTrue(retrievedProject.getFiles().contains(DUMMY_FILE_2));
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
        RawArrayData arrayData = hybridization.getRawDataCollection().iterator().next();
        assertNotNull(arrayData);
        assertEquals(hybridization, arrayData.getHybridizations().iterator().next());
    }

    private void checkBioMaterials(Experiment dummyInv, Experiment retrievedInv) {
        CollectionUtils.isEqualCollection(dummyInv.getSources(), retrievedInv.getSources());
        CollectionUtils.isEqualCollection(dummyInv.getSamples(), retrievedInv.getSamples());
        CollectionUtils.isEqualCollection(dummyInv.getExtracts(), retrievedInv.getExtracts());
        CollectionUtils.isEqualCollection(dummyInv.getLabeledExtracts(), retrievedInv.getLabeledExtracts());
    }

    /**
     * Tests searching for a <code>Person</code> by example, including associations in the search.
     */
    @Test
    public void testDeepSearchPersonByExample() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PERSON);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            Person examplePerson = new Person();
            examplePerson.setLastName(DUMMY_PERSON.getLastName());
            examplePerson.getAffiliations().add(DUMMY_ORGANIZATION);
            Person retrievedPerson = null;
            List<Person> matchingPersons = DAO_OBJECT.queryEntityAndAssociationsByExample(examplePerson);
            assertNotNull(matchingPersons);
            assertTrue(matchingPersons.size() > 0);
            retrievedPerson = matchingPersons.get(0);
            assertEquals("Retrieved person is different from saved person.", DUMMY_PERSON.getLastName(), retrievedPerson.getLastName());
            assertEquals("Retrieved person is different from saved person.", DUMMY_PERSON.getFirstName(), retrievedPerson.getFirstName());
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
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            File file = new File("test/path/file.txt");
            DUMMY_FILE_1.setName(file.getName());
            FileValidationResult result = new FileValidationResult(file);
            ValidationMessage message1 = result.addMessage(ValidationMessage.Type.INFO, "info message");
            ValidationMessage message2 = result.addMessage(ValidationMessage.Type.ERROR, "error message");
            DUMMY_FILE_1.setValidationResult(result);
            DAO_OBJECT.save(DUMMY_FILE_1);
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

    @Test
    public void testProtectionElements() {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        String str =
                "FROM " + ProtectionElement.class.getName() + " pe " + "WHERE pe.objectId = :objId "
                        + "  AND pe.attribute = :attr " + "  AND pe.value = :value";
        Query q = HibernateUtil.getCurrentSession().createQuery(str);
        q.setParameter("objId", Project.class.getName());
        q.setParameter("attr", "id");
        q.setParameter("value", DUMMY_PROJECT_1.getId().toString());

        ProtectionElement pe = (ProtectionElement) q.uniqueResult();
        assertNotNull(pe);
        assertEquals("id", pe.getAttribute());
        assertEquals(DUMMY_PROJECT_1.getId().toString(), pe.getValue());
        assertEquals(((User) pe.getOwners().iterator().next()).getLoginName(), UsernameHolder.getUser());

        str = "FROM " + ProtectionGroup.class.getName() + " pg " + "WHERE :pe in elements(pg.protectionElements)";
        q = HibernateUtil.getCurrentSession().createQuery(str);
        q.setParameter("pe", pe);

        ProtectionGroup pg = (ProtectionGroup) q.uniqueResult();
        assertNotNull(pg);
        assertEquals(pe, pg.getProtectionElements().iterator().next());
        tx.commit();
    }

    @Test
    public void testProjectPermissions() throws Exception {
        Group group = null;
        try {
            UsernameHolder.setUser(STANDARD_USER);
            Transaction tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            assertTrue(SecurityUtils.canWrite(DUMMY_PROJECT_1, UsernameHolder.getCsmUser()));
            HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
            Long experimentId = DUMMY_PROJECT_1.getExperiment().getId();
            tx.commit();
            assertNotNull(experimentId);
            checkVisible(DUMMY_PROJECT_1);

            // a new project, in default draft status, will not be visible to the anonymous user
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            tx = HibernateUtil.beginTransaction();
            Experiment e = SEARCH_DAO.retrieve(Experiment.class, experimentId);
            assertNull(e);
            assertFalse(SecurityUtils.canRead(DUMMY_PROJECT_1, UsernameHolder.getCsmUser()));
            tx.commit();

            // after changing the project to visible, it can be browsed
            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.VISIBLE);
            tx.commit();


            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            HibernateUtil.getCurrentSession().clear();
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);
            assertNull(p.getExperiment().getDesignDescription());
            assertFalse(SecurityUtils.canRead(p, UsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(p, UsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canModifyPermissions(p, UsernameHolder.getCsmUser()));
            assertEquals(0, p.getFiles().size());
            HibernateUtil.getCurrentSession().clear();
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_1.getId()));
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_2.getId()));
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_DATA_FILE.getId()));
            assertFalse(SecurityUtils.canRead(p, UsernameHolder.getCsmUser()));
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertTrue(SecurityUtils.isOwner(p, UsernameHolder.getCsmUser()));
            assertTrue(SecurityUtils.canWrite(DUMMY_SOURCE, UsernameHolder.getCsmUser()));
            assertNotNull(p.getPublicProfile());
            //checkVisible(p);
            assertEquals(p.getHostProfile().getSecurityLevel(), SecurityLevel.READ_WRITE_SELECTIVE);
            //e = SEARCH_DAO.retrieve(Experiment.class, experimentId);
            //assertNotNull(e);

            assertEquals(2, p.getFiles().size());
            assertNotNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_1.getId()));
            assertNotNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_2.getId()));
            assertNotNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_DATA_FILE.getId()));

            List<UserGroupRoleProtectionGroup> list = SecurityUtils.getUserGroupRoleProtectionGroups(p);
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.READ_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.WRITE_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.PERMISSIONS_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.BROWSE_ROLE))));

            assertTrue(SecurityUtils.canRead(p, UsernameHolder.getCsmUser()));
            assertTrue(SecurityUtils.canWrite(p, UsernameHolder.getCsmUser()));
            assertTrue(SecurityUtils.canModifyPermissions(p, UsernameHolder.getCsmUser()));

            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            assertFalse(SecurityUtils.canRead(p, UsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(p, UsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canModifyPermissions(p, UsernameHolder.getCsmUser()));

            UsernameHolder.setUser(STANDARD_USER);
            p.getPublicProfile().setSecurityLevel(SecurityLevel.NO_VISIBILITY);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNull(p);
            e = SEARCH_DAO.retrieve(Experiment.class, experimentId);
            assertNull(e);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);
            assertEquals(p.getPublicProfile().getSecurityLevel(), SecurityLevel.READ);
            assertNotNull(p.getExperiment().getDesignDescription());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(1, p.getExperiment().getSamples().size());
            list = SecurityUtils.getUserGroupRoleProtectionGroups(p);
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.READ_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.WRITE_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.PERMISSIONS_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.BROWSE_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.READ_ROLE))));
            assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                    SecurityUtils.BROWSE_ROLE))));

            assertEquals(1, p.getFiles().size());
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_1.getId()));
            assertNotNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_2.getId()));
            assertNotNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_DATA_FILE.getId()));
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ_SELECTIVE);
            p.getPublicProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.NONE);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);
            assertEquals(SecurityLevel.READ_SELECTIVE, p.getPublicProfile().getSecurityLevel());
            assertNotNull(p.getExperiment().getDesignDescription());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(0, p.getExperiment().getSamples().size());

            assertEquals(1, p.getFiles().size());
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_1.getId()));
            assertNotNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_2.getId()));
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_DATA_FILE.getId()));
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ_SELECTIVE);
            p.getPublicProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.READ);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);
            assertEquals(SecurityLevel.READ_SELECTIVE, p.getPublicProfile().getSecurityLevel());
            assertNotNull(p.getExperiment().getDesignDescription());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(1, p.getExperiment().getSamples().size());
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            Sample s = new Sample();
            s.setName("New Sample");
            p.getExperiment().getSamples().add(s);
            DAO_OBJECT.save(p);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(1, p.getExperiment().getSamples().size());
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            AuthorizationManager am = SecurityUtils.getAuthorizationManager();
            group = new Group();
            group.setGroupName("Foo");
            group.setGroupDesc("Collaborator Group");
            group.setApplication(SecurityUtils.getApplication());
            am.createGroup(group);
            String[] groupMembers = { am.getUser("caarrayuser").getUserId().toString() };
            am.assignUsersToGroup(group.getGroupId().toString(), groupMembers);
            CollaboratorGroup cg = new CollaboratorGroup(group, UsernameHolder.getCsmUser());
            COLLAB_DAO.save(cg);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            AccessProfile groupProfile = p.addGroupProfile(cg);
            groupProfile.setSecurityLevel(SecurityLevel.READ_WRITE_SELECTIVE);
            groupProfile.getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.READ_WRITE);
            groupProfile.getSampleSecurityLevels().put(s, SampleSecurityLevel.READ);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser("caarrayuser");
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            assertTrue(SecurityUtils.canWrite(DUMMY_SAMPLE, UsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(s, UsernameHolder.getCsmUser()));
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(1, p.getExperiment().getSamples().size());
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.NO_VISIBILITY);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNull(p);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser("caarrayuser");
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            assertTrue(SecurityUtils.canWrite(DUMMY_SAMPLE, UsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(s, UsernameHolder.getCsmUser()));
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.VISIBLE);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);
            assertNotNull(p.getExperiment().getAssayTypeEnum());
            assertNull(p.getExperiment().getDesignDescription());
            HibernateUtil.getCurrentSession().clear();
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            UsernameHolder.setUser("caarrayuser");
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            assertTrue(SecurityUtils.canWrite(DUMMY_SAMPLE, UsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(s, UsernameHolder.getCsmUser()));
            tx.commit();
        } finally {
            if (group != null && group.getGroupId() != null) {
                SecurityUtils.getAuthorizationManager().removeGroup(group.getGroupId().toString());
            }
        }
    }

    @Test
    public void testInitialProjectPermissions() {
        // create project
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        //check initial settings.. drafts should be not visible
        tx = HibernateUtil.beginTransaction();
        Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        List<UserGroupRoleProtectionGroup> list = SecurityUtils.getUserGroupRoleProtectionGroups(p);
        assertEquals(4, list.size()); // expect the user-only ones only
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.READ_ROLE))));
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.WRITE_ROLE))));
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.PERMISSIONS_ROLE))));
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.BROWSE_ROLE))));
        p.getPublicProfile().setSecurityLevel(SecurityLevel.VISIBLE);
        tx.commit();

        // check that after changing to visible, the role is reflected.
        tx = HibernateUtil.beginTransaction();
        list = SecurityUtils.getUserGroupRoleProtectionGroups(p);
        assertEquals(5, list.size()); // expect the user-only ones and the anonymous access one
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.READ_ROLE))));
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.WRITE_ROLE))));
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.PERMISSIONS_ROLE))));
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.BROWSE_ROLE))));
        assertTrue(CollectionUtils.exists(list, new AndPredicate(new IsGroupPredicate(), new HasRolePredicate(
                SecurityUtils.BROWSE_ROLE))));
        tx.commit();
    }

    @Test
    public void testProjectWorkflow() {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        checkVisible(p);
        p.getPublicProfile().setSecurityLevel(SecurityLevel.NO_VISIBILITY);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(SecurityLevel.NO_VISIBILITY, p.getPublicProfile().getSecurityLevel());
        p.setStatus(ProposalStatus.IN_PROGRESS);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        checkVisible(p);
        p.getPublicProfile().setSecurityLevel(SecurityLevel.NO_VISIBILITY);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(SecurityLevel.NO_VISIBILITY, p.getPublicProfile().getSecurityLevel());
        p.setStatus(ProposalStatus.PUBLIC);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(SecurityLevel.READ, p.getPublicProfile().getSecurityLevel());
        tx.commit();
    }

    @Test
    public void testTcgaPolicy() {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        DUMMY_PROJECT_1.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        assertFalse(DUMMY_PROJECT_1.isUseTcgaPolicy());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Sample s = SEARCH_DAO.retrieve(Sample.class, DUMMY_SAMPLE.getId());
        assertNotNull(s.getDescription());
        assertEquals(1, DUMMY_SAMPLE.getCharacteristics().size());
        tx.commit();

        UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        tx = HibernateUtil.beginTransaction();
        s = SEARCH_DAO.retrieve(Sample.class, DUMMY_SAMPLE.getId());
        assertNotNull(s.getDescription());
        assertEquals(1, DUMMY_SAMPLE.getCharacteristics().size());
        tx.commit();

        UsernameHolder.setUser(STANDARD_USER);
        tx = HibernateUtil.beginTransaction();
        Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        p.setUseTcgaPolicy(true);
        DAO_OBJECT.save(p);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = SEARCH_DAO.retrieve(Sample.class, DUMMY_SAMPLE.getId());
        assertNotNull(s.getDescription());
        assertEquals(1, DUMMY_SAMPLE.getCharacteristics().size());
        tx.commit();

        UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        tx = HibernateUtil.beginTransaction();
        p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(1, p.getExperiment().getSamples().size());
        s = p.getExperiment().getSamples().iterator().next();
        assertNull(s.getDescription());
        assertEquals(0, s.getCharacteristics().size());
        tx.rollback();
    }

    @Test
    public void testFilters() {
        Transaction tx = HibernateUtil.beginTransaction();
        FilterClause searchFilterClause = new FilterClause();
        searchFilterClause.setClassName("*");
        SearchCriteria searchCriteria = new FilterClauseSearchCriteria(searchFilterClause);
        List<?> list = SecurityUtils.getAuthorizationManager().getObjects(searchCriteria);
        assertTrue(list.size() > 0);
        tx.commit();
    }

    @Test
    public void testSearchByCategory() {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        DAO_OBJECT.save(DUMMY_PROJECT_2);
        DAO_OBJECT.save(DUMMY_PROJECT_3);
        tx.commit();
        tx = HibernateUtil.beginTransaction();

        // test search all
        PageSortParams<Project> psp = new PageSortParams<Project>(20, 0, ProjectSortCriterion.PUBLIC_ID, false);
        List<Project> projects = DAO_OBJECT.searchByCategory(psp, "DummyExperiment", SearchCategory.values());
        assertEquals(3, projects.size());

        // test count
        assertEquals(3, DAO_OBJECT.searchCount("DummyExperiment", SearchCategory.values()));

        // test paging
        psp.setPageSize(2);
        projects = DAO_OBJECT.searchByCategory(psp, "DummyExperiment", SearchCategory.values());
        assertEquals(2, projects.size());

        psp.setIndex(2);
        psp.setSortCriterion(ProjectSortCriterion.TITLE);
        projects = DAO_OBJECT.searchByCategory(psp, "DummyExperiment", SearchCategory.values());
        assertEquals(1, projects.size());
        psp.setIndex(0);

        // test sorting
        psp.setPageSize(20);
        projects = DAO_OBJECT.searchByCategory(psp, "DummyExperiment", SearchCategory.values());
        assertTrue(DUMMY_PROJECT_3.equals(projects.get(0)));
        psp.setDesc(true);
        projects = DAO_OBJECT.searchByCategory(psp, "DummyExperiment", SearchCategory.values());
        assertTrue(DUMMY_PROJECT_3.equals(projects.get(2)));

        // test search by title
        projects = DAO_OBJECT.searchByCategory(psp, "DummyExperiment1", SearchCategory.EXPERIMENT_TITLE);
        assertTrue(DUMMY_PROJECT_1.equals(projects.get(0)));

        tx.commit();
    }

    @Test
    public void testGetProjectsForCurrentUser() {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        DAO_OBJECT.save(DUMMY_PROJECT_2);
        DAO_OBJECT.save(DUMMY_PROJECT_3);
        tx.commit();
        tx = HibernateUtil.beginTransaction();

        // test search all
        PageSortParams<Project> psp = new PageSortParams<Project>(20,0, ProjectSortCriterion.TITLE,false);
        List<Project> projects = DAO_OBJECT.getProjectsForCurrentUser(false, psp);
        assertEquals(3, projects.size());
        projects = DAO_OBJECT.getProjectsForCurrentUser(true, psp);
        assertEquals(0, projects.size());

        // test count
        assertEquals(3, DAO_OBJECT.getProjectCountForCurrentUser(false));
        assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser(true));

        // test paging
        psp.setPageSize(2);
        projects = DAO_OBJECT.getProjectsForCurrentUser(false, psp);
        assertEquals(2, projects.size());

        psp.setIndex(2);
        psp.setSortCriterion(ProjectSortCriterion.TITLE);
        projects = DAO_OBJECT.getProjectsForCurrentUser(false, psp);
        assertEquals(1, projects.size());
        assertEquals(DUMMY_EXPERIMENT_2.getTitle(), projects.get(0).getExperiment().getTitle());

        psp.setDesc(true);
        projects = DAO_OBJECT.getProjectsForCurrentUser(false, psp);
        assertEquals(1, projects.size());
        assertEquals(DUMMY_EXPERIMENT_3.getTitle(), projects.get(0).getExperiment().getTitle());

        tx.commit();
    }

    @Test
    public void testGetTermsForExperiment() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            Organism org = new Organism();
            org.setScientificName("Foo");
            org.setTermSource(DUMMY_TERM_SOURCE);
            Experiment e = new Experiment();
            e.setTitle("test title");
            e.setServiceType(ServiceType.FULL);
            e.setAssayTypeEnum(AssayType.ACGH);
            e.setManufacturer(new Organization());
            e.setOrganism(org);
            DAO_OBJECT.save(e);
            assertEquals(0, DAO_OBJECT.getCellTypesForExperiment(e).size());
            assertEquals(0, DAO_OBJECT.getDiseaseStatesForExperiment(e).size());
            assertEquals(0, DAO_OBJECT.getTissueSitesForExperiment(e).size());
            assertEquals(0, DAO_OBJECT.getMaterialTypesForExperiment(e).size());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetBioMaterialForExperiment() throws Exception {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            assertNull(DAO_OBJECT.getSourceForExperiment(null, "DummySource"));
            assertNull(DAO_OBJECT.getSampleForExperiment(null, "DummySample"));
            assertNull(DAO_OBJECT.getExtractForExperiment(null, "DummyExtract"));
            assertNull(DAO_OBJECT.getLabeledExtractForExperiment(null, "DummyLabeledExtract"));

            assertNull(DAO_OBJECT.getSourceForExperiment(DUMMY_EXPERIMENT_1, "NonExistentName"));
            assertNull(DAO_OBJECT.getSampleForExperiment(DUMMY_EXPERIMENT_1, "NonExistentName"));
            assertNull(DAO_OBJECT.getExtractForExperiment(DUMMY_EXPERIMENT_1, "NonExistentName"));
            assertNull(DAO_OBJECT.getLabeledExtractForExperiment(DUMMY_EXPERIMENT_1, "NonExistentName"));

            assertNotNull(DAO_OBJECT.getSourceForExperiment(DUMMY_EXPERIMENT_1, "DummySource"));
            assertNotNull(DAO_OBJECT.getSampleForExperiment(DUMMY_EXPERIMENT_1, "DummySample"));
            assertNotNull(DAO_OBJECT.getExtractForExperiment(DUMMY_EXPERIMENT_1, "DummyExtract"));
            assertNotNull(DAO_OBJECT.getLabeledExtractForExperiment(DUMMY_EXPERIMENT_1, "DummyLabeledExtract"));

            assertEquals(1, DAO_OBJECT.getUnfilteredSamplesForProject(DUMMY_PROJECT_1).size());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }


    private static class HasRolePredicate implements Predicate {
        private final String role;

        /**
         * @param role
         */
        public HasRolePredicate(String role) {
            super();
            this.role = role;
        }

        /**
         * {@inheritDoc}
         */
        public boolean evaluate(Object o) {
            UserGroupRoleProtectionGroup ugrpg = (UserGroupRoleProtectionGroup) o;
            return this.role.equals(ugrpg.getRole().getName());
        }
    }

    private static class IsGroupPredicate implements Predicate {
        /**
         * {@inheritDoc}
         */
        public boolean evaluate(Object o) {
            UserGroupRoleProtectionGroup ugrpg = (UserGroupRoleProtectionGroup) o;
            return ugrpg.getGroup() != null;
        }
    }
}
