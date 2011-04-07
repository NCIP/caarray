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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.Privileges;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.domain.search.AnnotationCriterion;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Transaction;
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

    /**
     * Tests retrieving the <code>Project</code> with the given id. Test encompasses save and delete of a
     * <code>Project</code>.
     */
    @Test
    public void testGetProject() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            final int size = DAO_OBJECT.getProjectCountForCurrentUser();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            Project retrievedProject = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertTrue(DUMMY_PROJECT_1.equals(retrievedProject));
            checkFiles(DUMMY_PROJECT_1, retrievedProject);
            assertTrue(compareExperiments(retrievedProject.getExperiment(), DUMMY_PROJECT_1.getExperiment()));
            assertEquals(size + 1, DAO_OBJECT.getProjectCountForCurrentUser());
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            retrievedProject = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            DAO_OBJECT.remove(retrievedProject);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            final Project deletedProject = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNull(deletedProject);
            assertEquals(size, DAO_OBJECT.getProjectCountForCurrentUser());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Project</code> with the given id. Test encompasses save and delete of a
     * <code>Project</code>.
     * 
     * @throws CSTransactionException
     */
    @Test
    public void testGetWorkspaceProjects() throws CSTransactionException {
        Transaction tx = null;

        Group group = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            DAO_OBJECT.save(DUMMY_ASSAYTYPE_1);
            DAO_OBJECT.save(DUMMY_ASSAYTYPE_2);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(ALL_BY_ID).size());
            tx.commit();

            CaArrayUsernameHolder.setUser("caarrayuser");
            tx = this.hibernateHelper.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser());
            tx.commit();

            CaArrayUsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = this.hibernateHelper.beginTransaction();
            Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.setLocked(false);
            tx.commit();

            CaArrayUsernameHolder.setUser("caarrayuser");
            tx = this.hibernateHelper.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser());
            tx.commit();

            CaArrayUsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = this.hibernateHelper.beginTransaction();
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            CaArrayUsernameHolder.setUser("caarrayuser");
            tx = this.hibernateHelper.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectCountForCurrentUser());
            tx.commit();

            CaArrayUsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = this.hibernateHelper.beginTransaction();
            final AuthorizationManager am = SecurityUtils.getAuthorizationManager();
            group = new Group();
            group.setGroupName("Foo");
            group.setGroupDesc("Collaborator Group");
            group.setApplication(SecurityUtils.getApplication());
            am.createGroup(group);
            final String[] groupMembers = { am.getUser("caarrayuser").getUserId().toString() };
            am.assignUsersToGroup(group.getGroupId().toString(), groupMembers);
            
            CollaboratorGroup cg = new CollaboratorGroup(group, CaArrayUsernameHolder.getCsmUser());
            COLLAB_DAO.save(cg);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.addGroupProfile(cg).setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            CaArrayUsernameHolder.setUser("caarrayuser");
            tx = this.hibernateHelper.beginTransaction();
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(ALL_BY_ID).size());
            tx.commit();

            CaArrayUsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = this.hibernateHelper.beginTransaction();
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.setLocked(true);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(ALL_BY_ID).size());
            tx.commit();

            CaArrayUsernameHolder.setUser("caarrayuser");
            tx = this.hibernateHelper.beginTransaction();
            assertEquals(1, DAO_OBJECT.getProjectsForCurrentUser(ALL_BY_ID).size());
            tx.commit();

            CaArrayUsernameHolder.setUser("biostatistician");
            tx = this.hibernateHelper.beginTransaction();
            assertEquals(0, DAO_OBJECT.getProjectsForCurrentUser(ALL_BY_ID).size());
            tx.commit();

            CaArrayUsernameHolder.setUser("caarrayadmin");

            tx = this.hibernateHelper.beginTransaction();
            Map<Long, Privileges> privilegeMap = SecurityUtils.getPrivileges(Arrays.asList(DUMMY_PROJECT_1), CaArrayUsernameHolder.getCsmUser());
            assertEquals(1, privilegeMap.size());
            Privileges privileges = privilegeMap.get(DUMMY_PROJECT_1.getId());
            assertNotNull(privileges);
            assertTrue(privileges.isBrowse());
            assertTrue(privileges.isRead());
            assertTrue(privileges.isWrite());
            assertTrue(privileges.isPermissions());
            tx.commit();

            CaArrayUsernameHolder.setUser("systemadministrator");
            tx = this.hibernateHelper.beginTransaction();
            privilegeMap = SecurityUtils.getPrivileges(Arrays.asList(DUMMY_PROJECT_1), CaArrayUsernameHolder.getCsmUser());
            assertEquals(1, privilegeMap.size());
            privileges = privilegeMap.get(DUMMY_PROJECT_1.getId());
            assertNotNull(privileges);
            assertTrue(privileges.isBrowse());
            assertTrue(privileges.isRead());
            assertTrue(privileges.isWrite());
            assertTrue(privileges.isPermissions());
            tx.commit();

            CaArrayUsernameHolder.setUser("caarrayuser");
            tx = this.hibernateHelper.beginTransaction();
            privilegeMap = SecurityUtils.getPrivileges(Arrays.asList(DUMMY_PROJECT_1), CaArrayUsernameHolder.getCsmUser());
            assertEquals(1, privilegeMap.size());
            privileges = privilegeMap.get(DUMMY_PROJECT_1.getId());
            assertNotNull(privileges);
            assertTrue(privileges.isBrowse());
            assertTrue(privileges.isRead());
            assertFalse(privileges.isWrite());
            assertFalse(privileges.isPermissions());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
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
        final Collection<ExperimentContact> contacts = retrievedInv.getExperimentContacts();
        if (contacts.isEmpty() || contacts.size() != 1) {
            return false;
        }
        final Iterator<ExperimentContact> i = contacts.iterator();
        final Person person = i.next().getContact();
        if (!DUMMY_PERSON.getFirstName().equals(person.getFirstName())) {
            return false;
        }
        // Annotations
        final Collection<Term> retrievedNormTypes = retrievedInv.getNormalizationTypes();
        if (retrievedNormTypes.isEmpty() || retrievedNormTypes.size() != 1) {
            return false;
        }
        final Iterator<Term> i2 = retrievedNormTypes.iterator();
        final Term retrievedNormType = i2.next();
        if (!DUMMY_NORMALIZATION_TYPE.getValue().equals(retrievedNormType.getValue())) {
            return false;
        }
        // Factors
        final Collection<Factor> factors = retrievedInv.getFactors();
        if (factors.isEmpty() || factors.size() != 2) {
            return false;
        }

        // Publications
        final Collection<Publication> publications = retrievedInv.getPublications();
        if (publications.isEmpty() || publications.size() != 2) {
            return false;
        }
        return true;
    }

    private void checkHybridizations(Experiment dummyInv, Experiment retrievedInv) {
        final LabeledExtract labeledExtract = retrievedInv.getLabeledExtracts().iterator().next();
        final Hybridization hybridization = labeledExtract.getHybridizations().iterator().next();
        assertEquals(labeledExtract, hybridization.getLabeledExtracts().iterator().next());
        final RawArrayData arrayData = hybridization.getRawDataCollection().iterator().next();
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
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PERSON);
            tx.commit();
            tx = this.hibernateHelper.beginTransaction();
            final Person examplePerson = new Person();
            examplePerson.setLastName(DUMMY_PERSON.getLastName());
            examplePerson.getAffiliations().add(DUMMY_ORGANIZATION);
            Person retrievedPerson = null;
            final List<Person> matchingPersons = DAO_OBJECT.queryEntityByExample(examplePerson);
            assertNotNull(matchingPersons);
            assertTrue(matchingPersons.size() > 0);
            retrievedPerson = matchingPersons.get(0);
            assertEquals("Retrieved person is different from saved person.", DUMMY_PERSON.getLastName(),
                    retrievedPerson.getLastName());
            assertEquals("Retrieved person is different from saved person.", DUMMY_PERSON.getFirstName(),
                    retrievedPerson.getFirstName());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search of person: " + e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testValidationMessages() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_ASSAYTYPE_1);
            DAO_OBJECT.save(DUMMY_ASSAYTYPE_2);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();
            tx = this.hibernateHelper.beginTransaction();
            final File file = new File("test/path/file.txt");
            DUMMY_FILE_1.setName(file.getName());
            final FileValidationResult result = new FileValidationResult();
            final ValidationMessage message1 = result.addMessage(ValidationMessage.Type.INFO, "info message");
            final ValidationMessage message2 = result.addMessage(ValidationMessage.Type.ERROR, "error message");
            DUMMY_FILE_1.setValidationResult(result);
            DAO_OBJECT.save(DUMMY_FILE_1);
            final CaArrayFile retrievedFile = DAO_OBJECT.queryEntityByExample(DUMMY_FILE_1).iterator().next();
            assertNotNull(retrievedFile.getValidationResult());
            final FileValidationResult retrievedResult = retrievedFile.getValidationResult();
            assertEquals(2, retrievedResult.getMessages().size());
            final ValidationMessage retrievedMesssage1 = retrievedResult.getMessages().get(0);
            final ValidationMessage retrievedMesssage2 = retrievedResult.getMessages().get(1);
            // order of messages should be swapped (ERRORs returned before INFOs)
            assertEquals(message2.getType(), retrievedMesssage1.getType());
            assertEquals(message2.getMessage(), retrievedMesssage1.getMessage());
            assertEquals(message1.getType(), retrievedMesssage2.getType());
            assertEquals(message1.getMessage(), retrievedMesssage2.getMessage());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search of person: " + e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testProtectionElements() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        this.hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_1);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_2);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        String str = "FROM " + ProtectionElement.class.getName() + " pe " + "WHERE pe.objectId = :objId "
                + "  AND pe.attribute = :attr " + "  AND pe.value = :value";
        Query q = this.hibernateHelper.getCurrentSession().createQuery(str);
        q.setParameter("objId", Project.class.getName());
        q.setParameter("attr", "id");
        q.setParameter("value", DUMMY_PROJECT_1.getId().toString());

        final ProtectionElement pe = (ProtectionElement) q.uniqueResult();
        assertNotNull(pe);
        assertEquals("id", pe.getAttribute());
        assertEquals(DUMMY_PROJECT_1.getId().toString(), pe.getValue());
        assertEquals(((User) pe.getOwners().iterator().next()).getLoginName(), CaArrayUsernameHolder.getUser());

        str = "FROM " + ProtectionGroup.class.getName() + " pg " + "WHERE :pe in elements(pg.protectionElements)";
        q = this.hibernateHelper.getCurrentSession().createQuery(str);
        q.setParameter("pe", pe);

        final ProtectionGroup pg = (ProtectionGroup) q.uniqueResult();
        assertNotNull(pg);
        assertEquals(pe, pg.getProtectionElements().iterator().next());
        tx.commit();
    }

    @Test
    public void testProjectPermissions() throws Exception {
        Group group = null;
        try {
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            Transaction tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            assertTrue(SecurityUtils.canWrite(DUMMY_PROJECT_1, CaArrayUsernameHolder.getCsmUser()));
            this.hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
            this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_1);
            this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_2);
            final Long experimentId = DUMMY_PROJECT_1.getExperiment().getId();
            tx.commit();
            assertNotNull(experimentId);
            assertEquals(SecurityLevel.NO_VISIBILITY, DUMMY_PROJECT_1.getPublicProfile().getSecurityLevel());

            // a new project, in default draft status, will not be visible to the anonymous user
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            tx = this.hibernateHelper.beginTransaction();
            Experiment e = SEARCH_DAO.retrieve(Experiment.class, experimentId);
            assertNull(e);
            assertFalse(SecurityUtils.canRead(DUMMY_PROJECT_1, CaArrayUsernameHolder.getCsmUser()));
            tx.commit();

            // after changing the project to visible, it can be browsed
            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.VISIBLE);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            this.hibernateHelper.getCurrentSession().clear();
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);

            assertFalse(SecurityUtils.canRead(p, CaArrayUsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(p, CaArrayUsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canModifyPermissions(p, CaArrayUsernameHolder.getCsmUser()));
            // browse policy no longer applied on post load, but only on remote api.

            // verify manually that it would do the right thing
            Set<SecurityPolicy> policies = p.getRemoteApiSecurityPolicies(CaArrayUsernameHolder.getCsmUser());
            assertEquals(1, policies.size());
            assertTrue(policies.contains(SecurityPolicy.BROWSE));
            SecurityPolicy.applySecurityPolicies(p, policies);
            policies = p.getExperiment().getRemoteApiSecurityPolicies(CaArrayUsernameHolder.getCsmUser());
            assertEquals(1, policies.size());
            assertTrue(policies.contains(SecurityPolicy.BROWSE));
            SecurityPolicy.applySecurityPolicies(p.getExperiment(), policies);
            assertNull(p.getExperiment().getDesignDescription());
            assertEquals(0, p.getFiles().size());
            this.hibernateHelper.getCurrentSession().clear();
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_1.getId()));
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_FILE_2.getId()));
            assertNull(SEARCH_DAO.retrieve(CaArrayFile.class, DUMMY_DATA_FILE.getId()));
            assertFalse(SecurityUtils.canRead(p, CaArrayUsernameHolder.getCsmUser()));
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertTrue(SecurityUtils.isOwner(p, CaArrayUsernameHolder.getCsmUser()));
            assertTrue(SecurityUtils.canWrite(DUMMY_SOURCE, CaArrayUsernameHolder.getCsmUser()));
            assertNotNull(p.getPublicProfile());
            // checkVisible(p);

            // e = SEARCH_DAO.retrieve(Experiment.class, experimentId);
            // assertNotNull(e);

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

            assertTrue(SecurityUtils.canRead(p, CaArrayUsernameHolder.getCsmUser()));
            assertTrue(SecurityUtils.canWrite(p, CaArrayUsernameHolder.getCsmUser()));
            assertTrue(SecurityUtils.canModifyPermissions(p, CaArrayUsernameHolder.getCsmUser()));

            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            assertFalse(SecurityUtils.canRead(p, CaArrayUsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(p, CaArrayUsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canModifyPermissions(p, CaArrayUsernameHolder.getCsmUser()));

            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p.getPublicProfile().setSecurityLevel(SecurityLevel.NO_VISIBILITY);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNull(p);
            e = SEARCH_DAO.retrieve(Experiment.class, experimentId);
            assertNull(e);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
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

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ_SELECTIVE);
            p.getPublicProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.NONE);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
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

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ_SELECTIVE);
            p.getPublicProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.READ);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);
            assertEquals(SecurityLevel.READ_SELECTIVE, p.getPublicProfile().getSecurityLevel());
            assertNotNull(p.getExperiment().getDesignDescription());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(1, p.getExperiment().getSamples().size());
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            final Sample s = new Sample();
            s.setName("New Sample");
            p.getExperiment().getSamples().add(s);
            s.setExperiment(p.getExperiment());
            DAO_OBJECT.save(p);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(1, p.getExperiment().getSamples().size());
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            final AuthorizationManager am = SecurityUtils.getAuthorizationManager();
            group = new Group();
            group.setGroupName("Foo");
            group.setGroupDesc("Collaborator Group");
            group.setApplication(SecurityUtils.getApplication());
            am.createGroup(group);
            final String[] groupMembers = { am.getUser("caarrayuser").getUserId().toString() };
            am.assignUsersToGroup(group.getGroupId().toString(), groupMembers);
            
            CollaboratorGroup cg = new CollaboratorGroup(group, CaArrayUsernameHolder.getCsmUser());
            COLLAB_DAO.save(cg);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            final AccessProfile groupProfile = p.addGroupProfile(cg);
            groupProfile.setSecurityLevel(SecurityLevel.READ_WRITE_SELECTIVE);
            groupProfile.getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.READ_WRITE);
            groupProfile.getSampleSecurityLevels().put(s, SampleSecurityLevel.READ);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser("caarrayuser");
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            assertTrue(SecurityUtils.canWrite(DUMMY_SAMPLE, CaArrayUsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(s, CaArrayUsernameHolder.getCsmUser()));
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(1, p.getExperiment().getSamples().size());
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.NO_VISIBILITY);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNull(p);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser("caarrayuser");
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            assertTrue(SecurityUtils.canWrite(DUMMY_SAMPLE, CaArrayUsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(s, CaArrayUsernameHolder.getCsmUser()));
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(STANDARD_USER);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            p.getPublicProfile().setSecurityLevel(SecurityLevel.VISIBLE);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            assertNotNull(p);
            assertNotNull(p.getExperiment().getAssayTypes());
            policies = p.getRemoteApiSecurityPolicies(CaArrayUsernameHolder.getCsmUser());
            assertEquals(1, policies.size());
            assertTrue(policies.contains(SecurityPolicy.BROWSE));
            SecurityPolicy.applySecurityPolicies(p, policies);
            policies = p.getExperiment().getRemoteApiSecurityPolicies(CaArrayUsernameHolder.getCsmUser());
            assertEquals(1, policies.size());
            assertTrue(policies.contains(SecurityPolicy.BROWSE));
            SecurityPolicy.applySecurityPolicies(p.getExperiment(), policies);
            assertNull(p.getExperiment().getDesignDescription());
            this.hibernateHelper.getCurrentSession().clear();
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            CaArrayUsernameHolder.setUser("caarrayuser");
            p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            // because Exp.samples is extra lazy, must initialize it explicitly to verify security
            Hibernate.initialize(p.getExperiment().getSamples());
            assertEquals(2, p.getExperiment().getSamples().size());
            assertTrue(SecurityUtils.canWrite(DUMMY_SAMPLE, CaArrayUsernameHolder.getCsmUser()));
            assertFalse(SecurityUtils.canWrite(s, CaArrayUsernameHolder.getCsmUser()));
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
        Transaction tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        this.hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_1);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_2);
        tx.commit();

        // check initial settings.. drafts should be not visible
        tx = this.hibernateHelper.beginTransaction();
        final Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        List<UserGroupRoleProtectionGroup> list = SecurityUtils.getUserGroupRoleProtectionGroups(p);
        assertEquals(8, list.size()); // expect the user-only ones only
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
        tx = this.hibernateHelper.beginTransaction();
        list = SecurityUtils.getUserGroupRoleProtectionGroups(p);
        assertEquals(9, list.size()); // expect the user-only ones and the anonymous access one
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
        Transaction tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        this.hibernateHelper.getCurrentSession().save(DUMMY_PROJECT_1);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_1);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAYTYPE_2);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        Project p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(SecurityLevel.NO_VISIBILITY, p.getPublicProfile().getSecurityLevel());
        p.getPublicProfile().setSecurityLevel(SecurityLevel.NO_VISIBILITY);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        p = SEARCH_DAO.retrieve(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(SecurityLevel.NO_VISIBILITY, p.getPublicProfile().getSecurityLevel());
        tx.commit();
    }

    @Test
    public void testFilters() {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final FilterClause searchFilterClause = new FilterClause();
        searchFilterClause.setClassName("*");
        final SearchCriteria searchCriteria = new FilterClauseSearchCriteria(searchFilterClause);
        final List<?> list = SecurityUtils.getAuthorizationManager().getObjects(searchCriteria);
        assertTrue(list.size() > 0);
        tx.commit();
    }

    @Test
    public void testSearchByCategory() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        DAO_OBJECT.save(DUMMY_PROJECT_2);
        DAO_OBJECT.save(DUMMY_PROJECT_3);
        DAO_OBJECT.save(DUMMY_ASSAYTYPE_1);
        DAO_OBJECT.save(DUMMY_ASSAYTYPE_2);
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();

        // test search all
        final PageSortParams<Project> psp = new PageSortParams<Project>(20, 0, ProjectSortCriterion.PUBLIC_ID, false);
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

        // test search by description
        projects = DAO_OBJECT.searchByCategory(psp, "Desc", SearchCategory.EXPERIMENT_DESCRIPTION);
        assertTrue(DUMMY_PROJECT_1.equals(projects.get(0)));

        // test search by pubmedid.
        testSearchByPubmedId();

        // test search by publication authors.
        testSearchByPublicationAuthors();

        tx.commit();
    }

    private void testSearchByPubmedId() {
        PageSortParams<Project> psp = new PageSortParams<Project>(20, 0, ProjectSortCriterion.PUBLIC_ID, false);
        List<Project> projects = null;
        Set<Publication> pubs = null;

        // test search by pubmedid
        projects  = DAO_OBJECT.searchByCategory(psp, "DummyPubMedId1", SearchCategory.PUBMED_ID);
        assertEquals(1, projects.size());
        assertTrue(DUMMY_PROJECT_1.equals(projects.get(0)));
        pubs = projects.get(0).getExperiment().getPublications();
        assertEquals(2, pubs.size());

        projects  = DAO_OBJECT.searchByCategory(psp, "DummyPubMedId1", SearchCategory.PUBMED_ID);
        assertEquals(1, projects.size());
        assertTrue(DUMMY_PROJECT_1.equals(projects.get(0)));
        pubs = projects.get(0).getExperiment().getPublications();
        assertEquals(2, pubs.size());

        projects  = DAO_OBJECT.searchByCategory(psp, "PubMed", SearchCategory.PUBMED_ID);
        assertEquals(1, projects.size());
    }

    private void testSearchByPublicationAuthors() {
        PageSortParams<Project> psp = new PageSortParams<Project>(20, 0, ProjectSortCriterion.PUBLIC_ID, false);
        List<Project> projects = null;
        Set<Publication> pubs = null;

        // test search by publication authors
        projects  = DAO_OBJECT.searchByCategory(psp, "DummyAuthors1", SearchCategory.PUBLICATION_AUTHOR);
        assertEquals(1, projects.size());
        assertTrue(DUMMY_PROJECT_1.equals(projects.get(0)));
        pubs = projects.get(0).getExperiment().getPublications();
        assertEquals(2, pubs.size());

        projects  = DAO_OBJECT.searchByCategory(psp, "DummyAuthors2", SearchCategory.PUBLICATION_AUTHOR);
        assertEquals(1, projects.size());
        assertTrue(DUMMY_PROJECT_1.equals(projects.get(0)));
        pubs = projects.get(0).getExperiment().getPublications();
        assertEquals(2, pubs.size());

        projects  = DAO_OBJECT.searchByCategory(psp, "uthors", SearchCategory.PUBLICATION_AUTHOR);
        assertEquals(1, projects.size());
    }

    @Test
    public void testSearchByCriteria() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        DUMMY_SAMPLE.setDiseaseState(DUMMY_NORMALIZATION_TYPE);
        DUMMY_SOURCE.setTissueSite(DUMMY_REPLICATE_TYPE);
        DUMMY_SOURCE.setCellType(DUMMY_FACTOR_TYPE_1);
        saveSupportingObjects();
        DAO_OBJECT.save(DUMMY_ASSAYTYPE_1);
        DAO_OBJECT.save(DUMMY_ASSAYTYPE_2);
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        DAO_OBJECT.save(DUMMY_PROJECT_2);
        DAO_OBJECT.save(DUMMY_PROJECT_3);
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();

        // test search by title
        final PageSortParams<Experiment> psp = new PageSortParams<Experiment>(20, 0,
                new AdHocSortCriterion<Experiment>("title"), false);
        ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
        crit.setTitle(DUMMY_EXPERIMENT_1.getTitle());
        List<Experiment> experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(1, experiments.size());
        assertEquals(DUMMY_EXPERIMENT_1, experiments.get(0));

        // test search by assay type
        crit = new ExperimentSearchCriteria();
        crit.setAssayType(DUMMY_ASSAYTYPE_1);
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(2, experiments.size());
        assertEquals(DUMMY_EXPERIMENT_1, experiments.get(0));
        assertEquals(DUMMY_EXPERIMENT_2, experiments.get(1));

        // test search by assay type and public id
        crit = new ExperimentSearchCriteria();
        crit.setPublicIdentifier(DUMMY_EXPERIMENT_2.getPublicIdentifier());
        crit.setAssayType(DUMMY_ASSAYTYPE_1);
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(1, experiments.size());
        assertEquals(DUMMY_EXPERIMENT_2, experiments.get(0));

        // test search by manufacturer
        crit = new ExperimentSearchCriteria();
        crit.setArrayProvider(DUMMY_PROVIDER);
        crit.setOrganism(DUMMY_ORGANISM);
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(2, experiments.size());
        assertEquals(DUMMY_EXPERIMENT_3, experiments.get(0));
        assertEquals(DUMMY_EXPERIMENT_1, experiments.get(1));

        // test annotation criteria
        crit = new ExperimentSearchCriteria();
        final Category ds = new Category();
        ds.setName(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName());
        crit.getAnnotationCriterions().add(new AnnotationCriterion(ds, DUMMY_NORMALIZATION_TYPE.getValue()));
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(1, experiments.size());
        assertEquals(DUMMY_EXPERIMENT_1, experiments.get(0));

        final Category ts = new Category();
        ts.setName(ExperimentOntologyCategory.ORGANISM_PART.getCategoryName());
        crit.getAnnotationCriterions().add(new AnnotationCriterion(ts, DUMMY_REPLICATE_TYPE.getValue()));
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(1, experiments.size());
        assertEquals(DUMMY_EXPERIMENT_1, experiments.get(0));

        crit.getAnnotationCriterions().add(new AnnotationCriterion(ts, DUMMY_FACTOR_TYPE_1.getValue()));
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(1, experiments.size());
        assertEquals(DUMMY_EXPERIMENT_1, experiments.get(0));

        final Category ct = new Category();
        ct.setName(ExperimentOntologyCategory.CELL_TYPE.getCategoryName());
        crit.getAnnotationCriterions().add(new AnnotationCriterion(ct, DUMMY_REPLICATE_TYPE.getValue()));
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        assertEquals(0, experiments.size());

        // test principal investigator criteria
        crit = new ExperimentSearchCriteria();
        crit.getPrincipalInvestigators().add(DUMMY_PERSON);
        experiments = DAO_OBJECT.searchByCriteria(psp, crit);
        // DUMMY_PERSON is not a PI.
        assertTrue(experiments.isEmpty());

        tx.commit();
    }

    @Test
    public void testGetProjectsForCurrentUser() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        DAO_OBJECT.save(DUMMY_PROJECT_2);
        DAO_OBJECT.save(DUMMY_PROJECT_3);
        DAO_OBJECT.save(DUMMY_ASSAYTYPE_1);
        DAO_OBJECT.save(DUMMY_ASSAYTYPE_2);
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();

        // test search all
        final PageSortParams<Project> psp = new PageSortParams<Project>(20, 0, ProjectSortCriterion.TITLE, false);
        List<Project> projects = DAO_OBJECT.getProjectsForCurrentUser(psp);
        assertEquals(3, projects.size());

        // test count
        assertEquals(3, DAO_OBJECT.getProjectCountForCurrentUser());

        // test paging
        psp.setPageSize(2);
        projects = DAO_OBJECT.getProjectsForCurrentUser(psp);
        assertEquals(2, projects.size());

        psp.setIndex(2);
        psp.setSortCriterion(ProjectSortCriterion.TITLE);
        projects = DAO_OBJECT.getProjectsForCurrentUser(psp);
        assertEquals(1, projects.size());
        assertEquals(DUMMY_EXPERIMENT_2.getTitle(), projects.get(0).getExperiment().getTitle());

        psp.setDesc(true);
        projects = DAO_OBJECT.getProjectsForCurrentUser(psp);
        assertEquals(1, projects.size());
        assertEquals(DUMMY_EXPERIMENT_3.getTitle(), projects.get(0).getExperiment().getTitle());

        tx.commit();
    }

    @Test
    public void testGetProjectsForOwner() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        DAO_OBJECT.save(DUMMY_PROJECT_2);
        DAO_OBJECT.save(DUMMY_PROJECT_3);
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();
        User u = CaArrayUsernameHolder.getCsmUser();
        List<Project> projects = DAO_OBJECT.getProjectsForOwner(u);
        assertEquals(3, projects.size());
        tx.commit();
    }

    @Test
    public void testGetTermsForExperiment() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            final Organism org = new Organism();
            org.setScientificName("Foo");
            org.setTermSource(DUMMY_TERM_SOURCE);
            final Experiment e = new Experiment();
            e.setTitle("test title");
            final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
            assayTypes.add(DUMMY_ASSAYTYPE_1);
            e.setAssayTypes(assayTypes);
            e.setManufacturer(new Organization());
            e.setOrganism(org);
            DAO_OBJECT.save(e);
            DAO_OBJECT.save(DUMMY_ASSAYTYPE_1);
            DAO_OBJECT.save(DUMMY_ASSAYTYPE_2);
            assertEquals(0, DAO_OBJECT.getCellTypesForExperiment(e).size());
            assertEquals(0, DAO_OBJECT.getDiseaseStatesForExperiment(e).size());
            assertEquals(0, DAO_OBJECT.getTissueSitesForExperiment(e).size());
            assertEquals(0, DAO_OBJECT.getMaterialTypesForExperiment(e).size());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetBioMaterialsForExperiment() throws Exception {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
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

            final Set<AbstractBioMaterial> bms = DAO_OBJECT
                    .getUnfilteredBiomaterialsForProject(DUMMY_PROJECT_1.getId());
            final int size = bms.size();
            assertEquals(4, size);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetProjectsWithReImportable() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        final CaArrayFile f1 = new CaArrayFile();
        f1.setProject(DUMMY_PROJECT_1);
        f1.setName("foo");
        f1.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        f1.setFileType(AFFYMETRIX_CEL);
        f1.setDataHandle(DUMMY_HANDLE);
        DUMMY_PROJECT_1.getFiles().add(f1);
        DAO_OBJECT.save(f1);

        final CaArrayFile f2 = new CaArrayFile();
        f2.setProject(DUMMY_PROJECT_2);
        f2.setName("bar");
        f2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        f2.setFileType(AFFYMETRIX_DAT);
        f2.setDataHandle(DUMMY_HANDLE);
        DUMMY_PROJECT_2.getFiles().add(f2);
        DAO_OBJECT.save(f2);
        DAO_OBJECT.save(DUMMY_PROJECT_2);

        tx.commit();
        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().evict(DUMMY_PROJECT_1);
        this.hibernateHelper.getCurrentSession().evict(DUMMY_PROJECT_2);
        DUMMY_PROJECT_1 = (Project) this.hibernateHelper.getCurrentSession().load(Project.class,
                DUMMY_PROJECT_1.getId());
        DUMMY_PROJECT_2 = (Project) this.hibernateHelper.getCurrentSession().load(Project.class,
                DUMMY_PROJECT_2.getId());
        assertTrue(f1.isUnparsedAndReimportable());
        assertTrue(DUMMY_PROJECT_1.isUnparsedAndReimportable());
        assertFalse(f2.isUnparsedAndReimportable());
        assertFalse(DUMMY_PROJECT_2.isUnparsedAndReimportable());

        final List<Project> projects = DAO_OBJECT.getProjectsWithReImportable();
        assertEquals(1, projects.size());
        assertEquals(DUMMY_PROJECT_1.getId(), projects.get(0).getId());

        tx.commit();
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
        @Override
        public boolean evaluate(Object o) {
            final UserGroupRoleProtectionGroup ugrpg = (UserGroupRoleProtectionGroup) o;
            return this.role.equals(ugrpg.getRole().getName());
        }
    }

    private static class IsGroupPredicate implements Predicate {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean evaluate(Object o) {
            final UserGroupRoleProtectionGroup ugrpg = (UserGroupRoleProtectionGroup) o;
            return ugrpg.getGroup() != null;
        }
    }
}
