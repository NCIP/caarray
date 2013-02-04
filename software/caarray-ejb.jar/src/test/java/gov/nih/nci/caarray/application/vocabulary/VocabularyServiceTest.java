//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.vocabulary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * @author John Pike
 * 
 */
@SuppressWarnings("PMD")
public class VocabularyServiceTest extends AbstractServiceTest {

    private static final int NUM_PROT_TYPES = 5;
    private VocabularyService vocabularyService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    private static final String SOURCE_NAME = ExperimentOntology.MGED_ONTOLOGY.getOntologyName();
    private static final String SOURCE_VERSION = ExperimentOntology.MGED_ONTOLOGY.getVersion();
    private static final String CATEGORY_NAME = ExperimentOntologyCategory.PROTOCOL_TYPE.getCategoryName();

    private static TermSource source;
    private static Category category;

    // //TEST METHODS///////////////////////////////////

    @Before
    public void setUpService() {
        final VocabularyServiceBean vsBean = new VocabularyServiceBean();
        vsBean.setProtocolDao(this.daoFactoryStub.getProtocolDao());
        vsBean.setSearchDao(this.daoFactoryStub.getSearchDao());
        vsBean.setVocabularyDao(this.daoFactoryStub.getVocabularyDao());
        this.vocabularyService = vsBean;

        source = new TermSource();
        source.setName(SOURCE_NAME);
        source.setVersion(SOURCE_VERSION);

        category = new Category();
        category.setName(CATEGORY_NAME);
        category.setSource(source);

        final Term term1 = new Term();
        term1.setCategory(category);
        term1.setSource(source);
        term1.setValue("term1");
        this.daoFactoryStub.getVocabularyDao().save(term1);

        final Term term2 = new Term();
        term2.setCategory(category);
        term2.setSource(source);
        term2.setValue("term2");
        this.daoFactoryStub.getVocabularyDao().save(term2);

        final Term term3 = new Term();
        term3.setCategory(category);
        term3.setSource(source);
        term3.setValue("term3");
        this.daoFactoryStub.getVocabularyDao().save(term3);

        final Term term4 = new Term();
        term4.setCategory(category);
        term4.setSource(source);
        term4.setValue("term4a");
        this.daoFactoryStub.getVocabularyDao().save(term4);

        final Term term5 = new Term();
        term5.setCategory(category);
        term5.setSource(source);
        term5.setValue("term4b");
        this.daoFactoryStub.getVocabularyDao().save(term5);
    }

    /**
     * Test to ensure that entries are returned for the categoryName "ProtocolType".
     */
    @Test
    public void testGetTerms() {
        Set<Term> terms = new HashSet<Term>();
        final TermSource mo = this.vocabularyService.getSource(SOURCE_NAME, SOURCE_VERSION);
        final Category protocolType = this.vocabularyService.getCategory(mo, CATEGORY_NAME);
        terms = this.vocabularyService.getTerms(protocolType);
        assertTrue(!terms.isEmpty());
        assertEquals(NUM_PROT_TYPES, terms.size());

        terms = this.vocabularyService.getTerms(protocolType, "term4");
        assertEquals(2, terms.size());
        terms = this.vocabularyService.getTerms(protocolType, "term4a");
        assertEquals(1, terms.size());

        terms = this.vocabularyService.getTerms(protocolType, "gibberish");
        assertEquals(0, terms.size());

        final Term term = this.vocabularyService.getTerm(source, "term1");
        assertNotNull(term);
        assertEquals("term1", term.getValue());
        assertEquals(source.getNameAndVersion(), term.getSource().getNameAndVersion());
    }

    /**
     * Test to ensure IllegalArgumentException is thrown if a null arg is passed to "getTerms()" method
     */
    @Test(expected = IllegalArgumentException.class)
    public void getTermsNullCategory() {
        this.vocabularyService.getTerms(null);
    }

