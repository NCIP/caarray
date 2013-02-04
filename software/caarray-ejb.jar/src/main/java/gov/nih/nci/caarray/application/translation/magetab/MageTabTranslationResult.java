//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

/**
 * Result of MAGE-TAB translation. Also used to pass-around and look up shared objects.
 */
final class MageTabTranslationResult implements CaArrayTranslationResult {

    private static final long serialVersionUID = -1084448170352002555L;

    private final Map<OntologyTerm, Term> termMap = new HashMap<OntologyTerm, Term>();
    private final Map<String, Category> categoryMap = new HashMap<String, Category>();
    private final Map<TermSource, gov.nih.nci.caarray.domain.vocabulary.TermSource> termSourceMap =
        new HashMap<TermSource, gov.nih.nci.caarray.domain.vocabulary.TermSource>();
    private final Map<ExperimentalFactor, Factor> factorMap = new HashMap<ExperimentalFactor, Factor>();
    private final Collection<Experiment> investigations = new HashSet<Experiment>();
    private final Collection<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();

    /**
     * {@inheritDoc}
     */
    public Collection<ArrayDesign> getArrayDesigns() {
        return arrayDesigns;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Experiment> getInvestigations() {
        return investigations;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Term> getTerms() {
        return termMap.values();
    }

    public Collection<Category> getCategories() {
        return categoryMap.values();
    }

    Term getTerm(OntologyTerm term) {
        return termMap.get(term);
    }

    void addTerm(OntologyTerm mageTerm, Term term) {
        termMap.put(mageTerm, term);
    }

    Category getCategory(String name) {
        return categoryMap.get(StringUtils.upperCase(name));
    }

    void addCategory(String name, Category cat) {
        categoryMap.put(StringUtils.upperCase(name), cat);
    }

    /**
     * Punt organism terms from the cache of translated terms (since they
     * will be saved as Organism instances instead.
     */
    void removeOrganismTerms() {
        for (Iterator<Map.Entry<OntologyTerm, Term>> it = termMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<OntologyTerm, Term> termEntry = it.next();
            if (CollectionUtils.exists(termEntry.getValue().getCategories(), IsOrganismCategory.INSTANCE)) {
                it.remove();
            }
        }
    }

    gov.nih.nci.caarray.domain.vocabulary.TermSource getSource(TermSource termSource) {
        return termSourceMap.get(termSource);
    }

    void addSource(TermSource termSource, gov.nih.nci.caarray.domain.vocabulary.TermSource source) {
        termSourceMap.put(termSource, source);
    }

    void addInvestigation(Experiment investigation) {
        investigations.add(investigation);
    }

    Factor getFactor(ExperimentalFactor factor) {
        return factorMap.get(factor);
    }

    void addFactor(ExperimentalFactor mageTabFactor, Factor factor) {
        factorMap.put(mageTabFactor, factor);
    }

    /**
     * Predicate checking whether a given category is the Organism category.
     */
    private static final class IsOrganismCategory implements Predicate {
        public static final IsOrganismCategory INSTANCE = new IsOrganismCategory();

        public boolean evaluate(Object o) {
            Category c = (Category) o;
            return c.getName().equalsIgnoreCase(ExperimentOntologyCategory.ORGANISM.getCategoryName());
        }
    }
}
