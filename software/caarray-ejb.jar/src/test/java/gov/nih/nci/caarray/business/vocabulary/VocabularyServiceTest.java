/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.business.vocabulary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.OrganismDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.OrganismDaoStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * @author John Pike
 * 
 */
@SuppressWarnings("PMD")
public class VocabularyServiceTest {

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
        VocabularyServiceBean vsBean = new VocabularyServiceBean();
        vsBean.setDaoFactory(this.daoFactoryStub);
        vocabularyService = vsBean;
        
        source = new TermSource();
        source.setName(SOURCE_NAME);
        source.setVersion(SOURCE_VERSION);
        
        category = new Category();
        category.setName(CATEGORY_NAME);
        category.setSource(source);
        
        Term term1 = new Term();
        term1.setCategory(category);
        term1.setSource(source);
        term1.setValue("term1");
        daoFactoryStub.getVocabularyDao().save(term1);
        
        Term term2 = new Term();
        term2.setCategory(category);
        term2.setSource(source);
        term2.setValue("term2");
        daoFactoryStub.getVocabularyDao().save(term2);
        
        Term term3 = new Term();
        term3.setCategory(category);
        term3.setSource(source);
        term3.setValue("term3");
        daoFactoryStub.getVocabularyDao().save(term3);

        Term term4 = new Term();
        term4.setCategory(category);
        term4.setSource(source);
        term4.setValue("term4a");
        daoFactoryStub.getVocabularyDao().save(term4);
        
