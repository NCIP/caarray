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

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.hibernate.Transaction;

import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Investigation;
import gov.nih.nci.caarray.domain.project.InvestigationContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;

/**
 * Unit tests for the Project DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProjectDaoTest {
    private static final Log LOG = LogFactory.getLog(ProjectDaoTest.class);

    // Investigation
    private static final String UNCHECKED = "unchecked";
    private static final Long DUMMY_START_ID = new Long(150);
    private static final Project DUMMY_PROJECT_1 = new Project();
    private static final Investigation DUMMY_INVESTIGATION_1 = new Investigation();
    private static final Source DUMMY_SOURCE = new Source();
    private static final Category DUMMY_CATEGORY = new Category();

    // Contacts
    private static final InvestigationContact DUMMY_INVESTIGATION_CONTACT = new InvestigationContact();
    private static final Person DUMMY_PERSON = new Person();
    private static final Organization DUMMY_ORGANIZATION = new Organization();

    // Annotations
    private static final Term DUMMY_REPLICATE_TYPE = new Term();
    private static final Term DUMMY_NORMALIZATION_TYPE = new Term();
    private static final Term DUMMY_QUALITY_CTRL_TYPE = new Term();

    // Factors
    private static final Term DUMMY_FACTOR_TYPE_1 = new Term();
    private static final Term DUMMY_FACTOR_TYPE_2 = new Term();
    private static final Factor DUMMY_FACTOR_1 = new Factor();
    private static final Factor DUMMY_FACTOR_2 = new Factor();

    // Publications
    private static final Publication DUMMY_PUBLICATION_1 = new Publication();
    private static final Publication DUMMY_PUBLICATION_2 = new Publication();
    private static final Term DUMMY_PUBLICATION_STATUS = new Term();

    private static final ProjectDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getProjectDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        long id = DUMMY_START_ID;
        setInvestigationSummary(id);
        setInvestigationContacts();
        DUMMY_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_CATEGORY.setName("Dummy Category");
        setInvestigationAnnotations();
        setExperimentalFactors();
        setPublications();
        DUMMY_PROJECT_1.setId(id);
        DUMMY_PROJECT_1.setExperiment(DUMMY_INVESTIGATION_1);
    }

    private static void setInvestigationSummary(long id) {
        DUMMY_INVESTIGATION_1.setId(id);
        DUMMY_INVESTIGATION_1.setTitle("DummyInvestigation1");
        DUMMY_INVESTIGATION_1.setDescription("DummyInvestigation1Desc");
        Date currDate = new Date();
        DUMMY_INVESTIGATION_1.setDateOfExperiment(currDate);
        DUMMY_INVESTIGATION_1.setPublicReleaseDate(currDate);
    }

    @SuppressWarnings(UNCHECKED)
    private static void setInvestigationContacts() {
        DUMMY_ORGANIZATION.setName("DummyOrganization1");
        DUMMY_PERSON.setFirstName("DummyFirstName1");
        DUMMY_PERSON.setLastName("DummyLastName1");
        DUMMY_PERSON.getAffiliations().add(DUMMY_ORGANIZATION);
        DUMMY_INVESTIGATION_CONTACT.setContact(DUMMY_PERSON);
        DUMMY_INVESTIGATION_1.getInvestigationContacts().add(DUMMY_INVESTIGATION_CONTACT);
    }

    @SuppressWarnings(UNCHECKED)
    private static void setInvestigationAnnotations() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_INVESTIGATION_1.getReplicateTypes().add(DUMMY_REPLICATE_TYPE);
        DUMMY_INVESTIGATION_1.getNormalizationTypes().add(DUMMY_NORMALIZATION_TYPE);
        DUMMY_INVESTIGATION_1.getQualityControlTypes().add(DUMMY_QUALITY_CTRL_TYPE);
    }

    @SuppressWarnings(UNCHECKED)
    private static void setExperimentalFactors() {
        DUMMY_FACTOR_TYPE_1.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_1.setSource(DUMMY_SOURCE);
        DUMMY_FACTOR_TYPE_1.setValue("Dummy Factor Type 1");
        DUMMY_FACTOR_TYPE_2.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_2.setSource(DUMMY_SOURCE);
        DUMMY_FACTOR_TYPE_2.setValue("Dummy Factor Type 2");
        DUMMY_FACTOR_1.setName("Dummy Factor 1");
        DUMMY_FACTOR_1.setType(DUMMY_FACTOR_TYPE_1);
        DUMMY_FACTOR_2.setName("Dummy Factor 2");
        DUMMY_FACTOR_2.setType(DUMMY_FACTOR_TYPE_2);
        DUMMY_INVESTIGATION_1.getFactors().add(DUMMY_FACTOR_1);
        DUMMY_INVESTIGATION_1.getFactors().add(DUMMY_FACTOR_2);
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
        DUMMY_PUBLICATION_STATUS.setSource(DUMMY_SOURCE);
        DUMMY_PUBLICATION_STATUS.setValue("Dummy Status: Published");
        DUMMY_PUBLICATION_1.setStatus(DUMMY_PUBLICATION_STATUS);
        DUMMY_PUBLICATION_2.setStatus(DUMMY_PUBLICATION_STATUS);

        DUMMY_INVESTIGATION_1.getPublications().add(DUMMY_PUBLICATION_1);
        DUMMY_INVESTIGATION_1.getPublications().add(DUMMY_PUBLICATION_2);
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
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();
            Project retrievedProject = DAO_OBJECT.getProject(DUMMY_PROJECT_1.getId());
            if (DUMMY_PROJECT_1.equals(retrievedProject)) {
                if (compareInvestigations(retrievedProject.getExperiment(), DUMMY_PROJECT_1.getExperiment())) {
                    // The retrieved project is the same as the saved project. Test passed.
                    assertTrue(true);
                }
            } else {
                fail("Retrieved project is different from saved project.");
            }
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of project: " + e.getMessage());
        } finally {
            cleanUpProject();
        }
    }

    /**
     * Compare 2 investigations to check if they are the same.
     *
     * @return true if the 2 investigations are the same and false otherwise.
     */
    @SuppressWarnings("PMD")
    private boolean compareInvestigations(Investigation retrievedInv, Investigation dummyInv) {
        // Investigation summary.
        if (!dummyInv.getTitle().equals(retrievedInv.getTitle())) {
            return false;
        }
        // Contacts
        Collection contacts = retrievedInv.getInvestigationContacts();
        if (contacts.isEmpty() || contacts.size() != 1) {
            return false;
        }
        Iterator i = contacts.iterator();
        Person person = (Person) ((InvestigationContact) i.next()).getContact();
        if (!DUMMY_PERSON.getFirstName().equals(person.getFirstName())) {
            return false;
        }
        // Annotations
        Collection retrievedNormTypes = retrievedInv.getNormalizationTypes();
        if (retrievedNormTypes.isEmpty() || retrievedNormTypes.size() != 1) {
            return false;
        }
        i = retrievedNormTypes.iterator();
        Term retrievedNormType = (Term) i.next();
        if (!DUMMY_NORMALIZATION_TYPE.getValue().equals(retrievedNormType.getValue())) {
            return false;
        }
        // Factors
        Collection factors = retrievedInv.getFactors();
        if (factors.isEmpty() || factors.size() != 2) {
            return false;
        }

        // Publications
        Collection publications = retrievedInv.getPublications();
        if (publications.isEmpty() || publications.size() != 2) {
            return false;
        }
        return true;
    }

    /**
     * Clean up after a test by removing the dummy project.
     */
    @SuppressWarnings("PMD")
    private void cleanUpProject() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.remove(DUMMY_PROJECT_1);
            DAO_OBJECT.remove(DUMMY_INVESTIGATION_CONTACT);
            DAO_OBJECT.remove(DUMMY_INVESTIGATION_1);
            DAO_OBJECT.remove(DUMMY_PERSON);
            DAO_OBJECT.remove(DUMMY_ORGANIZATION);

            DAO_OBJECT.remove(DUMMY_REPLICATE_TYPE);
            DAO_OBJECT.remove(DUMMY_NORMALIZATION_TYPE);
            DAO_OBJECT.remove(DUMMY_QUALITY_CTRL_TYPE);

            DAO_OBJECT.remove(DUMMY_FACTOR_1);
            DAO_OBJECT.remove(DUMMY_FACTOR_2);
            DAO_OBJECT.remove(DUMMY_FACTOR_TYPE_1);
            DAO_OBJECT.remove(DUMMY_FACTOR_TYPE_2);

            DAO_OBJECT.remove(DUMMY_PUBLICATION_1);
            DAO_OBJECT.remove(DUMMY_PUBLICATION_2);
            DAO_OBJECT.remove(DUMMY_PUBLICATION_STATUS);

            DAO_OBJECT.remove(DUMMY_SOURCE);
            DAO_OBJECT.remove(DUMMY_CATEGORY);
            tx.commit();
        } catch (DAOException deleteException) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.error("Error cleaning up dummy project.", deleteException);
            fail("DAO exception during deletion of project: " + deleteException.getMessage());
        }
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
            DAO_OBJECT.save(DUMMY_PERSON);
            tx.commit();
            Person examplePerson = new Person();
            examplePerson.setLastName(DUMMY_PERSON.getLastName());
            examplePerson.getAffiliations().add(DUMMY_ORGANIZATION);
            Person retrievedPerson = null;
            List<AbstractCaArrayEntity> matchingPersons =
                DAO_OBJECT.queryEntityAndAssociationsByExample(examplePerson);
            if ((matchingPersons != null) && (matchingPersons.size() >= 1)) {
                retrievedPerson = (Person) matchingPersons.get(0);
            }
            if (DUMMY_PERSON.equals(retrievedPerson)) {
                // The retrieved person is the same as the saved person. Test passed.
                assertTrue(true);
            } else {
                fail("Retrieved person is different from saved person.");
            }
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search of person: " + e.getMessage());
        } finally {
            cleanUpDeepSearch();
        }
    }

    /**
     * Clean up after deep search test by removing the dummy person.
     */
    private void cleanUpDeepSearch() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.remove(DUMMY_PERSON);
            DAO_OBJECT.remove(DUMMY_ORGANIZATION);
            tx.commit();
        } catch (DAOException deleteException) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.error("Error cleaning up dummy person.", deleteException);
            fail("DAO exception during deletion of person: " + deleteException.getMessage());
        }
    }
}
