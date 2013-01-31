//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.business.vocabulary;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Basic stub for tests.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class VocabularyServiceStub implements VocabularyService {

    public Set<Term> getTerms(Category category) {
        return getTerms(category, null);
    }

    public Set<Term> getTerms(Category category, String value) {
        Set<Term> terms = new HashSet<Term>();
        TermSource source = getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                ExperimentOntology.MGED_ONTOLOGY.getVersion());
        for (int i = 0; i < 10; i++) {
            Term term = createTerm(source, category, "term" + i);
            terms.add(term);
        }
        return terms;
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSource(String name, String version) {
        TermSource source = new TermSource();
        source.setName(name);
        source.setVersion(version);
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSourceByUrl(String url, String version) {
        TermSource source = new TermSource();
        source.setName("Name for " + url);
        source.setUrl(url);
        source.setVersion(version);
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSources(String name) {
        TermSource ts = getSource(name, null);
        Set<TermSource> result = new HashSet<TermSource>();
        result.add(ts);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSourcesByUrl(String url) {
        TermSource ts = getSourceByUrl(url, null);
        Set<TermSource> result = new HashSet<TermSource>();
        result.add(ts);
        return result;
    }

    public Term getTerm(TermSource source, String value) {
        Term term = new Term();
        term.setSource(source);
        term.setValue(value);
        Category cat = new Category();
        cat.setSource(source);
        cat.setName("Category for " + value);
        term.setCategory(cat);
        return term;
    }

    @SuppressWarnings("deprecation")
    public Term getTerm(Long id) {
        Term term = new Term();
        term.setId(id);
        return term;
    }

    public Organism getOrganism(Long id) {
        Organism org = new Organism();
        org.setId(id);
        return org;
    }

    public Organism getOrganism(TermSource source, String scientificName) {
        Organism org = new Organism();
        org.setTermSource(source);
        org.setScientificName(scientificName);
        return org;
    }

    public List<Organism> getOrganisms() {
        List<Organism> orgs = new ArrayList<Organism>();
        Organism o1 = new Organism();
        o1.setId(1L);
        o1.setScientificName("Mizouse");
        orgs.add(o1);
        return orgs;
    }

    public Category createCategory(TermSource source, String categoryName) {
        Category category = new Category();
        category.setSource(source);
        category.setName(categoryName);
        return category;
    }

    public Term createTerm(TermSource source, Category category, String value) {
        Term term = new Term();
        term.setSource(source);
        term.setValue(value);
        term.setCategory(category);
        return term;
    }

    public Category getCategory(TermSource source, String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    public TermSource createSource(String name, String url, String version) {
        TermSource ts = getSource(name, version);
        ts.setUrl(url);
        return ts;
    }

    public void saveTerm(Term term) {
        // do nothing
    }

    public List<TermSource> getAllSources() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Protocol> getProtocolsByProtocolType(Term type, String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Protocol getProtocol(String name, TermSource source) {
        return null;
    }

    public Term findTermInAllTermSourceVersions(TermSource termSource, String value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> searchForCharacteristicCategory(String keyword) {
        return new ArrayList<Category>();
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return new ArrayList<Organism>();
    }


}