        Term term5 = new Term();
        term5.setCategory(category);
        term5.setSource(source);
        term5.setValue("term4b");
        daoFactoryStub.getVocabularyDao().save(term5);
    }

    /**
     * Test to ensure that entries are returned for the categoryName "ProtocolType".
     */
    @Test
    public void testGetTerms() {
        Set<Term> terms = new HashSet<Term>();
        TermSource mo = vocabularyService.getSource(SOURCE_NAME, SOURCE_VERSION);
        Category protocolType = vocabularyService.getCategory(mo, CATEGORY_NAME);
        terms = vocabularyService.getTerms(protocolType);
        assertTrue(!terms.isEmpty());
        assertEquals(NUM_PROT_TYPES, terms.size());

        terms = vocabularyService.getTerms(protocolType, "term4");
        assertEquals(2, terms.size());
        terms = vocabularyService.getTerms(protocolType, "term4a");
        assertEquals(1, terms.size());

        terms = vocabularyService.getTerms(protocolType, "gibberish");
        assertEquals(0, terms.size());
        
        Term term = vocabularyService.getTerm(source, "term1");
        assertNotNull(term);
        assertEquals("term1", term.getValue());
        assertEquals(source.getNameAndVersion(), term.getSource().getNameAndVersion());
    }

    /**
     * Test to ensure IllegalArgumentException is thrown if a null arg is passed to "getTerms()" method
     */
    @Test(expected = IllegalArgumentException.class)
    public void getTermsNullCategory() {
        vocabularyService.getTerms(null);
    }
    
    @Test
    public void testGetOrganisms() throws Exception {
        List<Organism> orgs = vocabularyService.getOrganisms();
        assertEquals(0, orgs.size());

        Organism org1 = new Organism();
        org1.setScientificName("Homo sapiens");
        org1.setTermSource(source);
        daoFactoryStub.getOrganismDao().save(org1);
        
        Organism org2 = new Organism();
        org2.setScientificName("Mus musculus");
        org2.setTermSource(source);
        daoFactoryStub.getOrganismDao().save(org2);

        Organism org3 = new Organism();
        org3.setScientificName("Rattus norvegicus");
        org3.setTermSource(source);
        daoFactoryStub.getOrganismDao().save(org3);

        Organism org4 = new Organism();
        org4.setScientificName("Rattus rattus");
        org4.setTermSource(source);
        daoFactoryStub.getOrganismDao().save(org4);

        Organism org5 = new Organism();
        org5.setScientificName("Drosophila melanogaster");
        org5.setTermSource(source);
        daoFactoryStub.getOrganismDao().save(org5);
        
        orgs = vocabularyService.getOrganisms();
        assertEquals(5, orgs.size());
        
        Organism org = vocabularyService.getOrganism(org5.getId());
        assertEquals(org5.getScientificName(), org.getScientificName());
        assertEquals(org5.getTermSource(), org.getTermSource());
        
        org = vocabularyService.getOrganism(new Long(1825));
        assertNull(org);
        
        org = vocabularyService.getOrganism(null, null);
        assertNull(org);
        org = vocabularyService.getOrganism(null, "");
        assertNull(org);
        org = vocabularyService.getOrganism(null, "asdf");
        assertNull(org);
        org = vocabularyService.getOrganism(source, null);
        assertNull(org);
        org = vocabularyService.getOrganism(source, "");
        assertNull(org);
        org = vocabularyService.getOrganism(source, "asdf");
        assertNull(org);
        org = vocabularyService.getOrganism(new TermSource(), org1.getScientificName());
        assertNull(org);
        org = vocabularyService.getOrganism(source, org1.getScientificName());
        assertEquals(org1.getScientificName(), org.getScientificName());
        assertEquals(org1.getTermSource(), org.getTermSource());
        org = vocabularyService.getOrganism(source, org2.getScientificName().toUpperCase());
        assertEquals(org2.getScientificName(), org.getScientificName());
        assertEquals(org2.getTermSource(), org.getTermSource());
        assertEquals(org2.getTermSource().toString(), org.getTermSource().toString());
    }

    // ////// INNER CLASS TEST STUBS///////////////////////
    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        LocalVocabularyDaoStub vocabularyDao = new LocalVocabularyDaoStub();
        LocalOrganismDaoStub organismDao = new LocalOrganismDaoStub();

        /**
         * {@inheritDoc}
         */
        @Override
        public VocabularyDao getVocabularyDao() {
            return vocabularyDao;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OrganismDao getOrganismDao() {
            return organismDao;
        }

    }

    private static class LocalVocabularyDaoStub extends VocabularyDaoStub {
        private static long nextId = 1;
        private static Map<Long, Term> termMap = new HashMap<Long, Term>();
        
        @Override
        public Set<Term> getTerms(Category category) throws DAOException {
            Set<Term> terms = new HashSet<Term>();
            for (Term term : termMap.values()) {
                if (term.getCategories().contains(category)) {
                    terms.add(term);
                }
            }
            return terms;
        }
        
        @Override
        public Set<Term> getTermsRecursive(Category category, String value) {
            Set<Category> searchCategories = new HashSet<Category>();
            searchCategories.add(category);
            addChildren(searchCategories, category);

            Set<Term> terms = new HashSet<Term>();
            for (Term term : termMap.values()) {
                if (CollectionUtils.containsAny(term.getCategories(), searchCategories)) {
                    if (value == null || term.getValue().toLowerCase().startsWith(value.toLowerCase())) {
                        terms.add(term);
                    }
                }
            }
            return terms;
        }
        
        private void addChildren(Collection<Category> searchCategories, Category category) {
            for (Category child : category.getChildren()) {
                searchCategories.add(child);
                addChildren(searchCategories, child);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Category getCategory(TermSource source, String name) {
            if (SOURCE_NAME.equals(source.getName()) && SOURCE_VERSION.equals(source.getVersion()) && CATEGORY_NAME.equalsIgnoreCase(name)) {
                Category category = new Category();
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
            for (Term term : termMap.values()) {
                if (term.getValue().equalsIgnoreCase(value) && term.getSource().equals(source)){
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
        public <T> List<T> queryEntityByExample(T entityToMatch, MatchMode mode, boolean excludeNulls,
                String[] excludeProperties, Order... order) {
            if (entityToMatch instanceof TermSource) {
                TermSource termSource = (TermSource) entityToMatch;
                if (SOURCE_NAME.equalsIgnoreCase(termSource.getName()) && SOURCE_VERSION.equalsIgnoreCase(termSource.getVersion())) {
                    List list = new ArrayList();
                    list.add(termSource);
                    return list;
                }
            }
                
            return super.queryEntityByExample(entityToMatch, mode, excludeNulls, excludeProperties, order);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Organism getOrganism(TermSource source, String scientificName) {
            List<Organism> orgs = new LocalDaoFactoryStub().getOrganismDao().getAllOrganisms();
            for (Organism org : orgs) {
                if (org.getTermSource().equals(source) && org.getScientificName().equalsIgnoreCase(scientificName)) {
                    return org;
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("deprecation")
        public void save(PersistentObject object) {
            Term term = (Term) object;
            term.setId(nextId++);
            this.termMap.put(term.getId(), term);
        }
    }
    
    private static class LocalOrganismDaoStub extends OrganismDaoStub {
        private static long nextId = 1;
        private static Map<Long, Organism> orgMap = new HashMap<Long, Organism>();
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Organism getOrganism(long id) {
            return orgMap.get(id);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public List<Organism> getAllOrganisms() {
            return new ArrayList<Organism>(orgMap.values());
        }
        
        public void save(PersistentObject object) {
            Organism org = (Organism) object;
            org.setId(nextId++);
            this.orgMap.put(org.getId(), org);
        }
    }
}
