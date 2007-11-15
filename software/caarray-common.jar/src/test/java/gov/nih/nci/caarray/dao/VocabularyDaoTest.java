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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.vocabulary.Accession;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Vocabulary DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class VocabularyDaoTest extends AbstractDaoTest {

    private static Category DUMMY_CATEGORY_1 = new Category();
    private static Category DUMMY_CATEGORY_2 = new Category();
    private static Category DUMMY_CATEGORY_3 = new Category();
    private static Category DUMMY_CATEGORY_4 = new Category();
    private static final int NUM_DUMMY_TERMS = 3;
    private static Term DUMMY_TERM_1 = new Term();
    private static Term DUMMY_TERM_2 = new Term();
    private static Term DUMMY_TERM_3 = new Term();
    private static TermSource DUMMY_SOURCE_1 = new TermSource();
    private static TermSource DUMMY_SOURCE_2 = new TermSource();
    private static Accession DUMMY_ACCESSION_1 = new Accession();
    private static Accession DUMMY_ACCESSION_2 = new Accession();

    private static final VocabularyDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getVocabularyDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        // Initialize all the dummy objects needed for the tests.
        initializeCategoriesAndTerms();
        initializeSourcesAndAccessions();
    }

    /**
     * Initialize the dummy <code>Category</code> and <code>Term</code> objects.
     */
    @SuppressWarnings("unchecked")
    private static void initializeCategoriesAndTerms() {
        DUMMY_CATEGORY_1 = new Category();
        DUMMY_CATEGORY_2 = new Category();
        DUMMY_CATEGORY_3 = new Category();
        DUMMY_CATEGORY_4 = new Category();
        DUMMY_TERM_1 = new Term();
        DUMMY_TERM_2 = new Term();
        DUMMY_TERM_3 = new Term();
        DUMMY_CATEGORY_1.setName("DummyTestCategory1");
        DUMMY_CATEGORY_2.setName("DummyTestCategory2");
        DUMMY_CATEGORY_3.setName("DummyTestCategory3");
        DUMMY_CATEGORY_4.setName("DummyTestCategory4");
        DUMMY_CATEGORY_3.setParent(DUMMY_CATEGORY_4);
        DUMMY_CATEGORY_4.getChildren().add(DUMMY_CATEGORY_3);

        DUMMY_TERM_1.setDescription("DummyTestTerm1");
        DUMMY_TERM_1.setCategory(DUMMY_CATEGORY_3);
        DUMMY_TERM_2.setDescription("DummyTestTerm2");
        DUMMY_TERM_2.setCategory(DUMMY_CATEGORY_3);
        DUMMY_TERM_3.setDescription("DummyTestTerm3");
        DUMMY_TERM_3.setCategory(DUMMY_CATEGORY_4);
    }

    /**
     * Initialize the dummy <code>Source</code> and <code>Accession</code> objects.
     */
    private static void initializeSourcesAndAccessions() {
        DUMMY_SOURCE_1 = new TermSource();
        DUMMY_SOURCE_2 = new TermSource();
        DUMMY_ACCESSION_1 = new Accession();
        DUMMY_ACCESSION_2 = new Accession();
        DUMMY_SOURCE_1.setName("DummyTestSource1");
        DUMMY_SOURCE_1.setUrl("DummyUrlForSource1");
        DUMMY_SOURCE_1.setVersion("1.0");
        DUMMY_SOURCE_2.setName("DummyTestSource2");
        DUMMY_SOURCE_2.setUrl("DummyUrlForSource2");
        DUMMY_SOURCE_2.setVersion("1.0");

        DUMMY_ACCESSION_1.setUrl("DummyUrlForAccession1");
        DUMMY_ACCESSION_1.setValue("DummyValueForAccession1");
        DUMMY_ACCESSION_1.setSource(DUMMY_SOURCE_1);
        DUMMY_ACCESSION_2.setUrl("DummyUrlForAccession2");
        DUMMY_ACCESSION_2.setValue("DummyValueForAccession2");
        DUMMY_ACCESSION_2.setSource(DUMMY_SOURCE_1);
    }

    /**
     * Tests retrieving all <code>Term</code>s in a given <code>Category</code>.
     */
    @Test
    public void testGetTerms() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            setupTestGetTerms();
            List<Term> retrievedTerms = DAO_OBJECT.getTerms(DUMMY_CATEGORY_3.getName());
            if (retrievedTerms.size() != 2) {
                fail("Did not retrieve the expected number of terms.");
            }
            // Check if we got the expected terms, and accordingly pass or fail the test.
            checkIfExpectedTerms(retrievedTerms);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception while getting terms in a category: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving all <code>Term</code>s in a given <code>Category</code> recursively.
     */
    @Test
    public void testGetTermsRecursive() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            setupTestGetTermsRecursive();
            tx.commit();
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            Set<Term> retrievedTerms = DAO_OBJECT.getTermsRecursive(DUMMY_CATEGORY_4.getName(), null);
            if (retrievedTerms.size() != NUM_DUMMY_TERMS) {
                fail("Did not retrieve the expected number of terms.");
            }
            // Check if we got the expected terms, and accordingly pass or fail the test.
            checkIfExpectedTermsRecursive(retrievedTerms);
            tx.commit();
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception while getting terms in a category: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving terms by id recursively.
     */
    @Test
    public void testGetTermsById() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            setupTestGetTermsRecursive();
            Term retrievedTerm1 = DAO_OBJECT.getTermById(DUMMY_TERM_1.getId());
            Term retrievedTerm2 = DAO_OBJECT.getTermById(DUMMY_TERM_2.getId());
            Term retrievedTerm3 = DAO_OBJECT.getTermById(DUMMY_TERM_3.getId());
            tx.commit();
            assertEquals(DUMMY_TERM_1, retrievedTerm1);
            assertEquals(DUMMY_TERM_2, retrievedTerm2);
            assertEquals(DUMMY_TERM_3, retrievedTerm3);
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception while getting terms in a category: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving <code>Category</code> with the given name.
     */
    @Test
    public void testGetCategory() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(DUMMY_CATEGORY_1);
            String categoryName = DUMMY_CATEGORY_1.getName();
            Category retrievedCategory = DAO_OBJECT.getCategory(categoryName);
            if ((retrievedCategory != null) && (categoryName.equals(retrievedCategory.getName()))) {
                assertTrue(true);
            } else {
                fail("Did not retrieve the expected category.");
            }
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception while getting category by name: " + e.getMessage());
        }
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Category</code>.
     */
    @Test
    public void testCategoryCrud() {
        Transaction tx = null;

        // Try saving the dummy category and then retrieving it.
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(DUMMY_CATEGORY_1);
            // Check if we got the expected category, and accordingly pass or fail the test.
            checkIfExpectedCategory(DUMMY_CATEGORY_1.getId());
            tx.commit();
        } catch (DAOException saveException) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of category: " + saveException.getMessage());
        }
    }

    /**
     * Tests saving a <code>Category</code> collection.
     */
    @Test
    public void testSaveCategoryCollection() {
        Transaction tx = null;
        List<Category> categoryList = new ArrayList<Category>();
        categoryList.add(DUMMY_CATEGORY_1);
        categoryList.add(DUMMY_CATEGORY_2);
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(categoryList);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save of category collection: " + e.getMessage());
        }
        assertTrue(true);
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Term</code>.
     */
    @Test
    public void testTermCrud() {
        Transaction tx = null;
        // Try saving the dummy term and then retrieving it.
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(DUMMY_TERM_1);
            // Check if we got the expected term, and accordingly pass or fail the test.
            checkIfExpectedTerm(DUMMY_TERM_1.getId());
            tx.commit();
        } catch (DAOException saveException) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of term: " + saveException.getMessage());
        }
    }

    /**
     * Tests saving a <code>Term</code> collection.
     */
    @Test
    public void testSaveTermCollection() {
        Transaction tx = null;
        List<Term> termList = new ArrayList<Term>();
        termList.add(DUMMY_TERM_1);
        termList.add(DUMMY_TERM_2);
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(termList);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save of term collection: " + e.getMessage());
        }
        assertTrue(true);
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Source</code>.
     */
    @Test
    public void testSourceCrud() {
        Transaction tx = null;
        // Try saving the dummy source and then retrieving it.
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(DUMMY_SOURCE_1);
            // Check if we got the expected source, and accordingly pass or fail the test.
            checkIfExpectedSource(DUMMY_SOURCE_1.getId());
            tx.commit();
        } catch (DAOException saveException) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of source: " + saveException.getMessage());
        }
    }

    /**
     * Tests saving a <code>Source</code> collection.
     */
    @Test
    public void testSaveSourceCollection() {
        Transaction tx = null;
        List<TermSource> sourceList = new ArrayList<TermSource>();
        sourceList.add(DUMMY_SOURCE_1);
        sourceList.add(DUMMY_SOURCE_2);
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(sourceList);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save of source collection: " + e.getMessage());
        }
        assertTrue(true);
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Accession</code>.
     * Relies on cascading save-update being set in the Hibernate mapping.
     */
    @Test
    public void testAccessionCrud() {
        Transaction tx = null;
        // Try saving the dummy accession and then retrieving it.
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(DUMMY_ACCESSION_1);
            // Check if we got the expected accession, and accordingly pass or fail the test.
            checkIfExpectedAccession(DUMMY_ACCESSION_1.getId());
            tx.commit();
        } catch (DAOException saveException) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of accession: " + saveException.getMessage());
        }
    }

    /**
     * Tests saving an <code>Accession</code> collection.
     * Relies on cascading save-update being set in the Hibernate mapping.
     */
    @Test
    public void testSaveAccessionCollection() {
        Transaction tx = null;
        List<Accession> accessionList = new ArrayList<Accession>();
        accessionList.add(DUMMY_ACCESSION_1);
        accessionList.add(DUMMY_ACCESSION_2);
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(accessionList);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save of accession collection: " + e.getMessage());
        }
        assertTrue(true);
    }

    /**
     * Methods to check if we got the expected results from the tests.
     */
    private void checkIfExpectedTerms(List<Term> retrievedTerms) {
        for (int i = 0; i < 2; i++) {
            Term term = retrievedTerms.get(i);
            if (!(DUMMY_TERM_1.equals(term) || DUMMY_TERM_2.equals(term))) {
                fail("Did not retrieve the expected terms.");
            }
        }
        // Test passed.
        assertTrue(true);
    }

    private void checkIfExpectedTermsRecursive(Set<Term> retrievedTerms) {
        Iterator<Term> iterator = retrievedTerms.iterator();
        while (iterator.hasNext()) {
            Term term = iterator.next();
            if (!(DUMMY_TERM_1.equals(term) || DUMMY_TERM_2.equals(term)
                    || DUMMY_TERM_3.equals(term))) {
                fail("Did not retrieve the expected terms.");
            }
        }
        // Test passed.
        assertTrue(true);
    }

    private void checkIfExpectedTerm(long id) {
        Term retrievedTerm = (Term) HibernateUtil.getCurrentSession().get(Term.class, id);
        if (DUMMY_TERM_1.equals(retrievedTerm)) {
            // The retrieved term is the same as the saved term. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved term is different from saved term.");
        }
    }

    private void checkIfExpectedCategory(long id) {
        Category retrievedCategory = (Category) HibernateUtil.getCurrentSession().get(Category.class, id);
        if (DUMMY_CATEGORY_1.equals(retrievedCategory)) {
            // The retrieved category is the same as the saved category. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved category is different from saved category.");
        }
    }

    private void checkIfExpectedSource(long id) {
        TermSource retrievedSource = (TermSource) HibernateUtil.getCurrentSession().get(TermSource.class, id);
        if (DUMMY_SOURCE_1.equals(retrievedSource)) {
            // The retrieved source is the same as the saved source. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved source is different from saved source.");
        }
    }

    private void checkIfExpectedAccession(long id) {
        Accession retrievedAccession = (Accession) HibernateUtil.getCurrentSession().get(Accession.class, id);
        if (DUMMY_ACCESSION_1.equals(retrievedAccession)) {
            // The retrieved accession is the same as the saved accession. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved accession is different from saved accession.");
        }
    }

    /**
     * Save dummy entities in the database to prepare for tests.
     */
    private void setupTestGetTerms() {
        DAO_OBJECT.save(DUMMY_TERM_1);
        DAO_OBJECT.save(DUMMY_TERM_2);
    }

    private void setupTestGetTermsRecursive() {
        DAO_OBJECT.save(DUMMY_TERM_1);
        DAO_OBJECT.save(DUMMY_TERM_2);
        DAO_OBJECT.save(DUMMY_TERM_3);
    }
}
