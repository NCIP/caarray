//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.validator.ClassValidator;
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
    private static Organism DUMMY_ORGANISM_1 = new Organism();
    private static Organism DUMMY_ORGANISM_2 = new Organism();
    private static Term DUMMY_MATERIAL_TYPE = new Term();
    private static TermBasedCharacteristic DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();

    private VocabularyDao daoObject;

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        this.daoObject = new VocabularyDaoImpl(this.hibernateHelper);

        DUMMY_SOURCE_1 = new TermSource();
        DUMMY_SOURCE_1.setName("DummyTestSource1");
        DUMMY_SOURCE_1.setUrl("DummyUrlForSource1");
        DUMMY_SOURCE_1.setVersion("1.0");
        DUMMY_SOURCE_2 = new TermSource();
        DUMMY_SOURCE_2.setName("DummyTestSource2");
        DUMMY_SOURCE_2.setUrl("DummyUrlForSource2");
        DUMMY_SOURCE_2.setVersion("1.0");

        DUMMY_CATEGORY_1 = new Category();
        DUMMY_CATEGORY_1.setName("DummyTestCategory1");
        DUMMY_CATEGORY_1.setSource(DUMMY_SOURCE_1);
        DUMMY_CATEGORY_2 = new Category();
        DUMMY_CATEGORY_2.setName("DummyTestCategory2");
        DUMMY_CATEGORY_2.setSource(DUMMY_SOURCE_1);
        DUMMY_CATEGORY_3 = new Category();
        DUMMY_CATEGORY_3.setName("DummyTestCategory3");
        DUMMY_CATEGORY_3.setSource(DUMMY_SOURCE_2);
        DUMMY_CATEGORY_4 = new Category();
        DUMMY_CATEGORY_4.getChildren().add(DUMMY_CATEGORY_3);
        DUMMY_CATEGORY_4.setName("DummyTestCategory4");
        DUMMY_CATEGORY_4.setSource(DUMMY_SOURCE_2);
        DUMMY_CATEGORY_3.getParents().add(DUMMY_CATEGORY_4);
        DUMMY_CATEGORY_4.getChildren().add(DUMMY_CATEGORY_3);

        DUMMY_TERM_1 = new Term();
        DUMMY_TERM_1.setValue("DammyValue1");
        DUMMY_TERM_1.setDescription("DummyTestTerm1");
        DUMMY_TERM_1.setAccession("DummyAccession1");
        DUMMY_TERM_1.setUrl("DummyUrl1");
        DUMMY_TERM_1.setCategory(DUMMY_CATEGORY_3);
        DUMMY_TERM_1.setSource(DUMMY_SOURCE_1);
        DUMMY_TERM_2 = new Term();
        DUMMY_TERM_2.setValue("DammyValue2");
        DUMMY_TERM_2.setDescription("DummyTestTerm2");
        DUMMY_TERM_2.setCategory(DUMMY_CATEGORY_3);
        DUMMY_TERM_2.setUrl("DummyUrlForAccession2");
        DUMMY_TERM_2.setAccession("DummyValueForAccession2");
        DUMMY_TERM_2.setSource(DUMMY_SOURCE_1);
        DUMMY_TERM_3 = new Term();
        DUMMY_TERM_3.setValue("DammyValue3");
        DUMMY_TERM_3.setDescription("DummyTestTerm3");
        DUMMY_TERM_3.setCategory(DUMMY_CATEGORY_4);
        DUMMY_TERM_3.setSource(DUMMY_SOURCE_2);

        DUMMY_MATERIAL_TYPE = new Term();
        DUMMY_MATERIAL_TYPE.setValue("Dummy Material Type");
        DUMMY_MATERIAL_TYPE.setSource(DUMMY_SOURCE_1);
        DUMMY_MATERIAL_TYPE.setCategory(DUMMY_CATEGORY_1);

        DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
        DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY_1);
        DUMMY_CHARACTERISTIC.setTerm(DUMMY_MATERIAL_TYPE);

        DUMMY_ORGANISM_1 = new Organism();
        DUMMY_ORGANISM_1.setScientificName("name1");
        DUMMY_ORGANISM_1.setCommonName("foo");
        DUMMY_ORGANISM_1.setTermSource(DUMMY_SOURCE_1);

        DUMMY_ORGANISM_2 = new Organism();
        DUMMY_ORGANISM_2.setScientificName("name1");
        DUMMY_ORGANISM_2.setCommonName("baz");
        DUMMY_ORGANISM_2.setTermSource(DUMMY_SOURCE_2);
    }

    /**
     * Tests retrieving all <code>Term</code>s in a given <code>Category</code> recursively.
     */
    @Test
    public void testGetTermsRecursive() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            setupTestGetTermsRecursive();
            tx.commit();
            tx = this.hibernateHelper.beginTransaction();
            Set<Term> retrievedTerms = this.daoObject.getTermsRecursive(DUMMY_CATEGORY_4, null);
            assertEquals("Did not retrieve the expected number of terms.", NUM_DUMMY_TERMS, retrievedTerms.size());
            // Check if we got the expected terms, and accordingly pass or fail the test.
            checkIfExpectedTermsRecursive(retrievedTerms);
            retrievedTerms = this.daoObject.getTermsRecursive(DUMMY_CATEGORY_4, "DammyValue");
            assertEquals("Did not retrieve the expected number of terms.", NUM_DUMMY_TERMS, retrievedTerms.size());
            retrievedTerms = this.daoObject.getTermsRecursive(DUMMY_CATEGORY_4, "DammyValue1");
            assertEquals("Did not retrieve the expected number of terms.", 1, retrievedTerms.size());
            assertEquals(DUMMY_TERM_1, retrievedTerms.iterator().next());

            tx.commit();
        } catch (final Exception e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception while getting terms in a category: " + e.getMessage());
        }
    }

    @Test
    public void testGetOrganism() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(DUMMY_ORGANISM_1);
            this.daoObject.save(DUMMY_ORGANISM_2);
            Organism o = this.daoObject.getOrganism(DUMMY_SOURCE_1, DUMMY_ORGANISM_1.getScientificName());
            assertEquals(DUMMY_ORGANISM_1, o);
            o = this.daoObject.getOrganism(DUMMY_SOURCE_2, "foobar");
            assertNull(o);
            o = this.daoObject.getOrganism(DUMMY_SOURCE_2, DUMMY_ORGANISM_2.getScientificName());
            assertEquals(DUMMY_ORGANISM_2, o);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception while getting organisms: " + e.getMessage());
        }
    }

    @Test
    public void testGetTerm() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            setupTestGetTerms();
            Term t = this.daoObject.getTerm(DUMMY_SOURCE_1, DUMMY_TERM_1.getValue());
            assertEquals(DUMMY_TERM_1, t);
            t = this.daoObject.getTerm(DUMMY_SOURCE_1, DUMMY_TERM_1.getValue().toUpperCase());
            assertEquals(DUMMY_TERM_1, t);
            t = this.daoObject.getTerm(DUMMY_SOURCE_2, DUMMY_TERM_1.getValue());
            assertNull(t);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception while getting organisms: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving terms by id recursively.
     */
    @Test
    public void testGetTermsById() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            setupTestGetTermsRecursive();
            final Term retrievedTerm1 = this.daoObject.getTermById(DUMMY_TERM_1.getId());
            final Term retrievedTerm2 = this.daoObject.getTermById(DUMMY_TERM_2.getId());
            final Term retrievedTerm3 = this.daoObject.getTermById(DUMMY_TERM_3.getId());
            tx.commit();
            assertEquals(DUMMY_TERM_1, retrievedTerm1);
            assertEquals(DUMMY_TERM_2, retrievedTerm2);
            assertEquals(DUMMY_TERM_3, retrievedTerm3);
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
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
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(DUMMY_CATEGORY_1);
            final Category retrievedCategory =
                    this.daoObject.getCategory(DUMMY_CATEGORY_1.getSource(), DUMMY_CATEGORY_1.getName());
            assertNotNull(retrievedCategory);
            assertEquals(DUMMY_CATEGORY_1.getId(), retrievedCategory.getId());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
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
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(DUMMY_CATEGORY_1);
            // Check if we got the expected category, and accordingly pass or fail the test.
            checkIfExpectedCategory(DUMMY_CATEGORY_1.getId());
            tx.commit();
        } catch (final DAOException saveException) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of category: " + saveException.getMessage());
        }
    }

    /**
     * Tests saving a <code>Category</code> collection.
     */
    @Test
    public void testSaveCategoryCollection() {
        Transaction tx = null;
        final List<Category> categoryList = new ArrayList<Category>();
        categoryList.add(DUMMY_CATEGORY_1);
        categoryList.add(DUMMY_CATEGORY_2);
        try {
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(categoryList);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
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
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(DUMMY_TERM_1);
            // Check if we got the expected term, and accordingly pass or fail the test.
            checkIfExpectedTerm(DUMMY_TERM_1.getId());
            tx.commit();
        } catch (final DAOException saveException) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of term: " + saveException.getMessage());
        }
    }

    /**
     * Tests saving a <code>Term</code> collection.
     */
    @Test
    public void testSaveTermCollection() {
        Transaction tx = null;
        final List<Term> termList = new ArrayList<Term>();
        termList.add(DUMMY_TERM_1);
        termList.add(DUMMY_TERM_2);
        try {
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(termList);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
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
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(DUMMY_SOURCE_1);
            // Check if we got the expected source, and accordingly pass or fail the test.
            checkIfExpectedSource(DUMMY_SOURCE_1.getId());
            tx.commit();
        } catch (final DAOException saveException) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of source: " + saveException.getMessage());
        }
    }

    /**
     * Tests saving a <code>Source</code> collection.
     */
    @Test
    public void testSaveSourceCollection() {
        Transaction tx = null;
        final List<TermSource> sourceList = new ArrayList<TermSource>();
        sourceList.add(DUMMY_SOURCE_1);
        sourceList.add(DUMMY_SOURCE_2);
        try {
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(sourceList);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save of source collection: " + e.getMessage());
        }
        assertTrue(true);
    }

    @Test
    public void testFindTermInAllSourceVersions() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            setupTestGetTermsRecursive();

            final TermSource DUMMY_SOURCE_1_V2 = new TermSource();
            DUMMY_SOURCE_1_V2.setName("Some name");
            DUMMY_SOURCE_1_V2.setUrl(DUMMY_SOURCE_1.getUrl());
            DUMMY_SOURCE_1_V2.setVersion("2.0");
            final Term t = new Term();
            t.setValue("bazfoo");
            t.setAccession("MO23");
            t.setSource(DUMMY_SOURCE_1_V2);
            this.daoObject.save(DUMMY_SOURCE_1_V2);
            this.daoObject.save(t);

            Term result = this.daoObject.findTermInAllTermSourceVersions(DUMMY_SOURCE_1, "bazfoo");
            assertEquals(t, result);
            result = this.daoObject.findTermInAllTermSourceVersions(DUMMY_SOURCE_2, "bazfoo");
            assertNull(result);
            result = this.daoObject.findTermInAllTermSourceVersions(DUMMY_SOURCE_1_V2, "bazfoo");
            assertEquals(t, result);
            result = this.daoObject.findTermInAllTermSourceVersions(DUMMY_SOURCE_1, "bazfoo2");
            assertNull(result);

            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save of source collection: " + e.getMessage());
        }
    }

    /**
     * Methods to check if we got the expected results from the tests.
     */
    private void checkIfExpectedTerms(Collection<Term> retrievedTerms) {
        for (final Term term : retrievedTerms) {
            if (!(DUMMY_TERM_1.equals(term) || DUMMY_TERM_2.equals(term))) {
                fail("Did not retrieve the expected terms.");
            }
        }
        // Test passed.
        assertTrue(true);
    }

    private void checkIfExpectedTermsRecursive(Set<Term> retrievedTerms) {
        final Iterator<Term> iterator = retrievedTerms.iterator();
        while (iterator.hasNext()) {
            final Term term = iterator.next();
            if (!(DUMMY_TERM_1.equals(term) || DUMMY_TERM_2.equals(term) || DUMMY_TERM_3.equals(term))) {
                fail("Did not retrieve the expected terms, actual term: " + term);
            }
        }
        // Test passed.
        assertTrue(true);
    }

    private void checkIfExpectedTerm(long id) {
        final Term retrievedTerm = (Term) this.hibernateHelper.getCurrentSession().get(Term.class, id);
        if (DUMMY_TERM_1.equals(retrievedTerm)) {
            // The retrieved term is the same as the saved term. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved term is different from saved term.");
        }
    }

    private void checkIfExpectedCategory(long id) {
        final Category retrievedCategory = (Category) this.hibernateHelper.getCurrentSession().get(Category.class, id);
        if (DUMMY_CATEGORY_1.equals(retrievedCategory)) {
            // The retrieved category is the same as the saved category. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved category is different from saved category.");
        }
    }

    private void checkIfExpectedSource(long id) {
        final TermSource retrievedSource =
                (TermSource) this.hibernateHelper.getCurrentSession().get(TermSource.class, id);
        if (DUMMY_SOURCE_1.equals(retrievedSource)) {
            // The retrieved source is the same as the saved source. Save and retrieve test passed.
            assertTrue(true);
        } else {
            fail("Retrieved source is different from saved source.");
        }
    }

    /**
     * Save dummy entities in the database to prepare for tests.
     */
    private void setupTestGetTerms() {
        this.daoObject.save(DUMMY_TERM_1);
        this.daoObject.save(DUMMY_TERM_2);
    }

    private void setupTestGetTermsRecursive() {
        this.daoObject.save(DUMMY_TERM_1);
        this.daoObject.save(DUMMY_TERM_2);
        this.daoObject.save(DUMMY_TERM_3);
    }

    /**
     * Tests retrieving the <code>Characteristic</code> with the given name.
     */
    @Test
    public void testGetCharacteristicCategoryNoKeyword() {
        Transaction tx = null;

        try {

            tx = this.hibernateHelper.beginTransaction();
            Project p = new Project();
            Experiment e = p.getExperiment();
            e.setProject(p);
            e.setTitle("Foo");
            e.setOrganism(DUMMY_ORGANISM_1);
            final Source s1 = new Source();
            s1.setName("Test Source");
            s1.setExperiment(e);
            s1.getCharacteristics().add(DUMMY_CHARACTERISTIC);
            DUMMY_CHARACTERISTIC.setBioMaterial(s1);

            final TermBasedCharacteristic char2 = new TermBasedCharacteristic();
            char2.setCategory(DUMMY_CATEGORY_2);
            char2.setTerm(DUMMY_TERM_3);

            final Source s2 = new Source();
            s2.setName("Test Source2");
            s1.getCharacteristics().add(char2);
            char2.setBioMaterial(s2);

            this.daoObject.save(DUMMY_CHARACTERISTIC);
            this.daoObject.save(char2);
            this.daoObject.save(DUMMY_CATEGORY_1);
            this.daoObject.save(DUMMY_CATEGORY_2);
            this.daoObject.save(p);
            this.daoObject.save(s1);
            this.daoObject.save(s2);

            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            List<Category> chars =
                    this.daoObject.searchForCharacteristicCategory(null, TermBasedCharacteristic.class, null);
            assertEquals(2, chars.size());

            chars = this.daoObject.searchForCharacteristicCategory(e, TermBasedCharacteristic.class, null);
            assertEquals(1, chars.size());

            chars =
                    this.daoObject.searchForCharacteristicCategory(null, TermBasedCharacteristic.class,
                            "DummyTestCategory2");
            assertEquals(1, chars.size());

            chars =
                    this.daoObject.searchForCharacteristicCategory(e, TermBasedCharacteristic.class,
                            "DummyTestCategory2");
            assertEquals(0, chars.size());

            tx.commit();

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void test13755() {

        final String errorMessage =
                "A term with the same value and source or accession value and source already exists";
        final ClassValidator<Term> cv = new ClassValidator<Term>(Term.class);
        Transaction tx = null;

        try {
            // Save term
            tx = this.hibernateHelper.beginTransaction();
            final Term term1 = new Term();
            term1.setValue("13755-value-1");
            final TermSource termSource1 = new TermSource();
            termSource1.setName("termSource-13755-1");
            term1.setSource(termSource1);
            term1.setAccession("13755-accession-1");
            this.daoObject.save(term1);

            // Test validation of unique key constraint
            // for (Term.value, Term.source) of new Term
            final Term term2 = new Term();
            term2.setValue("13755-value-1");
            term2.setSource(termSource1);
            assertTrue(cv.getInvalidValues(term2)[0].getMessage().contains(errorMessage));

            term2.setValue("13755-value-2");
            assertEquals(0, cv.getInvalidValues(term2).length);

            // Test validation of unique key constraint
            // for (Term.accession, Term.source) of new Term
            final Term term3 = new Term();
            term3.setAccession("13755-accession-1");
            term3.setSource(termSource1);
            term3.setValue("13755-value-2");
            assertTrue(cv.getInvalidValues(term3)[0].getMessage().contains(errorMessage));

            // Test successful validation of new term
            term3.setAccession("13755-accession-2");
            assertEquals(0, cv.getInvalidValues(term3).length);

            this.daoObject.save(term2);
            tx.commit();

            // Test validation of unique key constraint
            // for (Term.value, Term.source)
            // of existing term
            tx = this.hibernateHelper.beginTransaction();
            term2.setValue("13755-value-1");
            term2.setSource(termSource1);
            term2.setAccession("13755-accession-100");
            final MockTermHibernateProxy proxy = new MockTermHibernateProxy(term2);
            assertTrue(cv.getInvalidValues(proxy)[0].getMessage().contains(errorMessage));

            // Test validation of unique key constraint
            // for (Term.accession, Term.source)
            // of existing term
            term2.setValue("13755-value-100");
            term2.setSource(termSource1);
            term2.setAccession("13755-accession-1");
            proxy.copyValues(term2);
            assertTrue(cv.getInvalidValues(proxy)[0].getMessage().contains(errorMessage));

            // Test successful validation of existing term
            term2.setValue("13755-value-404");
            term2.setSource(termSource1);
            term2.setAccession("13755-accession-404");
            proxy.copyValues(term2);
            assertEquals(0, cv.getInvalidValues(proxy).length);
            tx.commit();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