    @Test
    public void testGetOrganisms() throws Exception {
        List<Organism> orgs = this.vocabularyService.getOrganisms();
        assertEquals(0, orgs.size());

        final Organism org1 = new Organism();
        org1.setScientificName("Homo sapiens");
        org1.setTermSource(source);
        this.daoFactoryStub.getVocabularyDao().save(org1);

        final Organism org2 = new Organism();
        org2.setScientificName("Mus musculus");
        org2.setTermSource(source);
        this.daoFactoryStub.getVocabularyDao().save(org2);

        final Organism org3 = new Organism();
        org3.setScientificName("Rattus norvegicus");
        org3.setTermSource(source);
        this.daoFactoryStub.getVocabularyDao().save(org3);

        final Organism org4 = new Organism();
        org4.setScientificName("Rattus rattus");
        org4.setTermSource(source);
        this.daoFactoryStub.getVocabularyDao().save(org4);

        final Organism org5 = new Organism();
        org5.setScientificName("Drosophila melanogaster");
        org5.setTermSource(source);
        this.daoFactoryStub.getVocabularyDao().save(org5);

        orgs = this.vocabularyService.getOrganisms();
        assertEquals(5, orgs.size());

        Organism org = this.vocabularyService.getOrganism(null, null);
        assertNull(org);
        org = this.vocabularyService.getOrganism(null, "");
        assertNull(org);
        org = this.vocabularyService.getOrganism(null, "asdf");
        assertNull(org);
        org = this.vocabularyService.getOrganism(source, null);
        assertNull(org);
        org = this.vocabularyService.getOrganism(source, "");
        assertNull(org);
        org = this.vocabularyService.getOrganism(source, "asdf");
        assertNull(org);
        org = this.vocabularyService.getOrganism(new TermSource(), org1.getScientificName());
        assertNull(org);
        org = this.vocabularyService.getOrganism(source, org1.getScientificName());
        assertEquals(org1.getScientificName(), org.getScientificName());
        assertEquals(org1.getTermSource(), org.getTermSource());
        org = this.vocabularyService.getOrganism(source, org2.getScientificName().toUpperCase());
        assertEquals(org2.getScientificName(), org.getScientificName());
        assertEquals(org2.getTermSource(), org.getTermSource());
        assertEquals(org2.getTermSource().toString(), org.getTermSource().toString());
    }

    @Test
    public void testSearchForCharacteristic() {
        assertEquals(Collections.EMPTY_LIST,
                this.vocabularyService.searchForCharacteristicCategory(TermBasedCharacteristic.class, null));
    }

    // ////// INNER CLASS TEST STUBS///////////////////////
    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        LocalVocabularyDaoStub vocabularyDao = new LocalVocabularyDaoStub();

        /**
         * {@inheritDoc}
         */
        @Override
        public VocabularyDao getVocabularyDao() {
            return this.vocabularyDao;
        }

        @Override
        public SearchDao getSearchDao() {
            return new SearchDaoStub() {
                @Override
                public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
                    if (Organism.class.equals(entityClass)) {
                        return (List<T>) new ArrayList<Organism>(LocalDaoFactoryStub.this.vocabularyDao.orgMap.values());
                    }
                    return super.retrieveAll(entityClass, orders);
                }
            };
        }
    }

    private static class LocalVocabularyDaoStub extends VocabularyDaoStub {
        private static long nextId = 1;
        private static Map<Long, Term> termMap = new HashMap<Long, Term>();
        private static Map<Long, Organism> orgMap = new HashMap<Long, Organism>();

        @Override
        public Set<Term> getTermsRecursive(Category category, String value) {
            final Set<Category> searchCategories = new HashSet<Category>();
            searchCategories.add(category);
            addChildren(searchCategories, category);

            final Set<Term> terms = new HashSet<Term>();
            for (final Term term : termMap.values()) {
                if (CollectionUtils.containsAny(term.getCategories(), searchCategories)) {
                    if (value == null || term.getValue().toLowerCase().startsWith(value.toLowerCase())) {
                        terms.add(term);
                    }
                }
            }
            return terms;
        }

        private void addChildren(Collection<Category> searchCategories, Category category) {
            for (final Category child : category.getChildren()) {
                searchCategories.add(child);
                addChildren(searchCategories, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Category getCategory(TermSource source, String name) {
            if (SOURCE_NAME.equals(source.getName()) && SOURCE_VERSION.equals(source.getVersion())
                    && CATEGORY_NAME.equalsIgnoreCase(name)) {
                final Category category = new Category();
                category.setSource(source);
                category.setName(name);
                return category;
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Term getTerm(TermSource source, String value) {
            for (final Term term : termMap.values()) {
                if (term.getValue().equalsIgnoreCase(value) && term.getSource().equals(source)) {
                    return term;
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria,
                Order... order) {
            if (criteria.getExample() instanceof TermSource) {
                final TermSource termSource = (TermSource) criteria.getExample();
                if (SOURCE_NAME.equalsIgnoreCase(termSource.getName())
                        && SOURCE_VERSION.equalsIgnoreCase(termSource.getVersion())) {
                    final List list = new ArrayList();
                    list.add(termSource);
                    return list;
                }
            }

            return super.queryEntityByExample(criteria, order);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Organism getOrganism(TermSource source, String scientificName) {
            for (final Organism org : orgMap.values()) {
                if (org.getTermSource().equals(source) && org.getScientificName().equalsIgnoreCase(scientificName)) {
                    return org;
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("deprecation")
        public Long save(PersistentObject object) {
            if (object instanceof Term) {
                final Term term = (Term) object;
                term.setId(nextId++);
                termMap.put(term.getId(), term);
            } else if (object instanceof Organism) {
                final Organism org = (Organism) object;
                org.setId(nextId++);
                orgMap.put(org.getId(), org);
            } else {
                throw new IllegalArgumentException("Unsupported object class: " + object.getClass());
            }
            return object.getId();
        }
    }
}
