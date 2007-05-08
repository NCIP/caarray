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

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.Source;
import gov.nih.nci.caarray.domain.vocabulary.Accession;

/**
 * Unit tests for the Vocabulary DAO.
 *
 * @author Rashmi Srinivasa
 */
public class VocabularyDaoTest {
    private static final Log LOG = LogFactory.getLog(VocabularyDaoTest.class);

    private static final Long DUMMY_START_ID = new Long(150);
    private static Category dummyCategory1 = null;
    private static Category dummyCategory2 = null;
    private static Category dummyCategory3 = null;
    private static final int NUM_DUMMY_TERMS = 2;
    private static Term dummyTerm1 = null;
    private static Term dummyTerm2 = null;
    private static Source dummySource1 = null;
    private static Source dummySource2 = null;
    private static Accession dummyAccession1 = null;
    private static Accession dummyAccession2 = null;

    private static final VocabularyDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getVocabularyDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        // Initialize all the dummy objects needed for the tests.
        initializeCategoriesAndTerms();
        initializeSourcesAndAccessions();
    }

    /**
     * Initialize the dummy <code>Category</code> and <code>Term</code> objects.
     */
    private static void initializeCategoriesAndTerms() {
        dummyCategory1 = new Category();
        dummyCategory1.setId(DUMMY_START_ID);
        dummyCategory1.setName("DummyTestCategory1");
        dummyCategory2 = new Category();
        dummyCategory2.setId(DUMMY_START_ID + 1);
        dummyCategory2.setName("DummyTestCategory2");
        dummyCategory3 = new Category();
        dummyCategory3.setId(DUMMY_START_ID + 2);
        dummyCategory3.setName("DummyTestCategory3");
        dummyTerm1 = new Term();
        dummyTerm1.setId(DUMMY_START_ID);
        dummyTerm1.setDescription("DummyTestTerm1");
        dummyTerm1.setCategory(dummyCategory3);
        dummyTerm2 = new Term();
        dummyTerm2.setId(DUMMY_START_ID + 1);
        dummyTerm2.setDescription("DummyTestTerm2");
        dummyTerm2.setCategory(dummyCategory3);
    }

    /**
     * Initialize the dummy <code>Source</code> and <code>Accession</code> objects.
     */
    private static void initializeSourcesAndAccessions() {
        dummySource1 = new Source();
        dummySource1.setId(DUMMY_START_ID);
        dummySource1.setName("DummyTestSource1");
        dummySource1.setUrl("DummyUrlForSource1");
        dummySource1.setVersion("1.0");
        dummySource2 = new Source();
        dummySource2.setId(DUMMY_START_ID + 1);
        dummySource2.setName("DummyTestSource2");
        dummySource2.setUrl("DummyUrlForSource2");
        dummySource2.setVersion("1.0");
        dummyAccession1 = new Accession();
        dummyAccession1.setId(DUMMY_START_ID);
        dummyAccession1.setUrl("DummyUrlForAccession1");
        dummyAccession1.setValue("DummyValueForAccession1");
        dummyAccession1.setSource(dummySource1);
        dummyAccession2 = new Accession();
        dummyAccession2.setId(DUMMY_START_ID + 1);
        dummyAccession2.setUrl("DummyUrlForAccession2");
        dummyAccession2.setValue("DummyValueForAccession2");
        dummyAccession2.setSource(dummySource1);
    }

    /**
     * Clear the dummy objects that were used by the tests.
     */
    @AfterClass
    public static void tearDownAfterClass() {
        dummyCategory1 = null;
        dummyCategory2 = null;
        dummyCategory3 = null;
        dummyTerm1 = null;
        dummyTerm2 = null;
        dummySource1 = null;
        dummySource2 = null;
        dummyAccession1 = null;
        dummyAccession2 = null;
    }
    /**
     * Tests retrieving all <code>Term</code>s in a given <code>Category</code>.
     */
    @Test
    public void testGetTerms() {
        try {
            setupTestGetTerms();
            List<Term> retrievedTerms = DAO_OBJECT.getTerms(dummyCategory3.getName());
            if (retrievedTerms.size() != NUM_DUMMY_TERMS) {
                fail("Did not retrieve the expected number of terms.");
            }
            // Check if we got the expected terms, and accordingly pass or fail the test.
            checkIfExpectedTerms(retrievedTerms);
        } catch (DAOException e) {
            fail("DAO exception while getting terms in a category: " + e.getMessage());
        } finally {
            cleanUpTestGetTerms();
        }
    }

    /**
     * Check if we got the expected terms, and accordingly pass or fail the test.
     *
     * @param retrievedTerms the <code>Term</code>s retrieved from the database.
     */
    private void checkIfExpectedTerms(List<Term> retrievedTerms) {
        for (int i = 0; i < NUM_DUMMY_TERMS; i++) {
            Term term = retrievedTerms.get(i);
            if (!(dummyTerm1.equals(term) || dummyTerm2.equals(term))) {
                fail("Did not retrieve the expected terms.");
            }
        }
        // Test passed.
        assertTrue(true);
    }

    /**
     * Cleans up after testGetTerms by removing the dummy category and terms.
     */
    private void cleanUpTestGetTerms() {
        try {
            DAO_OBJECT.remove(dummyTerm1);
            DAO_OBJECT.remove(dummyTerm2);
            DAO_OBJECT.remove(dummyCategory3);
        } catch (DAOException deleteException) {
            LOG.error("Error cleaning up dummy category and terms.", deleteException);
        }
    }

    /**
     * Save dummy category and terms in the database for testGetTerms.
     *
     * @throws DAOException
     */
    private void setupTestGetTerms() throws DAOException {
        DAO_OBJECT.save(dummyCategory3);
        DAO_OBJECT.save(dummyTerm1);
        DAO_OBJECT.save(dummyTerm2);
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Category</code>.
     */
    @Test
    public void testCategoryCrud() {
        // Try saving the dummy category and then retrieving it.
        try {
            DAO_OBJECT.save(dummyCategory1);
            // Check if we got the expected category, and accordingly pass or fail the test.
            checkIfExpectedCategory();
        } catch (DAOException saveException) {
            fail("DAO exception during save and retrieve of category: " + saveException.getMessage());
        } finally {
            // Clean up by removing the dummy category.
            try {
                DAO_OBJECT.remove(dummyCategory1);
            } catch (DAOException deleteException) {
                LOG.error("Error cleaning up dummy category.", deleteException);
                fail("DAO exception during deletion of category: " + deleteException.getMessage());
            }
        }
    }

    /**
     * Check if we got the expected <code>Category</code>, and accordingly pass or fail the test.
     *
     * @throws DAOException
     */
    private void checkIfExpectedCategory() throws DAOException {
        Category retrievedCategory = (Category) DAO_OBJECT.queryEntityById(dummyCategory1);
        if (dummyCategory1.equals(retrievedCategory)) {
            // The retrieved category is the same as the saved category. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved category is different from saved category.");
        }
    }

    /**
     * Tests saving a <code>Category</code> collection.
     */
    @Test
    public void testSaveCategoryCollection() {
        List<Category> categoryList = new ArrayList<Category>();
        categoryList.add(dummyCategory1);
        categoryList.add(dummyCategory2);
        try {
            DAO_OBJECT.save(categoryList);
        } catch (DAOException e) {
            fail("DAO exception during save of category collection: " + e.getMessage());
        } finally {
            // Clean up by removing the dummy categories.
            try {
                DAO_OBJECT.remove(dummyCategory1);
                DAO_OBJECT.remove(dummyCategory2);
            } catch (DAOException deleteException) {
                LOG.error("Error cleaning up dummy categories.", deleteException);
                fail("DAO exception during deletion of categories: " + deleteException.getMessage());
            }
        }
        assertTrue(true);
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Term</code>.
     */
    @Test
    public void testTermCrud() {
        // Try saving the dummy term and then retrieving it.
        try {
            DAO_OBJECT.save(dummyCategory3);
            DAO_OBJECT.save(dummyTerm1);
            // Check if we got the expected term, and accordingly pass or fail the test.
            checkIfExpectedTerm();
        } catch (DAOException saveException) {
            fail("DAO exception during save and retrieve of term: " + saveException.getMessage());
        } finally {
            // Clean up by removing the dummy term.
            try {
                DAO_OBJECT.remove(dummyTerm1);
                DAO_OBJECT.remove(dummyCategory3);
            } catch (DAOException deleteException) {
                LOG.error("Error cleaning up dummy term.", deleteException);
                fail("DAO exception during deletion of term: " + deleteException.getMessage());
            }
        }
    }

    /**
     * Check if we got the expected <code>Term</code>, and accordingly pass or fail the test.
     *
     * @throws DAOException
     */
    private void checkIfExpectedTerm() throws DAOException {
        Term retrievedTerm = (Term) DAO_OBJECT.queryEntityById(dummyTerm1);
        if (dummyTerm1.equals(retrievedTerm)) {
            // The retrieved term is the same as the saved term. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved term is different from saved term.");
        }
    }

    /**
     * Tests saving a <code>Term</code> collection.
     */
    @Test
    public void testSaveTermCollection() {
        List<Term> termList = new ArrayList<Term>();
        termList.add(dummyTerm1);
        termList.add(dummyTerm2);
        try {
            DAO_OBJECT.save(dummyCategory3);
            DAO_OBJECT.save(termList);
        } catch (DAOException e) {
            fail("DAO exception during save of term collection: " + e.getMessage());
        } finally {
            // Clean up by removing the dummy terms.
            try {
                DAO_OBJECT.remove(dummyTerm1);
                DAO_OBJECT.remove(dummyTerm2);
                DAO_OBJECT.remove(dummyCategory3);
            } catch (DAOException deleteException) {
                LOG.error("Error cleaning up dummy terms.", deleteException);
                fail("DAO exception during deletion of terms: " + deleteException.getMessage());
            }
        }
        assertTrue(true);
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Source</code>.
     */
    @Test
    public void testSourceCrud() {
        // Try saving the dummy source and then retrieving it.
        try {
            DAO_OBJECT.save(dummySource1);
            // Check if we got the expected source, and accordingly pass or fail the test.
            checkIfExpectedSource();
        } catch (DAOException saveException) {
            fail("DAO exception during save and retrieve of source: " + saveException.getMessage());
        } finally {
            // Clean up by removing the dummy source.
            try {
                DAO_OBJECT.remove(dummySource1);
            } catch (DAOException deleteException) {
                LOG.error("Error cleaning up dummy category.", deleteException);
                fail("DAO exception during deletion of category: " + deleteException.getMessage());
            }
        }
    }

    /**
     * Check if we got the expected <code>Source</code>, and accordingly pass or fail the test.
     *
     * @throws DAOException
     */
    private void checkIfExpectedSource() throws DAOException {
        Source retrievedSource = (Source) DAO_OBJECT.queryEntityById(dummySource1);
        if (dummySource1.equals(retrievedSource)) {
            // The retrieved category is the same as the saved category. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved source is different from saved source.");
        }
    }

    /**
     * Tests saving a <code>Source</code> collection.
     */
    @Test
    public void testSaveSourceCollection() {
        List<Source> sourceList = new ArrayList<Source>();
        sourceList.add(dummySource1);
        sourceList.add(dummySource2);
        try {
            DAO_OBJECT.save(sourceList);
        } catch (DAOException e) {
            fail("DAO exception during save of source collection: " + e.getMessage());
        } finally {
            // Clean up by removing the dummy sources.
            try {
                DAO_OBJECT.remove(dummySource1);
                DAO_OBJECT.remove(dummySource2);
            } catch (DAOException deleteException) {
                LOG.error("Error cleaning up dummy sources.", deleteException);
                fail("DAO exception during deletion of sources: " + deleteException.getMessage());
            }
        }
        assertTrue(true);
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>Accession</code>.
     */
    @Test
    public void testAccessionCrud() {
        // Try saving the dummy accession and then retrieving it.
        try {
            DAO_OBJECT.save(dummyAccession1);
            // Check if we got the expected accession, and accordingly pass or fail the test.
            checkIfExpectedAccession();
        } catch (DAOException saveException) {
            fail("DAO exception during save and retrieve of accession: " + saveException.getMessage());
        } finally {
            // Clean up by removing the dummy accession.
            try {
                DAO_OBJECT.remove(dummyAccession1);
            } catch (DAOException deleteException) {
                LOG.error("Error cleaning up dummy category.", deleteException);
                fail("DAO exception during deletion of category: " + deleteException.getMessage());
            }
        }
    }

    /**
     * Check if we got the expected <code>Accession</code>, and accordingly pass or fail the test.
     *
     * @throws DAOException
     */
    private void checkIfExpectedAccession() throws DAOException {
        Accession retrievedAccession = (Accession) DAO_OBJECT.queryEntityById(dummyAccession1);
        if (dummyAccession1.equals(retrievedAccession)) {
            // The retrieved category is the same as the saved category. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved accession is different from saved accession.");
        }
    }
}
